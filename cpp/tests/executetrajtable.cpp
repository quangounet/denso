#include "../src/DensoController.cpp"
#include <math.h>
#include <time.h>
#include <chrono>
#include <fstream>
#include <string>

// utilities
void VectorFromString(std::string& s, std::vector<double>& resvect) {
    // remove trailing spaces
    s.erase(std::find_if (s.rbegin(), s.rend(), std::bind1st(std::not_equal_to<char>(), ' ')).base(), s.end());
    std::istringstream iss(s);
    std::string sub;
    double value;
    resvect.resize(0);
    while (iss.good()) {
        iss >> sub;
        value = atof(sub.c_str());
        resvect.push_back(value);
    }
}

int main(int argc, char* argv[]) {
    std::string inputfilename = argv[1];

    //////////////////// Initialize DENSO controller ////////////////////
    DensoController::DensoController denso;
    denso.bCapEnterProcess();
    BCAP_HRESULT hr;

    //////////////////// Get table from a file ////////////////////
    std::ifstream myfile(inputfilename);
    std::string buff;
    std::vector<std::vector<double> > qvector;
    std::vector<double> q;

    qvector.resize(0);

    while(myfile.good()) {
        getline(myfile, buff, '\n');
        VectorFromString(buff, q);
        qvector.push_back(q);
    }

    //// VERY IMPORTANT :: MOVE TO INITIAL POSE BEFORE EXECUTING TRAJ IN SLAVE MODE ////
    hr = denso.SetExtSpeed("100");

    std::vector<double> tmp;

    tmp = DensoController::VRad2Deg(qvector[0]); // data is recorded in radian
    std::string commandstring;
    const char* command;
    commandstring = "J(" + std::to_string(tmp[0]) + ", " + std::to_string(tmp[1])
                    + ", " + std::to_string(tmp[2]) + ", " + std::to_string(tmp[3])
                    + ", " + std::to_string(tmp[4]) + ", " + std::to_string(tmp[5]) + ")";
    command = commandstring.c_str(); // convert string -> const shar*
    std::cout << commandstring << "\n";
    denso.bCapRobotMove(command, "Speed = 25");
    sleep(2);

    hr = denso.bCapRobotExecute("ClearLog", ""); // enable control logging

    int nsteps = qvector.size();

    ////////////////////////////// BEGIN SLAVE MODE //////////////////////////////
    hr = denso.bCapSlvChangeMode("514");

    BCAP_VARIANT vntPose, vntReturn;
    std::vector<BCAP_VARIANT> history;
    history.resize(0);

    std::stringstream trealtime;
    struct timespec tic, toc;

    double s = 0.0;
    double step;
    q = qvector[0];
    vntPose = denso.VNTFromRadVector(q);
    clock_gettime(CLOCK_MONOTONIC, &tic); //TIC
    hr = bCap_RobotExecute2(denso.iSockFD, denso.lhRobot, "slvMove", &vntPose, &vntReturn);
    history.push_back(vntReturn);

    for (int i = 0; i < nsteps; i++) {
        q = qvector[i];
        vntPose = denso.VNTFromRadVector(q);
        clock_gettime(CLOCK_MONOTONIC, &toc); //TOC
        step = ((toc.tv_sec - tic.tv_sec) + (toc.tv_nsec - tic.tv_nsec)/nSEC_PER_SECOND);
        s += step;
        trealtime << std::setprecision(17) << s << " ";
        clock_gettime(CLOCK_MONOTONIC, &tic); //TIC
        hr = bCap_RobotExecute2(denso.iSockFD, denso.lhRobot, "slvMove", &vntPose, &vntReturn);
        history.push_back(vntReturn);
    }

    clock_gettime(CLOCK_MONOTONIC, &toc); //TOC
    s += (toc.tv_sec - tic.tv_sec) + (toc.tv_nsec - tic.tv_nsec)/nSEC_PER_SECOND;
    trealtime << std::setprecision(17) << s << " ";

    ////////////////////////////// STOP SLAVE MODE //////////////////////////////
    hr = denso.bCapSlvChangeMode("0");

    ////////////////////////////// EXIT B-CAP PROCESS //////////////////////////////
    hr = denso.bCapRobotExecute("StopLog", "");
    denso.bCapExitProcess();
}
