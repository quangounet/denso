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
    std::vector<std::string> gainlist;
    std::string line;
    std::ifstream handle;
    handle.open("../data/waypoints");
    while(!handle.eof()) {
        std::getline(handle,line);
        if(line.length()<2) {
            break;
        }
        pointlist.push_back(line);
        std::getline(handle,line);
        gainlist.push_back(line);
    }
    handle.close();

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
    uint32_t lComp = 1;
    uint32_t lResult;
    /* Get robot handle */
    hr = bCap_ControllerGetRobot(iSockFD, lhController, "Arm", "", &lhRobot);
    /* Get arm control authority */
    hr = bCap_RobotExecute(iSockFD, lhRobot, "Takearm", "", &lResult);
    /* Motor on */
    hr = bCap_RobotExecute(iSockFD, lhRobot, "Motor", "1", &lResult);

    /* Execute trajectory */
    // First waypoint
    std::cout << "\nMove to start" << "\nPosition: " << pointlist[0] << "\nGain: " << gainlist[0] << "\n\n";
    hr = bCap_RobotMove(iSockFD, lhRobot, lComp, pointlist[0].c_str(), gainlist[0].c_str());
    sleep(5);

    //Intermediate waypoints
    for(int i=1; i<int(pointlist.size()); i++) {
        std::cout << "Waypoint " << i << "\nPosition: " << pointlist[i] << "\nGain: " << gainlist[i] << "\n\n";
        hr = bCap_RobotMove(iSockFD, lhRobot, lComp, pointlist[i].c_str(), gainlist[i].c_str());
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
