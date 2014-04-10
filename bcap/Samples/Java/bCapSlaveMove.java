/** @file bCapSlvMove.java
 *
 *  @brief b-CAP client program
 *
 *  @version	1.0
 *	@date		2013/2/20
 *	@author		DENSO WAVE (m)
 *
 */

/*
[NOTES]
 This is a sample source code controlling RC8 with SlaveMode.
 Copy and modify this code in accordance with a device and a device version.
 Especially please note timeout and timeout-retry settings.
*/

import jbCAP.*;
import jVARIANT.*;

public class bCapSlaveMove extends Thread{
	public static void main(String[] args){
		new bCapSlaveMove();
	}
	
	private static final String SERVER_IP_ADDRESS = "192.168.0.1"; /* Your controller IP address */
	private static final int SERVER_PORT_NUM = 5007;
	
	private static final int PERIOD = 100;	/* Period Cycle */
	private static final int AMPLITUDE = 10;/* Amplitude */
	
	private static final int E_BUF_FULL = 0x83201483;
	
	public bCapSlaveMove(){
		Thread th = new Thread(this);
		th.start();
	}
	
	@Override
	public void run(){
		bCapEngine eng = new bCapEngine();

		/* Get controller handle */
		bCapController ctrl = eng.AddController(SERVER_IP_ADDRESS, SERVER_PORT_NUM, true, 1000, 1, "", "CaoProv.DENSO.VRC", SERVER_IP_ADDRESS, "");

		if(eng.HRESULT() >= 0){
			/* Robot access */
			bCapRobot robot = ctrl.AddRobot("", "");
			if(ctrl.HRESULT() >= 0){
				/* Get arm control authority */
				robot.Execute("Takearm", new VARIANT(VARENUM.VT_ARRAY | VARENUM.VT_I4, new SAFEARRAY(VARENUM.VT_I4, new Object[]{0, 1})));
				
				/* Motor on */
				robot.Execute("Motor", new VARIANT(VARENUM.VT_ARRAY | VARENUM.VT_I4, new SAFEARRAY(VARENUM.VT_I4, new Object[]{1, 0})));
				
				/* Move to first pose */
				robot.Move(1, new VARIANT(VARENUM.VT_BSTR, "@E J1"), "");
				
				/* Get current angle */
				double[] dPos = new double[8];
				VARIANT vntReturn = robot.Execute("CurJnt", new VARIANT());
				if(vntReturn.VariantGetType() == (VARENUM.VT_ARRAY | VARENUM.VT_R8)){
					SAFEARRAY aryReturn = (SAFEARRAY) vntReturn.VariantGetObject();
					
					for(int i = 0; i < 8; i++){
						dPos[i] = (Double) aryReturn.SafeArrayGetElement(i);
					}
				}
				
				/* Start slave mode (Mode 0, J Type) */
				robot.Execute("slvChangeMode", new VARIANT(VARENUM.VT_I4, 0x2));
				
				Object[] objArray = new Object[8];

				for(int i = 0; i < PERIOD; i++){
					objArray[0] = dPos[0] + i / 10.0;
					objArray[1] = dPos[1] + AMPLITUDE * Math.sin(2*Math.PI*i/PERIOD);
					for(int j = 2; j < 8; j++){
						objArray[j] = dPos[j];
					}
					
					robot.Execute("slvMove", new VARIANT(VARENUM.VT_ARRAY | VARENUM.VT_R8, new SAFEARRAY(VARENUM.VT_R8, objArray)));
					
					/* if return code is not S_OK, then keep the message sending process waiting for 8 msec */
					if(robot.HRESULT() != 0){
						try {
							sleep(8);
						} catch (InterruptedException e){
							// Do Nothing
						}
						
						/* if return code is E_BUF_FULL, then retry previous packet */
						if(robot.HRESULT() < 0){
							if(robot.HRESULT() == E_BUF_FULL){
								i--;
							}else{
								break;
							}
						}
					}
				}
				
				/* Stop robot */
				robot.Execute("slvMove", new VARIANT(VARENUM.VT_ARRAY | VARENUM.VT_R8, new SAFEARRAY(VARENUM.VT_R8, objArray)));
				
				/* Stop slave mode */
				robot.Execute("slvChangeMode", new VARIANT(VARENUM.VT_I4, 0x0));
				
				/* Motor off */
				robot.Execute("Motor", new VARIANT(VARENUM.VT_ARRAY | VARENUM.VT_I4, new SAFEARRAY(VARENUM.VT_I4, new Object[]{0, 0})));
				
				/* Release arm control authority */
				robot.Execute("Givearm", new VARIANT());
			}
			
			/* Release robot handle */
			ctrl.get_Robots().Remove(robot.get_Name());
		}
		
		/* Release controller handle, Stop b-CAP service (Very important in UDP/IP connection) */
		eng.get_Controllers().Remove(ctrl.get_Name());
		
		eng.Release();
	}
}
