package jbCAP;

/** @file bCapCommand.java
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

public class bCapCommand {
	private int m_hr;
	private int m_iHandle;
	
	private bCapSocket m_bCapSock;
	
	protected bCapCommand(String strName, String strOption, int iHandle, bCapSocket sock)
	{
		m_hr = 0;
		
		m_iHandle = iHandle;
		m_bCapSock = sock;
	}
	
	protected void Disconnect()
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(127); // Command_Release
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);

		msg.AddVariant(vnt);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public VARIANT get_Name()
	{
		VARIANT vntReturn = new VARIANT(VARENUM.VT_BSTR, "");
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(122); // Command_GetName
		
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
		msg.SetID(120); // Command_GetAttribute
		
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
		msg.SetID(121); // Command_GetHelp
		
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
	
	public VARIANT get_Tag()
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(123); // Command_GetTag
		
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
		msg.SetID(124); // Command_PutTag
		
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
		msg.SetID(125); // Command_GetID
		
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
		msg.SetID(126); // Command_PutID
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		
		msg.AddVariant(vnt);
		msg.AddVariant(newVal);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public int get_Timeout()
	{
		int iReturn = 0;
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(114); // Command_GetTimeout
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		
		msg.AddVariant(vnt);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
		
		if(msg.SizeVariant() == 1 && msg.GetVariant(0).VariantGetType() == VARENUM.VT_I4)
		{
			iReturn = (Integer) msg.GetVariant(0).VariantGetObject();
		}
		
		return iReturn;
	}
	
	public void put_Timeout(int newVal)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(115); // Command_PutTimeout
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		VARIANT vntVal = new VARIANT(VARENUM.VT_I4, newVal);

		msg.AddVariant(vnt);
		msg.AddVariant(vntVal);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public VARIANT get_Parameters()
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(117); // Command_GetParameters
		
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
	
	public void put_Parameters(VARIANT newVal)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(118); // Command_PutParameters
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		
		msg.AddVariant(vnt);
		msg.AddVariant(newVal);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public VARIANT get_Result()
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(119); // Command_GetResult
		
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
	
	public int get_State()
	{
		int iReturn = 0;
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(116); // Command_GetState
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		
		msg.AddVariant(vnt);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
		
		if(msg.SizeVariant() == 1 && msg.GetVariant(0).VariantGetType() == VARENUM.VT_I4)
		{
			iReturn = (Integer) msg.GetVariant(0).VariantGetObject();
		}
		
		return iReturn;		
	}
	
	public void Cancel()
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(113); // Command_Cancel
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		
		msg.AddVariant(vnt);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public void Execute(int iMode)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(112); // Command_Execute
		
		VARIANT[] vnt = new VARIANT[2];
		
		vnt[0] = new VARIANT(VARENUM.VT_I4, m_iHandle);	
		vnt[1] = new VARIANT(VARENUM.VT_I4, iMode);
		
		msg.AddVariant(vnt[0]);
		msg.AddVariant(vnt[1]);
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public int HRESULT()
	{
		return m_hr;
	}
}
