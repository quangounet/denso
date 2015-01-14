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

"""
reach2.py
a test file for reaching task
the position of the bottle is imported from a text file
"""

env = Environment()
env.Load('../src/xml/env1.xml')
env.SetViewer('qtcoin')

## set up environment
robot = env.GetRobots()[0]
floor = env.GetKinBody('floor')
box1 = env.GetKinBody('box1')
box2 = env.GetKinBody('box2')
box3 = env.GetKinBody('box3')
box4 = env.GetKinBody('box4')
box5 = env.GetKinBody('box5')
bottle = env.GetKinBody('bottle')

opening_height = 0.5

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

box4.Enable(False)
box4.SetVisible(False)

H = np.array([[0, 1, 0, 0], [-1, 0, 0, 0], [0, 0, 1, 0.59], [0, 0, 0, 1]])
robot.SetTransform(H)
bottle.SetTransform(H)

H_rotation = np.array([[-1,  0,  0,  0], 
                       [ 0, -1,  0,  0], 
                       [ 0,  0,  1,  0], 
                       [ 0,  0,  0,  1]])

H_translation = np.array([[ 1,  0,  0, -0.25], 
                          [ 0,  1,  0, -0.25], 
                          [ 0,  0,  1,  0.00], 
                          [ 0,  0,  0,  1.00]])

## rotate the bottle 180 degree and translate it the external ref frame
H_2ref = np.dot(np.dot(H, H_rotation), H_translation)
bottle.SetTransform(H_2ref)

## transfromation matrix from the ref frame to the real position
ipfilename = 'real2ref_transformation.txt'
filedirectory = '../src/data/' + ipfilename

with open(ipfilename, 'r') as f:
    matrixstring = f.read()

y = [float(x) for x in matrixstring.split()]
# transformation matrix from camera's frame to the calibration frame
H_ref2realpos = np.array([[y[0], y[1], y[2], y[3]], 
                          [y[4], y[5], y[6], y[7]], 
                          [y[8], y[9], y[10], y[11]], 
                          [0.0, 0.0, 0.0, 1.0]])

H_res = np.dot(H_2ref, H_ref2realpos)
bottle.SetTransfrom(H_res)

init_config = np.array([  0.00000000e+00,   9.12246826e-01,   6.39601469e-01,
                          4.63102048e-16,  -1.34505559e+00,   8.51749227e-16])

H_goal = H_res
H_goal[2][3] -= 0.016 # offset (the height of the tray)

manip = robot.SetActiveManipulator('Flange') # set the manipulator to the flange (end-effector)
ikmodel = databases.inversekinematics.InverseKinematicsModel(robot, iktype = IkParameterization.Type.Transform6D)
if not ikmodel.load():
    ikmodel.autogenerate()

with env: # lock environment
    sol = manip.FindIKSolution(H_goal, IkFilterOptions.CheckEnvCollisions) # get collision-free solution

robot.SetDOFValues(init_config)
with robot:
    params = Planner.PlannerParameters()
    params.SetRobotActiveJoints(robot)
    params.SetGoalConfig(sol)
    extraparams = '<_postprocessing planner="' + 'parabolic' + 'smoother' + '">'
    extraparams += '<_nmaxiterations>' + str(int(maxiter)) + '</_nmaxiterations></_postprocessing>'
    params.SetExtraParameters(extraparams)
            
    planner = RaveCreatePlanner(env, 'birrt')
    planner.InitPlan(robot, params)
            
    ravetraj = RaveCreateTrajectory(env, '')
    status = planner.PlanPath(ravetraj)
    if (status == 1 or status == 3):
        TOPPtraj = TOPPopenravepy.FromRaveTraj(robot, ravetraj)
        
