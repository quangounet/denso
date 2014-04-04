package jbCAP;

/** @file bCapExtension.java
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

public class bCapExtension {
	private int m_hr;
	private int m_iHandle;
	
	private bCapSocket m_bCapSock;
	
	private bCapVariables m_vars;
	
	protected bCapExtension(String strName, String strOption, int iHandle, bCapSocket sock)
	{
		m_hr = 0;
		
		m_iHandle = iHandle;
		m_bCapSock = sock;
				
		m_vars = new bCapVariables(m_iHandle, m_bCapSock, 1);
	}
	
	protected void Disconnect()
	{
		// Clear bCap Objects
		m_vars.Clear();
		
		// Disconnect bCapExtension
		bCapPacket msg = new bCapPacket();
		msg.SetID(36); // Extension_Release
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
			
		msg.AddVariant(vnt);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public VARIANT get_Name()
	{
		VARIANT vntReturn = new VARIANT(VARENUM.VT_BSTR, "");
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(31); // Extension_GetName
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);

		msg.AddVariant(vnt);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
		
		if((msg.SizeVariant() == 1 && msg.GetVariant(0).VariantGetType() == VARENUM.VT_BSTR))
		{
			vntReturn.VariantPutObject(msg.GetVariant(0).VariantGetObject());
		}
		
		return vntReturn;
	}
	
	public int get_Attribute()
	{
		int iReturn = 0;
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(29); // Extension_GetAttribute
		
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
		msg.SetID(30); // Extension_GetHelp
		
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
		msg.SetID(32); // Extension_GetTag
		
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
		msg.SetID(33); // Extension_PutTag
		
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
		msg.SetID(34); // Extension_GetID
		
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
		msg.SetID(35); // Extension_PutID
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		
		msg.AddVariant(vnt);
		msg.AddVariant(newVal);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public VARIANT get_VariableNames(String strOption)
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(27); // Extension_GetVariableNames
		
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
	
	public bCapVariable AddVariable(String strName, String strOption)
	{
		bCapVariable var = m_vars.Add(strName, strOption);
		m_hr = m_vars.HRESULT();
		
		return var;
	}
	
	public VARIANT Execute(String strCommand, VARIANT vntParam)
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(28); // Extension_Execute
		
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
