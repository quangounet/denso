#include "../src/DensoController.cpp"
#include <math.h>
#include <time.h>
#include <chrono>
#include <fstream>

int main() {

    //////////////////// Initialize DENSO controller ////////////////////
    DensoController::DensoController denso;
    denso.bCapEnterProcess();
    BCAP_HRESULT hr;

    /////////////// Get Joint Values Input from The User ///////////////
    std::cout << "Please enter the unit of your input values\n";
    std::cout << "\t0: Degree\n";
    std::cout << "\t1: Radian\n";
    int option;
    std::cin >> option;

    std::vector<double> tmp;
    std::vector<double> qdeg;
    tmp.resize(0);
    double jval;
    std::cout << "Please enter the values for each joint\n";
    for (int j = 1; j <=6; j++) {
        std::cout << "joint " << std::to_string(j) << ": ";
        std::cin >> jval;
        tmp.push_back(jval);
    }
    if (option == 1) {
        qdeg = DensoController::VRad2Deg(tmp);
    }
    else {
        qdeg = tmp;
    }


    //// MOVE TO INITIAL POSE ////
    hr = denso.SetExtSpeed("100");
    std::cout << "Moving to the given pose...\n";

    std::string commandstring;
    const char* command;
    commandstring = "J(" + std::to_string(qdeg[0]) + ", " + std::to_string(qdeg[1])
                    + ", " + std::to_string(qdeg[2]) + ", " + std::to_string(qdeg[3])
                    + ", " + std::to_string(qdeg[4]) + ", " + std::to_string(qdeg[5]) + ")";
    command = commandstring.c_str(); // convert string -> const shar*
    std::cout << commandstring << "\n";
    denso.bCapRobotMove(command, "Speed = 10");


    ////////////////////////////// EXIT B-CAP PROCESS //////////////////////////////
    hr = denso.bCapRobotExecute("StopLog", "");
    denso.bCapExitProcess();
}
