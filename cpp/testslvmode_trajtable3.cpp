#include "DensoController.cpp"
#include <math.h>
#include <time.h>
#include <chrono>
#include <fstream>

/// data loaded is already a table

int main() {
    std::string filename = "../data/table1.traj";

    //////////////////// Initialize DENSO controller ////////////////////
    DensoController::DensoController denso;
    denso.bCapEnterProcess();
    BCAP_HRESULT hr;

    //////////////////// Get table from a file ////////////////////
    std::ifstream myfile(filename);
    std::string temp;
    std::vector<std::string> trajtable;
    trajtable.resize(0);

    std::getline(myfile, temp);
    trajtable.push_back(temp);
    while (std::getline(myfile, temp)) {
        trajtable.push_back(temp);
    }




    // std::ifstream myfile(filename);
    // std::string temp;
    // std::string trajectorystring;
    // std::getline(myfile, temp);
    // trajectorystring += temp;
    // while (std::getline(myfile, temp)) {
    //     trajectorystring += "\n";
    //     trajectorystring += temp;
    // }
    // TOPP::Trajectory *ptraj = new TOPP::Trajectory(trajectorystring);
    // std::vector<double> q(ptraj->dimension);
    // std::vector<double> tmp;

    // //// VERY IMPORTANT :: MOVE TO INITIAL POSE BEFORE EXECUTING TRAJ IN SLAVE MODE ////
    // hr = denso.SetExtSpeed("100");
    // std::cout << "Moving to the initial pose...\n";

    // ptraj->Eval(0.0, q);
    // tmp = DensoController::VRad2Deg(q);
    // std::string commandstring;
    // const char* command;
    // commandstring = "J(" + std::to_string(tmp[0]) + ", " + std::to_string(tmp[1])
    //                 + ", " + std::to_string(tmp[2]) + ", " + std::to_string(tmp[3])
    //                 + ", " + std::to_string(tmp[4]) + ", " + std::to_string(tmp[5]) + ")";
    // command = commandstring.c_str(); // convert string -> const shar*
    // std::cout << commandstring << "\n";
    std::string command = trajtable[0];
    denso.bCapRobotMove(command, "Speed = 25");
    sleep(5);

    hr = denso.bCapRobotExecute("ClearLog", ""); // enable control logging

    //////////////////// Build a LUT for the Trajectory ////////////////////
    double s = 0.0;
    double slower = 1.0;
    double timestep = 8.0*(1e-3)*slower;
    std::stringstream t;
    std::vector<std::vector<double> > LUT;
    std::cout << "Building a look-up table for the trajectory. . ." << "\n";
    while (s < ptraj->duration) {
        ptraj->Eval(s, q);
        LUT.push_back(q);
        t << std::setprecision(17) << s << " ";
        s += timestep;
    }

    int nsteps = LUT.size();

    ////////////////////////////// BEGIN SLAVE MODE //////////////////////////////
    hr = denso.bCapSlvChangeMode("514");

    BCAP_VARIANT vntPose, vntReturn;
    std::vector<BCAP_VARIANT> history;
    history.resize(0);

    std::stringstream trealtime;
    struct timespec tic, toc;

    s = 0.0;
    q = LUT[0];
    vntPose = denso.VNTFromRadVector(q);
    clock_gettime(CLOCK_MONOTONIC, &tic); //TIC
    hr = bCap_RobotExecute2(denso.iSockFD, denso.lhRobot, "slvMove", &vntPose, &vntReturn);
    history.push_back(vntReturn);

    for (int i = 0; i < nsteps; i++) {
        q = LUT[i];
        vntPose = denso.VNTFromRadVector(q);
        clock_gettime(CLOCK_MONOTONIC, &toc); //TOC
        s += (toc.tv_sec - tic.tv_sec) + (toc.tv_nsec - tic.tv_nsec)/nSEC_PER_SECOND;
        trealtime << std::setprecision(17) << s << " ";
        clock_gettime(CLOCK_MONOTONIC, &tic); //TIC
        hr = bCap_RobotExecute2(denso.iSockFD, denso.lhRobot, "slvMove", &vntPose, &vntReturn);
        history.push_back(vntReturn);
    }

    clock_gettime(CLOCK_MONOTONIC, &toc); //TOC
    s += (toc.tv_sec - tic.tv_sec) + (toc.tv_nsec - tic.tv_nsec)/nSEC_PER_SECOND;
    trealtime << std::setprecision(17) << s << " ";



    // while (s < ptraj->duration) {
    //     clock_gettime(CLOCK_MONOTONIC, &tic);
    //     clock_gettime(CLOCK_MONOTONIC, &tic1);
    //     ptraj->Eval(s, q);
    //     clock_gettime(CLOCK_MONOTONIC, &toc1);
    //     t << std::setprecision(17) << s << " ";
    //     vntPose = denso.VNTFromRadVector(q);
    //     clock_gettime(CLOCK_MONOTONIC, &tic2);
    //     hr = bCap_RobotExecute2(denso.iSockFD, denso.lhRobot, "slvMove", &vntPose, &vntReturn);
    //     clock_gettime(CLOCK_MONOTONIC, &toc2);
    //     // data collecting
    //     history.push_back(vntReturn);
    //     std::cout << "Eval = " << (toc1.tv_sec - tic1.tv_sec) + (toc1.tv_nsec - tic1.tv_nsec)/nSEC_PER_SECOND << "\n";
    //     std::cout << "Execute = " << (toc2.tv_sec - tic2.tv_sec) + (toc2.tv_nsec - tic2.tv_nsec)/nSEC_PER_SECOND << "\n";
    //     clock_gettime(CLOCK_MONOTONIC, &toc);
    //     s += (toc.tv_sec - tic.tv_sec) + (toc.tv_nsec - tic.tv_nsec)/nSEC_PER_SECOND;
    // }

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

    std::ofstream out3("densohistory.realtimestamp");
    out3 << trealtime.str();
    out3.close();
    std::cout << "realtimestamp successfully written in denhistory.timestamp\n";


    ////////////////////////////// EXIT B-CAP PROCESS //////////////////////////////
    hr = denso.bCapRobotExecute("StopLog", "");
    denso.bCapExitProcess();
}
