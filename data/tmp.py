traj = traj000

from scipy import interpolate
import numpy as np
from numpy import *
import matplotlib.pyplot as plt

nsamples = 50

t = linspace(0, traj.duration, nsamples)

dt = 0.008

tnew0 = [i for i in np.arange(0, 1.5, dt*0.7)]
tnew1 = [i for i in np.arange(1.5, 3.25, dt*1.1)]
tnew2 = [i for i in np.arange(3.25, 3.75, dt*1.35*1.1)]
tnew3 = [i for i in np.arange(3.75, traj.duration + dt, dt)]
tnew = []
for j in tnew0:
    tnew.append(j)
for j in tnew1:
    tnew.append(j)
for j in tnew2:
    tnew.append(j)
for j in tnew3:
    tnew.append(j)
tnew = np.asarray(tnew)

q0 = np.array([traj.Eval(i)[0] for i in t])
q1 = np.array([traj.Eval(i)[1] for i in t])
q2 = np.array([traj.Eval(i)[2] for i in t])
q3 = np.array([traj.Eval(i)[3] for i in t])
q4 = np.array([traj.Eval(i)[4] for i in t])
q5 = np.array([traj.Eval(i)[5] for i in t])

qd0 = np.array([traj.Evald(i)[0] for i in t])
qd1 = np.array([traj.Evald(i)[1] for i in t])
qd2 = np.array([traj.Evald(i)[2] for i in t])
qd3 = np.array([traj.Evald(i)[3] for i in t])
qd4 = np.array([traj.Evald(i)[4] for i in t])
qd5 = np.array([traj.Evald(i)[5] for i in t])

qdd0 = np.array([traj.Evaldd(i)[0] for i in t])
qdd1 = np.array([traj.Evaldd(i)[1] for i in t])
qdd2 = np.array([traj.Evaldd(i)[2] for i in t])
qdd3 = np.array([traj.Evaldd(i)[3] for i in t])
qdd4 = np.array([traj.Evaldd(i)[4] for i in t])
qdd5 = np.array([traj.Evaldd(i)[5] for i in t])




degree = 5
smooth = .0008

tck0 = scipy.interpolate.splrep(t, q0, k = degree, s = smooth)
tck1 = scipy.interpolate.splrep(t, q1, k = degree, s = smooth)
tck2 = scipy.interpolate.splrep(t, q2, k = degree, s = smooth)
tck3 = scipy.interpolate.splrep(t, q3, k = degree, s = smooth)
tck4 = scipy.interpolate.splrep(t, q4, k = degree, s = smooth)
tck5 = scipy.interpolate.splrep(t, q5, k = degree, s = smooth)

q0new = scipy.interpolate.splev(tnew, tck0)
q1new = scipy.interpolate.splev(tnew, tck1)
q2new = scipy.interpolate.splev(tnew, tck2)
q3new = scipy.interpolate.splev(tnew, tck3)
q4new = scipy.interpolate.splev(tnew, tck4)
q5new = scipy.interpolate.splev(tnew, tck5)

qd0new = scipy.interpolate.splev(tnew, tck0, der = 1)
qd1new = scipy.interpolate.splev(tnew, tck1, der = 1)
qd2new = scipy.interpolate.splev(tnew, tck2, der = 1)
qd3new = scipy.interpolate.splev(tnew, tck3, der = 1)
qd4new = scipy.interpolate.splev(tnew, tck4, der = 1)
qd5new = scipy.interpolate.splev(tnew, tck5, der = 1)

qdd0new = scipy.interpolate.splev(tnew, tck0, der = 2)
qdd1new = scipy.interpolate.splev(tnew, tck1, der = 2)
qdd2new = scipy.interpolate.splev(tnew, tck2, der = 2)
qdd3new = scipy.interpolate.splev(tnew, tck3, der = 2)
qdd4new = scipy.interpolate.splev(tnew, tck4, der = 2)
qdd5new = scipy.interpolate.splev(tnew, tck5, der = 2)


plt.figure(1)
plt.hold(True)
plt.plot(t, q0, 'b')
plt.plot(tnew, q0new, 'b--')

plt.plot(t, qd0, 'r')
plt.plot(tnew, qd0new, 'r--')

