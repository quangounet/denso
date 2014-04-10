package jbCAP;

/** @file bCapPacket.java
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

import java.util.ArrayList;
import jVARIANT.*;

public final class bCapPacket {
	private short m_sSerial;
	private short m_sReserv;
	private int m_iID;
	private ArrayList<VARIANT> m_vnt;
	
	// Constructor
	public bCapPacket(){
		m_sSerial = 0;
		m_sReserv = 0;
		m_iID = 0;
		m_vnt = new ArrayList<VARIANT>();
	}
	
	public void SetSerial(short sValue){
		m_sSerial = sValue;
	}
	
	public short GetSerial(){
		return m_sSerial;
	}
	
	public void SetReserv(short sValue){
		m_sReserv = sValue;
	}
	
	public short GetReserv(){
		return m_sReserv;
	}
	
	public void SetID(int iID){
		m_iID = iID;
	}
	
	public int GetID(){
		return m_iID;
	}
	
	public void AddVariant(VARIANT vnt){
		if(vnt == null){
			m_vnt.add(new VARIANT());
		}else{
			m_vnt.add(vnt);
		}
	}
	
	public VARIANT GetVariant(int index){
		if(0 <= index && index < m_vnt.size()){
			return m_vnt.get(index);
		}else{
			return null;
		}
	}
	
	public int SizeVariant(){
		return m_vnt.size();
	}
	
	public void ClearVariant(){
		m_vnt.clear();
	}
}
