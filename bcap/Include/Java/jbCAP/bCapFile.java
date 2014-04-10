package jbCAP;

/** @file bCapFile.java
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

public class bCapFile {
	private int m_hr;
	private int m_iHandle;
	
	private bCapSocket m_bCapSock;
	
	private bCapFiles m_files;
	private bCapVariables m_vars;
	
	protected bCapFile(String strName, String strOption, int iHandle, bCapSocket sock)
	{
		m_hr = 0;
		
		m_iHandle = iHandle;
		m_bCapSock = sock;
		
		m_files = new bCapFiles(m_iHandle, m_bCapSock, 1);
		m_vars = new bCapVariables(m_iHandle, m_bCapSock, 2);
	}
	
	protected void Disconnect()
	{
		// Clear bCap Objects
		m_files.Clear();
		m_vars.Clear();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(61); // File_Release
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
			
		msg.AddVariant(vnt);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public VARIANT get_Name()
	{
		VARIANT vntReturn = new VARIANT(VARENUM.VT_BSTR, "");

		bCapPacket msg = new bCapPacket();
		msg.SetID(56); // File_GetName
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		
		msg.AddVariant(vnt);
		msg = m_bCapSock.SendMessage(msg);

		m_hr = msg.GetID();
		
		if(msg.SizeVariant() == 1 && msg.GetVariant(0).VariantGetType() == VARENUM.VT_BSTR)
		{
			vntReturn.VariantPutObject(msg.GetVariant(0).VariantGetObject());
		}
		
		return vntReturn;
	}
	
	public int get_Attribute()
	{
		int iReturn = 0;
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(54); // File_GetAttribute
		
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
		msg.SetID(55); // File_GetHelp
		
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
		msg.SetID(57); // File_GetTag
		
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
		msg.SetID(58); // File_PutTag
		
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
		msg.SetID(59); // File_GetID
		
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
		msg.SetID(60); // File_PutID
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		
		msg.AddVariant(vnt);
		msg.AddVariant(newVal);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public VARIANT get_FileNames(String strOption)
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(39); // File_GetVariableNames
		
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
	
	public bCapFiles get_Files()
	{
		return m_files;
	}
	
	public VARIANT get_VariableNames(String strOption)
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(40); // File_GetVariableNames
		
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
	
	public bCapFile AddFile(String strName, String strOption)
	{
		bCapFile file = m_files.Add(strName, strOption);
		m_hr = m_files.HRESULT();
		
		return file;
	}
	
	public bCapVariable AddVariable(String strName, String strOption)
	{
		bCapVariable var = m_vars.Add(strName, strOption);
		m_hr = m_vars.HRESULT();
		
		return var;
	}
	
	public VARIANT get_DateCreated()
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(46); // File_GetDateCreated
		
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
	
	public VARIANT get_DateLastAccessed()
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(47); // File_GetLastAccessed
		
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
	
	public VARIANT get_DateLastModified()
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(48); // File_GetLastModified
		
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
	
	public int get_Size()
	{
		int iReturn = 0;
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(50); // File_GetSize
		
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
	
	public String get_Path()
	{
		String strReturn = "";
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(49); // File_GetPath
		
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
	
	public String get_Type()
	{
		String strReturn = "";
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(51); // File_GetType
		
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
	
	public VARIANT get_Value()
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(52); // File_GetValue
		
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
		msg.SetID(53); // File_PutValue
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);

		msg.AddVariant(vnt);
		msg.AddVariant(newVal);
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public void Copy(String strName, String strOption)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(42); // File_Copy
		
		VARIANT[] vnt = new VARIANT[3];
		vnt[0] = new VARIANT(VARENUM.VT_I4, m_iHandle);
		vnt[1] = new VARIANT(VARENUM.VT_BSTR, strName);
		vnt[2] = new VARIANT(VARENUM.VT_BSTR, strOption);
		
		for(int i = 0; i < vnt.length; i++)
		{	
			msg.AddVariant(vnt[i]);
		}
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();		
	}
	
	public void Delete(String strOption)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(43); // File_Delete
		
		VARIANT[] vnt = new VARIANT[2];
		vnt[0] = new VARIANT(VARENUM.VT_I4, m_iHandle);
		vnt[1] = new VARIANT(VARENUM.VT_BSTR, strOption);
		
		for(int i = 0; i < vnt.length; i++)
		{
			msg.AddVariant(vnt[i]);
		}
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();			
	}
	
	public void Move(String strName, String strOption)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(44); // File_Move
		
		VARIANT[] vnt = new VARIANT[3];
		vnt[0] = new VARIANT(VARENUM.VT_I4, m_iHandle);
		vnt[1] = new VARIANT(VARENUM.VT_BSTR, strName);
		vnt[2] = new VARIANT(VARENUM.VT_BSTR, strOption);
		
		for(int i = 0; i < vnt.length; i++)
		{
			msg.AddVariant(vnt[i]);
		}
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();		
	}
	
	public String Run(String strOption)
	{
		String strReturn = "";
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(45); // File_Run
		
		VARIANT[] vnt = new VARIANT[2];
		vnt[0] = new VARIANT(VARENUM.VT_I4, m_iHandle);
		vnt[1] = new VARIANT(VARENUM.VT_BSTR, strOption);
		
		for(int i = 0; i < vnt.length; i++)
		{	
			msg.AddVariant(vnt[i]);
		}
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
		
		if(msg.SizeVariant() == 1 && msg.GetVariant(0).VariantGetType() == VARENUM.VT_BSTR)
		{
			strReturn = (String)msg.GetVariant(0).VariantGetObject();
		}
		
		return strReturn;
	}
	
	public VARIANT Execute(String strCommand, VARIANT vntParam)
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(17); // Controller_Execute
		
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
