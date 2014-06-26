#include "DensoController.cpp"
#include <math.h>
#include <time.h>

#define nSEC_PER_SECOND 1E9;

int main() {
    DensoController::DensoController denso;
    denso.bCapEnterProcess();

    BCAP_HRESULT hr;

    hr = denso.SetExtSpeed("100");
    hr = denso.bCapRobotMove("J(-45, 30, 30, 0, -45, 0)", "Speed = 25");
    sleep(1);
    hr = denso.bCapRobotExecute("ClearLog", ""); // enable control logging
    hr = denso.bCapSlvChangeMode("258");
    std::vector<double> jointvalues = denso.GetCurJnt();
    BCAP_VARIANT vntPose, vntReturn;
    vntPose = denso.VNTFromVector(jointvalues);
    vntPose.Value.DoubleArray[0] = jointvalues[0];

    struct timespec tick, tock;
    struct timespec t_beg, t_end;

    for (int i = 1; i <= 100; i++) {
        clock_gettime(CLOCK_MONOTONIC, &t_beg);
        vntPose.Value.DoubleArray[0] += 1/50.0;
        //vntPose.Value.DoubleArray[1] = jointvalues[1] + 2*sin(2*M_PI*i/100);
        clock_gettime(CLOCK_MONOTONIC, &tick);
        hr = bCap_RobotExecute2(denso.iSockFD, denso.lhRobot, "slvMove", &vntPose, &vntReturn);
        clock_gettime(CLOCK_MONOTONIC, &tock);
        //usleep(800);
        jointvalues = denso.GetCurJnt();

        std::cout << "vntReturn0 ";
        std::cout << vntReturn.Value.DoubleArray[0] << "\t";
        std::cout << vntPose.Value.DoubleArray[0] << "\t";
        std::cout << vntPose.Value.DoubleArray[0] - vntReturn.Value.DoubleArray[0] << "\n";
        std::cout << "from GetCurJnt" << jointvalues[0] << "\n";
        // std::cout << vntReturn.Value.DoubleArray[1] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[2] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[3] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[4] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[5] << "\n";
        // std::cout << vntReturn.Value.DoubleArray[6] << "\t";
        // std::cout << vntReturn.Value.DoubleArray[7] << "\n";

        clock_gettime(CLOCK_MONOTONIC, &t_end);
        double timediff = ((tock.tv_sec + tock.tv_nsec) - (tick.tv_sec + tick.tv_nsec))/nSEC_PER_SECOND;
        std::cout << "slave mode execution time " << timediff  << "\n";
        double looptimediff = ((t_end.tv_sec + t_end.tv_nsec) - (t_beg.tv_sec + t_beg.tv_nsec))/nSEC_PER_SECOND;
        std::cout << "loop time " << looptimediff << "\n";




        // hr = bCap_RobotExecute(denso.iSockFD, denso.lhRobot, "CurJSpd", "0", &JSpd);
        // usleep(10000);
        // std::cout << JSpd.Value.DoubleArray[0] << " " << JSpd.Value.DoubleArray[1] << " " << JSpd.Value.DoubleArray[2] << " " << JSpd.Value.DoubleArray[3] << " " << JSpd.Value.DoubleArray[4] << " " << JSpd.Value.DoubleArray[5] << " " << JSpd.Value.DoubleArray[6] << " " << JSpd.Value.DoubleArray[7] << "\n";
    }

    hr = denso.bCapSlvChangeMode("0");
    hr = denso.bCapRobotExecute("StopLog", "");
    denso.bCapExitProcess();
}
