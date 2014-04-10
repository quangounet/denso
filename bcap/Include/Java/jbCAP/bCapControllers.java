package jbCAP;

/** @file bCapControllers.java
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
import java.util.ArrayList;

public class bCapControllers {
	private int m_hr;
	private ArrayList<bCapController> m_ctrl;
	
	protected bCapControllers()
	{
		m_hr = 0;
		m_ctrl = new ArrayList<bCapController>();
	}
	
	public bCapController Item(int index)
	{
		bCapController ctrl = null;
		
		if(0 <= index && index < m_ctrl.size())
		{
			ctrl = m_ctrl.get(index);
		}
		
		return ctrl;
	}
	
	public bCapController Item(VARIANT index)
	{
		bCapController ctrl = null;
		
		if(index.VariantGetType() == VARENUM.VT_BSTR)
		{
			for(int i = 0; i < m_ctrl.size(); i++)
			{
				if(((String)m_ctrl.get(i).get_Name().VariantGetObject()).equals((String) index.VariantGetObject()))
					ctrl = m_ctrl.get(i);
			}
		}
		
		return ctrl;
	}
	
	public int get_Count()
	{
		return m_ctrl.size();
	}
	
	public bCapController Add(String host, int port, boolean UDP, int timeout, int retry, String strController, String strProvider, String strMachine, String strOption)
	{
		bCapController ctrl = new bCapController(host, port, UDP, timeout, retry);
		
		if(ctrl.Connect(strController, strProvider, strMachine, strOption))
		{
			m_ctrl.add(ctrl);
			m_hr = ctrl.HRESULT();
			
			return ctrl;
		}
		else
		{
			m_hr = ctrl.HRESULT();
			
			return null;
		}
	}
	
	public void Remove(int index)
	{
		if(0 <= index && index < m_ctrl.size())
		{
			m_ctrl.get(index).Disconnect();
			m_hr = m_ctrl.get(index).HRESULT();
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
			for(int i = 0; i < m_ctrl.size(); i++)
			{
				if(((String) m_ctrl.get(i).get_Name().VariantGetObject()).equals((String) index.VariantGetObject()))
				{
					m_ctrl.get(i).Disconnect();
					m_hr = m_ctrl.get(i).HRESULT();
					
					m_ctrl.remove(i);
					break;
				}
			}
		}
	}
	
	public void Clear()
	{
		for(int i = 0; i < m_ctrl.size(); i++)
		{
			m_ctrl.get(i).Disconnect();
		}
		m_ctrl.clear();
		
		m_hr = 0;
	}
	
	public VARIANT IsMember(VARIANT index)
	{
		VARIANT vntReturn = new VARIANT(VARENUM.VT_BOOL, false);
		
		if(index.VariantGetType() == VARENUM.VT_BSTR)
		{
			for(int i = 0; i < m_ctrl.size(); i++)
			{
				if(((String) m_ctrl.get(i).get_Name().VariantGetObject()).equals((String) index.VariantGetObject()))
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
