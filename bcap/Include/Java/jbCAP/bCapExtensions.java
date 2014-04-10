package jbCAP;

/** @file bCapExtensions.java
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

public class bCapExtensions {
	private int m_hr;
	private int m_iHandle;
	private bCapSocket m_bCapSock;
	private ArrayList<bCapExtension> m_ext;
	
	protected bCapExtensions(int iHandle, bCapSocket sock)
	{
		m_hr = 0;
		m_iHandle = iHandle;
		m_bCapSock = sock;
		m_ext = new ArrayList<bCapExtension>();
	}
	
	public bCapExtension Item(int index)
	{
		bCapExtension ext = null;
		
		if(0 <= index && index < m_ext.size())
		{
			ext = m_ext.get(index);
		}
		
		return ext;
	}
	
	public bCapExtension Item(VARIANT index)
	{
		bCapExtension ext = null;
		
		if(index.VariantGetType() == VARENUM.VT_BSTR)
		{
			for(int i = 0; i < m_ext.size(); i++)
			{
				if(((String)m_ext.get(i).get_Name().VariantGetObject()).equals((String) index.VariantGetObject()))
					ext = m_ext.get(i);
			}
		}
		
		return ext;
	}
	
	public int get_Count()
	{
		return m_ext.size();
	}
	
	public bCapExtension Add(String strName, String strOption)
	{
		bCapPacket msg = new bCapPacket();
		msg.SetID(5); // Controller_GetExtension
		
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
			bCapExtension ext = new bCapExtension(strName, strOption, (Integer) msg.GetVariant(0).VariantGetObject(), m_bCapSock);
			
			m_ext.add(ext);
			
			return ext;
		}
		else
		{
			return null;
		}
	}
	
	public void Remove(int index)
	{
		if(0 <= index && index < m_ext.size())
		{
			m_ext.get(index).Disconnect();
			m_hr = m_ext.get(index).HRESULT();
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
			for(int i = 0; i < m_ext.size(); i++)
			{
				if(((String) m_ext.get(i).get_Name().VariantGetObject()).equals((String) index.VariantGetObject()))
				{
					m_ext.get(i).Disconnect();
					m_hr = m_ext.get(i).HRESULT();
					
					m_ext.remove(i);
					break;
				}
			}
		}
	}
	
	public void Clear()
	{
		for(int i = 0; i < m_ext.size(); i++)
		{
			m_ext.get(i).Disconnect();
		}
		m_ext.clear();
		
		m_hr = 0;
	}
	
	public VARIANT IsMember(VARIANT index)
	{
		VARIANT vntReturn = new VARIANT(VARENUM.VT_BOOL, false);
		
		if(index.VariantGetType() == VARENUM.VT_BSTR)
		{
			for(int i = 0; i < m_ext.size(); i++)
			{
				if(((String) m_ext.get(i).get_Name().VariantGetObject()).equals((String) index.VariantGetObject()))
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
