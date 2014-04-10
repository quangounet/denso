#include "b-Cap.c"
#define SERVER_IP_ADDRESS "192.168.0.1"
#define SERVER_PORT_NUM 5007
#include <iostream>

int main()
{
    int iSockFD;
    uint32_t lhController;
    BCAP_HRESULT hr = BCAP_S_OK;

    std::cout << "toto\n";

    /* Init and Start b-CAP */
    hr = bCap_Open(SERVER_IP_ADDRESS, SERVER_PORT_NUM, &iSockFD);

    std::cout << "tata\n";

    /* Init socket */
    if FAILED(hr) return (hr);

    /* Start b-CAP service */
    hr = bCap_ServiceStart(iSockFD);

    /* Get controller handle */
    hr = bCap_ControllerConnect(iSockFD, "b-CAP", "caoProv.DENSO.VRC", SERVER_IP_ADDRESS, "", &lhController);
    uint32_t lhTask;
    long lMode;

    /* Get task handle */
    hr = bCap_ControllerGetTask(iSockFD, lhController, "Pro1", "", &lhTask);

    /* Start task */
    lMode = 2L;
    bCap_TaskStart(iSockFD, lhTask, lMode, "");

    /* Stop task */
    lMode = 3L;
    bCap_TaskStop(iSockFD, lhTask, lMode, "");

    /* Release task handle */
    bCap_TaskRelease(iSockFD, lhTask);

    /* Release controller handle */
    bCap_ControllerDisconnect(iSockFD, lhController);

    /* Stop b-CAP service (Very important in UDP/IP connection) */
    bCap_ServiceStop(iSockFD);
    bCap_Close(iSockFD);
    return 0;
}
