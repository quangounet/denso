import sys
sys.path.append('../')

from openravepy import *

from TOPP import TOPPbindings
from TOPP import TOPPpy
from TOPP import TOPPopenravepy
from TOPP import Trajectory

import time
import string
import numpy as np
import copy
import random

"""
reach3.py
a test file for reaching task
the position of the bottle is imported from a text file

1) the transformation matrix obtained from the file is that of the end-effector, not including the tray
2) the task is divided into two subtasks
   2.1) the first goal is to move the tray to be 5cm below the bottle (-0.05 in the direction of z-axis)
   2.2) the second goal is to move the tray in the upward direction to reach the bottle
3) the joint values for the first subtask is computed using IK
4) the joint values for the second subtask is computed using differential kinematic,
   i.e., dx = J*dq
   assuming that 5cm is small enough

"""

env = Environment()
env.Load('../xml/env1.xml')
env.SetViewer('qtcoin')

collisionChecker = RaveCreateCollisionChecker(env, 'ode')
env.SetCollisionChecker(collisionChecker)

## set up environment
robot = env.GetRobots()[0]
floor = env.GetKinBody('floor')
floor.Enable(False)
box1 = env.GetKinBody('box1')
box2 = env.GetKinBody('box2')
box3 = env.GetKinBody('box3')
box4 = env.GetKinBody('box4')
box5 = env.GetKinBody('box5')
bottle = env.GetKinBody('bottle')

opening_height = 0.4

box1.SetTransform(np.array([[  0.0,  1.0,  0.0,  0.000], 
                            [ -1.0,  0.0,  0.0,  0.380], 
                            [  0.0,  0.0,  1.0,  0.695], 
                            [  0.0,  0.0,  0.0,  1.000]]))

box2.SetTransform(np.array([[  1.0,  0.0,  0.0,  0.00], 
                            [  0.0,  1.0,  0.0, -0.364], 
                            [  0.0,  0.0,  0.0,  opening_height + 0.32 + 0.79], 
                            [  0.0,  0.0,  0.0,  1.00]]))

box3.SetTransform(np.array([[  1.0,  0.0,  0.0,  0.00], 
                            [  0.0,  1.0,  0.0, -0.665], 
                            [  0.0,  0.0,  1.0,  0.455], 
                            [  0.0,  0.0,  0.0,  1.00]]))

box4.SetTransform(np.array([[  1.0,  0.0,  0.0,  0.00], 
                            [  0.0,  1.0,  0.0,  0.101],
                            [  0.0,  0.0,  1.0,  1.34],
                            [  0.0,  0.0,  0.0,  1.00]]))

box5.SetTransform(np.array([[  1.0,  0.0,  0.0,  0.00], 
                            [  0.0,  1.0,  0.0, -0.955], 
                            [  0.0,  0.0,  1.0,  0.800], 
                            [  0.0,  0.0,  0.0,  1.00]]))

box3.Enable(False)
box3.SetVisible(False)
box4.Enable(False)
box4.SetVisible(False)

H = np.array([[0, 1, 0, 0], [-1, 0, 0, 0], [0, 0, 1, 0.59], [0, 0, 0, 1]])
robot.SetTransform(H)

# bottle.SetTransform(H)
# bottle.Enable(False)
# bottle.SetVisible(False)

maxshortcutiter = 100

ipfilename = 'M_newpose2robot.txt'
filedirectory = '../../cri2/kinect_code_compile/bottle_picking/' + ipfilename

with open(filedirectory, 'r') as f:
    matrixstring = f.read()

y = [float(x) for x in matrixstring.split()]
## transformation matrix from camera's frame to the calibration frame
H_ref2realpos = np.array([[y[0], y[1], y[2], y[3]], 
                          [y[4], y[5], y[6], y[7]], 
                          [y[8], y[9], y[10], y[11]], 
                          [0.0, 0.0, 0.0, 1.0]])

init_config = np.array([  1.48000000e+00,   9.12246826e-01,   6.39601469e-01,
                          4.63102048e-16,  -1.34505559e+00,   8.51749227e-16])
# init_config = np.array([  2.40000000e-01,   9.12246826e-01,   6.39601469e-01,
#                           4.63102048e-16,  -1.34505559e+00,   8.51749227e-16])
robot.SetDOFValues(init_config)

H_goal = np.dot(H, H_ref2realpos)
# H_goal = H_ref2realpos

## desired position of the bottle
H_bottle = np.copy(H_goal)

x_endeffector = H_goal[0:3, 0]
y_endeffector = H_goal[0:3, 1]
z_endeffector = H_goal[0:3, 2]
p_endeffector = H_goal[0:3, 3]
offset = 0.017## the transformation matrix obtained is that of the end-effector (not including the tray)
pnew = p_endeffector + offset*z_endeffector
H_bottle[0][3] = pnew[0]
H_bottle[1][3] = pnew[1]
H_bottle[2][3] = pnew[2]
bottle.SetTransform(H_bottle)

