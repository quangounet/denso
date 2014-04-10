/** @file bCapSlvMove.c
 *
 *  @brief b-CAP client program
 *
 *  @version	1.0
 *	@date		2013/2/22
 *	@author		DENSO WAVE (m)
 *
 */

/*
[NOTES]
 This is a sample source code controlling RC8 with SlaveMode.
 Copy and modify this code in accordance with a device and a device version.
 Especially please note timeout and timeout-retry settings.
*/

#define SERVER_IP_ADDRESS		"192.168.0.1"		/* Your controller IP address */
#define SERVER_PORT_NUM			5007

#define PERIOD					100					/* Period Cycle */
#define AMPLITUDE				10					/* Amplitude */

#define E_BUF_FULL				0x83201483

#include <windows.h>

#define _USE_MATH_DEFINES
#include <math.h>

#include "b-Cap.h"

int main(int argc, char* argv[])
{
	int iSockFD;
	uint32_t lhController;
	BCAP_HRESULT hr = BCAP_S_OK;

	/* Init and Start b-CAP		*/
	hr = bCap_Open(SERVER_IP_ADDRESS, SERVER_PORT_NUM, &iSockFD);				/* Init socket  */
	if FAILED(hr){
		return (hr);
	}

	hr = bCap_ServiceStart(iSockFD);											/* Start b-CAP service */
	if FAILED(hr){
		return (hr);
	}

	/* Get controller handle */
	hr = bCap_ControllerConnect(iSockFD, "", "caoProv.DENSO.VRC", SERVER_IP_ADDRESS, "", &lhController);

	if FAILED(hr){
		return (hr);
	}

	/*==========================================*/
	/* Robot Access							*/
	{
		uint32_t lhRobot;
		long lResult;

		hr = bCap_ControllerGetRobot(iSockFD, lhController, "", "", &lhRobot);		/* Get robot handle */

		if SUCCEEDED(hr){
			int i, j;
			double dJnt[8];
			BCAP_VARIANT vntPose, vntReturn;

			/* Get arm control authority */
			hr = bCap_RobotExecute(iSockFD, lhRobot, "Takearm", "", &lResult);

			/* Motor on */
			hr = bCap_RobotExecute(iSockFD, lhRobot, "Motor", "1", &lResult);

			/* Move to first pose */
			hr = bCap_RobotMove(iSockFD, lhRobot, 1L, "@E J1", "");

			/* Get current angle */
			hr = bCap_RobotExecute(iSockFD, lhRobot, "CurJnt", "", &dJnt);

			/* Start slave mode (Mode 0, J Type) */
			hr = bCap_RobotExecute(iSockFD, lhRobot, "slvChangeMode", "2", &lResult);

			/* Execute slave move */
			vntPose.Type = VT_R8 | VT_ARRAY;
			vntPose.Arrays = 8;
			for(i = 0; i < PERIOD; i++)
			{
				vntPose.Value.DoubleArray[0] = dJnt[0] + i / 10.0;
				vntPose.Value.DoubleArray[1] = dJnt[1] + AMPLITUDE * sin(2*M_PI*i/PERIOD);
				for(j = 2; j < 8; j++)
				{
					vntPose.Value.DoubleArray[j] = dJnt[j];
				}

				hr = bCap_RobotExecute2(iSockFD, lhRobot, "slvMove", &vntPose, &vntReturn);

				/* if return code is not S_OK, then keep the message sending process waiting for 8 msec */
				if(hr != 0)
				{
					Sleep(8);

					/* if return code is E_BUF_FULL, then retry previous packet */
					if(FAILED(hr)){
						if(hr == E_BUF_FULL){
							i--;
						}else{
							break;
						}
					}
				}
			}

			/* Stop robot */
			hr = bCap_RobotExecute2(iSockFD, lhRobot, "slvMove", &vntPose, &vntReturn);

			/* Stop slave mode */
			hr = bCap_RobotExecute(iSockFD, lhRobot, "slvChangeMode", "0", &lResult);

			/* Motor off */
			hr = bCap_RobotExecute(iSockFD, lhRobot, "Motor", "0", &lResult);

			/* Release arm control authority */
			hr = bCap_RobotExecute(iSockFD, lhRobot, "Givearm", "", &lResult);
		}

		bCap_RobotRelease(iSockFD, lhRobot);	/* Release robot handle */
	}

	/* Release controller handle */
	bCap_ControllerDisconnect(iSockFD, lhController);

	bCap_ServiceStop(iSockFD);	/* Stop b-CAP service (Very important in UDP/IP connection) */
	bCap_Close(iSockFD);

	return 0;
}