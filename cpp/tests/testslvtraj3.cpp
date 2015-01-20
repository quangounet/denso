#include "DensoController.cpp"
#include <math.h>
#include <time.h>
#include <chrono>
#include <fstream>

int main() {
    //////////////////// Initialize DENSO controller ////////////////////
    DensoController::DensoController denso;
    denso.bCapEnterProcess();

    //////////////////// Get trajectorystring from a file ////////////////////
    std::ifstream myfile("reach2_1.traj");
    std::string temp;
    std::string trajectorystring;
    std::getline(myfile, temp);
    trajectorystring += temp;
    while (std::getline(myfile, temp)) {
        trajectorystring += "\n";
        trajectorystring += temp;
    }

    //////////////////// Execute the trajectory ////////////////////
    TOPP::Trajectory *ptraj = new TOPP::Trajectory(trajectorystring);
    std::vector<BCAP_VARIANT> history;
    denso.bCapSlvFollowTraj(*ptraj, history);

    //////////////////// Stop b-CAP process ////////////////////
    denso.bCapExitProcess();
}
