#include "DensoController.cpp"
#include <math.h>
#include <time.h>
#include <chrono>
#include <fstream>

int main() {
    //////////////////// Initialize DENSO controller ////////////////////
    DensoController::DensoController denso;
    denso.bCapEnterProcess();
    BCAP_HRESULT hr;

    //////////////////// Get trajectorystring from a file ////////////////////
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

    //// VERY IMPORTANT :: MOVE TO INITIAL POSE BEFORE EXECUTING TRAJ IN SLAVE MODE ////
    hr = denso.SetExtSpeed("100");
    std::cout << "Moving to the initial pose...\n";

    ptraj->Eval(0.0, q);

    const char* command = denso.CommandFromVector(q);
    std::string commandstring(command);
    std::cout << commandstring << "\n";
    denso.bCapRobotMove(command, "Speed = 25");
    sleep(3);

    hr = denso.bCapRobotExecute("ClearLog", ""); // enable control logging

    ////////////////////////////// BEGIN SLAVE MODE //////////////////////////////
    hr = denso.bCapSlvChangeMode("258");

    double s = 0.0;
    BCAP_VARIANT vntPose, vntReturn;

    struct timespec tic, toc;

    std::vector<BCAP_VARIANT> history;
    history.resize(0);
    std::stringstream t;

    while (s < ptraj->duration) {
        ptraj->Eval(s, q);
        t << std::setprecision(17) << s << " ";
        vntPose = denso.VNTFromRadVector(q);
        clock_gettime(CLOCK_MONOTONIC, &tic);
        hr = bCap_RobotExecute2(denso.iSockFD, denso.lhRobot, "slvMove", &vntPose, &vntReturn);
        // data collecting
        history.push_back(vntReturn);
        clock_gettime(CLOCK_MONOTONIC, &toc);
        s += (toc.tv_sec - tic.tv_sec) + (toc.tv_nsec - tic.tv_nsec)/nSEC_PER_SECOND;
    }

    ////////////////////////////// STOP SLAVE MODE //////////////////////////////
    hr = denso.bCapSlvChangeMode("0");

    ////////////////////////////// SAVE ENCODER DATA //////////////////////////////
    std::stringstream ss;
    ss << std::setprecision(17) << history[0].Value.DoubleArray[0];
    for (int k = 1; k < 6; k++) {
        ss << " " << std::setprecision(17) << history[0].Value.DoubleArray[k];
    }
    ss << "\n";
    for (int i = 1; i < int(history.size()); i++) {
        ss << std::setprecision(17) << history[i].Value.DoubleArray[0];
        for (int j = 1; j < 6; j++) {
            ss << " " << std::setprecision(17) << history[i].Value.DoubleArray[j];
        }
        ss << "\n";
    }
    std::ofstream out1("densohistory.traj");
    out1 << ss.str();
    out1.close();
    std::cout << "waypoints successfully written in denhistory.traj\n";

    std::ofstream out2("densohistory.timestamp");
    out2 << t.str();
    out2.close();
    std::cout << "timestamp successfully written in denhistory.timestamp\n";


    ////////////////////////////// EXIT B-CAP PROCESS //////////////////////////////
    hr = denso.bCapRobotExecute("StopLog", "");
    denso.bCapExitProcess();
}
