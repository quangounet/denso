from pylab import *
from numpy import *
import Trajectory
import denso


ion()

vmax = array([ 3.92699082,  2.61799388,  2.85832572,  3.92699082,  3.02168853,
        6.28318531])
amax = array([ 19.73356519,  16.84469621,  20.70885517,  20.96646577,
        23.72286426,  33.51032164])
sampling = 0.008

# Load the trajectory
trajref = Trajectory.PiecewisePolynomialTrajectory.FromString(open("../data/constraintparabolicsmoothing.topptraj","r").read())
trajref = trajref.ExtractDOFs([0,1,2])

# Compute the optimal waypoints
nwaypoints = 7
nsamples = 100
cpos = 10000
cvel = 10
cacc = 0.01
cdur = 0.01
trajopt,qlistopt = denso.FindOptTraj(trajref,nwaypoints,nsamples,[cpos,cvel,cacc,cdur],vmax,amax)
denso.PlotKinematics(trajref.Retime(trajopt.duration/trajref.duration),trajopt,dt=0.001,colorcycle=['r','g','b'],tstart=0)

# Write the optimal waypoints into a pcs file
nextracols = 3
denso.CreateProgram(qlistopt,"../data/constraintparabolicsmoothingopt.pcs",nextracols)
open("../data/constraintparabolicsmoothingopt.topptraj","w").write(str(trajopt))

