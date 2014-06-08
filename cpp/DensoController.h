#include "b-Cap.c"
#include <exception>
#include <string>
#include <cassert>
#include <boost/typeof/std/vector.hpp>
#define DEFAULT_SERVER_IP_ADDRESS    "192.168.0.1"
#define DEFAULT_SERVER_PORT_NUM      5007

#define E_BUF_FULL           0x83201483
#define S_BUF_FULL           0x0F200501

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
    void bCapEnterProcess();
    void bCapExitProcess();

    std::vector<double> GetCurJnt();
    std::vector<double> VectorFromVNT(BCAP_VARIANT vnt0);
    BCAP_VARIANT VNTFromVector(std::vector<double> vect0);

    const char* server_ip_address;
    int server_port_num;
    int iSockFD;
    uint32_t lhController;
    uint32_t lhRobot;

};

} // end namespace DensoController
