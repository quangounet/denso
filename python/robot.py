from pylab import *
from numpy import *
from openravepy import *

env = Environment()
env.Load("../../robots/denso-vs060.zae")
env.SetViewer('qtcoin')
robot=env.GetRobots()[0]


pole = RaveCreateKinBody(env,'')
pole.SetName('Pole')
pole.InitFromBoxes(array([array([0,0,0,0.02,0.02,0.6])]),True)
g=pole.GetLinks()[0].GetGeometries()[0]
g.SetAmbientColor([1,0,0])
g.SetDiffuseColor([1,0,0])
env.Add(pole,True)
Tr = eye(4)
Tr[0:3,3]=[0.2,0.2,0.6]
pole.SetTransform(Tr)


vmax = robot.GetDOFVelocityLimits()
amax = robot.GetDOFAccelerationLimits()

startvalues = zeros(3)
goalvalues = zeros(3)
goalvalues[0] = pi/2
goalvalues[1] = pi/2
robot.SetActiveDOFs(range(3))


robot.SetActiveDOFValues(startvalues)
params = Planner.PlannerParameters()
params.SetRobotActiveJoints(robot)
params.SetGoalConfig(goalvalues)
affix = "constraint"
affix = ""
extraparams = """<_postprocessing planner="%sparabolicsmoother">
    <_fStepLength>0</_fStepLength>
    <minswitchtime>0.1</minswitchtime>
    <_nmaxiterations>100</_nmaxiterations>
</_postprocessing>"""%(affix)

params.SetExtraParameters(extraparams)


planner=RaveCreatePlanner(env,'birrt')
planner.InitPlan(robot, params)

traj = RaveCreateTrajectory(env,'')
planner.PlanPath(traj)

robot.GetController().SetPath(traj)
