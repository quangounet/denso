from openravepy import *
from pylab import *

from TOPP import Utilities

import string
import numpy as np

INF = np.infty
EPS = 1e-12


############################## POLYNOMIALS ##############################
"""
NB: we adopt the weak-term-first convention for inputs
"""

def FindPolynomialCriticalPoints(coeff_list, interval = None):
    p = np.poly1d(coeff_list[::-1])
    pd = np.polyder(p)
    critical_points = pd.r
    pointslist = []
    if interval == None:
        interval = [-INF, INF]
    for x in critical_points:
        if (abs(x.imag) < EPS):
            if (x.real <= interval[1]) and (x.real >= interval[0]):
                pointslist.append(x.real)
    return pointslist


def TrajString3rdDegree(q_beg, q_end, qs_beg, qs_end, duration):
    trajectorystring = ''
    ndof = len(q_beg)
    trajectorystring += "%f\n%d"%(duration, ndof)
    for k in range(ndof):
        a, b, c, d = Utilities.Interpolate3rdDegree(q_beg[k], q_end[k], qs_beg[k], qs_end[k], duration)
        trajectorystring += "\n%f %f %f %f"%(d, c, b, a)
    return trajectorystring


def TrajString5thDegree(q_beg, q_end, qs_beg, qs_end, qss_beg, qss_end, duration):
    trajectorystring = ''
    ndof = len(q_beg)
    trajectorystring += "%f\n%d"%(duration, ndof)
    for k in range(ndof):
        a, b, c, d, e, f = Utilities.Interpolate5thDegree(q_beg[k], q_end[k], qs_beg[k], qs_end[k], qss_beg[k], qss_end[k], duration)
        trajectorystring += "\n%f %f %f %f %f %f"%(f, e, d, c, b, a)
    return trajectorystring


def CheckDOFLimits(robot, trajectorystring):
    trajinfo = string.split(trajectorystring, "\n")
    dur = float(trajinfo[0])
    ndof = int(trajinfo[1])
    
    for i in range(ndof):
        coeff_list = [float(j) for j in string.split(trajinfo[i + 2])]
        q = np.poly1d(coeff_list[::-1])
        qd = np.polyder(q)
        criticalqlist = FindPolynomialCriticalPoints(coeff_list, [0.0, dur])
        criticalqdlist = FindPolynomialCriticalPoints(qd.coeffs[::-1], [0.0, dur])
        
        if (len(criticalqlist) == 0):
            continue
        else:
            # check DOF values
            for j in criticalqlist:
                if (not abs(q(j)) <= robot.GetDOFLimits()[1][i]):
                    return False
            if (len(criticalqdlist) == 0):
                continue
            else:
                # check DOF velocities
                for k in criticalqdlist:
                    if (not abs(qd(k)) <= robot.GetDOFVelocityLimits()[i]):
                        return False
    
    return True


############################## ETC ##############################
def CheckIntersection(interval0, interval1):
    """CheckIntersection checks whether interval0 intersects interval1.
    """
    
    if (np.max(interval0) < np.min(interval1)):
        return False

    elif (np.max(interval1) < np.min(interval0)):
        return False
    
    else:
        return True


def Normalize(vect0):
    vect_norm = np.linalg.norm(vect0)
    assert(not vect_norm == 0)
    return vect0/vect_norm

def VRad2Deg(vect0):
    vect = [np.rad2deg(k) for k in vect0]
    return np.asarray(vect)

############################## PLOTTING ##############################

