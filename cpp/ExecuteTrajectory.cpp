#include <iostream>
#include <fstream>
#include <string>
#include <vector>

#include "b-Cap.c"

#define SERVER_IP_ADDRESS "192.168.0.1"
#define SERVER_PORT_NUM 5007

int main()
{

    /* Load file */
    std::vector<std::string> pointlist;
    std::vector<std::string> vellist;
    std::vector<std::string> acclist;
    std::string line;
    std::ifstream handle;
    handle.open("traj.waypoints");
    handle.close();
    while(!handle.eof()) {
        std::getline(handle,line);
        pointlist.push_back(line);
        std::getline(handle,line);
        vellist.push_back(line);
        std::getline(handle,line);
        acclist.push_back(line);
    }

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


    uint32_t lhRobot;
    uint32_t lResult;
    /* Get robot handle */
    hr = bCap_ControllerGetRobot(iSockFD, lhController, "Arm", "", &lhRobot);
    /* Get arm control authority */
    hr = bCap_RobotExecute(iSockFD, lhRobot, "Takearm", "", &lResult);
    /* Motor on */
    hr = bCap_RobotExecute(iSockFD, lhRobot, "Motor", "1", &lResult);

    /* Execute trajectory */
    // First waypoint
    //hr = bCap_RobotMove(iSockFD, lhRobot, 1L, pointlist[0], vellist[0]);
    std::cout << pointlist[0] << " " << vellist[0] << "\n";

    // Intermediate waypoints
    for(int i=1; i<int(pointlist.size())+1; i++) {
        //hr = bCap_RobotMove(iSockFD, lhRobot, 1L, pointlist[i], vellist[i]);
        std::cout << pointlist[i] << " " << vellist[i] << "\n";
    }

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
