/*
'=================================================================================
' Example programm how to use the "DENSO b-Cap Connection"
' 
' Warning: THIS SAMPLE PROGRAMM IS NOT TESTED WITH A LINUX PC,
'	   IT`S ONLY CREATED TO EXPLAIN THE COMMUNICATION BETWEEN DENSO RC7 CONTROLLER
'          VIA B-CAP AND A LINUX PC.
'	   IT IS NECESSARY TO CREATE ADDITIONAL ERROR HANDLERS AND ERROR EXEPTIONS.
'=================================================================================
*/
/** @file sample.c
 *
 *  @brief b-CAP Sample program
 *
 *  @version	1.1
 *	@date		2012/06/01
 *	@author		DENSO WAVE (y)
 *
 */
#include <stdio.h>

#include "b-Cap.h"

#define	SERVER_PORT_NUM			5007			/* Port number (is fixed in this version) */
#define SERVER_IP_ADDRESS		"192.168.0.1"	/* Your controller IP address */
#define SERVER_TYPE				8				/* Your controller type , RC8 = 8, else is RC7 */

/* Main */
int main(int argc, char* argv[]){

	int iSockFD;
	int i;
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
#if SERVER_TYPE > 7 /* RC8 */
	hr = bCap_ControllerConnect(iSockFD, "", "CaoProv.DENSO.VRC", "localhost", "", &lhController);
#else /* RC7 */
	/* hr = bCap_ControllerConnect(iSockFD, "", "CaoProv.DENSO.VRC", "localhost", "", &lhController); 
	   It is possible to specify same parameters as RC8, becouse RC7 ignore all parameters. */
	hr = bCap_ControllerConnect(iSockFD, "", "", "", "", &lhController);		
#endif
	if FAILED(hr){
		return (hr);
	}


	/*==========================================*/
	/* Task Access								*/
	{
		uint32_t lhTask;

		hr = bCap_ControllerGetTask(iSockFD, lhController, "RobSlave", "", &lhTask);/* Get task handle  */
		/*------------------------------------------*/
		/* Task- Start & Stop						*/
		if SUCCEEDED(hr){
			hr = bCap_TaskStart(iSockFD, lhTask, 1 ,"");							/* Start task 1 cycle */

		}
		bCap_TaskRelease(iSockFD, lhTask);											/* Release task handle  */
	}

	/*==========================================*/
	/* Variable Access							*/
	{
		uint32_t lhVar;
									
		/*------------------------------------------*/
		/* Variable - PutValue						*/
		hr = bCap_ControllerGetVariable(iSockFD, lhController, "I5", "", &lhVar);	/* Get var handle  */
		if SUCCEEDED(hr){
			long lValue = 32;
			hr = bCap_VariablePutValue(iSockFD, lhVar, VT_I4, 1, &lValue);			/* Put Value */
		}
		bCap_VariableRelease(iSockFD, lhVar);										/* Release var handle  */

		hr = bCap_ControllerGetVariable(iSockFD, lhController, "P5", "", &lhVar);	/* Get var handle  */
		if SUCCEEDED(hr){
			float fValue[7] = {7,6,5,4,3,2,1};
			hr = bCap_VariablePutValue(iSockFD, lhVar, VT_R4|VT_ARRAY, 7,fValue);	/* Put Value */
		}
		bCap_VariableRelease(iSockFD, lhVar);										/* Release var handle  */

		hr = bCap_ControllerGetVariable(iSockFD, lhController, "S5", "", &lhVar);	/* Get var handle  */
		if SUCCEEDED(hr){
			char *str = "1234567890ABCDEFGHIJ";
			hr = bCap_VariablePutValue(iSockFD, lhVar, VT_BSTR, 1, str);			/* Put Value */
		}
		bCap_VariableRelease(iSockFD, lhVar);										/* Release var handle  */

		/*------------------------------------------*/
		/* Variable - GetValue						*/
		hr = bCap_ControllerGetVariable(iSockFD, lhController, "I5", "", &lhVar);	/* Get var handle  */
		if SUCCEEDED(hr){
			long lValue;
			hr = bCap_VariableGetValue(iSockFD, lhVar, &lValue);					/* Get Value */
			if SUCCEEDED(hr){
				printf("Value:%ld\n", lValue);
			}			
		}
		bCap_VariableRelease(iSockFD, lhVar);										/* Release var handle  */

		hr = bCap_ControllerGetVariable(iSockFD, lhController, "P5", "", &lhVar);	/* Get var handle  */
		if SUCCEEDED(hr){
			float fValue[7];
			hr = bCap_VariableGetValue(iSockFD, lhVar, fValue);						/* Get Value */
			if SUCCEEDED(hr){
				for (i = 0;i < 7;i++){		printf("Value:%d %f \n", i, fValue[i]); }
			}			
		}
		bCap_VariableRelease(iSockFD, lhVar);										/* Release var handle  */
		
		hr = bCap_ControllerGetVariable(iSockFD, lhController, "S5", "", &lhVar);	/* Get var handle  */
		if SUCCEEDED(hr){
			char str[256];
			hr = bCap_VariableGetValue(iSockFD, lhVar, str);						/* Get Value */
			if SUCCEEDED(hr){
				printf("Value:%s\n", str);
			}			
		}
		bCap_VariableRelease(iSockFD, lhVar);										/* Release var handle  */

	}


	/*==========================================*/
	/* Robot Access							*/
	{
		uint32_t lhRobot;
		uint32_t lhVar;
		long lResult;

		hr = bCap_ControllerGetRobot(iSockFD, lhController, "", "", &lhRobot);		/* Get robot handle  */

		if SUCCEEDED(hr){

			/*------------------------------------------*/
			/* Robot - Move						*/				
			printf("Press anykey to Move Robot\n");
			getchar();

#if SERVER_TYPE > 7 /* RC8 */
			/* Take robot arm semaphore */
			hr = bCap_RobotExecute(iSockFD, lhRobot, "TakeArm", "", &lResult);
			if SUCCEEDED(hr) 
			{
				/* Set external speed */
				hr = bCap_RobotExecute(iSockFD, lhRobot, "ExtSpeed", "50.0", &lResult);

				/* Get busy status variable */
				hr = bCap_RobotGetVariable(iSockFD, lhRobot, "@BUSY_STATUS", "", &lhVar);	/* Get var handle  */		
				if SUCCEEDED(hr){
					u_short isbusy = 0;
					
					hr = bCap_RobotMove(iSockFD, lhRobot, 1 ,"@P P(300,-250,350,0,0,20,0)", "Next");/* Move Robot */

					/* Wait for motion done */
					if SUCCEEDED(hr){
						do {
							
							/* usleep(10000); or Sleep(10); here !*/
							
							hr = bCap_VariableGetValue(iSockFD, lhVar, &isbusy);				/* Get Value */

						} while(isbusy);
					}			
				}
				bCap_VariableRelease(iSockFD, lhVar);									/* Release var handle  */

				/*------------------------------------------*/
				/* Robot - Change Tool						*/				
				hr = bCap_RobotChange(iSockFD, lhRobot, "Tool5");						/* Change Tool */

			}
			/* Give robot arm semaphore */
			hr = bCap_RobotExecute(iSockFD, lhRobot, "GiveArm", "", &lResult);
#else /* RC7 */
			{
				do{
					hr = bCap_RobotMove(iSockFD, lhRobot, 1 ,"@P P(300,-250,350,0,0,20,0)", "");/* Move Robot */
				} while (hr == BCAP_E_ROBOTISBUSY);

				/*------------------------------------------*/
				/* Robot - Change Tool						*/				
				hr = bCap_RobotChange(iSockFD, lhRobot, "Tool5");						/* Change Tool */

			}
#endif
			hr = BCAP_S_OK;
		}


		/*------------------------------------------*/
		/* Robot - Get value						*/				
		if SUCCEEDED(hr){
			hr = bCap_RobotGetVariable(iSockFD, lhRobot, "@CURRENT_POSITION", "", &lhVar);	/* Get var handle  */
			
			if SUCCEEDED(hr){
				float fValue[7];
				hr = bCap_VariableGetValue(iSockFD, lhVar, fValue);					/* Get Value */
				if SUCCEEDED(hr){
					for (i = 0;i < 7;i++){		printf("@CurPos:%d %f \n", i, fValue[i]); }
				}			
			}
			bCap_VariableRelease(iSockFD, lhVar);									/* Release var handle  */
		}

		bCap_RobotRelease(iSockFD, lhRobot);										/* Release robot handle */
	}

	/* Release controller handle */
	bCap_ControllerDisconnect(iSockFD, lhController);

	bCap_ServiceStop(iSockFD);	/* Stop b-CAP service (Very important in UDP/IP connection) */
	bCap_Close(iSockFD);

	printf("Put any key \n");
	getchar();
	return 0;
}

