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

    //// MOVE TO INITIAL POSE ////
    hr = denso.SetExtSpeed("100");
    std::cout << "Moving to the initial pose...\n";

    std::vector<double> qdeg;
    qdeg.resize(0);
    // qdeg.push_back(0);
    // qdeg.push_back(84.99451885);
    // qdeg.push_back(7.19659742);
    // qdeg.push_back(-22);
    // qdeg.push_back(-92.39518894);
    // qdeg.push_back(92.50403602);

    // qdeg.push_back(-7.42728043);
    // qdeg.push_back(83.13361778);
    // qdeg.push_back(10.17991691);
    // qdeg.push_back(-28.30339451);
    // qdeg.push_back(-87.40552314);
    // qdeg.push_back(100.50299691);

    // // bottle25
    // qdeg.push_back(-4.59817324);
    // qdeg.push_back(83.51832379);
    // qdeg.push_back(8.87019441);
    // qdeg.push_back(-26.76508128);
    // qdeg.push_back(-89.96325755);
    // qdeg.push_back(100.34151339);

    // bottle28-1
    // qdeg.push_back(-5.93788829);
    // qdeg.push_back(78.49171785);
    // qdeg.push_back(18.94780659);
    // qdeg.push_back(-27.00047705);
    // qdeg.push_back(-89.97532958);
    // qdeg.push_back(92.62508553);

    // // bottle28-1 initial config
    // qdeg.push_back(-90.298149);
    // qdeg.push_back(78.724401);
    // qdeg.push_back(0.0);
    // qdeg.push_back(0.0);
    // qdeg.push_back(-78.352838);
    // qdeg.push_back(92.532684);


    qdeg.push_back(-73.53419608);
    qdeg.push_back(98.96834061);
    qdeg.push_back(-32.16545011);
    qdeg.push_back(-6.45967465);
    qdeg.push_back(-79.27464785);
    qdeg.push_back(68.68207283);



    std::string commandstring;
    const char* command;
    commandstring = "J(" + std::to_string(qdeg[0]) + ", " + std::to_string(qdeg[1])
                    + ", " + std::to_string(qdeg[2]) + ", " + std::to_string(qdeg[3])
                    + ", " + std::to_string(qdeg[4]) + ", " + std::to_string(qdeg[5]) + ")";
    command = commandstring.c_str(); // convert string -> const shar*
    std::cout << commandstring << "\n";
    denso.bCapRobotMove(command, "Speed = 25");


    ////////////////////////////// EXIT B-CAP PROCESS //////////////////////////////
    hr = denso.bCapRobotExecute("StopLog", "");
    denso.bCapExitProcess();
}
