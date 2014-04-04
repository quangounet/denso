from pylab import *
from numpy import *
import utilities
import converter
import Trajectory


ion()

vmax = array([ 3.92699082,  2.61799388,  2.85832572,  3.92699082,  3.02168853,
        6.28318531])
amax = array([ 19.73356519,  16.84469621,  20.70885517,  20.96646577,
        23.72286426,  33.51032164])

sampling = 0.008



affix = "constraint"
#affix = ""
filenamebase = affix + "parabolicsmoothing"
traj = Trajectory.PiecewisePolynomialTrajectory.FromString(open("../data/"+filenamebase+".topptraj","r").read())


















coef = 1.3
traj2 = traj.Retime(coef)

data = loadtxt("../data/"+filenamebase+".csv",delimiter=',',skiprows=5,converters={33: lambda x:0}) 


j0command = data[:-3,1]*pi/180
j0encoder = data[:-3,2]*pi/180
j1command = data[:-3,5]*pi/180
j1encoder = data[:-3,6]*pi/180
onset_command = utilities.detect_onset(diff(j0command)/sampling,start = 1000)
onset_encoder = utilities.detect_onset(diff(j0encoder)/sampling,start = 1000)
j0command = j0command[onset_command:-1]
j1command = j1command[onset_command:-1]
j0encoder = j0encoder[onset_encoder:-1]
j1encoder = j1encoder[onset_encoder:-1]
tvect = arange(0,10,0.008)

print Trajectory.Diff(trajx,traj,20)




utilities.PlotKinematics(traj,trajx2,0.001,vmax,amax)D
figure(0)
plot(tvect[range(len(j0command))],j0command,"r--",linewidth = 2)
plot(tvect[range(len(j0encoder))],j0encoder,"r-.",linewidth = 2)
plot(tvect[range(len(j1command))],j1command,"g--",linewidth = 2)
plot(tvect[range(len(j1encoder))],j1encoder,"g-.",linewidth = 2)
axis([0,tvect[len(j0encoder)],-pi,pi])
figure(1)
plot(tvect[range(len(j0command)-1)],diff(j0command)/sampling,"r--",linewidth = 2)
plot(tvect[range(len(j0encoder)-1)],diff(j0encoder)/sampling,"r-.",linewidth = 2)
plot(tvect[range(len(j1command)-1)],diff(j1command)/sampling,"g--",linewidth = 2)
plot(tvect[range(len(j1encoder)-1)],diff(j1encoder)/sampling,"g-.",linewidth = 2)
axis([0,tvect[len(j0encoder)],-7,7])
figure(2)
plot(tvect[range(len(j0command)-2)],diff(diff(j0command))/sampling/sampling,"r--",linewidth = 2)
plot(tvect[range(len(j0encoder)-2)],diff(diff(j0encoder))/sampling/sampling,"r-.",linewidth = 2)
plot(tvect[range(len(j1command)-2)],diff(diff(j1command))/sampling/sampling,"g--",linewidth = 2)
plot(tvect[range(len(j1encoder)-2)],diff(diff(j1encoder))/sampling/sampling,"g-.",linewidth = 2)
axis([0,tvect[len(j0encoder)],-40,40])

converter.CreateProgram(robot,traj,"../data/"+filenamebase+".pcs")
