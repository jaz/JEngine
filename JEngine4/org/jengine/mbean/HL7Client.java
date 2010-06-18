/*
 * JEngine Copyright (C) 2001 Jacek Zagorski, Shasta NetWorks LLC Jeff Gunther,
 * Intalgent Technologies, LLC
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 *  
 */

package org.jengine.mbean;

import org.jengine.tools.network.*;
import org.jengine.tools.hl7.HL7Utils;

import java.io.*;
import java.net.*;
import java.util.Vector;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Iterator;
import java.util.TimeZone;
import javax.jms.Session;
import javax.jms.Queue;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.QueueSender;
import javax.jms.QueueReceiver;
import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.MessageListener;
import javax.naming.*;

import org.jboss.web.ThreadPool; // import org.jboss.mx.util.ThreadPool;
import org.apache.log4j.Category;

public class HL7Client implements Runnable, MessageListener {

	// see ticket:83
	// could also be = java:ConnectionFactory
	// OR as defined in jms/jms-ds.xml = JmsXA (see http://www.jboss.org/community/docs/DOC-10482)
	//private static String CONNECTION_FACTORY = "ConnectionFactory"
	//private static String CONNECTION_FACTORY = "java:ConnectionFactory";
	//private static String CONNECTION_FACTORY = "JmsXA";
	private static String CONNECTION_FACTORY = "java:ConnectionFactory";
	private static String PROVIDER_URL = System.getProperty("jboss.bind.address");

	private QueueConnectionFactory factory;
	private QueueConnection connection;
	private QueueSession session;
	private QueueReceiver qReceiver;
	private QueueSender qSender;
	private javax.jms.Queue queue;
	private javax.jms.Queue errorQueue;
	private int port;
	private int resendFailedCount;
	private int connectRetryInterval;
	private int resendRetryInterval;
	private String IPAddress;
	private String QueueName;
	private String ErrorQueueName;
	private LLPWriter so;
	private LLPReader si;
	private Socket socket;
	private boolean retrySocketSend;
	private TextMessage msg;
	private String msgText;
	private String ifName;
	private boolean connectionStatus;
	private int numberMessages;
	private int numberMessagesFailed;
	private String timeStampLastMsg;
	private int nakCount;
	private boolean onDemandTCPConnect;

	// set up threadpool so interface can start up without holding back the rest
	// of the deployment if connection
	// can't be established right away.
	private ThreadPool threadPool;
	private Category log;

	// If we have an incorrect socket:port (or a dead peer) we need the
	// connection loops to terminate when we are stopped. - ATS
	private boolean running;

	// volatile boolean running;

	public HL7Client() {
		factory = null;
		connection = null;
		session = null;
		qReceiver = null;
		qSender = null;
		queue = null;
		errorQueue = null;
		retrySocketSend = true;
		port = 0;
		msgText = null;
		so = null;
		si = null;
		socket = null;
		resendFailedCount = 20;
		resendRetryInterval = 5000;
		connectRetryInterval = 30000;
		ifName = "";
		threadPool = new ThreadPool();
		connectionStatus = false;
		numberMessages = 0;
		numberMessagesFailed = 0;
		timeStampLastMsg = getCurrentTime();
		nakCount = 0;
		onDemandTCPConnect = false;
	}

	public boolean getConnectionStatus() {
		return connectionStatus;
	}

	public int getNumberMessages() {
		return numberMessages;
	}

	public int getNumberMessagesFailed() {
		return numberMessagesFailed;
	}

	public String getTimeStampLastMsg() {
		return timeStampLastMsg;
	}

	public void setResendFailedCount(int t) {
		resendFailedCount = t;
	}

	public int getResendFailedCount() {
		return resendFailedCount;
	}

	public void setResendRetryInterval(int t) {
		resendRetryInterval = t;
	}

	public int getResendRetryInterval() {
		return resendRetryInterval;
	}

	public void setConnectRetryInterval(int t) {
		connectRetryInterval = t;
	}

	public int getConnectRetryInterval() {
		return connectRetryInterval;
	}

