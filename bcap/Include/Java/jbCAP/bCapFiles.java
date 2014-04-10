package jbCAP;

/** @file bCapFiles.java
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

public class bCapFiles {
	private int m_hr;
	private int m_iParent;
	private int m_iHandle;
	private bCapSocket m_bCapSock;
	private ArrayList<bCapFile> m_file;
	
	protected bCapFiles(int iHandle, bCapSocket sock, int iParent)
	{
		m_hr = 0;
		m_iParent = iParent;
		m_iHandle = iHandle;
		m_bCapSock = sock;
		m_file = new ArrayList<bCapFile>();
	}
	
	public bCapFile Item(int index)
	{
		bCapFile file = null;
		
		if(0 <= index && index < m_file.size())
		{
			file = m_file.get(index);
		}
		
		return file;
	}
	
	public bCapFile Item(VARIANT index)
	{
		bCapFile file = null;
		
		if(index.VariantGetType() == VARENUM.VT_BSTR)
		{
			for(int i = 0; i < m_file.size(); i++)
			{
				if(((String)m_file.get(i).get_Name().VariantGetObject()).equals((String) index.VariantGetObject()))
					file = m_file.get(i);
			}
		}
		
		return file;
	}
	
	public int get_Count()
	{
		return m_file.size();
	}
	
	public bCapFile Add(String strName, String strOption)
	{
		bCapPacket msg = new bCapPacket();
		
		if(m_iParent == 0){
			msg.SetID(6); // Controller_GetFile
		}else{
			msg.SetID(37); // File_GetFile
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
			bCapFile file = new bCapFile(strName, strOption, (Integer) msg.GetVariant(0).VariantGetObject(), m_bCapSock);
			
			m_file.add(file);
			
			return file;
		}
		else
		{
			return null;
		}
	}
	
	public void Remove(int index)
	{
		if(0 <= index && index < m_file.size())
		{
			m_file.get(index).Disconnect();
			m_hr = m_file.get(index).HRESULT();
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
			for(int i = 0; i < m_file.size(); i++)
			{
				if(((String) m_file.get(i).get_Name().VariantGetObject()).equals((String) index.VariantGetObject()))
				{
					m_file.get(i).Disconnect();
					m_hr = m_file.get(i).HRESULT();
					
					m_file.remove(i);
					break;
				}
			}
		}
	}
	
	public void Clear()
	{
		for(int i = 0; i < m_file.size(); i++)
		{
			m_file.get(i).Disconnect();
		}
		m_file.clear();
		
		m_hr = 0;
	}
	
	public VARIANT IsMember(VARIANT index)
	{
		VARIANT vntReturn = new VARIANT(VARENUM.VT_BOOL, false);
		
		if(index.VariantGetType() == VARENUM.VT_BSTR)
		{
			for(int i = 0; i < m_file.size(); i++)
			{
				if(((String) m_file.get(i).get_Name().VariantGetObject()).equals((String) index.VariantGetObject()))
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
