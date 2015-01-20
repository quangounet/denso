#include "b-Cap.c"
#define SERVER_IP_ADDRESS "192.168.0.1"
#define SERVER_PORT_NUM 5007
#include <iostream>

int main()
{
    int iSockFD;
    uint32_t lhController;
    BCAP_HRESULT hr = BCAP_S_OK;

    /* Init and Start b-CAP */
    hr = bCap_Open(SERVER_IP_ADDRESS, SERVER_PORT_NUM, &iSockFD);

    /* Init socket */
    if FAILED(hr) return (hr);

    /* Start b-CAP service */
    hr = bCap_ServiceStart(iSockFD);

    hr = bCap_ControllerConnect(iSockFD, "b-CAP", "caoProv.DENSO.VRC", SERVER_IP_ADDRESS, "", &lhController);

    // uint32_t lhVar;
    // float lResult[6];

    // /* Get variable handle */
    // hr = bCap_ControllerGetVariable(iSockFD, lhController, "J0", "", &lhVar);

    // lResult[0] = 90;
    // lResult[3] = 90;
    // bCap_VariablePutValue(iSockFD, lhVar, VT_ARRAY, 6, &lResult);

    // /* Read variable */
    // bCap_VariableGetValue(iSockFD, lhVar, &lResult);

    // std::cout << "var 0: "<< lResult[0] << "\n";
    // std::cout << "var 1: "<< lResult[1] << "\n";
    // std::cout << "var 2: "<< lResult[2] << "\n";
    // std::cout << "var 3: "<< lResult[3] << "\n";

    // /* Release variable handle */
    // bCap_VariableRelease(iSockFD, lhVar);

    uint32_t lhRobot;
    uint32_t lResult;
    /* Get robot handle */
    hr = bCap_ControllerGetRobot(iSockFD, lhController, "Arm", "", &lhRobot);
    /* Get arm control authority */
    hr = bCap_RobotExecute(iSockFD, lhRobot, "Takearm", "", &lResult);
    /* Motor on */
    hr = bCap_RobotExecute(iSockFD, lhRobot, "Motor", "1", &lResult);
    /* Move to J0 */
    hr = bCap_RobotMove(iSockFD, lhRobot, 1L, "J(0,0,0,0,0,0)", "Speed=50");
    sleep(3);
    hr = bCap_RobotMove(iSockFD, lhRobot, 1L, "J(45,45,90, 45,-90,20)", "Speed=25");
    /* Motor off */
    hr = bCap_RobotExecute(iSockFD, lhRobot, "Motor", "0", &lResult);
    /* Release arm control authority */
    hr = bCap_RobotExecute(iSockFD, lhRobot, "Givearm", "", &lResult);
    /* Release robot handle */
    bCap_RobotRelease(iSockFD, lhRobot);


    /* Release controller handle */
    bCap_ControllerDisconnect(iSockFD, lhController);

    /* Stop b-CAP service (Very important in UDP/IP connection) */
    bCap_ServiceStop(iSockFD);
    bCap_Close(iSockFD);
    return 0;



}
