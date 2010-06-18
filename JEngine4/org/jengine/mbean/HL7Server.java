/*
 * 	JEngine Copyright (C) 2001
 *  	Jacek Zagorski, Shasta NetWorks LLC
 *  	Jeff Gunther, Intalgent Technologies, LLC
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.jengine.mbean;

import org.jengine.tools.hl7.*;
import org.jengine.tools.network.*;

import org.jboss.web.*;
import org.apache.log4j.Category;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.jms.*;
import javax.naming.*;

public class HL7Server implements Runnable {

	// see ticket:83
	// could also be = java:ConnectionFactory
	// OR as defined in jms/jms-ds.xml = JmsXA (see http://www.jboss.org/community/docs/DOC-10482)
	//private static String CONNECTION_FACTORY = "ConnectionFactory"
	//private static String CONNECTION_FACTORY = "java:ConnectionFactory";
	//private static String CONNECTION_FACTORY = "JmsXA";
	private static String CONNECTION_FACTORY = "java:ConnectionFactory";
	private static String PROVIDER_URL       = System.getProperty("jboss.bind.address");

	private Context ctx = null;
	private QueueConnectionFactory factory = null;
	private QueueConnection connection = null;
	private QueueSession session = null;
	private QueueSender qSender = null;
	private javax.jms.Queue queue = null;

	private int port = 0;
	private InetAddress bindAddress;
	private int backlog = 50;
	private ServerSocket server = null;
	private ThreadPool threadPool = new ThreadPool();

	private boolean connectionStatus;
	private int numberMessages;
	private String timeStampLastMsg;

	private String QueueNames = null;
	private ArrayList Queues = new ArrayList();
	private ArrayList QSenders = new ArrayList();

	private String ifName = "";

	private boolean running;

	private Category log;

	public HL7Server()
	{
		connectionStatus = false;
		numberMessages = 0;
		timeStampLastMsg = getCurrentTime();
	}

	public boolean getConnectionStatus()
	{
		return connectionStatus;
	}
	public int getNumberMessages()
	{
		return numberMessages;
	}
	public String getTimeStampLastMsg()
	{
		return timeStampLastMsg;
	}


	public void setIFName(String n)
	{
		ifName = n;
	}
	public String getIFName()
	{
		return ifName;
	}

	public void setPort(int p) {
		port = p;
	}

	public int getPort() {
		return port;
	}

	public String getBindAddress() {
		String address = null;
		if( bindAddress != null )
			address = bindAddress.getHostAddress();
		return address;
	}

	public void setBindAddress(String host) {
		try {
			if (host != null)
				bindAddress = InetAddress.getByName(host);
		}
		catch(UnknownHostException e) {
			String msg = "Invalid host address specified: " + host;
			log.error(msg, e);
		}
	}

	public int getBacklog() {
		return backlog;
	}

	public void setBacklog(int backlog) {
		if( backlog <= 0 )
			backlog = 50;
		this.backlog = backlog;
	}

	public void setQueues(String q) {
		QueueNames = q;
	}

	public String getQueues() {
		return QueueNames;
	}

	private boolean initializeQueues() {
		try {
			log.info("initializeQueues()");

			ctx = getInitialContext();
			factory = (QueueConnectionFactory) ctx.lookup(CONNECTION_FACTORY);
			connection = factory.createQueueConnection();
			connection.setExceptionListener(new ExceptionListenerImpl());
			session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			//session = connection.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);

			if (Queues.size()>0) Queues.clear();
			if (QSenders.size()>0) QSenders.clear();

			StringTokenizer st = new StringTokenizer(QueueNames,":");
			while(st.hasMoreElements())
			{
				queue = (javax.jms.Queue)ctx.lookup("queue/" + st.nextElement());
				Queues.add(queue);
				log.debug("session.createSender(" + queue.toString() + ")");
				qSender = session.createSender(queue);
				QSenders.add(qSender);
			}
			//No messages will be delivered until the Connection.start method is explicitly called
			//this call was previously in run() after a new connection was established
			//connection.start();
		} catch (NamingException ne) {
			log.error("NamingException: initializeQueues(): " + ne);
			ne.printStackTrace();
			return false;
		} catch (JMSException je) {
			log.error("JMSException: initializeQueues(): " + je);
			je.printStackTrace();
			return false;
		}

		return true;
	}

	public void start() throws IOException {
		log = Category.getInstance(getClass()+":"+ifName);
		log.debug("---start()---");
		
		try {
			if (session != null) session = null;
			if (connection != null) connection.close();
		} catch (JMSException je) {
			log.error("JMSException: " + je.getMessage());
			je.printStackTrace();
		}
		boolean queuesGood = initializeQueues();

		try {
			log.debug("---calling new ServerSocket---");
			server = new ServerSocket(port, backlog, bindAddress);
			log.debug("---calling listen()---");
			//listen();
			running = true;
			threadPool.run(this);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void stop() {
		try {
			log.info("Interface caught -STOP- event");
			running = false;
			ServerSocket srv = server;
			srv.close();
			server.close();
			if (server!=null) server = null;
			//session.close();
		}
		catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void run()
	{
		Socket socket;

		try {
			socket = server.accept(); //accept() call BLOCKS
			socket.setSoTimeout(5000);
			socket.setKeepAlive(true);
			socket.setSoLinger(true,1000);
			//listen();
			running = true;
			threadPool.run(this);
			connectionStatus = true;
			log.info("New connection accepted "	+ socket.getInetAddress() + ":" + socket.getPort());
			LLPWriter so = new LLPWriter(socket.getOutputStream());
			LLPReader si = new LLPReader(socket.getInputStream());
			try { 
				//No messages will be delivered until the Connection.start method is explicitly called
				connection.start();
			} catch(JMSException je) {
				log.error("JMSException : Queue connection.start()", je);
			}
			while(true) {
				log.info("Connected - readSocket()");
				String message = null;
				try {
					message = readMsg(si);
				} catch (SocketTimeoutException e) {
					if (running == false) {
						log.debug("Socket Timeout > interface -stop()-");
						break;
					} else {
						log.debug("Socket Timeout > continue");
						continue;
					}
				}
				if (message == null) {
					log.error("readMsg returned NULL message");
					break;
				}
				log.info(message);
				log.debug("Connected - sendMsgToQ()");
				if (sendMsgToQ(message) != true)
				{   //NO ACK TO EXTERNAL IF ERROR
					log.error("sendMsgToQ(message) error, RE-INITIALIZE QUEUES");
					//TODO: handle message producer is closed problem
					try {
						session = null;
						connection.close();
					} catch (JMSException je) {
						log.error("JMSException: " + je.getMessage());
						je.printStackTrace();
					}
					initializeQueues();
					break;
				}
				log.debug("Connected - sendResponse()");
				if (sendResponse(so, message) != true) {
					log.error("sendResponse() error");
					break;
				}
				numberMessages++;   //increment msg counter
				timeStampLastMsg = getCurrentTime(); //set timestamp here
				if (running == false) {
					log.debug("Done Processing Message > interface -stop()-");
					break;
				}
			}

			// connection closed by client
			// OR
			// send2Q error
			// OR
			// sendResponse error
			// OR
			// stop has been requested
			try {
				si.close();
				so.close();
				connectionStatus = false;
				socket.close();
				log.info("Socket Connection Closed");
			}
			catch (IOException e) {
				log.error(e);
			}
		}
		catch (IOException e) {
			log.error(e);
		}
	}

	protected void listen() {
		log.info("---listen()---");
		running = true;
		threadPool.run(this);
	}

	private Context getInitialContext() throws NamingException {
		Properties env = new Properties();
		//env.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
		//env.put("java.naming.provider.url", PROVIDER_URL);
		env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
		env.put(Context.PROVIDER_URL, PROVIDER_URL);
		env.put(Context.URL_PKG_PREFIXES, "org.jnp.interfaces");
		log.info("PROVIDER_URL: " + PROVIDER_URL);
		return new InitialContext(env);
	}

	private boolean sendResponse(LLPWriter so, String msg) {
		try {
			// send ACK/NAK
			// HL7Ack(success|failure, mcId)
			HL7Ack ack = new HL7Ack(true, msg);
			log.debug("Send ACK : " + ack.getMessage());
			so.writeMsg(ack.getMessage());
			return true;
		}
		catch (Exception ex) {
			log.error("Write ACK Exception : " + ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}

	private String readMsg(LLPReader si) throws SocketTimeoutException {
		try {
			return si.readMsg();
		}
		catch (SocketTimeoutException se) {
			throw new SocketTimeoutException();
		}
		catch (Exception e) {
			log.debug("si.readMsg() Exception : " + e.getMessage());
			return null;
		}
	}

	private boolean sendMsgToQ(String text) {

		Iterator it = QSenders.iterator();

		while (it.hasNext()) {
			try {
				qSender = (QueueSender)it.next();
				TextMessage message = session.createTextMessage();
				message.setText(text);
				qSender.send(message);
			} catch(Exception e) {
				log.error("sendMsgToQ() Exception : " + e.getMessage());
				e.printStackTrace();
				return false;
			}
		}//end of while
		return true;
	}

	private String getCurrentTime()
	{
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
		sdf.setTimeZone(TimeZone.getDefault());
		return sdf.format(cal.getTime());
	}


	private class ExceptionListenerImpl implements javax.jms.ExceptionListener {
		public void onException(JMSException e) {
			reCreateQueues();
		}
		private static final int NUM_RETRIES = 3;

		private void reCreateQueues() {
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