plt.plot(t, qdd0, 'g')
plt.plot(tnew, qdd0new, 'g--')


#######################################################################################

def integ(x, tck, constant=-1):
    x = np.atleast_1d(x)
    out = np.zeros(x.shape, dtype=x.dtype)
    for n in xrange(len(out)):
        out[n] = interpolate.splint(0, x[n], tck)
    out += constant
    return out

smooth = 0.08
degree = 3

tck0 = scipy.interpolate.splrep(t, qd0, k = degree, s = smooth)
tck1 = scipy.interpolate.splrep(t, qd1, k = degree, s = smooth)
tck2 = scipy.interpolate.splrep(t, qd2, k = degree, s = smooth)
tck3 = scipy.interpolate.splrep(t, qd3, k = degree, s = smooth)
tck4 = scipy.interpolate.splrep(t, qd4, k = degree, s = smooth)
tck5 = scipy.interpolate.splrep(t, qd5, k = degree, s = smooth)

qd0new = scipy.interpolate.splev(tnew, tck0, der = 0)
qd1new = scipy.interpolate.splev(tnew, tck1, der = 0)
qd2new = scipy.interpolate.splev(tnew, tck2, der = 0)
qd3new = scipy.interpolate.splev(tnew, tck3, der = 0)
qd4new = scipy.interpolate.splev(tnew, tck4, der = 0)
qd5new = scipy.interpolate.splev(tnew, tck5, der = 0)

qdd0new = scipy.interpolate.splev(tnew, tck0, der = 1)
qdd1new = scipy.interpolate.splev(tnew, tck1, der = 1)
qdd2new = scipy.interpolate.splev(tnew, tck2, der = 1)
qdd3new = scipy.interpolate.splev(tnew, tck3, der = 1)
qdd4new = scipy.interpolate.splev(tnew, tck4, der = 1)
qdd5new = scipy.interpolate.splev(tnew, tck5, der = 1)

q0new = integ(tnew, tck0)
q0new = q0new - (q0new[0] - q0[0])
q1new = integ(tnew, tck1)
q1new = q1new - (q1new[0] - q1[0])
q2new = integ(tnew, tck2)
q2new = q2new - (q2new[0] - q2[0])
q3new = integ(tnew, tck3)
q3new = q3new - (q3new[0] - q3[0])
q4new = integ(tnew, tck4)
q4new = q4new - (q4new[0] - q4[0])
q5new = integ(tnew, tck5)
q5new = q5new - (q5new[0] - q5[0])


plt.figure(2)
plt.hold(True)
plt.plot(t, q0, 'b')
plt.plot(tnew, q0new, 'b--')

plt.plot(t, qd0, 'r')
plt.plot(tnew, qd0new, 'r--')

plt.plot(t, qdd0, 'g')
plt.plot(tnew, qdd0new, 'g--')



qnew = np.array([[q for q in q0new], 
		 [q for q in q1new], 
		 [q for q in q2new], 
		 [q for q in q3new], 
		 [q for q in q4new], 
		 [q for q in q5new]]).T

qdnew = np.array([[qd for qd in qd0new], 
		  [qd for qd in qd1new], 
		  [qd for qd in qd2new], 
		  [qd for qd in qd3new], 
		  [qd for qd in qd4new], 
		  [qd for qd in qd5new]]).T

qddnew = np.array([[qdd for qdd in qdd0new], 
		   [qdd for qdd in qdd1new], 
		   [qdd for qdd in qdd2new], 
		   [qdd for qdd in qdd3new], 
		   [qdd for qdd in qdd4new], 
		   [qdd for qdd in qdd5new]]).T



ss = ''
separator = ''
q0newdeg = np.rad2deg(q0new)
q1newdeg = np.rad2deg(q1new)
q2newdeg = np.rad2deg(q2new)
q3newdeg = np.rad2deg(q3new)
q4newdeg = np.rad2deg(q4new)
q5newdeg = np.rad2deg(q5new)
for i in range(len(q0new)):
    ss += separator
    ss += "{0} {1} {2} {3} {4} {5}".format(q0newdeg[i], q1newdeg[i], q2newdeg[i], q3newdeg[i], q4newdeg[i], q5newdeg[i])
    separator = "\n"




with open('../data/table4.traj', 'w') as f:
    f.write(ss)
