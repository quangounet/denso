#include "DensoController.h"
#include <iostream>
#include <math.h>
#include <time.h>

namespace DensoController {

DensoController::DensoController(const char* server_ip_address0, int server_port_num0) {
    server_ip_address = server_ip_address0;
    server_port_num = server_port_num0;
}

////////////////////////////// Low Level Commands //////////////////////////////

void DensoController::bCapOpen() {
    std::cout << "\033[1;32mInitialize and start b-CAP.\033[0m\n";
    BCAP_HRESULT hr = bCap_Open(server_ip_address, server_port_num, &iSockFD);
    if FAILED(hr) {
        throw bCapException("\033[1;31bCap_Open failed.\033[0m\n");
    }
}

void DensoController::bCapClose() {
    std::cout << "\033[1;32mStop b-CAP.\033[0m\n";
    BCAP_HRESULT hr = bCap_Close(iSockFD);
    if FAILED(hr) {
        throw bCapException("\033[1;31bCap_Close failed.\033[0m\n");
    }
}

void DensoController::bCapServiceStart() {
    std::cout << "\033[1;32mStart b-CAP service.\033[0m\n";
    BCAP_HRESULT hr = bCap_ServiceStart(iSockFD);
    if FAILED(hr) {
        throw bCapException("\033[1;31bCap_ServiceStart failed.\033[0m\n");
    }
}

void DensoController::bCapServiceStop() {
    std::cout << "\033[1;32mStop b-CAP service.\033[0m\n";
    BCAP_HRESULT hr = bCap_ServiceStop(iSockFD);
    if FAILED(hr) {
        throw bCapException("\033[1;31mbCap_ServiceStop failed.\033[0m\n");
    }
}

void DensoController::bCapControllerConnect() {
    std::cout << "Get controller handle.\n";
    BCAP_HRESULT hr = bCap_ControllerConnect(iSockFD, "b-CAP", "caoProv.DENSO.VRC", server_ip_address, "", &lhController);
    if FAILED(hr) {
        throw bCapException("bCap_ConrtollerConnect failed.\n");
    }
}

void DensoController::bCapControllerDisconnect() {
    std::cout << "Release controller handle.\n";
    BCAP_HRESULT hr = bCap_ControllerDisconnect(iSockFD, lhController);
    if FAILED(hr) {
        throw bCapException("bCap_ConrtollerDisconnect failed.\n");
    }
}

void DensoController::bCapGetRobot() {
    std::cout << "Get robot handle.\n";
    BCAP_HRESULT hr = bCap_ControllerGetRobot(iSockFD, lhController, "Arm", "", &lhRobot);
    if FAILED(hr) {
        throw bCapException("bCap_ConrtollerDisconnect failed.\n");
    }
}

void DensoController::bCapReleaseRobot() {
    std::cout << "Release robot handle.\n";
    BCAP_HRESULT hr = bCap_RobotRelease(iSockFD, lhRobot);
    if FAILED(hr) {
        throw bCapException("bCap_RobotRelease failed.\n");
    }
}

BCAP_HRESULT DensoController::bCapRobotExecute(const char* command, const char* option) {
    long lResult;
    BCAP_HRESULT hr = bCap_RobotExecute(iSockFD, lhRobot, command, option, &lResult);
    return hr;
}

BCAP_HRESULT DensoController::bCapRobotMove(const char* pose, const char* option) {
    BCAP_HRESULT hr = bCap_RobotMove(iSockFD, lhRobot, 1L, pose, option);
    return hr;
}

BCAP_HRESULT DensoController::bCapMotor(bool command) {
    BCAP_HRESULT hr;
    if (command) {
        std::cout << "\033[1;33mTurn motor on.\033[0m\n";
        hr = bCapRobotExecute("Motor", "1");
    }
    else{
        std::cout << "\033[1;33mTurn motor off.\033[0m\n";
        hr = bCapRobotExecute("Motor", "0");
    }
    return hr;
}

BCAP_HRESULT DensoController::bCapSlvChangeMode(const char* mode) {
    BCAP_HRESULT hr = bCapRobotExecute("slvChangeMode", mode);
    if SUCCEEDED(hr) {
        long lResult;
        BCAP_HRESULT hr1 = bCap_RobotExecute(iSockFD, lhRobot, "slvGetMode", "", &lResult);
        if (lResult > 512) {
            std::cout << "Changed to mode 2 ";
            if (lResult == 513) std::cout << "P-type.\n";
            if (lResult == 514) std::cout << "J-type.\n";
            if (lResult == 515) std::cout << "T-type.\n";
        }
        else if (lResult > 256) {
            std::cout << "Changed to mode 1 ";
            if (lResult == 257) std::cout << "P-type.\n";
            if (lResult == 258) std::cout << "J-type.\n";
            if (lResult == 259) std::cout << "T-type.\n";
        }
        else if (lResult > 0) {
            std::cout << "Changed to mode 0 ";
            if (lResult == 1) std::cout << "P-type.\n";
            if (lResult == 2) std::cout << "J-type.\n";
            if (lResult == 3) std::cout << "T-type.\n";
        }
        else {
            std::cout << "Released slave mode.\n";
        }
    }
    return hr;
}

BCAP_HRESULT DensoController::SetExtSpeed(const char* speed) {
    BCAP_HRESULT hr;
    hr = bCapRobotExecute("ExtSpeed", speed);
    if SUCCEEDED(hr) {
        std::cout << "External speed is set to " << speed << " %\n";
    }
    return hr;
}

////////////////////////////// High Level Commands //////////////////////////////

void DensoController::bCapSlvFollowTraj(TOPP::Trajectory& traj, std::vector<BCAP_VARIANT>& encoderlog, int sleeptime){
    // move to initial pose first
    std::vector<double> q(traj.dimension);
    traj.Eval(0, q);
    const char* command = CommandFromVector(q);

    BCAP_HRESULT hr;
    hr = SetExtSpeed("100");
    std::cout << "\n\033[1;33mMoving to the initial pose...\033[0m\n\n";
    bCapRobotMove(command, "Speed = 25");
    sleep(sleeptime);

    // enable control logging
    hr = bCapRobotExecute("ClearLog", ""); // enable control logging

    // enter slave mode: mode 1 J-Type
    hr = bCapSlvChangeMode("258");

    double s = 0.0;
    BCAP_VARIANT vntPose, vntReturn;
    struct timespec tic, toc;
    encoderlog.resize(0);

    while (s < traj.duration) {
        clock_gettime(CLOCK_MONOTONIC, &tic);
        traj.Eval(s, q);
        vntPose = VNTFromRadVector(q);
        hr = bCap_RobotExecute2(iSockFD, lhRobot, "slvMove", &vntPose, &vntReturn);
        // collect encoder log
        encoderlog.push_back(vntReturn);
        clock_gettime(CLOCK_MONOTONIC, &toc);
        // set time increment based on actual used time
        s += (toc.tv_sec - tic.tv_sec) + (toc.tv_nsec - tic.tv_nsec)/nSEC_PER_SECOND;
    }

    // exit slave mode
    hr = bCapSlvChangeMode("0");

    // stop control logging
    hr = bCapRobotExecute("StopLog", "");
}

void DensoController::bCapEnterProcess(){
    BCAP_HRESULT hr;

    bCapOpen();
    bCapServiceStart();
    bCapControllerConnect();
    bCapGetRobot();

    hr = bCapRobotExecute("Takearm", "");
    if FAILED(hr) {
        throw bCapException("\033[1;31mFail to get arm control authority.\033[0m\n");
    }

    hr = bCapMotor(true);
    if FAILED(hr) {
        bCapExitProcess();
        throw bCapException("\033[1;31mFail to turn motor on.\033[0m\n");
    }

}

void DensoController::bCapExitProcess() {
    BCAP_HRESULT hr;
    hr = bCapMotor(false);
    if FAILED(hr) {
        std::cout << "\033[1;31mFail to turn off motor.\033[0m\n";
    }

    hr = bCapRobotExecute("Givearm", "");
    if FAILED(hr) {
        std::cout << "\033[1;31mFail to release arm control authority.\033[0m\n";
    }

    bCapReleaseRobot();
    bCapControllerDisconnect();
    bCapServiceStop();
    bCapClose();
}

////////////////////////////// Utilities //////////////////////////////

const char* DensoController::CommandFromVector(std::vector<double> q) {
    std::vector<double> tmp;
    tmp = VRad2Deg(q);
    std::string commandstring;
    commandstring = "J(" + std::to_string(tmp[0]) + ", " + std::to_string(tmp[1])
                    + ", " + std::to_string(tmp[2]) + ", " + std::to_string(tmp[3])
                    + ", " + std::to_string(tmp[4]) + ", " + std::to_string(tmp[5]) + ")";
    return commandstring.c_str(); // convert string -> const shar*
}

std::vector<double> DensoController::GetCurJnt() {
    BCAP_HRESULT hr;
    double dJnt[8];
    std::vector<double> jointvalues;
    jointvalues.resize(0);

    hr = bCap_RobotExecute(iSockFD, lhRobot, "CurJnt", "", &dJnt);
    if FAILED(hr) {
        std::cout << "\033[1;31mFail to get current joint values.\033[0m\n";
        return jointvalues;
    }
    for (int i = 0; i < 8; i++) {
        jointvalues.push_back(dJnt[i]);
    }
    return jointvalues;
}

std::vector<double> DensoController::VectorFromVNT(BCAP_VARIANT vnt0) {
    std::vector<double> vect;
    vect.resize(0);
    for (int i = 0; i < 8; i++) {
        vect.push_back(vnt0.Value.DoubleArray[i]);
    }
    return vect;
}

BCAP_VARIANT DensoController::VNTFromVector(std::vector<double> vect0) {
    assert(vect0.size() == 6 || vect0.size() == 8);
    BCAP_VARIANT vnt;
    vnt.Type = VT_R8 | VT_ARRAY;
    vnt.Arrays = 8;

    for (int i = 0; i < int(vect0.size()); i++) {
        vnt.Value.DoubleArray[i] = vect0[i];
    }
    return vnt;
}

std::vector<double> DensoController::RadVectorFromVNT(BCAP_VARIANT vnt0) {
    std::vector<double> vect;
    vect.resize(0);
    for (int i = 0; i < 8; i++) {
        vect.push_back(Deg2Rad(vnt0.Value.DoubleArray[i]));
    }
    return vect;
}

BCAP_VARIANT DensoController::VNTFromRadVector(std::vector<double> vect0) {
    assert(vect0.size() == 6 || vect0.size() == 8);
    BCAP_VARIANT vnt;
    vnt.Type = VT_R8 | VT_ARRAY;
    vnt.Arrays = 8;

    for (int i = 0; i < int(vect0.size()); i++) {
        vnt.Value.DoubleArray[i] = Rad2Deg(vect0[i]);
    }
    return vnt;
}

std::vector<double> VRad2Deg(std::vector<double> vect0) {
    std::vector<double> resvect;
    resvect.resize(0);
    for (int i = 0; i < int(vect0.size()); i++) {
        resvect.push_back(Rad2Deg(vect0[i]));
    }
    return resvect;
}

double Rad2Deg(double x) {
    double res = x * 180.0 / PI;
    if (res >= 0) {
        return fmod(res, 360.0);
    }
    else{
        return -1.0 * fmod(-1.0 * res, 360.0);
    }
}

double Deg2Rad(double x) {
    // double res;
    if (x >= 0) {
        return fmod(x, 360.0) * PI / 180.0;
    }
    else{
        return -1.0 * fmod(-1.0 * x, 360.0) * PI / 180;
    }
}

} //
