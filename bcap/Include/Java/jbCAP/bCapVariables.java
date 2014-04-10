package jbCAP;

/** @file bCapVariables.java
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

public class bCapVariables {
	private int m_hr;
	private int m_iParent;
	private int m_iHandle;
	private bCapSocket m_bCapSock;
	private ArrayList<bCapVariable> m_var;
	
	protected bCapVariables(int iHandle, bCapSocket sock, int iParent)
	{
		m_hr = 0;
		m_iParent = iParent;
		m_iHandle = iHandle;
		m_bCapSock = sock;
		m_var = new ArrayList<bCapVariable>();
	}
	
	public bCapVariable Item(int index)
	{
		bCapVariable var = null;
		
		if(0 <= index && index < m_var.size())
		{
			var = m_var.get(index);
		}
		
		return var;
	}
	
	public bCapVariable Item(VARIANT index)
	{
		bCapVariable var = null;
		
		if(index.VariantGetType() == VARENUM.VT_BSTR)
		{
			for(int i = 0; i < m_var.size(); i++)
			{
				if(((String)m_var.get(i).get_Name().VariantGetObject()).equals((String) index.VariantGetObject()))
					var = m_var.get(i);
			}
		}
		
		return var;
	}
	
	public int get_Count()
	{
		return m_var.size();
	}
	
	public bCapVariable Add(String strName, String strOption)
	{
		bCapPacket msg = new bCapPacket();

		switch(m_iParent)
		{
		case 0:
			msg.SetID(9); // Controller_GetVariable
			break;
		case 1:
			msg.SetID(26); // Extension_GetVariable
			break;
		case 2:
			msg.SetID(38); // File_GetVariable
			break;
		case 3:
			msg.SetID(62); // Robot_GetVariable
			break;
		case 4:
			msg.SetID(85); // Task_GetVariable
			break;
		default:
			return null;
		}

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
			bCapVariable var = new bCapVariable(strName, strOption, (Integer) msg.GetVariant(0).VariantGetObject(), m_bCapSock);
			
			m_var.add(var);
			
			return var;
		}
		else
		{
			return null;
		}
	}
	
	public void Remove(int index)
	{
		if(0 <= index && index < m_var.size())
		{
			m_var.get(index).Disconnect();
			m_hr = m_var.get(index).HRESULT();
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
			for(int i = 0; i < m_var.size(); i++)
			{
				if(((String) m_var.get(i).get_Name().VariantGetObject()).equals((String) index.VariantGetObject()))
				{
					m_var.get(i).Disconnect();
					m_hr = m_var.get(i).HRESULT();
					
					m_var.remove(i);
					break;
				}
			}
		}
	}
	
	public void Clear()
	{
		for(int i = 0; i < m_var.size(); i++)
		{
			m_var.get(i).Disconnect();
		}
		m_var.clear();
		
		m_hr = 0;
	}
	
	public VARIANT IsMember(VARIANT index)
	{
		VARIANT vntReturn = new VARIANT(VARENUM.VT_BOOL, false);
		
		if(index.VariantGetType() == VARENUM.VT_BSTR)
		{
			for(int i = 0; i < m_var.size(); i++)
			{
				if(((String) m_var.get(i).get_Name().VariantGetObject()).equals((String) index.VariantGetObject()))
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
