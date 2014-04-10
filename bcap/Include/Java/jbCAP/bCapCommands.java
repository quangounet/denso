package jbCAP;

/** @file bCapCommands.java
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

import java.util.ArrayList;

public class bCapCommands {
	private int m_hr;
	private int m_iHandle;
	private bCapSocket m_bCapSock;
	private ArrayList<bCapCommand> m_cmd;
	
	protected bCapCommands(int iHandle, bCapSocket sock)
	{
		m_hr = 0;
		m_iHandle = iHandle;
		m_bCapSock = sock;
		m_cmd = new ArrayList<bCapCommand>();
	}
	
	public bCapCommand Item(int index)
	{
		bCapCommand cmd = null;
		
		if(0 <= index && index < m_cmd.size())
		{
			cmd = m_cmd.get(index);
		}
		
		return cmd;
	}
	
	public bCapCommand Item(VARIANT index)
	{
		bCapCommand cmd = null;
		
		if(index.VariantGetType() == VARENUM.VT_BSTR)
		{
			for(int i = 0; i < m_cmd.size(); i++)
			{
				if(((String)m_cmd.get(i).get_Name().VariantGetObject()).equals((String) index.VariantGetObject()))
					cmd = m_cmd.get(i);
			}
		}
		
		return cmd;
	}
	
	public int get_Count()
	{
		return m_cmd.size();
	}
	
	public bCapCommand Add(String strName, String strOption)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(10); // Controller_GetCommand
		
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

		if(msg.SizeVariant() == 1 && msg.GetVariant(0).VariantGetType() == VARENUM.VT_I4)
		{
			bCapCommand cmd = new bCapCommand(strName, strOption, (Integer) msg.GetVariant(0).VariantGetObject(), m_bCapSock);
			
			m_cmd.add(cmd);
			
			return cmd;
		}
		else
		{
			return null;
		}
	}
	
	public void Remove(int index)
	{
		if(0 <= index && index < m_cmd.size())
		{
			m_cmd.get(index).Disconnect();
			m_hr = m_cmd.get(index).HRESULT();
		}
		else
		{
			m_hr = -1;
		}
	}
	
	public void Remove(VARIANT index)
	{
		m_hr = -1;
		
		if(index.VariantGetType() == VARENUM.VT_BSTR)
		{
			for(int i = 0; i < m_cmd.size(); i++)
			{
				if(((String) m_cmd.get(i).get_Name().VariantGetObject()).equals((String) index.VariantGetObject()))
				{
					m_cmd.get(i).Disconnect();
					m_hr = m_cmd.get(i).HRESULT();
					
					m_cmd.remove(i);
					break;
				}
			}
		}
	}
	
	public void Clear()
	{
		for(int i = 0; i < m_cmd.size(); i++)
		{
			m_cmd.get(i).Disconnect();
		}
		m_cmd.clear();
		
		m_hr = 0;
	}
	
	public VARIANT IsMember(VARIANT index)
	{
		VARIANT vntReturn = new VARIANT(VARENUM.VT_BOOL, false);
		
		if(index.VariantGetType() == VARENUM.VT_BSTR)
		{
			for(int i = 0; i < m_cmd.size(); i++)
			{
				if(((String) m_cmd.get(i).get_Name().VariantGetObject()).equals((String) index.VariantGetObject()))
				{
					vntReturn.VariantPutObject(true);
					break;
				}
			}
		}
		
		return vntReturn;
	}
	
	public int HRESULT()
	{
		return m_hr;
	}
}
