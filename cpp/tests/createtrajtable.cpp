#include "../src/DensoController.cpp"
#include <math.h>
#include <time.h>
#include <chrono>
#include <fstream>

#include "getopt.h"

void ShowHelp(char* filename) {
    std::cout << std::endl;
    std::cout << "Usage: " << filename << " inputfile.traj outputfile.table [options]\n\n";
    std::cout << "Options:" << std::endl;
    std::cout << "        -h:            Show this help." << std::endl;
    std::cout << "        -s val:        Create a trajectory table with <val>X full-speed" << std::endl;
    std::cout << std::endl;
}


// int main(int argc, char* argv[]) {
int main(int argc, char** argv) {

    double slower = 0.15; // default value of the fraction of full-speed
    bool setspeedflag = false;
    bool helpflag = false;
    int c;

    while ((c = getopt(argc, argv, "hs:")) != -1) {
	switch (c) {
	case 'h':
	    helpflag = true;
	    break;
	case 's':
	    slower = atof(optarg);
	    setspeedflag = true;
	    break;
	case '?':
	    if (optopt == 's')
		fprintf (stderr, "Option -%c requires an argument.\n", optopt);
	    else if (isprint (optopt))
		fprintf (stderr, "Unknown option `-%c'.\n", optopt);
	    else
		fprintf (stderr, "Unknown option character `\\x%x'.\n", optopt);
	    return 1;
	}
    }
    if (helpflag) {
	ShowHelp(argv[0]);
	return 0;
    }
    if (!setspeedflag) {
	std::cout << "No fraction of full-speed given\n";
    }
    
    printf("Creating a look-up table for the trajectory with %fX full-speed\n", slower);
    
    std::string inputfilename = argv[optind];
    std::string outputfilename = argv[optind + 1];
    
    std::cout << "Output Filename: " << outputfilename << std::endl;

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
    double samplingtime = 8.0*(1e-3);
    double s = 0.0;
    // double slower = 0.2; // executed speed will be slower*realspeed
    double timestep = samplingtime*slower; // default value for Denso slave mode

    std::string tablestring = "";
    std::string separator = "";

    std::stringstream t;
    std::vector<std::vector<double> > LUT;
    std::cout << "Building a look-up table for the trajectory. . ." << "\n";
    while (s < ptraj->duration) {
        ptraj->Eval(s, q);
        LUT.push_back(q);
        tablestring += separator;
        tablestring += std::to_string(q[0]) + " " + std::to_string(q[1]) +
                       " " + std::to_string(q[2]) + " " + std::to_string(q[3]) +
                       " " + std::to_string(q[4]) + " " + std::to_string(q[5]);
        separator = "\n";

        t << std::setprecision(17) << s << " "; //
        s += timestep;
    }
    s = ptraj->duration;
    ptraj->Eval(s, q);
    LUT.push_back(q);
    tablestring += separator;
    tablestring += std::to_string(q[0]) + " " + std::to_string(q[1]) +
                   " " + std::to_string(q[2]) + " " + std::to_string(q[3]) +
                   " " + std::to_string(q[4]) + " " + std::to_string(q[5]);

    t << std::setprecision(17) << s << " ";

    ///////////////////////// Write the LUT to file /////////////////////////
    std::ofstream out(outputfilename);
    out << tablestring;
    out.close();

    std::ofstream out2("densohistory.timestamp");
    out2 << t.str();
    out2.close();
    std::cout << "timestamp successfully written in denhistory.timestamp\n";
    return 0;
}
