package jbCAP;

/** @file bCapRobot.java
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

import jVARIANT.VARENUM;
import jVARIANT.VARIANT;

public class bCapRobot {
	private int m_hr;
	private int m_iHandle;

	private bCapSocket m_bCapSock;
	
	private bCapVariables m_vars;
	
	protected bCapRobot(String strName, String strOption, int iHandle, bCapSocket sock)
	{
		m_hr = 0;

		m_iHandle = iHandle;
		m_bCapSock = sock;
			
		m_vars = new bCapVariables(m_iHandle, m_bCapSock, 3);
	}
	
	protected void Disconnect()
	{
		// Clear bCap Objects
		m_vars.Clear();
		
		// Disconnect bCapRobot
		bCapPacket msg = new bCapPacket();
		msg.SetID(84); // Robot_Release
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
			
		msg.AddVariant(vnt);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public VARIANT get_Name()
	{
		VARIANT vntReturn = new VARIANT(VARENUM.VT_BSTR, "");
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(79); // Robot_GetName
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		
		msg.AddVariant(vnt);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
		
		if(msg.SizeVariant() == 1 && msg.GetVariant(0).VariantGetType() == VARENUM.VT_BSTR){
			vntReturn.VariantPutObject(msg.GetVariant(0).VariantGetObject());
		}
		
		return vntReturn;
	}
	
	public int get_Attribute()
	{
		int iReturn = 0;
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(77); // Robot_GetAttribute
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
			
		msg.AddVariant(vnt);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
		
		if(msg.SizeVariant() == 1 && msg.GetVariant(0).VariantGetType() == VARENUM.VT_I4)
		{
			iReturn = (Integer)msg.GetVariant(0).VariantGetObject();
		}
		
		return iReturn;
	}
	
	public String get_Help()
	{
		String strReturn = "";
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(78); // Robot_GetHelp
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
			
		msg.AddVariant(vnt);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
		
		if(msg.SizeVariant() == 1 && msg.GetVariant(0).VariantGetType() == VARENUM.VT_BSTR)
		{
			strReturn = (String)msg.GetVariant(0).VariantGetObject();
		}
		
		return strReturn;
	}
	
	public VARIANT get_VariableNames(String strOption)
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(63); // Robot_GetVariableNames
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		
		VARIANT vntStrOption = new VARIANT(VARENUM.VT_BSTR, strOption);
			
		msg.AddVariant(vnt);
		msg.AddVariant(vntStrOption);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
		
		if(msg.SizeVariant() == 1)
		{
			vntReturn = msg.GetVariant(0);
		}
		
		return vntReturn;		
	}
	
	public bCapVariables get_Variables()
	{
		return m_vars;
	}
	
	public VARIANT get_Tag()
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(80); // Robot_GetTag
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		
		msg.AddVariant(vnt);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
		
		if(msg.SizeVariant() == 1)
		{
			vntReturn = msg.GetVariant(0);
		}
		
		return vntReturn;
	}
	
	public void put_Tag(VARIANT newVal)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(81); // Robot_PutTag
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		
		msg.AddVariant(vnt);
		msg.AddVariant(newVal);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public VARIANT get_ID()
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(82); // Robot_GetID
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		
		msg.AddVariant(vnt);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
		
		if(msg.SizeVariant() == 1)
		{
			vntReturn = msg.GetVariant(0);
		}
		
		return vntReturn;
	}
	
	public void put_ID(VARIANT newVal)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(83); // Robot_PutID
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		
		msg.AddVariant(vnt);
		msg.AddVariant(newVal);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public bCapVariable AddVariable(String strName, String strOption)
	{
		bCapVariable var = m_vars.Add(strName, strOption);
		m_hr = m_vars.HRESULT();
		
		return var;
	}
	
	public void Accelerate(int iAxis, float fAccel, float fDecel)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(65); // Robot_Accelerate
		
		VARIANT[] vnt = new VARIANT[4];
		
		vnt[0] = new VARIANT(VARENUM.VT_I4, m_iHandle);
		vnt[1] = new VARIANT(VARENUM.VT_I4, iAxis);
		vnt[2] = new VARIANT(VARENUM.VT_R4, fAccel);
		vnt[3] = new VARIANT(VARENUM.VT_R4, fDecel);

		for(int i = 0; i < vnt.length; i++){
			msg.AddVariant(vnt[i]);
		}
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public void Change(String strName)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(66); // Robot_Change
		
		VARIANT[] vnt = new VARIANT[2];
		
		vnt[0] = new VARIANT(VARENUM.VT_I4, m_iHandle);
		vnt[1] = new VARIANT(VARENUM.VT_BSTR, strName);
		
		msg.AddVariant(vnt[0]);
		msg.AddVariant(vnt[1]);
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public void Chuck(String strOption)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(67); // Robot_Chuck
		
		VARIANT[] vnt = new VARIANT[2];
		
		vnt[0] = new VARIANT(VARENUM.VT_I4, m_iHandle);
		vnt[1] = new VARIANT(VARENUM.VT_BSTR, strOption);
		
		msg.AddVariant(vnt[0]);
		msg.AddVariant(vnt[1]);
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();		
	}
	
	public void Drive(int iNo, float fMov, String strOption)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(68); // Robot_Drive
		
		VARIANT[] vnt = new VARIANT[4];
		
		vnt[0] = new VARIANT(VARENUM.VT_I4, m_iHandle);
		vnt[1] = new VARIANT(VARENUM.VT_I4, iNo);
		vnt[2] = new VARIANT(VARENUM.VT_R4, fMov);
		vnt[3] = new VARIANT(VARENUM.VT_BSTR, strOption);

		for(int i = 0; i < vnt.length; i++){
			msg.AddVariant(vnt[i]);
		}
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();	
	}
	
	public void GoHome()
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(69); // Robot_GoHome
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);

		msg.AddVariant(vnt);
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public void Halt(String strOption)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(70); // Robot_Halt
		
		VARIANT[] vnt = new VARIANT[2];
		
		vnt[0] = new VARIANT(VARENUM.VT_I4, m_iHandle);
		vnt[1] = new VARIANT(VARENUM.VT_BSTR, strOption);
		
		msg.AddVariant(vnt[0]);
		msg.AddVariant(vnt[1]);
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();		
	}	
	
	public void Hold(String strOption)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(71); // Robot_Hold
		
		VARIANT[] vnt = new VARIANT[2];
		
		vnt[0] = new VARIANT(VARENUM.VT_I4, m_iHandle);
		vnt[1] = new VARIANT(VARENUM.VT_BSTR, strOption);
		
		msg.AddVariant(vnt[0]);
		msg.AddVariant(vnt[1]);
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();		
	}
	
	public void Move(int iComp, VARIANT vntPose, String strOption)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(72); // Robot_Move
		
		VARIANT[] vnt = new VARIANT[3];
		
		vnt[0] = new VARIANT(VARENUM.VT_I4, m_iHandle);
		vnt[1] = new VARIANT(VARENUM.VT_I4, iComp);
		vnt[2] = new VARIANT(VARENUM.VT_BSTR, strOption);
		
		msg.AddVariant(vnt[0]);
		msg.AddVariant(vnt[1]);
		msg.AddVariant(vntPose);
		msg.AddVariant(vnt[2]);
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public void Rotate(VARIANT vntRotSuf, float fDeg, VARIANT vntPivot, String strOption)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(73); // Robot_Rotate
		
		VARIANT[] vnt = new VARIANT[3];
		
		vnt[0] = new VARIANT(VARENUM.VT_I4, m_iHandle);
		vnt[1] = new VARIANT(VARENUM.VT_R4, fDeg);
		vnt[2] = new VARIANT(VARENUM.VT_BSTR, strOption);

		msg.AddVariant(vnt[0]);
		msg.AddVariant(vntRotSuf);
		msg.AddVariant(vnt[1]);
		msg.AddVariant(vntPivot);
		msg.AddVariant(vnt[2]);
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();		
	}
	
	public void Speed(int iAxis, float fSpeed)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(74); // Robot_Speed
		
		VARIANT[] vnt = new VARIANT[3];
		
		vnt[0] = new VARIANT(VARENUM.VT_I4, m_iHandle);
		vnt[1] = new VARIANT(VARENUM.VT_I4, iAxis);
		vnt[2] = new VARIANT(VARENUM.VT_R4, fSpeed);
		
		msg.AddVariant(vnt[0]);
		msg.AddVariant(vnt[1]);
		msg.AddVariant(vnt[2]);
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();			
	}
	
	public void Unchuck(String strOption)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(75); // Robot_Unchuck
		
		VARIANT[] vnt = new VARIANT[2];
		
		vnt[0] = new VARIANT(VARENUM.VT_I4, m_iHandle);
		vnt[1] = new VARIANT(VARENUM.VT_BSTR, strOption);

		msg.AddVariant(vnt[0]);
		msg.AddVariant(vnt[1]);
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();		
	}	
	
	public void Unhold(String strOption)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(76); // Robot_Unhold
		
		VARIANT[] vnt = new VARIANT[2];
		
		vnt[0] = new VARIANT(VARENUM.VT_I4, m_iHandle);
		vnt[1] = new VARIANT(VARENUM.VT_BSTR, strOption);
		
		msg.AddVariant(vnt[0]);
		msg.AddVariant(vnt[1]);
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();		
	}
	
	public VARIANT Execute(String strCommand, VARIANT vntParam)
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(64); // Robot_Execute
		
		VARIANT[] vnt = new VARIANT[2];
		
		vnt[0] = new VARIANT(VARENUM.VT_I4, m_iHandle);	
		vnt[1] = new VARIANT(VARENUM.VT_BSTR, strCommand);

		msg.AddVariant(vnt[0]);
		msg.AddVariant(vnt[1]);
		msg.AddVariant(vntParam);
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();

		if(msg.SizeVariant() == 1)
		{
			vntReturn = msg.GetVariant(0);
		}

		return vntReturn;
	}
	
	public int HRESULT()
	{
		return m_hr;
	}
}
