package jbCAP;

/** @file bCapConverter.java
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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import jVARIANT.*;

public final class bCapConverter {
	// Message size offset
	private static final int m_iDefaultSize = 16;
	private static final int m_iReserveSize = 0;
	
	// {"Message Length", "Serial Number", "Reservation Area", "Function ID or Return Code", "Number of Arguments", "Arguments"}
	private static final int[] m_iMsgPoint = new int[]{1, 5, 7, 9, 13, 15};
	
	// {"Data Type", "Number of Data", "Data"}
	private static final int[] m_iArgPoint = new int[]{4, 6, 10};
	
	// Byte order
	private static final ByteOrder m_bo = ByteOrder.LITTLE_ENDIAN;
	
	// Byte order for String
	private static final String	m_strbo = "UTF-16LE";
	
	/*
	 * Decode byte[] to bCapPacket
	 */
	public static bCapPacket Decode(byte[] bin) throws Throwable
	{
		if(bin == null){
			throw new NullPointerException();
		}
		
		if((bin.length < m_iDefaultSize + m_iReserveSize)){
			throw new IllegalAccessException();
		}
		
		int iLength = ByteBuffer.wrap(bin, m_iMsgPoint[0], m_iMsgPoint[1] - m_iMsgPoint[0]).order(m_bo).getInt();
		if((bin[0] != 0x1) || (bin[iLength - 1] != 0x4)){
			throw new ClassNotFoundException();
		}

		bCapPacket msgRet = new bCapPacket();
		
		// Serial number
		msgRet.SetSerial(ByteBuffer.wrap(bin, m_iMsgPoint[1], m_iMsgPoint[2] - m_iMsgPoint[1]).order(m_bo).getShort());
		
		// Reservation area
		msgRet.SetReserv(ByteBuffer.wrap(bin, m_iMsgPoint[2], m_iMsgPoint[3] - m_iMsgPoint[2]).order(m_bo).getShort());
	
		// Return code
		msgRet.SetID(ByteBuffer.wrap(bin, m_iMsgPoint[3], m_iMsgPoint[4] - m_iMsgPoint[3]).order(m_bo).getInt());
		
		// Number of arguments
		int m_iDataNum = ByteBuffer.wrap(bin, m_iMsgPoint[4], m_iMsgPoint[5] - m_iMsgPoint[4]).order(m_bo).getShort();
		
		// Arguments
		int[] m_iDataSize;
		if(m_iDataNum != 0)
		{
			int ipoint = 0;
			m_iDataSize = new int[m_iDataNum];
			for(int i = 0; i < m_iDataNum; i++)
			{
				if(i == 0)
				{
					ipoint = m_iMsgPoint[5];
				}
				else
				{
					ipoint += m_iDataSize[i - 1];
				}
				
				// Size of argument
				m_iDataSize[i] = ByteBuffer.wrap(bin, ipoint, m_iArgPoint[0]).order(m_bo).getInt() + 4;
				
				int iTemp;
				VARIANT vntTemp = new VARIANT();
				
				// Data Type
				iTemp = (int) ByteBuffer.wrap(bin, ipoint + m_iArgPoint[0], m_iArgPoint[1] - m_iArgPoint[0]).order(m_bo).getShort();
				vntTemp.VariantPutType(iTemp);
				
				// Number of Data
				iTemp = ByteBuffer.wrap(bin, ipoint + m_iArgPoint[1], m_iArgPoint[2] - m_iArgPoint[1]).order(m_bo).getInt();
				vntTemp.VariantPutObject(byte2VARIANT(ipoint + m_iArgPoint[2], iTemp, vntTemp.VariantGetType(), bin));
				
				msgRet.AddVariant(vntTemp);
			}
		}
		
		return msgRet;
	}
	
	/*
	 * byte2VARIANT
	 * 
	 * Arguments
	 * 	ipoint:	Index of bin
	 * 	iNum:	Number of Data
	 * 	vt:		Data Type
	 * 	bin:	Message to Decode
	 * 
	 * Return
	 *  objRet:	Decoded Object
	 */
	private static Object byte2VARIANT(int ipoint, int iNum, int vt, byte[] bin) throws Throwable
	{
		int i;
		Object objRet = null;
		
		if((vt & VARENUM.VT_ARRAY) != 0)
		{
			SAFEARRAY aryTemp = new SAFEARRAY(iNum);
			aryTemp.SafeArrayPutType(vt ^ VARENUM.VT_ARRAY);
			
			for(i = 0; i < iNum; i++)
			{
				aryTemp.SafeArrayPutElement(i, byte2VARIANT(ipoint, 1, aryTemp.SafeArrayGetType(), bin));
				ipoint += VARIANT2Size(vt ^ VARENUM.VT_ARRAY, aryTemp.SafeArrayGetElement(i));
			}
			
			objRet = aryTemp;
		}
		else
		{
			switch(vt)
			{
				case VARENUM.VT_EMPTY:
				case VARENUM.VT_NULL:
				case VARENUM.VT_ERROR:
					// return null
					break;
				case VARENUM.VT_UI1:
					objRet = bin[ipoint];
					break;
				case VARENUM.VT_I2:
				case VARENUM.VT_UI2:
					objRet = ByteBuffer.wrap(bin, ipoint, 2).order(m_bo).getShort();
					break;
				case VARENUM.VT_I4:
				case VARENUM.VT_UI4:
					objRet = ByteBuffer.wrap(bin, ipoint, 4).order(m_bo).getInt();
					break;
				case VARENUM.VT_R4:
					objRet = ByteBuffer.wrap(bin, ipoint, 4).order(m_bo).getFloat();
					break;
				case VARENUM.VT_R8:
					objRet = ByteBuffer.wrap(bin, ipoint, 8).order(m_bo).getDouble();
					break;
				case VARENUM.VT_DATE:
					objRet = ByteBuffer.wrap(bin, ipoint, 8).order(m_bo).getDouble();
					objRet = DateConverter.CDate2JDate((Double) objRet);
					break;
				case VARENUM.VT_BOOL:
					objRet = ByteBuffer.wrap(bin, ipoint, 2).order(m_bo).getShort();
					if((Short) objRet == 0)
					{
						objRet = false;
					}
					else
					{
						objRet = true;
					}
					break;
				case VARENUM.VT_BSTR:
					objRet = ByteBuffer.wrap(bin, ipoint, 4).order(m_bo).getInt();
					
					byte[] bTemp = new byte[(Integer) objRet];
					for(i = 0; i < bTemp.length; i++)
					{
						bTemp[i] = bin[ipoint + 4 + i];
					}
					
					try{
						objRet = new String(bTemp, m_strbo);
					}catch(UnsupportedEncodingException e){
						objRet = null;
					}
					
					break;
				case VARENUM.VT_VARIANT:
					VARIANT vntTemp = new VARIANT();

					objRet = (int) ByteBuffer.wrap(bin, ipoint, 2).order(m_bo).getShort();
					vntTemp.VariantPutType((Integer) objRet);
					
					objRet = ByteBuffer.wrap(bin, ipoint + 2, 4).order(m_bo).getInt();					
					vntTemp.VariantPutObject(byte2VARIANT(ipoint + 6, (Integer) objRet, vntTemp.VariantGetType(), bin));
					
					objRet = vntTemp;
					
					break;
				default:
			}
		}
		
		return objRet;
	}
	
	/*
	 * Encode bCapPacket to byte[]
	 */
	public static byte[] Encode(bCapPacket msg) throws Throwable
	{
		if(msg == null){
			throw new NullPointerException();
		}
		
		int i;
		int iDataNum = msg.SizeVariant();
		int[] iDataSize = GetDataSize(msg, iDataNum);
		
		// Calculate Message Size
		int iSize = m_iDefaultSize + m_iReserveSize;
		if(iDataNum != 0)
		{
			for(i = 0; i < iDataSize.length; i++)
			{
				iSize += iDataSize[i];
			}
		}
		
		byte[] bRet = new byte[iSize];
		
		// SOH
		bRet[0] = 0x1;
		
		// Message Length
		ByteBuffer.wrap(bRet, m_iMsgPoint[0], m_iMsgPoint[1] - m_iMsgPoint[0]).order(m_bo).putInt(iSize);
	
		// Serial Number
		ByteBuffer.wrap(bRet, m_iMsgPoint[1], m_iMsgPoint[2] - m_iMsgPoint[1]).order(m_bo).putShort(msg.GetSerial());

		// Reservation Area
		ByteBuffer.wrap(bRet, m_iMsgPoint[2], m_iMsgPoint[3] - m_iMsgPoint[2]).order(m_bo).putShort(msg.GetReserv());
		
		// Function ID
		ByteBuffer.wrap(bRet, m_iMsgPoint[3], m_iMsgPoint[4] - m_iMsgPoint[3]).order(m_bo).putInt(msg.GetID());
		
		// Number of Arguments
		ByteBuffer.wrap(bRet, m_iMsgPoint[4], m_iMsgPoint[5] - m_iMsgPoint[4]).order(m_bo).putShort((short) iDataNum);
		
		// Arguments
		if(iDataNum != 0)
		{
			int ipoint = 0;
			for(i = 0; i < iDataSize.length; i++)
			{
				if(i == 0)
				{
					ipoint = m_iMsgPoint[5];
				}
				else
				{
					ipoint += iDataSize[i - 1];
				}
				
				// Size of Argument
				ByteBuffer.wrap(bRet, ipoint, m_iArgPoint[0]).order(m_bo).putInt(iDataSize[i] - m_iArgPoint[0]);
	
				// Data Type
				ByteBuffer.wrap(bRet, ipoint + m_iArgPoint[0], m_iArgPoint[1] - m_iArgPoint[0]).order(m_bo).putShort((short) msg.GetVariant(i).VariantGetType());
	
				// Number of Data
				if(((msg.GetVariant(i).VariantGetType() & VARENUM.VT_ARRAY) != 0) && (msg.GetVariant(i).VariantGetObject() != null))
				{
					ByteBuffer.wrap(bRet, ipoint + m_iArgPoint[1], m_iArgPoint[2] - m_iArgPoint[1]).order(m_bo).putInt(((SAFEARRAY)(msg.GetVariant(i).VariantGetObject())).SafeArrayGetElemsize());
				}
				else
				{
					ByteBuffer.wrap(bRet, ipoint + m_iArgPoint[1], m_iArgPoint[2] - m_iArgPoint[1]).order(m_bo).putInt(1);
				}
	
				bRet = VARIANT2byte(ipoint + m_iArgPoint[2], bRet, msg.GetVariant(i).VariantGetType(), msg.GetVariant(i).VariantGetObject());
			}
		}
		
		// Reservation Area
		for(i = bRet.length - m_iReserveSize - 1; i < bRet.length - 1; i++)
		{
			bRet[i] = 0x0;
		}
		
		// EOH
		bRet[bRet.length - 1] = 0x4;
		
		return bRet;
	}
	
	/*
	 * VARIANT2byte
	 * 
	 * Arguments
	 * 	ipoint:	Index of bin
	 *  bin:	Message to Encode
	 * 	vt:		Data Type
	 * 	objVal:	VARIANT Data
	 * 
	 * Return
	 *  bin:	Encoded byte
	 */
	private static byte[] VARIANT2byte(int ipoint, byte[] bin, int vt, Object objVal) throws Throwable
	{
		int i;
		
		if(objVal != null)
		{
			if((vt & VARENUM.VT_ARRAY) != 0)
			{
				for(i = 0; i < ((SAFEARRAY)objVal).SafeArrayGetElemsize(); i++)
				{
					bin = VARIANT2byte(ipoint, bin, vt ^ VARENUM.VT_ARRAY, ((SAFEARRAY) objVal).SafeArrayGetElement(i));
					ipoint += VARIANT2Size(vt ^ VARENUM.VT_ARRAY, ((SAFEARRAY) objVal).SafeArrayGetElement(i));
				}
			}
			else
			{
				switch(vt)
				{
					case VARENUM.VT_EMPTY:
					case VARENUM.VT_NULL:
					case VARENUM.VT_ERROR:
						// Do nothing
						break;
					case VARENUM.VT_UI1:
						bin[ipoint] = (Byte) objVal;
						break;
					case VARENUM.VT_I2:
					case VARENUM.VT_UI2:
						ByteBuffer.wrap(bin, ipoint, 2).order(m_bo).putShort((Short) objVal);
						break;
					case VARENUM.VT_I4:
					case VARENUM.VT_UI4:
						ByteBuffer.wrap(bin, ipoint, 4).order(m_bo).putInt((Integer) objVal);
						break;
					case VARENUM.VT_R4:
						ByteBuffer.wrap(bin, ipoint, 4).order(m_bo).putFloat((Float) objVal);
						break;
					case VARENUM.VT_R8:
						ByteBuffer.wrap(bin, ipoint, 8).order(m_bo).putDouble((Double) objVal);
						break;
					case VARENUM.VT_DATE:
						ByteBuffer.wrap(bin, ipoint, 8).order(m_bo).putDouble(DateConverter.JDate2CDate((java.util.Date) objVal));
						break;
					case VARENUM.VT_BOOL:
						if((Boolean) objVal)
						{
							ByteBuffer.wrap(bin, ipoint, 2).order(m_bo).putShort((short) -1);
						}
						else
						{
							ByteBuffer.wrap(bin, ipoint, 2).order(m_bo).putShort((short) 0);
						}
						break;
					case VARENUM.VT_BSTR:
						ByteBuffer.wrap(bin, ipoint, 4).order(m_bo).putInt(2 * ((String) objVal).length());
						
						byte[] bTemp;
						
						try {
							bTemp = ((String) objVal).getBytes(m_strbo);
						} catch (UnsupportedEncodingException e) {
							bTemp = new byte[((String) objVal).length()];
						}
	
						for(i = 0; i < bTemp.length; i++)
						{
							bin[ipoint + 4 + i] = bTemp[i];
						}
						break;
					case VARENUM.VT_VARIANT:
						ByteBuffer.wrap(bin, ipoint, 2).order(m_bo).putShort((short) ((VARIANT) objVal).VariantGetType());
						
						if(((((VARIANT) objVal).VariantGetType() & VARENUM.VT_ARRAY) != 0) && (((VARIANT) objVal).VariantGetObject() != null))
						{
							ByteBuffer.wrap(bin, ipoint + 2, 4).order(m_bo).putInt(((SAFEARRAY)((VARIANT) objVal).VariantGetObject()).SafeArrayGetElemsize());
						}
						else
						{
							ByteBuffer.wrap(bin, ipoint + 2, 4).order(m_bo).putInt(1);
						}
						
						bin = VARIANT2byte(ipoint + 6, bin, ((VARIANT)objVal).VariantGetType(), ((VARIANT)objVal).VariantGetObject());
						
						break;
					default:
				}
			}
		}
		
		return bin;
	}
	
	// Get Arguments Size
	private static int[] GetDataSize(bCapPacket msg, int iDataNum)
	{
		int[] iDataSize;
		
		if(msg.SizeVariant() == 0)
		{
			iDataSize = null;
		}
		else
		{
			iDataSize = new int[iDataNum];
			for(int i = 0; i < iDataSize.length; i++)
			{
				VARIANT vntTemp = msg.GetVariant(i);
				iDataSize[i] = 4 + 2 + 4 + VARIANT2Size(vntTemp.VariantGetType(), vntTemp.VariantGetObject());
			}
		}
		
		return iDataSize;
	}
	
	// Get VARIANT Size
	private static int VARIANT2Size(int vt, Object objVal)
	{
		int iRet = 0;
		
		if((vt & VARENUM.VT_ARRAY) != 0)
		{
			if(objVal != null){
				for(int i = 0; i < ((SAFEARRAY) objVal).SafeArrayGetElemsize(); i++)
				{
					iRet += VARIANT2Size(vt ^ VARENUM.VT_ARRAY, ((SAFEARRAY) objVal).SafeArrayGetElement(i));
				}
			}
		}
		else
		{
			switch(vt)
			{
				case VARENUM.VT_EMPTY:
					iRet = 0;
					break;
				case VARENUM.VT_NULL:
					iRet = 0;
					break;
				case VARENUM.VT_ERROR:
					iRet = 2;
					break;
				case VARENUM.VT_UI1:
					iRet = 1;
					break;
				case VARENUM.VT_I2:
				case VARENUM.VT_UI2:
					iRet = 2;
					break;
				case VARENUM.VT_I4:
				case VARENUM.VT_UI4:
				case VARENUM.VT_R4:
					iRet = 4;
					break;
				case VARENUM.VT_R8:
				case VARENUM.VT_DATE:
					iRet = 8;
					break;
				case VARENUM.VT_BOOL:
					iRet = 2;
					break;
				case VARENUM.VT_BSTR:
					if(objVal != null){
						iRet = 4 + 2 * ((String)objVal).length();
					}else{
						iRet = 4;
					}
					break;
				case VARENUM.VT_VARIANT:
					if(objVal != null){
						iRet = 2 + 4 + VARIANT2Size(((VARIANT)objVal).VariantGetType(), ((VARIANT)objVal).VariantGetObject());
					}else{
						iRet = 2 + 4;
					}
					break;
				default:
			}
		}

		return iRet;
	}
	
	public static int MsgSize2Int(byte[] msg)
	{
		if(msg.length == 4){
			return ByteBuffer.wrap(msg, 0, 4).order(m_bo).getInt();
		}else{
			return 0;
		}		
	}
}
