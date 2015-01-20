#define _USE_MATH_DEFINES
#include <math.h>

#include "b-Cap.c"
#include <iostream>

#define SERVER_IP_ADDRESS    "192.168.0.1"
#define SERVER_PORT_NUM      5007

#define E_BUF_FULL           0x83201483
#define S_BUF_FULL           0x0F200501

#define PERIOD     100
#define AMPLITUDE  1

int main(){

    int iSockFD;
    uint32_t lResult;
    uint32_t lhController, lhRobot;
    BCAP_HRESULT hr = BCAP_S_OK;

    double dJnt[8];
    BCAP_VARIANT vntPose, vntReturn;

    hr = bCap_Open(SERVER_IP_ADDRESS, SERVER_PORT_NUM, &iSockFD);
    std::cout << "bCAP OPEN " << hr << "\n";
    if FAILED(hr) {
        return hr;
    }

    // Start b-CAP service
    hr = bCap_ServiceStart(iSockFD);
    if FAILED(hr) {
        return hr;
    }

    // Get controller handle
    hr = bCap_ControllerConnect(iSockFD, "b-CAP", "caoProv.DENSO.VRC", SERVER_IP_ADDRESS, "", &lhController);
    if FAILED(hr) {
        return hr;
    }

    //hr = bCap_ControllerExecute(iSockFD, lhController, "ClearError", "", &lResult);
    // std::cout << "Clear Error " << hr << "\n";


    // Get robot handle
    hr = bCap_ControllerGetRobot(iSockFD, lhController, "Arm", "", &lhRobot);
    if SUCCEEDED(hr){

        // Get arm control authority
        hr = bCap_RobotExecute(iSockFD, lhRobot, "Takearm", "", &lResult);

        // Turn on motor
        hr = bCap_RobotExecute(iSockFD, lhRobot, "Motor", "1", &lResult);

        //hr = bCap_RobotExecute(iSockFD, lhRobot, "ClearLog", "", &lResult);

        // Move to J0
        hr = bCap_RobotMove(iSockFD, lhRobot, 1L, "J(-45,30,30,0,-45,0)", "Speed=25");
        // hr = bCap_RobotMove(iSockFD, lhRobot, 1L, "J(0.1,0.1,0.1,0.1,0.1,0.1)", "Speed=50");
        // hr = bCap_RobotMove(iSockFD, lhRobot, 1L, (char*)"@E J1", (char*)"Speed=25");

        // Get current configuration
        hr = bCap_RobotExecute(iSockFD, lhRobot, "CurJnt", "", &dJnt);
        std::cout << dJnt[0] << "\t" << dJnt[1] <<  "\t" << dJnt[2] <<  "\t" << dJnt[3]
                  <<  "\t" << dJnt[4] <<   "\t" << dJnt[5] << "\n";

        hr = bCap_RobotExecute(iSockFD, lhRobot, "slvChangeMode", "258", &lResult);
        std::cout << "bCap_RobotExecute " << hr << "\n";
        vntPose.Type = VT_R8 | VT_ARRAY;
        vntPose.Arrays = 8;


        // for (int i = 0; i < 8; i++) {
        //     vntPose.Value.DoubleArray[i] = double(0.02); //dJnt[i] - 0.1;
        // }

        // Test executing slave mode
        for (int i = 0; i < PERIOD; i++) {

            vntPose.Value.DoubleArray[0] = dJnt[0] + i/100.0;
            vntPose.Value.DoubleArray[1] = dJnt[1] + AMPLITUDE*sin(2*M_PI*i/PERIOD);
            vntPose.Value.DoubleArray[2] = dJnt[2];
            vntPose.Value.DoubleArray[3] = dJnt[3];
            vntPose.Value.DoubleArray[4] = dJnt[4];
            vntPose.Value.DoubleArray[5] = dJnt[5];
            vntPose.Value.DoubleArray[6] = dJnt[6];
            vntPose.Value.DoubleArray[7] = dJnt[7];

            // vntPose.Value.DoubleArray[0] = 10;
            // vntPose.Value.DoubleArray[1] = 10;
            // vntPose.Value.DoubleArray[2] = 10;
            // vntPose.Value.DoubleArray[3] = 10;
            // vntPose.Value.DoubleArray[4] = 10;
            // vntPose.Value.DoubleArray[5] = 10;

            // std::cout << vntPose.Value.DoubleArray[0] << "\t";
            // std::cout << vntPose.Value.DoubleArray[1] << "\t";
            // std::cout << vntPose.Value.DoubleArray[2] << "\t";
            // std::cout << vntPose.Value.DoubleArray[3] << "\t";
            // std::cout << vntPose.Value.DoubleArray[4] << "\t";
            // std::cout << vntPose.Value.DoubleArray[5] << "\t";
            // std::cout << vntPose.Value.DoubleArray[6] << "\t";
            // std::cout << vntPose.Value.DoubleArray[7] << "\n";
            // std::cout << i << "\n";
            // int j = 0;
            // while (j < 1024) {
            //     hr = bCap_RobotExecute2(iSockFD, lhRobot, "slvMove", &vntPose, &vntReturn);
            //     j++;
            // }
            hr = bCap_RobotExecute2(iSockFD, lhRobot, "slvMove", &vntPose, &vntReturn);

            std::cout << "bCap_RobotExecute2 " << hr << "\n";
            usleep(50000);
            if (hr != 0) {
                // sleep(8);
                if (FAILED(hr)) {
                    if (hr == E_BUF_FULL) {
                        i--;
                    } else {
                        break;
                    }
                }
            }
        }



        // while (hr == 0) {
        //     hr = bCap_RobotExecute2(iSockFD, lhRobot, "slvMove", &vntPose, &vntReturn);
        // }
        hr = bCap_RobotExecute2(iSockFD, lhRobot, "slvMove", &vntPose, &vntReturn);
        hr = bCap_RobotExecute2(iSockFD, lhRobot, "slvMove", &vntPose, &vntReturn);

        std::cout << "bCap_RobotExecute2 " << hr << "\n";

        // std::cout << vntPose.Value.DoubleArray[0] << "\t";
        // std::cout << vntPose.Value.DoubleArray[1] << "\t";
        // std::cout << vntPose.Value.DoubleArray[2] << "\t";
        // std::cout << vntPose.Value.DoubleArray[3] << "\t";
        // std::cout << vntPose.Value.DoubleArray[4] << "\t";
        // std::cout << vntPose.Value.DoubleArray[5] << "\t";
        // std::cout << vntPose.Value.DoubleArray[6] << "\t";
        // std::cout << vntPose.Value.DoubleArray[7] << "\t";
        // std::cout << vntPose.Value.DoubleArray[8] << "\t";
        // std::cout << vntPose.Value.DoubleArray[9] << "\t";
        // std::cout << vntPose.Value.DoubleArray[10] << "\t";
        // std::cout << vntPose.Value.DoubleArray[11] << "\t";
        // std::cout << vntPose.Value.DoubleArray[12] << "\t";
        // std::cout << vntPose.Value.DoubleArray[13] << "\t";
        // std::cout << vntPose.Value.DoubleArray[14] << "\t";
        // std::cout << vntPose.Value.DoubleArray[15] << "\n";

        // std::cout << vntReturn.Value.DoubleArray[0] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[1] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[2] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[3] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[4] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[5] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[6] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[7] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[8] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[9] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[10] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[11] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[12] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[13] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[14] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[15] << "\n";

        // Stop slave mode
        hr = bCap_RobotExecute(iSockFD, lhRobot, "slvChangeMode", (char*)"0", &lResult);
        std::cout << "bCap_RobotExecute " << hr << "\n";

        // Turn motor off
        hr = bCap_RobotExecute(iSockFD, lhRobot, "Motor", "0", &lResult);
        std::cout << "bCap_RobotExecute " << hr << "\n";

        // Release arm authority
        hr = bCap_RobotExecute(iSockFD, lhRobot, "Givearm", "", &lResult);

    }

    // Release robot handle
    bCap_RobotRelease(iSockFD, lhRobot);

    // Release controller handle
    bCap_ControllerDisconnect(iSockFD, lhController);

    // Stop b_CAP service
    bCap_ServiceStop(iSockFD);

    bCap_Close(iSockFD);

    return 0;
}
