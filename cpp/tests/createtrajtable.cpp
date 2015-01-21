#include "../src/DensoController.cpp"
#include <math.h>
#include <time.h>
#include <chrono>
#include <fstream>

int main(int argc, char* argv[]) {

    std::string inputfilename = argv[1];
    std::string outputfilename = argv[2];

    //////////////////// Get trajectorystring from a file ////////////////////
    std::ifstream myfile(inputfilename);
    std::string tmpstring;
    std::string trajectorystring;
    std::getline(myfile, tmpstring);
    trajectorystring += tmpstring;
    while (std::getline(myfile, tmpstring)) {
        trajectorystring += "\n";
        trajectorystring += tmpstring;
    }
    TOPP::Trajectory *ptraj = new TOPP::Trajectory(trajectorystring);
    std::vector<double> q(ptraj->dimension);
    std::vector<double> tmp;

    //////////////////// Build a LUT for the Trajectory ////////////////////
    double s = 0.0;
    double slower = 0.3;
    double timestep = 8.0*(1e-3)*slower; // default value for Denso slave mode

    std::string tablestring = "";
    std::string separator = "";

    std::stringstream t;
    std::vector<std::vector<double> > LUT;
    std::cout << "Building a look-up table for the trajectory. . ." << "\n";
    while (s < ptraj->duration) {
        ptraj->Eval(s, q);
        LUT.push_back(q);
        tablestring += separator;
        tablestring += std::to_string(q[0]) + " " + std::to_string(q[1])
                       + " " + std::to_string(q[2]) + " " + std::to_string(q[3])
                       + " " + std::to_string(q[4]) + " " + std::to_string(q[5]);
        separator = "\n";

        t << std::setprecision(17) << s << " ";
        s += timestep;
    }

    std::ofstream out(outputfilename);
    out << tablestring;
    out.close();

    int nsteps = LUT.size();
}
