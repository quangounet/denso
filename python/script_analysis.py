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

# Loading robot command and encoder data
#cd "C:\Documents and Settings\RRC Staff\git\cri1\denso\python"
#cd git/denso/python/
datafile = "../data/constraintparabolicsmoothingopt.csv"
data = loadtxt(datafile,delimiter=',',skiprows=5,converters={33: lambda x:0}) 
j0command = data[:-3,1]*pi/180
j0encoder = data[:-3,2]*pi/180
j1command = data[:-3,5]*pi/180
j1encoder = data[:-3,6]*pi/180
j2command = data[:-3,9]*pi/180
j2encoder = data[:-3,10]*pi/180
onset_command = 0
onset_encoder = 0
j0command = j0command[onset_command:-1]
j0encoder = j0encoder[onset_encoder:-1]
j1command = j1command[onset_command:-1]
j1encoder = j1encoder[onset_encoder:-1]
j2command = j2command[onset_command:-1]
j2encoder = j2encoder[onset_encoder:-1]
tvect = arange(0,100,0.008)

plot_command = True
plot_encoder = False

# Plotting robot command and encoder data
figure(0)
clf()
if plot_command:
    plot(tvect[range(len(j0command))],j0command,"r",linewidth = 2)
    plot(tvect[range(len(j1command))],j1command,"g",linewidth = 2)
    plot(tvect[range(len(j2command))],j2command,"b",linewidth = 2)
if plot_encoder:
    plot(tvect[range(len(j0encoder))],j0encoder,"r",linewidth = 2)
    plot(tvect[range(len(j1encoder))],j1encoder,"g",linewidth = 2)
    plot(tvect[range(len(j2encoder))],j2encoder,"b",linewidth = 2)
figure(1)
clf()
if plot_command:
    plot(tvect[range(len(j0command)-1)],diff(j0command)/sampling,"r",linewidth = 2)
    plot(tvect[range(len(j1command)-1)],diff(j1command)/sampling,"g",linewidth = 2)
    plot(tvect[range(len(j2command)-1)],diff(j2command)/sampling,"b",linewidth = 2)
if plot_encoder:
    plot(tvect[range(len(j0encoder)-1)],diff(j0encoder)/sampling,"r",linewidth = 2)
    plot(tvect[range(len(j1encoder)-1)],diff(j1encoder)/sampling,"g",linewidth = 2)
    plot(tvect[range(len(j2encoder)-1)],diff(j2encoder)/sampling,"b",linewidth = 2)
figure(2)
clf()
if plot_command:
    plot(tvect[range(len(j0command)-2)],diff(diff(j0command))/sampling/sampling,"r",linewidth = 2)
    plot(tvect[range(len(j1command)-2)],diff(diff(j1command))/sampling/sampling,"g",linewidth = 2)
    plot(tvect[range(len(j2command)-2)],diff(diff(j2command))/sampling/sampling,"b",linewidth = 2)
if plot_encoder:
    plot(tvect[range(len(j0encoder)-2)],diff(diff(j0encoder))/sampling/sampling,"r",linewidth = 2)
    plot(tvect[range(len(j1encoder)-2)],diff(diff(j1encoder))/sampling/sampling,"g",linewidth = 2)
    plot(tvect[range(len(j2encoder)-2)],diff(diff(j2encoder))/sampling/sampling,"b",linewidth = 2)

tstart = 5.673
tend = 6.987
#tstart = 8.25
#tend = 9.47

# Reference trajectory (compare with encoder values)
trajref = Trajectory.PiecewisePolynomialTrajectory.FromString(open("../data/constraintparabolicsmoothing.topptraj","r").read())
trajref = trajref.ExtractDOFs([0,1,2])
if plot_encoder:
    denso.PlotKinematics(trajref.Retime((tend-tstart)/trajref.duration),None,dt=0.001,colorcycle=['r','g','b'],tstart=tstart)

# Optimal trajectory computed by denso.FindOptTraj (compare with command values)
trajopt = Trajectory.PiecewisePolynomialTrajectory.FromString(open("../data/constraintparabolicsmoothingopt.topptraj","r").read())
if plot_command:
    denso.PlotKinematics(trajopt,None,dt=0.001,colorcycle=['r','g','b'],tstart=tstart-0.05)


# Set axis values
figure(0)
axis([tstart-0.1,tend+0.1,-0.5,1.7])
figure(1)
axis([tstart-0.1,tend+0.1,-4,4])
figure(2)
axis([tstart-0.1,tend+0.1,-25,25])




# Interpolation2
#tstart = 5.092   #5/8
#tstart = 7.91    #3/8
#tstart = 10.73   #80/140
#tstart = 14.87   #60/140
#tstart = 19.028  #5/90
#tstart = 22.77   #85/90
#tstart = 26.5    #5/2
#tstart = 29.19   #80/10
#tstart = 32.73   #80/75
#tstart = 36.35   #5/-75
#tstart = 39.99   #[80,20/140,45]
#tstart = 44.16   #[80,20/95,90]
# Interpolation3
#tstart = 8.643  #[80,20/90,40]
#tstart = 12.52  #[80,20/90,90]
#tstart = 16.92 #[80,20/90,-20]
#tstart = 20.885 #[80,20/90,-70]
# Interpolation4
#tstart = 5.02  #[80,20/90,30]
#tstart = 8.825  #[80,20/90,40]
#tstart = 12.72  #[80,20/90,50]
#tstart = 16.67  #[80,20/90,60]
#tstart = 29.036  #[80,20/90,90]
# Interpolationx
#tstart = 6.027  #[80,20/90,44]
#tstart = 9.922  #[80,20/90,45]
#tstart = 21.634  #[80,20/90,48]
# Multi
#tstart = 7.525  
# Multi2
#tstart = 3.97  
# Constraintparabolicsmoothing
# tstart = 8.186

# q0 = array([0,0,0])
# q1 = array([6.51692555191,-6.07150090305,4.50541291934])*pi/180.
# q2 = array([25.5761791963,-12.1385339664,12.50340902890])*pi/180. 
# q3 = array([59.9517377335,-0.595281829422,17.426050813])*pi/180.
# q4 = array([76.3672127063,44.2279916565,7.56450896392])*pi/180.
# q5 = array([79.4944556528,78.2444258031,0.040501735369])*pi/180.
# q6 = array([90.0,90.0,0.0])*pi/180.
# traj = utilities.Interpolate([q0,q1,q2,q3,q4,q5,q6],vmax[0:3],amax[0:3],tstart)
# utilities.PlotKinematics(traj,traj,dt=0.001,colorcycle=['m','c','y'],tstart=tstart)
# figure(0)
# axis([tstart,tstart+3,-pi,pi])
# figure(1)
# axis([tstart,tstart+3,-5,5])
# figure(2)
# axis([tstart,tstart+3,-30,30])

#utilities.PlotOptim(q0,q1,vmax0,amax0,tstart,color='c')
#tstart1 = 5.178
#tstart1 = 11.09
#tstart1 = 19.1
#tstart1 = 23.28
#tstart1 = 26.59
#tstart1 = 29.55
#utilities.PlotOptim(q1,q2,vmax0,amax0,tstart1,color='m')
#utilities.PlotOptim2(q0,q1,q2,vmax0,amax0,tstart,color='k')







