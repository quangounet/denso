package jbCAP;

/** @file bCapSocket.java
*
*  @brief b-CAP client library
*
*  @version	1.0
*	@date		2013/2/20
*	@author		DENSO WAVE (m)
*
*/

/*
[NOTES]
This is a sample source code. Copy and modify this code in accordance with a device and a device version. Especially please note timeout and timeout-retry settings.
*/

abstract class bCapSocket {
	abstract protected void Release();
	abstract protected boolean IsConnected();
	abstract protected bCapPacket SendMessage(bCapPacket msg);
	
	protected static final int m_iTimeoutCode = 0x80000900;
	
	protected void bCapStart()
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(1); // Service_Start
		
		SendMessage(msg);
	}
	
	protected void bCapStop()
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(2); // Service_Stop
		
		SendMessage(msg);
	}
}
