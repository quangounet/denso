package jVARIANT;

/** @file VARENUM.java
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

public final class VARENUM {
	  public static final int VT_EMPTY              = 0;
	  public static final int VT_NULL               = 1;
	  public static final int VT_ERROR              = 10;
	  public static final int VT_UI1                = 17;
	  public static final int VT_I2                 = 2;
	  public static final int VT_UI2                = 18;
	  public static final int VT_I4                 = 3;
	  public static final int VT_UI4                = 19;
	  public static final int VT_R4                 = 4;
	  public static final int VT_R8                 = 5;
	  public static final int VT_DATE               = 7;
	  public static final int VT_BOOL               = 11;
	  public static final int VT_BSTR               = 8;
	  public static final int VT_VARIANT            = 12;
	  public static final int VT_ARRAY              = 0x2000;
	  
	  public static boolean IsVARENUM(int iVAR)
	  {
		  switch(iVAR)
		  {
			  case VT_EMPTY:
			  case VT_NULL:
			  case VT_ERROR:
			  case VT_UI1:
			  case VT_ARRAY | VT_UI1:
			  case VT_I2:
			  case VT_ARRAY | VT_I2:
			  case VT_UI2:
			  case VT_ARRAY | VT_UI2:
			  case VT_I4:
			  case VT_ARRAY | VT_I4:
			  case VT_UI4:
			  case VT_ARRAY | VT_UI4:
			  case VT_R4:
			  case VT_ARRAY | VT_R4:
			  case VT_R8:
			  case VT_ARRAY | VT_R8:
			  case VT_DATE:
			  case VT_ARRAY | VT_DATE:
			  case VT_BOOL:
			  case VT_ARRAY | VT_BOOL:
			  case VT_BSTR:
			  case VT_ARRAY | VT_BSTR:
			  case VT_VARIANT:
			  case VT_ARRAY | VT_VARIANT:
				  return true;
			  default:
				  return false;
		  }
	  }
	  
	  public static Object MatchObject(int vt, Object objVal)
	  {
		  Object objRet = null;
		  
		  if((vt & VT_ARRAY) != 0)
		  {
			  if (objVal instanceof jVARIANT.SAFEARRAY && ((vt ^ VT_ARRAY) == ((SAFEARRAY) objVal).SafeArrayGetType()))
			  {
				  objRet = objVal;
			  }
		  }
		  else
		  {
			  if(objVal != null)
			  {			  
				  switch(vt)
				  {
					  case VT_EMPTY:
					  case VT_NULL:
					  case VT_ERROR:
						  break;
					  case VT_UI1:
						  try{
							  objRet = new Byte(objVal.toString());
						  }catch(NumberFormatException ignore){}
						  break;
					  case VT_I2:
					  case VT_UI2:
						  try{
							  objRet = new Short(objVal.toString());
						  }catch(NumberFormatException ignore){}
						  break;
					  case VT_I4:
					  case VT_UI4:
						  try{
							  objRet = new Integer(objVal.toString());
						  }catch(NumberFormatException ignore){}
						  break;
					  case VT_R4:
						  try{
							  objRet = new Float(objVal.toString());
						  }catch(NumberFormatException ignore){}
						  break;
					  case VT_R8:
						  try{
							  objRet = new Double(objVal.toString());
						  }catch(NumberFormatException ignore){}
						  break;
					  case VT_DATE:
						  if(objVal instanceof java.util.Date)
						  {
							  objRet = objVal;
						  }
						  break;
					  case VT_BOOL:
						  if(objVal instanceof java.lang.Boolean)
						  {
							  objRet = objVal;
						  }
						  break;
					  case VT_BSTR:
						  if(objVal instanceof String)
						  {
							  objRet = objVal;
						  }
						  break;
					  case VT_VARIANT:
						  if(objVal instanceof jVARIANT.VARIANT)
						  {
							  objRet = objVal;
						  }
						  break;
					  default:
						  break;
				  }
			  }
		  }
		  
		  return objRet;
	  }
}
