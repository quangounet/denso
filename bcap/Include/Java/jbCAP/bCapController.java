package jbCAP;

/** @file bCapController.java
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

import jVARIANT.*;

public class bCapController {
	private int m_hr;
	private int m_iHandle;
	
	private bCapSocket m_bCapSock;
	
	private bCapExtensions m_exts;
	private bCapFiles m_files;
	private bCapRobots m_robots;
	private bCapTasks m_tasks;
	private bCapVariables m_vars;
	private bCapCommands m_cmds;
	
	protected bCapController(String host, int port, boolean UDP, int timeout, int retry)
	{
		m_hr = 0;
		
		if(UDP)
		{
			m_bCapSock = new bCapSocketUDP(host, port, timeout, retry);
		}
		else
		{
			m_bCapSock = new bCapSocketTCP(host, port, timeout);
		}
	}
	
	protected boolean Connect(String strController, String strProvider, String strMachine, String strOption)
	{
		if(!m_bCapSock.IsConnected()){
			m_hr = -1;
			return false;
		}
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(3); // Controller_Connect
		
		VARIANT[] vnt = new VARIANT[4];
		vnt[0] = new VARIANT(VARENUM.VT_BSTR, strController);
		vnt[1] = new VARIANT(VARENUM.VT_BSTR, strProvider);
		vnt[2] = new VARIANT(VARENUM.VT_BSTR, strMachine);
		vnt[3] = new VARIANT(VARENUM.VT_BSTR, strOption);
		
		for(int i = 0; i < vnt.length; i++)
		{
			msg.AddVariant(vnt[i]);
		}
		
		msg = m_bCapSock.SendMessage(msg);

		m_hr = msg.GetID();

		if(msg.SizeVariant() == 1 && msg.GetVariant(0).VariantGetType() == VARENUM.VT_I4)
		{
			m_iHandle = (Integer) msg.GetVariant(0).VariantGetObject();
			
			Init();
			
			return true;
		}
		
		return false;
	}
	
	private void Init()
	{
		m_exts = new bCapExtensions(m_iHandle, m_bCapSock);
		m_files = new bCapFiles(m_iHandle, m_bCapSock, 0);
		m_robots = new bCapRobots(m_iHandle, m_bCapSock);
		m_tasks = new bCapTasks(m_iHandle, m_bCapSock);
		m_vars = new bCapVariables(m_iHandle, m_bCapSock, 0);
		m_cmds = new bCapCommands(m_iHandle, m_bCapSock);
	}
	
	protected void Disconnect()
	{
		// Clear bCap Objects
		m_exts.Clear();
		m_files.Clear();
		m_robots.Clear();
		m_tasks.Clear();
		m_vars.Clear();
		m_cmds.Clear();
		
		// Disconnect bCapController
		bCapPacket msg = new bCapPacket();
		msg.SetID(4); // Controller_Disconnect
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);			
		msg.AddVariant(vnt);
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
		
		m_bCapSock.Release();
	}
	
	public VARIANT get_Name()
	{
		VARIANT vntReturn = new VARIANT(VARENUM.VT_BSTR, "");
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(21); // Controller_GetName
		
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
		msg.SetID(19); // Controller_GetAttribute
		
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
	
	public String get_Help()
	{
		String strReturn = "";
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(20); // Controller_GetHelp
		
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
	
	public VARIANT get_ExtensionNames(String strOption)
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(11); // Controller_GetExtensionNames
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		msg.AddVariant(vnt);
		
		VARIANT vntStrOption = new VARIANT(VARENUM.VT_BSTR, strOption);
		msg.AddVariant(vntStrOption);
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
		
		if(msg.SizeVariant() == 1)
		{
			vntReturn = msg.GetVariant(0);
		}
		
		return vntReturn;		
	}
	
	public bCapExtensions get_Extensions()
	{
		return m_exts;
	}
	
	public VARIANT get_FileNames(String strOption)
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(12); // Controller_GetFileNames
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		msg.AddVariant(vnt);
		
		VARIANT vntStrOption = new VARIANT(VARENUM.VT_BSTR, strOption);
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
	
	public VARIANT get_RobotNames(String strOption)
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(13); // Controller_GetRobotNames
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		msg.AddVariant(vnt);
		
		VARIANT vntStrOption = new VARIANT(VARENUM.VT_BSTR, strOption);
		msg.AddVariant(vntStrOption);
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
		
		if(msg.SizeVariant() == 1)
		{
			vntReturn = msg.GetVariant(0);
		}
		
		return vntReturn;		
	}
	
	public bCapRobots get_Robots()
	{
		return m_robots;
	}
	
	public VARIANT get_TaskNames(String strOption)
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(14); // Controller_GetTaskNames
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		msg.AddVariant(vnt);
		
		VARIANT vntStrOption = new VARIANT(VARENUM.VT_BSTR, strOption);
		msg.AddVariant(vntStrOption);
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
		
		if(msg.SizeVariant() == 1)
		{
			vntReturn = msg.GetVariant(0);
		}
		
		return vntReturn;		
	}
	
	public bCapTasks get_Tasks()
	{
		return m_tasks;
	}
	
	public VARIANT get_VariableNames(String strOption)
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(15); // Controller_GetVariableNames
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		msg.AddVariant(vnt);
		
		VARIANT vntStrOption = new VARIANT(VARENUM.VT_BSTR, strOption);
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
	
	public VARIANT get_CommandNames(String strOption)
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(16); // Controller_GetCommandNames
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		msg.AddVariant(vnt);
		
		VARIANT vntStrOption = new VARIANT(VARENUM.VT_BSTR, strOption);
		msg.AddVariant(vntStrOption);
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
		
		if(msg.SizeVariant() == 1)
		{
			vntReturn = msg.GetVariant(0);
		}
		
		return vntReturn;		
	}
	
	public bCapCommands get_Commands()
	{
		return m_cmds;
	}
	
	public VARIANT get_Tag()
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(22); // Controller_GetTag
		
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
		msg.SetID(23); // Controller_PutTag
		
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
		msg.SetID(24); // Controller_GetID
		
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
		msg.SetID(25); // Controller_PutID
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		msg.AddVariant(vnt);
		
		msg.AddVariant(newVal);
		
		msg = m_bCapSock.SendMessage(msg);
		
		m_hr = msg.GetID();
	}
	
	public bCapExtension AddExtension(String strName, String strOption)
	{	
		bCapExtension ext = m_exts.Add(strName, strOption);
		m_hr = m_exts.HRESULT();
		
		return ext;
	}
	
	public bCapFile AddFile(String strName, String strOption)
	{
		bCapFile file = m_files.Add(strName, strOption);
		m_hr = m_files.HRESULT();
		
		return file;
	}
	
	public bCapRobot AddRobot(String strName, String strOption)
	{
		bCapRobot robot = m_robots.Add(strName, strOption);
		m_hr = m_robots.HRESULT();
		
		return robot;
	}
		
	public bCapTask AddTask(String strName, String strOption)
	{
		bCapTask task = m_tasks.Add(strName, strOption);
		m_hr = m_tasks.HRESULT();
		
		return task;
	}
		
	public bCapVariable AddVariable(String strName, String strOption)
	{	
		bCapVariable var = m_vars.Add(strName, strOption);
		m_hr = m_vars.HRESULT();
		
		return var;
	}
	
	public bCapCommand AddCommand(String strName, String strOption)
	{	
		bCapCommand cmd = m_cmds.Add(strName, strOption);
		m_hr = m_cmds.HRESULT();
		
		return cmd;
	}	
	
	public VARIANT Execute(String strCommand, VARIANT vntParam)
	{
		VARIANT vntReturn = new VARIANT();
		
		bCapPacket msg = new bCapPacket();
		msg.SetID(17); // Controller_Execute
		
		VARIANT vnt = new VARIANT(VARENUM.VT_I4, m_iHandle);
		msg.AddVariant(vnt);
		
		VARIANT vntCommand = new VARIANT(VARENUM.VT_BSTR, strCommand);
		msg.AddVariant(vntCommand);

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
