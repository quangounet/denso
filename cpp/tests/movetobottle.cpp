#include "DensoController.cpp"
#include <math.h>
#include <time.h>
#include <chrono>
#include <fstream>
#include <string>

/// execute the motion computed from OpenRAVE RRT (move to bottle)

void VectorFromString(std::string& s, std::vector<double>& resvect) {
    s.erase(std::find_if (s.rbegin(), s.rend(), std::bind1st(std::not_equal_to<char>(), ' ')).base(), s.end()); //remove trailing spaces
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

int main()
{
    std::string filename = "../data/movetobottle1.waypoints";

    //////////////////// Initialize DENSO controller ////////////////////
    DensoController::DensoController denso;
    denso.bCapEnterProcess();
    BCAP_HRESULT hr;

    //////////////////// Get table from a file ////////////////////
    std::ifstream myfile(filename);
    std::string buff;
    std::vector<std::vector<double> > qvector;
    std::vector<double> q;

    qvector.resize(0);

    while(myfile.good()) {
        getline(myfile, buff, '\n');
        VectorFromString(buff, q);
        qvector.push_back(q);
    }
    std::cout << qvector.size() << "\n";

    std::vector<double> tmp;
    for (int i = 0; i < qvector.size(); i++) {
        tmp = DensoController::VRad2Deg(qvector[i]);
        std::string commandstring;
        const char* command;
        commandstring = "J(" + std::to_string(tmp[0]) + ", " + std::to_string(tmp[1])
                        + ", " + std::to_string(tmp[2]) + ", " + std::to_string(tmp[3])
                        + ", " + std::to_string(tmp[4]) + ", " + std::to_string(tmp[5]) + ")";
        command = commandstring.c_str(); // convert string -> const shar*
        std::cout << commandstring << "\n";
        denso.bCapRobotMove(command, "Speed = 20");
    }

    hr = denso.bCapRobotExecute("StopLog", "");
    denso.bCapExitProcess();
    return 0;
}
