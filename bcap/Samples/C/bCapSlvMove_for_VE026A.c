/** @file bCapSlvMove.c
 *
 *  @brief b-CAP client program for VE026A
 *
 *  @version	1.0
 *	@date		2012/12/13
 *	@author		DENSO WAVE (m)
 *
 */

/*
[NOTES]
 This is a sample source code controlling VE026A with SlaveMode.
 Copy and modify this code in accordance with a device and a device version.
 Especially please note timeout and timeout-retry settings.
*/
#define BCAP_CONNECTION_COM 1					/* Use Com Port */
#define	SERVER_PORT_NUM			5				/* Com Port number */
#define SERVER_IP_ADDRESS		""	/* Your controller IP address */

#define PEORIOD					100				/* Perioda Cycle */
#define AMPTITUDE				15				/* Amplitude */

#include <stdio.h>
#include <conio.h>
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
	hr = bCap_ControllerConnect(iSockFD, "", "", "", "", &lhController);

	if FAILED(hr){
		return (hr);
	}

	/*==========================================*/
	/* Robot Access							*/
	{
		uint32_t lhRobot;
		uint32_t lhVar;
		long lResult;

		hr = bCap_ControllerGetRobot(iSockFD, lhController, "", "", &lhRobot);		/* Get robot handle */

		if SUCCEEDED(hr){
			int i, iCount = 0, iStep = 1;
			float fCurrent[8], fValue[8], fResult[8];

			/* Motor On */
			BCAP_VARIANT vntMotor, vntResult;
			vntMotor.Type = VT_I2;
			vntMotor.Arrays = 1;
			vntMotor.Value.ShortValue = 1;
			hr = bCap_RobotExecute2(iSockFD, lhRobot, "Motor", &vntMotor, &vntResult);		

			/* Get Current Angle */
			{
				hr = bCap_RobotGetVariable(iSockFD, lhRobot, "@CURRENT_ANGLE", "", &lhVar);	/* Get var handle  */
				
				if SUCCEEDED(hr){
					hr = bCap_VariableGetValue(iSockFD, lhVar, fCurrent);					/* Get Value */			
				}
				bCap_VariableRelease(iSockFD, lhVar);									/* Release var handle  */
			}

			printf("Start Slave Mode \n");

			/* Change to Slave Mode (Sync, J Type) */
			hr = bCap_RobotExecute(iSockFD, lhRobot, "SlvChangeMode", "2", &lResult);

			/* Do Until Key Hit */
			/* Not Change J2 ~ J8 */
			for(i = 2; i < 8; i++)
			{
				fValue[i] = fCurrent[i];
			}

			while(SUCCEEDED(hr) && !_kbhit()){
				fValue[0] = fCurrent[0] + iCount;
				fValue[1] = (float)(fCurrent[1] + iStep * AMPTITUDE * sin(2 * M_PI * ((double)iCount / PEORIOD)));

				if(iCount <= -PEORIOD/2)	iStep = 1;
				if(iCount >= PEORIOD/2)		iStep = -1;
				iCount = iCount + iStep;
				
				hr = bCap_RobotExecuteSlaveMove(iSockFD, lhRobot, "SlvMove", fValue, fResult);

				Sleep(8);
			}

			printf("Stop Slave Mode \n");
			getchar();

			/* Change to Master Mode */
			hr = bCap_RobotExecute(iSockFD, lhRobot, "SlvChangeMode", "0", &lResult);

			/* Motor Off */
			vntMotor.Value.ShortValue = 0;
			hr = bCap_RobotExecute2(iSockFD, lhRobot, "Motor", &vntMotor, &vntResult);

			hr = BCAP_S_OK;
		}

		bCap_RobotRelease(iSockFD, lhRobot);	/* Release robot handle */
	}

	/* Release controller handle */
	bCap_ControllerDisconnect(iSockFD, lhController);

	bCap_ServiceStop(iSockFD);	/* Stop b-CAP service (Very important in UDP/IP connection) */
	bCap_Close(iSockFD);

	printf("Put any key \n");
	getchar();

	return 0;
}