#cd "C:\Documents and Settings\RRC Staff\git\denso\python"
#cd git/denso/python/

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
trajref = Trajectory.PiecewisePolynomialTrajectory.FromString(open("../data/bottle.topptraj","r").read())
trajref = trajref.ExtractDOFs([0,1,2,3,4,5])

# Compute the optimal waypoints
nwaypoints = 5
nsamples = 100
cpos = 1000
cvel = 10
cacc = 1
cdur = 0
gainoptim = True
xopt = denso.FindOptTraj(trajref,nwaypoints,nsamples,[cpos,cvel,cacc,cdur],vmax,amax,gainoptim,maxiter=5000)
ndof = trajref.dimension
qstart = trajref.Eval(0)
qend = trajref.Eval(trajref.duration)
trajopt = denso.MakeTraj(xopt,qstart,qend,ndof,nwaypoints,vmax,amax)
close('all')
denso.PlotKinematics(trajref,trajopt,dt=0.001,colorcycle=['r','g','b','m','c','y'],tstart=0,rescale=True)

# Write the optimal waypoints into a pcs file
nextracols = 0
qlist,vcoeflist,acoeflist = denso.ListFromVector(xopt,trajref.dimension,nwaypoints)
qlist.insert(0,trajref.Eval(0))
qlist.append(trajref.Eval(trajref.duration))
denso.CreateProgramBCAP(qlist,"../data/bottle.waypoints",vcoeflist=vcoeflist,acoeflist=acoeflist,nextracols=nextracols)
#denso.CreateProgram(qlist,"../data/constraintparabolicsmoothingopt.pcs",vcoeflist=vcoeflist,acoeflist=acoeflist,nextracols=nextracols)
#open("../data/constraintparabolicsmoothingopt.topptraj","w").write(str(trajopt))

