package jVARIANT;

/** @file VARIANT.java
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

public final class VARIANT
{
	private int vt;
	private Object objVal;
	
	public VARIANT()
	{
		vt = VARENUM.VT_EMPTY;
		objVal = null;
	}
	
	public VARIANT(int inputvt, Object inputobjVal)
	{
		VariantPutType(inputvt);
		objVal = inputobjVal;
	}
	
	public void VariantPutType(int iType)
	{
		if(VARENUM.IsVARENUM(iType) && (iType != VARENUM.VT_VARIANT))
		{
			vt = iType;
		}
		else
		{
			vt = VARENUM.VT_EMPTY;
		}
	}
	
	public void VariantPutObject(Object obj)
	{
		objVal = obj;
	}
	
	public int VariantGetType()
	{
		return vt;
	}
	
	public Object VariantGetObject()
	{
		return objVal;
	}
}

