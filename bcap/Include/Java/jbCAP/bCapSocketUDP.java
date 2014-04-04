package jbCAP;

/** @file bCapSocketUDP.java
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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

class bCapSocketUDP extends bCapSocket implements Runnable{
	private InetSocketAddress m_addr;
	private DatagramSocket m_sock;
	
	private short m_sSerial;
	private int m_retry;
	
	private static final int m_iPackSize = 504;
	
	private bCapPacket m_msg;
	private bCapPacket m_msgRet;
	
	protected bCapSocketUDP(String host, int port, int timeout, int retry)
	{
		m_addr = new InetSocketAddress(host, port);
		
		try
		{
			m_sock = new DatagramSocket();
			
			if(timeout < 0)
			{
				m_sock.setSoTimeout(0);
			}
			else
			{
				m_sock.setSoTimeout(timeout);
			}
			
			m_sSerial = 0;
			
			if(retry < 1)
			{
				m_retry = 1;
			}
			else if(7 < retry)
			{
				m_retry = 7;
			}
			else
			{
				m_retry = retry;
			}
			
			bCapStart();
		}
		catch(Throwable ex)
		{
			Release();
		}
		
	}
	
	@Override
	protected void Release()
	{
		bCapStop();
		
		if(m_sock != null)
		{
			m_sock.close();
			m_sock = null;
		}
		
		m_addr = null;
	}
	
	@Override
	protected boolean IsConnected()
	{
		if(m_addr == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	@Override
	protected synchronized bCapPacket SendMessage(bCapPacket msg)
	{
		m_msg = msg;
		m_msgRet = null;
		
		Thread th = new Thread(this);
		
		th.start();
		
		while(th.isAlive());
		
		return m_msgRet;
	}
	
	public void run(){
		byte[] bSend, bRet, bRetCpy, bSize;
		int iSize;
		m_msgRet = new bCapPacket();
		
		short sSerialTemp = m_sSerial;
		int iCount = 0;
		
		bSize = new byte[4];
		bRet = new byte[m_iPackSize + 1];
		
		while(iCount <= m_retry)
		{
			try
			{
				m_msg.SetSerial(m_sSerial++);
				
				// Send Message
				bSend = bCapConverter.Encode(m_msg);
				m_sock.send(new DatagramPacket(bSend, bSend.length, m_addr));
								
				do{
					// Receive Message
					m_sock.receive(new DatagramPacket(bRet, bRet.length));

					// Copy packet of buffer size
					System.arraycopy(bRet, 1, bSize, 0, bSize.length);
					iSize = bCapConverter.MsgSize2Int(bSize);
					
					// Copy packet
					bRetCpy = new byte[iSize];
					System.arraycopy(bRet, 0, bRetCpy, 0, bRetCpy.length);
					
					// byte -> bCapPacket
					m_msgRet = bCapConverter.Decode(bRetCpy);
					
					if(m_msgRet.GetID() < 0){
						break;
					}
				}while(m_msg.GetSerial() != m_msgRet.GetSerial());
				
				iCount = m_retry + 1;
			}
			catch(Throwable error)
			{
				if(iCount < m_retry)
				{
					m_msg.SetReserv(sSerialTemp);
					
					iCount++;
				}
				else
				{
					// Return FAILED
					m_msgRet.SetID(m_iTimeoutCode);
					m_msgRet.SetSerial(m_msg.GetSerial());
					m_msgRet.ClearVariant();
					
					iCount = m_retry + 1;
				}
			}
		}
	}
}
