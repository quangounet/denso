#include "DensoController.h"
#include <iostream>

namespace DensoController {

DensoController::DensoController(const char* server_ip_address0, int server_port_num0) {
    server_ip_address = server_ip_address0;
    server_port_num = server_port_num0;
}

void DensoController::bCapOpen() {
    std::cout << "Initialize and start b-CAP.\n";
    BCAP_HRESULT hr = bCap_Open(server_ip_address, server_port_num, &iSockFD);
    if FAILED(hr) {
        throw bCapException("bCap_Open failed.\n");
    }
}

void DensoController::bCapClose() {
    std::cout << "Stop b-CAP.\n";
    BCAP_HRESULT hr = bCap_Close(iSockFD);
    if FAILED(hr) {
        throw bCapException("bCap_Close failed.\n");
    }
}

void DensoController::bCapServiceStart() {
    std::cout << "Start b-CAP service.\n";
    BCAP_HRESULT hr = bCap_ServiceStart(iSockFD);
    if FAILED(hr) {
        throw bCapException("bCap_ServiceStart failed.\n");
    }
}

void DensoController::bCapServiceStop() {
    std::cout << "Stop b-CAP service.\n";
    BCAP_HRESULT hr = bCap_ServiceStop(iSockFD);
    if FAILED(hr) {
        throw bCapException("bCap_ServiceStop failed.\n");
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
        std::cout << "Turn motor on.\n";
        hr = bCapRobotExecute("Motor", "1");
    }
    else{
        std::cout << "Turn motor off.\n";
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

void DensoController::bCapEnterProcess(){
    BCAP_HRESULT hr;

    bCapOpen();
    bCapServiceStart();
    bCapControllerConnect();
    bCapGetRobot();

    hr = bCapRobotExecute("Takearm", "");
    if FAILED(hr) {
        throw bCapException("Fail to get arm control authority.\n");
    }

    hr = bCapMotor(true);
    if FAILED(hr) {
        bCapExitProcess();
        throw bCapException("Fail to turn motor on.\n");
    }

}

void DensoController::bCapExitProcess() {
    BCAP_HRESULT hr;
    hr = bCapMotor(false);
    if FAILED(hr) {
        std::cout << "Fail to turn off motor.\n";
    }

    hr = bCapRobotExecute("Givearm", "");
    if FAILED(hr) {
        std::cout << "Fail to release arm control authority.\n";
    }

    bCapReleaseRobot();
    bCapControllerDisconnect();
    bCapServiceStop();
    bCapClose();
}

BCAP_HRESULT DensoController::SetExtSpeed(const char* speed) {
    BCAP_HRESULT hr;
    hr = bCapRobotExecute("ExtSpeed", speed);
    if SUCCEEDED(hr) {
        std::cout << "External speed is set to " << speed << " %\n";
    }
    return hr;
}

std::vector<double> DensoController::GetCurJnt() {
    BCAP_HRESULT hr;
    double dJnt[8];
    std::vector<double> jointvalues;
    jointvalues.resize(0);

    hr = bCap_RobotExecute(iSockFD, lhRobot, "CurJnt", "", &dJnt);
    if FAILED(hr) {
        std::cout << "Fail to get current joint values.\n";
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

    for (int i = 0; i < vect0.size(); i++) {
        vnt.Value.DoubleArray[i] = vect0[i];
    }
    return vnt;
}


} //