# bottle.Enable(False)

manip = robot.SetActiveManipulator('Flange') ## set the manipulator to the flange (end-effector)
ikmodel = databases.inversekinematics.InverseKinematicsModel(robot, iktype = IkParameterization.Type.Transform6D)
if not ikmodel.load():
    ikmodel.autogenerate()

withsol1 = False

ntrials = 20

xlim = 0.05
ylim = 0.05
r = random.SystemRandom()

goal1offset = 0.05

pnew1 = np.copy(p_endeffector) - goal1offset*z_endeffector
for i in range(ntrials):
    curx = -xlim + 2*r.random()*xlim
    cury = -ylim + 2*r.random()*ylim
    
    ## sample a position to place the bottle on the tray
    p = pnew1 + curx*x_endeffector + cury*y_endeffector
    H_goal1 = np.copy(H_goal)
    H_goal1[0][3] = p[0]
    H_goal1[1][3] = p[1]
    H_goal1[2][3] = p[2]

    pbottle_temp = np.copy(p_endeffector) + (-goal1offset + offset)*z_endeffector
    p = pbottle_temp + curx*x_endeffector + cury*y_endeffector
    H_bottle_temp = np.copy(H_goal)
    H_bottle_temp[0][3] = p[0]
    H_bottle_temp[1][3] = p[1]
    H_bottle_temp[2][3] = p[2]
    bottle.SetTransform(H_bottle_temp)
    
    t_sol1_start = time.time()
    with env:
        ## get collision-free solutions
        solutionset1 = manip.FindIKSolutions(H_goal1, IkFilterOptions.CheckEnvCollisions)
        
    if (len(solutionset1) == 0):
        ## no solution found
        continue
    t_sol1_end = time.time()
    
    print "IK solutions are found"
    
    for sol1 in solutionset1:

        ## look for an appropriate solution
        if (sol1[1] < 0):
            continue
        if (abs(sol1[3]) > np.pi/2.0):
            continue
        if (abs(sol1[4]) > np.pi/2.0):
            continue

        # constrain the last joint not to move
        sol1[-1] = 0.0

        robot.SetDOFValues(init_config)
        
        params = Planner.PlannerParameters()
        params.SetRobotActiveJoints(robot)
        params.SetGoalConfig(sol1)
        extraparams = '<_postprocessing></_postprocessing>'
        params.SetExtraParameters(extraparams)
        extraparams = '<_postprocessing planner="' + 'parabolic' + 'smoother' + '">'
        extraparams += '<_nmaxiterations>' + str(int(maxshortcutiter)) + '</_nmaxiterations></_postprocessing>'
        params.SetExtraParameters(extraparams)
        planner = RaveCreatePlanner(env, 'birrt')
        planner.InitPlan(robot, params)

        t_goal1_start = time.time()
        ravetraj1 = RaveCreateTrajectory(env, '')
        status1 = planner.PlanPath(ravetraj1)
        if (status1 == 1 or status1 == 3):
            t_goal1_end = time.time()
            TOPPtraj1 = TOPPopenravepy.FromRaveTraj(robot, ravetraj1)
            traj1 = TOPPopenravepy.FromRaveTraj(robot, ravetraj1)
            print "Solution trajectory (1) is found"
            withsol1 = True
            break
        
    if (withsol1):
        break

## actual goal
p = p + goal1offset*z_endeffector
H_bottle_temp = np.copy(H_goal)
H_bottle_temp[0][3] = p[0]
H_bottle_temp[1][3] = p[1]
H_bottle_temp[2][3] = p[2]

## set the bottle back to the actual position
bottle.SetTransform(H_bottle_temp)

##########################################
# withsol2 = False

# ## using differential kinematics
# L = robot.GetLink('link6')
# dx_angular = np.zeros(3)

# smallincrement = 0.001
# nsteps = (goal1offset/smallincrement) - 1
# sol2 = copy.deepcopy(sol1)

# t_sol2_start = time.time()
# for i in range(int(nsteps)):
#     J_linear = robot.CalculateJacobian(L.GetIndex(), L.GetLocalCOM())
#     J_angular = robot.CalculateAngularVelocityJacobian(L.GetIndex())
#     J = np.vstack((J_linear, J_angular))
    
#     dx_linear = smallincrement*z_endeffector
#     dx = np.hstack((dx_linear, dx_angular))

#     dq = np.dot(np.linalg.inv(J), dx)
#     sol2 = sol2 + dq
#     # robot.SetDOFValues(sol2)
#     # raw_input()
# t_sol2_end = time.time()

