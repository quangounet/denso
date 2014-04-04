package jbCAP;

/** @file bCapEngine.java
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

public class bCapEngine {
	private int m_hr;
	private bCapControllers m_ctrls;
	
	public bCapEngine()
	{
		m_hr = 0;
		m_ctrls = new bCapControllers();
	}
		
	public void Release()
	{
		m_ctrls.Clear();
	}

	public bCapControllers get_Controllers()
	{
		return m_ctrls;
	}
	
	public bCapController AddController(String host, int port, boolean UDP, int timeout, int retry, String strController, String strProvider, String strMachine, String strOption)
	{
		if(m_ctrls != null)
		{
			bCapController ctrl = m_ctrls.Add(host, port, UDP, timeout, retry, strController, strProvider, strMachine, strOption);
			m_hr = m_ctrls.HRESULT();
			
			return ctrl;
		}
		else
		{
			m_hr = -1;
			return null;
		}
	}
	
	public int HRESULT()
	{
		return m_hr;
	}
}
