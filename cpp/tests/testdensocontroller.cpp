#include "DensoController.cpp"

int main(){
    BCAP_HRESULT hr;

    DensoController::DensoController denso;
    denso.bCapEnterProcess();
    hr = denso.bCapRobotMove("J(0, 0, 0, 0, 0, 0)", "Speed = 25");
    hr = denso.bCapRobotMove("J(-45, 30, 30, 0, -45, 0)", "Speed = 25");
    denso.bCapExitProcess();
}
