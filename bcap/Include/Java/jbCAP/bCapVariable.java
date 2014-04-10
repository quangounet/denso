package jbCAP;

/** @file bCapVariable.java
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

public class bCapVariable {
	private int m_hr;
	private int m_iHandle;
	
	private bCapSocket m_bCapSock;

	protected bCapVariable(String strName, String strOption, int iHandle, bCapSocket sock)
	{
		m_hr = 0;
		
		m_iHandle = iHandle;
		m_bCapSock = sock;
	}
	
	protected void Disconnect()
	{
		// Disconnect bCapVariable
		bCapPacket msg = new bCapPacket();
		msg.SetID(111); // Variable_Release
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
			
		msg.AddVariant(vnt);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public VARIANT get_Name()
	{
		VARIANT vntReturn = new VARIANT(VARENUM.VT_BSTR, "");

		bCapPacket msg = new bCapPacket();
		msg.SetID(105); // Variable_GetName
		
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
		msg.SetID(103); // Variable_GetAttribute
		
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
		msg.SetID(104); // Variable_GetHelp
		
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
		msg.SetID(106); // Variable_GetTag
		
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
		msg.SetID(107); // Variable_PutTag
		
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
		msg.SetID(108); // Variable_GetID
		
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
		msg.SetID(109); // Variable_PutID
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		
		msg.AddVariant(vnt);
		msg.AddVariant(newVal);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public VARIANT get_Value()
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(101); // Variable_GetValue
		
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
	
	public void put_Value(VARIANT newVal)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(102); // Variable_PutValue
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
			
		msg.AddVariant(vnt);
		msg.AddVariant(newVal);
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public VARIANT get_DateTime()
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(100); // Variable_GetDateTime
		
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
	
	public int get_Microsecond()
	{
		int iReturn = 0;
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(110); // Variable_GetMicroSecond
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
			
		msg.AddVariant(vnt);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
		
		if((msg.SizeVariant() == 1 && msg.GetVariant(0).VariantGetType() == VARENUM.VT_I4))
		{
			iReturn = (Integer)msg.GetVariant(0).VariantGetObject();
		}
		
		return iReturn;
	}
	
	public int HRESULT()
	{
		return m_hr;
	}
}
