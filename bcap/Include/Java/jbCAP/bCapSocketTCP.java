package jbCAP;

/** @file bCapSocketTCP.java
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

class bCapSocketTCP extends bCapSocket implements Runnable{
	private Socket m_sock;
	private DataInputStream m_in;
	private DataOutputStream m_out;

	private short m_sSerial;
		
	private Object[] m_args;
	private bCapPacket m_msgRet;
	
	// Constructor
	protected bCapSocketTCP(String host, int port, int timeout)
	{
		m_args = new Object[]{1, host, port, timeout};
		
		Thread th = new Thread(this);
		
		th.start();
		
		while(th.isAlive());
		
		m_args = null;
	}
	
	@Override
	protected void Release()
	{
		m_args = new Object[]{2, null};
		
		Thread th = new Thread(this);
		
		th.start();
		
		while(th.isAlive());
		
		m_args = null;
	}
		
	@Override
	protected boolean IsConnected()
	{
		if(m_sock == null)
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
		m_args = new Object[]{3, msg};
		m_msgRet = null;
		
		Thread th = new Thread(this);
		
		th.start();
		
		while(th.isAlive());
		
		m_args = null;
		return m_msgRet;
	}
	
	public void run(){
		switch((Integer)m_args[0]){
			case 1:
				th_init((String)m_args[1], (Integer)m_args[2], (Integer)m_args[3]);
				break;
			case 2:
				th_release();
				break;
			case 3:
				th_sendmessage((bCapPacket)m_args[1]);
				break;
		}
	}
	
	private void th_init(String host, int port, int timeout){
		try
		{
			m_sock = new Socket(host, port);
			if(timeout < 0)
			{
				m_sock.setSoTimeout(0);
			}
			else
			{
				m_sock.setSoTimeout(timeout);
			}
	
			m_in = new DataInputStream(m_sock.getInputStream());
			m_out = new DataOutputStream(m_sock.getOutputStream());
			
			m_sSerial = 0;
			
			bCapStart();
		}
		catch(Throwable ex)
		{
			th_release();
		}
	}

	private void th_release(){
		bCapStop();
		
		// DataOutputStream
		try
		{
			m_out.close();
		}
		catch(Throwable ignore){
			// Do nothing
		}
		finally
		{
			m_out = null;
		}
		
		// DataInputStream
		try
		{
			m_in.close();
		}
		catch(Throwable ignore)
		{
			// Do nothing
		}
		finally
		{
			m_in = null;
		}
		
		// Socket
		try
		{
			m_sock.close();
		}
		catch(Throwable ignore)
		{
			// Do nothing
		}
		finally
		{
			m_sock = null;
		}
	}
	
	private void th_sendmessage(bCapPacket msg){
		byte[] bRet, bSize;
		int iSize;
		m_msgRet = new bCapPacket();

		bSize = new byte[4];

		try
		{
			msg.SetSerial(m_sSerial++);

			// Send Message
			m_out.write(bCapConverter.Encode(msg));
			
			do{
				// Throw first byte
				m_in.readByte();
				
				// Receive message size
				m_in.read(bSize);
				
				iSize = bCapConverter.MsgSize2Int(bSize);
				
				bRet = new byte[iSize];

				// Receive message
				m_in.readFully(bRet, bSize.length + 1, bRet.length - (bSize.length + 1));
				
				// Copy packet of buffer size
				bRet[0] = 1;
				System.arraycopy(bSize, 0, bRet, 1, bSize.length);
				
				// byte -> bCapPacket
				m_msgRet = bCapConverter.Decode(bRet);
				
				if(m_msgRet.GetID() < 0){
					break;
				}
			}while(msg.GetSerial() != m_msgRet.GetSerial());
		}
		catch(Throwable error)
		{
			// Return FAILED
			m_msgRet.SetID(m_iTimeoutCode);
			m_msgRet.SetSerial(msg.GetSerial());
			m_msgRet.ClearVariant();
		}
	}
}
