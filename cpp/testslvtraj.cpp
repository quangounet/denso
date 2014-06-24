#include "DensoController.cpp"
#include <math.h>
#include <time.h>
#include <chrono>
#include <fstream>

#include "../../TOPP/src/Trajectory.cpp"
#include "../../TOPP/src/TOPP.cpp"
#define nSEC_PER_SECOND 1E9;
#define dReal float

int main() {
    // Initialize DENSO controller
    DensoController::DensoController denso;
    denso.bCapEnterProcess();
    BCAP_HRESULT hr;

    // Get trajectorystring from a file
    std::ifstream myfile("denso3.traj");
    std::string temp;
    std::string trajectorystring;
    std::getline(myfile, temp);
    trajectorystring += temp;
    while (std::getline(myfile, temp)) {
        trajectorystring += "\n";
        trajectorystring += temp;
    }
    TOPP::Trajectory *ptraj = new TOPP::Trajectory(trajectorystring);
    std::vector<double> q(ptraj->dimension);
    std::vector<double> tmp;
    // VERY IMPORTANT //
    std::cout << "Moving to the initial pose...\n";

    ptraj->Eval(0.0, q);
    tmp = DensoController::VRad2Deg(q);
    std::string commandstring;
    const char* command;
    commandstring = "J(" + std::to_string(tmp[0]) + ',' + std::to_string(tmp[1]) + ',' + std::to_string(tmp[2]) + ','
                    + std::to_string(tmp[3]) + ',' + std::to_string(tmp[4]) + ',' + std::to_string(tmp[5]) + ')';
    command = commandstring.c_str();
    std::cout << commandstring << "\n";
    denso.bCapRobotMove(command, "Speed = 25");
    std:: cout << ptraj->duration << "\n";
    sleep(5);


    hr = denso.SetExtSpeed("100");
    hr = denso.bCapRobotExecute("ClearLog", ""); // enable control logging

    ////////////////////////////// BEGIN SLAVE MODE //////////////////////////////
    hr = denso.bCapSlvChangeMode("258");

    double s = 0.0;
    BCAP_VARIANT vntPose, vntReturn;

    struct timespec tic, toc;

    while (s < ptraj->duration) {
        ptraj->Eval(s, q);
        vntPose = denso.VNTFromRadVector(q);
        clock_gettime(CLOCK_MONOTONIC, &tic);
        hr = bCap_RobotExecute2(denso.iSockFD, denso.lhRobot, "slvMove", &vntPose, &vntReturn);
        clock_gettime(CLOCK_MONOTONIC, &toc);
        s += (toc.tv_sec - tic.tv_sec) + (toc.tv_nsec - tic.tv_nsec)/nSEC_PER_SECOND;
        // std::cout << s << ", ";
    }
    std::cout << s << "\n";

    hr = denso.bCapSlvChangeMode("0");
    ////////////////////////////// STOP SLAVE MODE //////////////////////////////


    hr = denso.bCapRobotExecute("StopLog", "");
    denso.bCapExitProcess();
}