# ## find a trajectory for subtask2
# t_goal2_start = time.time()
# robot.SetDOFValues(sol1)
# maxshortcutiter = 50      
# params = Planner.PlannerParameters()
# params.SetRobotActiveJoints(robot)
# params.SetGoalConfig(sol2)
# extraparams = '<_postprocessing></_postprocessing>'
# params.SetExtraParameters(extraparams)
# extraparams = '<_postprocessing planner="' + 'parabolic' + 'smoother' + '">'
# extraparams += '<_nmaxiterations>' + str(int(maxshortcutiter)) + '</_nmaxiterations></_postprocessing>'
# params.SetExtraParameters(extraparams)
# planner = RaveCreatePlanner(env, 'birrt')
# planner.InitPlan(robot, params)

# ravetraj2 = RaveCreateTrajectory(env, '')
# status2 = planner.PlanPath(ravetraj2)
# if (status2 == 1 or status2 == 3):
#     t_goal2_end = time.time()
#     TOPPtraj2 = TOPPopenravepy.FromRaveTraj(robot, ravetraj2)
#     traj2 = TOPPopenravepy.FromRaveTraj(robot, ravetraj2)
#     print "Solution trajectory (2) is found"
#     withsol2 = True
# else:
#     print "Error: cannot find solution trajectory (2)"
#     raw_input()


# print "--Subtask1--"
# print "\tcomputing IK solutions:", t_sol1_end - t_sol1_start, "sec."
# print "\tplanning trajectory:", t_goal1_end - t_goal1_start, "sec."
# print "\n"
# print "--Subtask2--"
# print "\tcomputing IK solutions:", t_sol2_end - t_sol2_start, "sec."
# print "\tplanning trajectory:", t_goal2_end - t_goal2_start, "sec."



# if (withsol1 and withsol2):
#     trajectorystring = str(traj1) + "\n" + str(traj2)
#     with open('../data/movetobottle.traj', 'w') as f:
#         f.write(trajectorystring)
#     print ""
#     print "exit reach3.py"
#     # quit
# else:
#     print "NO SOLUTION FOUND"
#     raw_input()





###################################################
withsol2 = False
if (withsol1):
    robot.SetDOFValues(sol1)

    pnew2 = np.copy(p_endeffector)
    curx = 0.0
    cury = 0.0
    for i in range(ntrials):
        
        p = pnew2 + curx*x_endeffector + cury*y_endeffector
        H_goal2 = np.copy(H_goal)
        H_goal2[0][3] = p[0]
        H_goal2[1][3] = p[1]
        H_goal2[2][3] = p[2]
        
        t_sol2_start = time.time()
        with env:
            solutionset2 = manip.FindIKSolutions(H_goal2, IkFilterOptions.CheckEnvCollisions)
        t_sol2_end = time.time()

        if (len(solutionset2) == 0):
            curx = -xlim + 2*r.random()*xlim
            cury = -ylim + 2*r.random()*ylim
            continue
    
        print "IK solutions are found"
    
        for sol2 in solutionset2:
            if (sol2[1] < 0):
                continue
            if (abs(sol2[3]) > np.pi/2.0):
                continue
            if (abs(sol2[4]) > np.pi/2.0):
                continue

            sol2[-1] = 0.0
  
            params = Planner.PlannerParameters()
            params.SetRobotActiveJoints(robot)
            params.SetGoalConfig(sol2)
            extraparams = '<_postprocessing></_postprocessing>'
            params.SetExtraParameters(extraparams)
            extraparams = '<_postprocessing planner="' + 'parabolic' + 'smoother' + '">'
            extraparams += '<_nmaxiterations>' + str(int(maxshortcutiter)) + '</_nmaxiterations></_postprocessing>'
            params.SetExtraParameters(extraparams)
            planner = RaveCreatePlanner(env, 'birrt')
            planner.InitPlan(robot, params)

            t_goal2_start = time.time()
            ravetraj2 = RaveCreateTrajectory(env, '')
            status2 = planner.PlanPath(ravetraj2)
            if (status2 == 1 or status2 == 3):
                t_goal2_end = time.time()
                TOPPtraj2 = TOPPopenravepy.FromRaveTraj(robot, ravetraj2)
                traj2 = TOPPopenravepy.FromRaveTraj(robot, ravetraj2)
                print "Solution trajectory (2) is found"
                withsol2 = True
                break
        
        if (withsol2):
            break
        
        curx = -xlim + 2*r.random()*xlim
        cury = -ylim + 2*r.random()*ylim

print "--Subtask1--"
print "\tcomputing IK solutions:", t_sol1_end - t_sol1_start, "sec."
print "\tplanning trajectory:", t_goal1_end - t_goal1_start, "sec."
print "\n"
print "--Subtask2--"
print "\tcomputing IK solutions:", t_sol2_end - t_sol2_start, "sec."
print "\tplanning trajectory:", t_goal2_end - t_goal2_start, "sec."

if (withsol1 and withsol2):
    trajectorystring = str(traj1) + "\n" + str(traj2)
    with open('../data/movetobottle.traj', 'w') as f:
        f.write(trajectorystring)
    print ""
    print "exit reach3.py"
    # quit
else:
    print "NO SOLUTION FOUND"
    raw_input()
