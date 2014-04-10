package jbCAP;

/** @file DateConverter.java
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

import java.util.Date;

public final class DateConverter {
	// Java Date(0) stands for Jan  1 09:00:00 JST 1970
	// C++  Date(0) stands for Dec 30 00:00:00 JST 1899
	// The difference is 25569 Day
	private static final double td = 25569.0;
	
	// 1 Day = 24 * 60 * 60 * 1000 ms
	private static final double ADay = 24 * 60 * 60 * 1000;

	public static Date CDate2JDate(double dTime)
	{
		return new Date((long) ((dTime - td) * ADay));
	}
	
	public static double JDate2CDate(Date jTime)
	{
		if(jTime != null){
			return (double)(jTime.getTime()) / ADay + td;
		}else{
			return (double)0;
		}
	}
}