	public void setIFName(String n) {
		ifName = n;
	}

	public String getIFName() {
		return ifName;
	}

	public void setPort(int p) {
		port = p;
	}

	public int getPort() {
		return port;
	}

	public void setIPAddress(String ip) {
		IPAddress = ip;
	}

	public String getIPAddress() {
		return IPAddress;
	}

	public void setQueue(String q) {
		QueueName = q;
	}

	public String getQueue() {
		return QueueName;
	}

	public void setQueueError(String q) {
		ErrorQueueName = q;
	}

	public String getQueueError() {
		return ErrorQueueName;
	}

	public boolean getOnDemandTCPConnect() {
		return onDemandTCPConnect;
	}

	public void setOnDemandTCPConnect(boolean c) {
		onDemandTCPConnect = c;
	}

	public void start() {

		log = Category.getInstance(getClass() + ":" + ifName);
		log.info("--- start() ---");

		running = true;

		boolean queuesGood = initializeQueues();

		/**
		 * SET UP NEW THREAD HERE TO take care of setting up and monitoring
		 * connection ----> run()
		 */
		// threadPool.setActive(true);
		threadPool.run(this);
		// run();
		// stop this thread so the start() method completes even if connections
		// to external are not established
		// ATS, I dont think this is necessarry, we dont have anything
		// to stop at this point
		// this.stop();
	}

