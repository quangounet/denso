#include "DensoController.cpp"

int main()
{
    //////////////////// Initialize DENSO controller ////////////////////
    DensoController::DensoController denso;
    denso.bCapEnterProcess();
    BCAP_HRESULT hr;

    hr = denso.SetExtSpeed("100");
    std::cout << "Moving to the initial pose...\n";
    denso.bCapRobotMove("J(-90, 45, 45, 0, -90, 0)", "Speed = 25");
    sleep(2);
    denso.bCapRobotMove("J(-90, 45, 45, 0, 0, 0)", "Speed = 1");

    ////////////////////////////// EXIT B-CAP PROCESS //////////////////////////////
    denso.bCapExitProcess();
}