def PlotDOF(robot, traj, dt, dof = -1):
    ndof = traj.dimension
    lowerdof_lim = robot.GetDOFLimits()[0]
    upperdof_lim = robot.GetDOFLimits()[1]
    q = []
    T = arange(0, traj.duration, dt)
    for t in T:
        q.append(traj.Eval(t))
    
    if (dof == -1):
        for i in range(ndof):
            plt.figure()
            x = [k[i] for k in q]
            plt.plot(T, x)
            plt.show(False)
            plt.hold(True)
            plt.plot([T[0], T[-1]], [lowerdof_lim[i], lowerdof_lim[i]], '--')
            plt.plot([T[0], T[-1]], [upperdof_lim[i], upperdof_lim[i]], '--')
    else:
        plt.figure()
        x = [k[dof] for k in q]
        plt.plot(T, x)
        plt.show(False)
        plt.hold(True)
        plt.plot([T[0], T[-1]], [lowerdof_lim[dof], lowerdof_lim[dof]], '--')
        plt.plot([T[0], T[-1]], [upperdof_lim[dof], upperdof_lim[dof]], '--')
            

def PlotdDOF(robot, traj, dt, dof = -1):
    ndof = traj.dimension
    vel_lim = robot.GetDOFVelocityLimits()
    qd = []
    T = arange(0, traj.duration, dt)
    for t in T:
        qd.append(traj.Evald(t))
    
    if (dof == -1):
        for i in range(ndof):
            plt.figure()
            xd = [k[i] for k in qd]
            plt.plot(T, xd)
            plt.show(False)
            plt.hold(True)
            plt.plot([T[0], T[-1]], [-vel_lim[i], -vel_lim[i]], '--')
            plt.plot([T[0], T[-1]], [vel_lim[i], vel_lim[i]], '--')
    else:
        plt.figure()
        xd = [k[dof] for k in qd]
        plt.plot(T, xd)
        plt.show(False)
        plt.hold(True)
        plt.plot([T[0], T[-1]], [-vel_lim[dof], -vel_lim[dof]], '--')
        plt.plot([T[0], T[-1]], [vel_lim[dof], vel_lim[dof]], '--')
        

def PlotddDOF(robot, traj, dt, dof = -1):
    ndof = traj.dimension
    acc_lim = robot.GetDOFAccelerationLimits()
    qdd = []
    T = arange(0, traj.duration, dt)
    for t in T:
        qdd.append(traj.Evaldd(t))
    
    if (dof == -1):
        for i in range(ndof):
            plt.figure()
            xdd = [k[i] for k in qdd]
            plt.plot(T, xdd)
            plt.show(False)
            plt.hold(True)
            plt.plot([T[0], T[-1]], [-acc_lim[i], -acc_lim[i]], '--')
            plt.plot([T[0], T[-1]], [acc_lim[i], acc_lim[i]], '--')
    else:
        plt.figure()
        xdd = [k[dof] for k in qdd]
        plt.plot(T, xdd)
        plt.show(False)
        plt.hold(True)
        plt.plot([T[0], T[-1]], [-acc_lim[dof], -acc_lim[dof]], '--')
        plt.plot([T[0], T[-1]], [acc_lim[dof], acc_lim[dof]], '--')
        

def PlotDOFDeg(traj, dt, dof = -1):
    ndof = traj.dimension
    q = []
    T = arange(0, traj.duration, dt)
    for t in T:
        q.append(traj.Eval(t))
    
    if (dof == -1):
        for i in range(ndof):
            plt.figure()
            x = [np.rad2deg(k[i]) for k in q]
            plt.plot(T, x)
            plt.show(False)
            plt.hold(True)
    else:
        plt.figure()
        x = [np.rad2deg(k[dof]) for k in q]
        plt.plot(T, x)
        plt.show(False)
        plt.hold(True)
            
def PlotdDOFDeg(traj, dt, dof = -1):
    ndof = traj.dimension
    qd = []
    T = arange(0, traj.duration, dt)
    for t in T:
        qd.append(traj.Evald(t))
    
    if (dof == -1):
        for i in range(ndof):
            plt.figure()
            xd = [np.rad2deg(k[i]) for k in qd]
            plt.plot(T, xd)
            plt.show(False)
            plt.hold(True)
    else:
        plt.figure()
        xd = [np.rad2deg(k[dof]) for k in qd]
        plt.plot(T, xd)
        plt.show(False)
        plt.hold(True)
