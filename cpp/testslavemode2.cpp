#include "DensoController.cpp"
#include <math.h>

int main() {
    DensoController::DensoController denso;
    denso.bCapEnterProcess();

    BCAP_HRESULT hr;

    hr = denso.SetExtSpeed("50");
    hr = denso.bCapRobotMove("J(-45, 30, 30, 0, -45, 0)", "Speed = 25");
    hr = denso.bCapRobotExecute("ClearLog", ""); // enable control logging
    hr = denso.bCapSlvChangeMode("258");
    std::vector<double> jointvalues = denso.GetCurJnt();
    BCAP_VARIANT vntPose, vntReturn;
    vntPose = denso.VNTFromVector(jointvalues);

    for (int i = 0; i < 200; i++) {
        vntPose.Value.DoubleArray[0] = jointvalues[0] + i/100.0;
        vntPose.Value.DoubleArray[1] = jointvalues[1] + 2*sin(2*M_PI*i/100);
        hr = bCap_RobotExecute2(denso.iSockFD, denso.lhRobot, "slvMove", &vntPose, &vntReturn);
        usleep(10000);
    }

    hr = denso.bCapSlvChangeMode("0");
    hr = denso.bCapRobotExecute("StopLog", "");
    denso.bCapExitProcess();
}
