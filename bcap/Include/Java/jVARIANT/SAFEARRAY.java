package jVARIANT;

/** @file SAFEARRAY.java
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

public final class SAFEARRAY
{
	private int vt;
	private Object objArray[];
	
	public SAFEARRAY(int iNum)
	{
		vt = VARENUM.VT_EMPTY;
		
		if(iNum > 0)
		{
			objArray = new Object[iNum];
		}
		else
		{
			objArray = null;
		}
	}
	
	public SAFEARRAY(int inputvt, Object[] inputobjArray)
	{
		if(inputobjArray != null && inputobjArray.length > 0)
		{
			SafeArrayPutType(inputvt);
			objArray = inputobjArray;
		}
		else
		{
			vt = VARENUM.VT_EMPTY;
			objArray = null;
		}
	}
	
	public void SafeArrayPutType(int iType)
	{
		if(VARENUM.IsVARENUM(iType) && ((iType & VARENUM.VT_ARRAY) == 0))
		{
			vt = iType;
		}
		else
		{
			vt = VARENUM.VT_EMPTY;
		}
	}
	
	public void SafeArrayPutElement(int index, Object obj)
	{
		if(objArray != null && ((0 <= index) && (index < objArray.length)))
		{
			objArray[index] = obj;
		}
	}
	
	public int SafeArrayGetType()
	{
		return vt;
	}
	
	public int SafeArrayGetElemsize()
	{
		if(objArray != null)
		{
			return objArray.length;
		}
		else
		{
			return 0;
		}
	}
	
	public Object SafeArrayGetElement(int index)
	{
		if(objArray != null && ((0 <= index) && (index < objArray.length)))
		{
			return objArray[index];
		}
		else
		{
			return null;
		}
	}
	
	public Object[] SafeArrayGetArray()
	{
		return objArray;
	}
}
