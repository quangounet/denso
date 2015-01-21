#include "b-Cap.c"
#include <exception>
#include <string>
#include <cassert>
#include <boost/typeof/std/vector.hpp>
#include "../../../TOPP/src/TOPP.h"
#include "../../../TOPP/src/Trajectory.cpp"
#include "../../../TOPP/src/TOPP.cpp"

#define DEFAULT_SERVER_IP_ADDRESS    "192.168.0.1"
#define DEFAULT_SERVER_PORT_NUM      5007

#define E_BUF_FULL           0x83201483
#define S_BUF_FULL           0x0F200501

#define PI 3.1415926535897932
#define nSEC_PER_SECOND 1E9
#define dReal float

namespace DensoController {

class bCapException : public std::exception {
public:
    bCapException() : std::exception(), _s("Unknown exception"), _errorcode(0) {
    }
    bCapException(const std::string& s, int errorcode = 0) : std::exception() {
        _s = s;
        _errorcode = errorcode;
    }

    virtual ~bCapException() throw() {
    }


private:
    std::string _s;
    int _errorcode;
};


class DensoController {

public:
    DensoController(const char* server_ip_address0 = DEFAULT_SERVER_IP_ADDRESS, int server_port_num0 = DEFAULT_SERVER_PORT_NUM);

    // low level commands
    void bCapOpen();
    void bCapClose();
    void bCapServiceStart();
    void bCapServiceStop();
    void bCapControllerConnect();
    void bCapControllerDisconnect();
    void bCapGetRobot();
    void bCapReleaseRobot();
    BCAP_HRESULT bCapRobotExecute(const char* command, const char* option);
    BCAP_HRESULT bCapRobotMove(const char* pose, const char* option);
    BCAP_HRESULT bCapMotor(bool command);
    BCAP_HRESULT bCapSlvChangeMode(const char* mode);
    BCAP_HRESULT bCapSlvMove(BCAP_VARIANT* pose, BCAP_VARIANT* result);
    BCAP_HRESULT SetExtSpeed(const char* speed);

    // high level commands
    void bCapEnterProcess();
    void bCapExitProcess();
    void bCapSlvFollowTraj(TOPP::Trajectory& traj, std::vector<BCAP_VARIANT>& encoderlog, int sleeptime = 5);

    // utilities
    const char* CommandFromVector(std::vector<double> q);
    std::vector<double> GetCurJnt();
    std::vector<double> VectorFromVNT(BCAP_VARIANT vnt0);
    std::vector<double> RadVectorFromVNT(BCAP_VARIANT vnt0);
    BCAP_VARIANT VNTFromVector(std::vector<double> vect0);
    BCAP_VARIANT VNTFromRadVector(std::vector<double> vect0);

    // class variables
    const char* server_ip_address;
    int server_port_num;
    int iSockFD;
    uint32_t lhController;
    uint32_t lhRobot;

};


////////////////////////////// Utilities //////////////////////////////
std::vector<double> VRad2Deg(std::vector<double> vect0);

double Rad2Deg(double x);
double Deg2Rad(double x);

} // end namespace DensoController