	public boolean initializeQueues() {
		try {
			log.info("initializeQueues()");
			Context ctx = getInitialContext();
			factory = (QueueConnectionFactory) ctx.lookup(CONNECTION_FACTORY);
			connection = factory.createQueueConnection();
			connection.setExceptionListener(new ExceptionListenerImpl());
			session = connection.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);

			queue = (javax.jms.Queue) ctx.lookup("queue/" + QueueName);
			qReceiver = session.createReceiver(queue);
			qReceiver.setMessageListener(this);

			queue = (javax.jms.Queue) ctx.lookup("queue/" + ErrorQueueName);
			qSender = session.createSender(queue);

			log.info("Calling connection.start()");
			connection.start();

			log.info("initializeQueues() DONE.");
			
		} catch (NamingException ne) {
			log.error("NamingException : Queue Init", ne);
			return false;
		} catch (JMSException je) {
			log.error("JMSException : Queue Init", je);
			return false;
		}
		return true;
	}

	public void initNetwork() {
		log.info("calling initSocket()");
		initSocket();
		// if we have been sitting in initSocket without a connection
		// we will come through here when stopped/undeployed, not a problem,
		// just a touch messy in the log file - ATS
		if (running) {
			log.debug("calling initIO()");
			initIO();
			log.debug("returned from initIO()");
		}
	}

	public void run() {
		log.debug("--- run() ---");
		if (getOnDemandTCPConnect() == false) {
			while (socket == null && running) {
				initNetwork();
				log.debug("returned from initNetwork()");
			}
		}

		// otherwise, only crank up tcp connection onMessage
		// crank up jms connection - moved to initializeQueues()
		//connection.start();
		
		// this is the blocking operation
		// if we're waiting for messages to come in on the JMS connection,
		// we won't detect the CLOSE_WAIT if a remote socket connection closes
		// any SoTimeout on JMS connections?

		log.debug("--- finished run() ---");

	}

	public void stop() {

		log.info("--- stop() ---");

		running = false;
		threadPool.disable();

		// see http://www.jboss.org/community/docs/DOC-10000 for details on cleanup...
		
		try {
			log.debug("connection.close()");
			connection.close();
		} catch (Exception e) {
			log.error("Exception on stop() : connection.close()", e);
		}
		try {
			log.debug("socket.close()");
			socket.close();
		} catch (Exception e) {
			log.error("Exception on stop() : socket.close()", e);
		}

		socket = null;
		connectionStatus = false;

	}

	private Context getInitialContext() throws NamingException {
		Properties env = new Properties();
		env.put(Context.PROVIDER_URL, PROVIDER_URL);
		env.put(Context.URL_PKG_PREFIXES, "org.jnp.interfaces");
		log.info("--- getInitialContext() : PROVIDER_URL=" + PROVIDER_URL);
		return new InitialContext(env);
	}

	private void initSocket() {
		while (socket == null && running) {
			try {
				log
						.info("new Socket(" + getIPAddress() + "," + getPort()
								+ ")");
				socket = new Socket(getIPAddress(), getPort());
				/*
				 * new code 20100126
				SocketImplFactory sockFact = new SocketImplFactory();
				socket = java.net.SocketImplFactory.createSocketImpl();
				*/
				// timeout the socket, to unblock on a read
				socket.setSoTimeout(20000);
				socket.setKeepAlive(true);
				log.info("Connected with server " + socket.getInetAddress()
						+ ":" + socket.getPort());
				connectionStatus = true;
			} catch (Exception e) {
				log.error("Cannot connect to " + getIPAddress() + ":"
						+ getPort() + " - sleep " + connectRetryInterval);
				// sleep and try to connect again
				try {
					Thread.sleep(connectRetryInterval);
				} catch (InterruptedException ie) {
					log.error("Sleep Interrupted Exception", ie);
				}
			}
		}
	}

	public void onMessage(Message message) {

		log.info("--- onMessage() ---");

		if (socket == null) {
			initNetwork();
		}

		if (socket != null) {
			retrySocketSend = true;
			readFromQ(message);
			while (retrySocketSend == true) {
				if (sendMsgToSocket() == true) {
					if (getResponse() == true) // this should be the ACK
					{
						qAcknowledge();
						retrySocketSend = false;
						numberMessages++; // increment msg counter
						timeStampLastMsg = getCurrentTime(); // set timestamp
																// here
						nakCount = 0;
						break;
					} else {
						if (nakCountExceeded() == true) {
							expireMessage(message);
							numberMessagesFailed++;
							nakCount = 0;
							break;
						}
					}
				} else
					nakCount++;

				if (true) // that is, if we've failed for any reason,
							// reconnect
				{
					try {
						Thread.sleep(resendRetryInterval);
					} catch (InterruptedException ie) {
						log.error("Sleep Interrupted Exception", ie);
					}
					if (socket == null)
						initNetwork();
				}

				if (running == false) {
					log.info("----- running NOT true");
					return;
				} else {
					log.info("----- running STILL true");
				}
			}

			if (getOnDemandTCPConnect() == true)
				closeStreams();

		}
	}

	private boolean nakCountExceeded() {
		if (nakCount > 0)
			log.info("nakCount : " + nakCount + ", resendFailedCount : "
					+ resendFailedCount);

		if (nakCount > resendFailedCount)
			return true;
		else
			return false;

	}

	private void expireMessage(Message message) {
		String actualText = null;
		TextMessage tm = (TextMessage) message;
		try {
			actualText = tm.getText();
		} catch (javax.jms.JMSException e) {
			log.error("JMSException : getText()", e);
			e.printStackTrace();
		}
		sendMsgToQ(actualText);
		qAcknowledge();
		// this spits out all the detail of the JMS message including the text
		log.info("EXPIRING MESSAGE : " + message.toString());
	}

	private void qAcknowledge() {
		try {
			log.info("msg.acknowledge()");
			msg.acknowledge();
		} catch (javax.jms.JMSException je) {
			log.error("msg.acknowledge() Exception", je);
		}
	}

	private boolean getResponse() {
		try {
			log.info("readMsg() : check for ACK");
			String rStr = si.readMsg();
			log.info("read : " + rStr);

			if (isAck(rStr) == true)
				return true;
			else {
				nakCount++;
				return false;
			}
		} catch (RuntimeException re) {
			log.error("Socket Read Runtime Exception", re);
			re.printStackTrace();
			nakCount++;
			closeStreams();
			return false;
		} catch (InterruptedIOException ie) {
			log.error("Interrupted IO Exception, no data from Read", ie);
			nakCount++;
			closeStreams();
			return false;
		} catch (Exception e) {
			log.error("Socket Read Exception", e);
			nakCount++;
			closeStreams();
			return false;
		}
	}

	private boolean sendMsgToQ(String text) {
		try {
			TextMessage message = session.createTextMessage();
			message.setText(text);
			qSender.send(message);
		} catch (Exception e) {
			log.error("sendMessageToQ() Exception", e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean sendMsgToSocket() {
		try {
			log.info("socket writeMsg()");
			so.writeMsg(msgText);
			return true;
		} catch (RuntimeException re) {
			log.error("Socket Write Runtime Exception", re);
			re.printStackTrace();
			closeStreams();
		} catch (Exception e) {
			log.error("Socket Write Exception", e);
			e.printStackTrace();
			closeStreams();
		}
		return false;

	}

	private void readFromQ(Message message) {
		msg = (TextMessage) message;
		try {
			msgText = msg.getText();
		} catch (Exception e) {
			log.error("Exception msg.getText()", e);
			//
			// HOW TO HANDLE THIS JMSException
			// - retry getText() ??
			//
		}
		log.info("Received incoming Q message, size : " + msgText.length());
		log.debug("Q message : " + msgText);
	}

	private void closeStreams() {
		so.close();
		si.close();
		so = null;
		si = null;
		try {
			// socket.shutdownOutput();
			// socket.shutdownInput();
			socket.close();
			socket = null;
			connectionStatus = false;
		} catch (IOException ie) {
			log.error("socket.close() Exception", ie);
		}
	}

	private boolean isAck(String msg) {
		boolean retval = false;
		try {
			HL7Utils hl7Util = new HL7Utils(msg);
			String outgoingMessageControlId = (new HL7Utils(msgText)).getMessageControlId();
			if (hl7Util.isAck() == true) {
				if (hl7Util.getMessageControlIdMsa() == null) {
					log.error("Accept as ACK -- even though ACK MessageControlId in MSA is missing");
					retval = true;
				} else if (hl7Util.getMessageControlIdMsa().equalsIgnoreCase(outgoingMessageControlId) == true) {
					retval = true;
				} else {
					log.error("ACK MessageControlId [" + hl7Util.getMessageControlIdMsa()
						+ "] doesn't match outgoing Id " + outgoingMessageControlId);
					retval = false;
				}
			}
		} catch (Exception e) {
			log.error("Exception in isAck()", e);
			retval = false;
		}
		return retval;
	}

	private void initIO() {
		log.debug("initIO() : setting up OutputStream");
		try {
			if (so == null) {
				so = new LLPWriter(socket.getOutputStream());
			}
		} catch (Exception e) {
			log.error("Could not create LLPWriter", e);
		}

		log.debug("initIO() : setting up InputStream");
		try {
			if (si == null)
				si = new LLPReader(socket.getInputStream());
		} catch (Exception e) {
			log.error("Could not create LLPReader", e);
		}
		log.debug("initIO() : finished");
	}

	private String getCurrentTime() {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				DATE_FORMAT);
		sdf.setTimeZone(TimeZone.getDefault());
		return sdf.format(cal.getTime());
	}
	
	private class ExceptionListenerImpl implements javax.jms.ExceptionListener {
		public static final int NUM_RETRIES = 3;
		
		public void onException(JMSException e) {

			for (int i = 0; i < NUM_RETRIES; i++) {
				log.warn("TICKET_083: Connection has problems, trying to re-create it, attempt " + (i + 1) + " ...");

				try {
					session = null;
					connection.close(); // unregisters the ExceptionListener
				} catch (Exception e2) {
					// I will get an Exception anyway, since the connection to the server is
					// broken, but close() frees up resources associated with the connection
				}

				boolean setupOK = initializeQueues();

				if (setupOK) {
					log.info("TICKET_083: Connection re-established");
					return;
				} else {
					log.warn("TICKET_083: Re-creating connection failed, retrying ...");
				}
			}

			log.error("TICKET_083: Cannot re-establish connection, giving up ...");
		}
	}
}
