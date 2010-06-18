/*
 * 	JEngine Copyright (C) 2006
 *  Jacek Zagorski, Shasta NetWorks LLC
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

import org.apache.commons.net.ftp.*;

import org.jboss.web.*;
import org.apache.log4j.Category;

import java.io.*;
import java.net.UnknownHostException;

import java.util.Vector;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Iterator;
import java.util.TimeZone;

import javax.jms.Session;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.QueueSender;
import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;

import javax.naming.*;

public class FTPHL7Client extends FTPClient implements Runnable {
	
	private static String CONNECTION_FACTORY = "ConnectionFactory";
	//private static String PROVIDER_URL       = "localhost";
	private static String PROVIDER_URL       = System.getProperty("jboss.bind.address");
	
	private Context ctx = null;
	private QueueConnectionFactory factory = null;
	private QueueConnection connection = null;
	private QueueSession session = null;
	private QueueSender qSender = null;
	private Queue queue = null;
	
	private ThreadPool threadPool = new ThreadPool();
	private boolean running;
	private Category log;
		
	private String QueueNames = null;
	private ArrayList Queues = new ArrayList();
	private ArrayList QSenders = new ArrayList();
	
	private String ifName = null;
	private String IPAddress = null;
	private String hostname = null;
	private String username = null;
	private String password = null;
	private int pollInterval = 120000;
	private String workingDirectory = null;
	private String workingDirectoryLocal = null;
	private String matchFilePattern = null;
	private boolean passiveMode = false;
	private String defaultDateFormatStr = null;
	private String recentDateFormatStr = null;
	private boolean connectionStatus;
	private int numberMessages;
	private String timeStampLastMsg;
	private int backlog = 50;
		
	public FTPHL7Client()
	{
		connectionStatus = false;
		numberMessages = 0;
		timeStampLastMsg = "";
	}
	
	public void setIFName(String n) { ifName = n; }
	public String getIFName() { return ifName; }
	public void setHostname(String s) { hostname = s; 	IPAddress = s; }
	public String getHostname() { return hostname; }
	public String getIPAddress() { return IPAddress; }

	public void setUsername(String s) { username = s; 	}
	public String getUsername() { return username; }
	public void setPassword(String s) { password = s; 	}
	public String getPassword() { return password; }
	public void setPollInterval(int n) { pollInterval = n; }
	public int getPollInterval() { return pollInterval; }
	public void setWorkingDirectory(String d) { workingDirectory = d; 	}
	public String getWorkingDirectory() { return workingDirectory; }
	public void setWorkingDirectoryLocal(String s) { workingDirectoryLocal = s; }
	public String getWorkingDirectoryLocal() { return workingDirectoryLocal; }
	public void setMatchFilePattern(String s) { matchFilePattern = s; }
	public String getMatchFilePattern() { return matchFilePattern; }
	public void setPassiveMode(boolean b) { passiveMode = b; }
	public boolean getPassiveMode() { return passiveMode; }
	public void setDefaultDateFormatStr(String s) { defaultDateFormatStr = s; }
	public String getDefaultDateFormatStr() { return defaultDateFormatStr; }
	public void setRecentDateFormatStr(String s) { recentDateFormatStr = s; }
	public String getRecentDateFormatStr() { return recentDateFormatStr; }
	public void setQueues(String q) { QueueNames = q; }
	public String getQueues() { return QueueNames; }
	public boolean getConnectionStatus() { return connectionStatus; }
	public int getNumberMessages() { return numberMessages; }
	public String getTimeStampLastMsg() { return timeStampLastMsg; }
	public int getBacklog() { return backlog; }
	public void setBacklog(int backlog) {
		if( backlog <= 0 )
			backlog = 50;
		this.backlog = backlog;
	}
	
	private void initializeQueueConnection() {
		try {
			if (session != null) session.close();
			ctx = getInitialContext();
			factory = (QueueConnectionFactory) ctx.lookup(CONNECTION_FACTORY);
			connection = factory.createQueueConnection();
			session = connection.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);
				
			StringTokenizer st = new StringTokenizer(QueueNames,":");
			while(st.hasMoreElements()) {
				queue = (Queue)ctx.lookup("queue/" + st.nextElement());
				Queues.add(queue);
				qSender = session.createSender(queue);
				QSenders.add(qSender);
			}
		} catch (JMSException je) {
			je.printStackTrace();
			
		} catch (NamingException ne) {
			ne.printStackTrace();
			
		}
		
	}
	
	public void start() throws IOException {
		log = Category.getInstance(getClass()+":"+ifName);
		log.debug("---start()---");
		try {
			
			initializeQueueConnection();
			
            FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
            conf.setDefaultDateFormatStr(getDefaultDateFormatStr());
            conf.setRecentDateFormatStr(getRecentDateFormatStr());
            this.configure(conf);
			
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
			if (isConnected() == true)
				disconnect();
			connectionStatus = false;
			running = false;
			threadPool.disable();
		}
		catch (Exception e) {
			log.error("Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void goGetThem() {
	
		if (running != true) return;
		
		try {
			//TODO attempt FTP connection to get files
			if (connectAndLogin(getHostname(), getUsername(), getPassword()) == true) {
				log.info("Connected...  process files... ");
				connectionStatus = true;
				if (changeWorkingDirectory(getWorkingDirectory()) != true) {
					log.error("Could not change to directory [" + getWorkingDirectory() + "]");
					return;
				} else {
					log.info("OK, changed directory to [" + getWorkingDirectory() + "]");
				}
				
				Vector files = listFileNames();
				log.info("OK, got file list, size: " + files.size());
				
				int count=1;
				for (Iterator it = files.iterator() ; it.hasNext() ; count++ ) {
					String file = (String)it.next();
					log.info(count + "> file: " + file);
					String localFileName = workingDirectoryLocal + "/" + file;
					if (fileMatchesPattern(file) == true) {
						downloadFile(file, localFileName);
						if (processFile(localFileName) == true) {
							deleteFile(file);
						}
					} else {
						System.out.println("Bypass & Delete file: " + file);
						//deleteFile(file);
					}
				}
			} else {
				connectionStatus = false;
			}
		} catch (IOException ie) {
			log.error("IOException", ie);
			try {
				disconnect();
				connectionStatus = false;
			} catch (Exception e) {
				log.error("Exception", e);
			}
		}
		try {
			log.debug("Disconnect");
			disconnect();
			connectionStatus = false;
		} catch (Exception e) {
			log.error("Exception", e);
		}
	}

	private boolean fileMatchesPattern(String filename) { // "^.*rad.dat$"  OR  "^.*lab.dat$"
		log.info("Does [" + filename + "] match patterns [" + matchFilePattern + "]");
		String [] patterns = matchFilePattern.split(":");
		for (int i=0 ; i<patterns.length ; i++) {
			if (filename.matches(patterns[i]) == true)
				return true;
			else
				return false;
		}
		return false;
	}

	private boolean downloadFile (String serverFile, String localFile)
	throws IOException, FTPConnectionClosedException {
		FileOutputStream out = new FileOutputStream(localFile);
		boolean result = retrieveFile(serverFile, out);
		out.close();
		return result;
	}

	private boolean processFile(String fileName) {
		File file = new File(fileName);
		BufferedReader inF = null;
		try {
			if (file.canRead() == true) {
				inF = new BufferedReader(new FileReader(fileName));
				char[] cbuf = new char[500000];
				int numRead = inF.read(cbuf);
				String messages = new String(cbuf).trim();
				String[] messageList = messages.split(System.getProperty("line.separator"));
				if (numRead == -1) {
					log.info("File [" + fileName + "] contains ZERO messages.");
					try { file.delete(); } catch(Exception e) { log.error("Exception deleting " + fileName, e); }
					return true;
				}
				log.info("File " + fileName + " (size:" + numRead + ") contains " + messageList.length + " messages.");
				log.info(messages);
				for (int i=0 ; i<messageList.length ; i++) {
					//System.out.println("message:" + messageList[i].toString());
					if (messageList[i].startsWith("#") != true) {
						//send HL7 message to Queue
						if (sendMsgToQueue(messageList[i]) != true) {
							log.error("sendMsgToQueue() error");
							return false;
						} else {
							numberMessages++;
							timeStampLastMsg = getCurrentTime();
						}
					}
				}
			}
		} catch (IOException e) {
			return false;
		}
		try {
			inF.close();
		} catch(IOException e) {
			
		}
		//file.delete();
		return true;
	}
    
    public void run()
    {
    	while (true) {
    		try {
    			log.debug("connect and get files...");
    			goGetThem();
    			log.debug("Sleep " + pollInterval + " milliseconds");
    			Thread.sleep(pollInterval);
    		} catch(InterruptedException e) {
    			log.error("Sleep InterruptedException", e);
    		}
    	}
    }
	
	private Context getInitialContext() throws NamingException {
		Properties env = new Properties();
		env.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
		env.put("java.naming.provider.url", PROVIDER_URL);
		env.put("java.naming.provider.url.pkgs", "org.jnp.interfaces");
		return new InitialContext(env);
	}
	
	private boolean sendMsgToQueue(String text) {
		
		Iterator it = QSenders.iterator();
		
		while (it.hasNext()) {
			try {
				qSender = (QueueSender)it.next();
				TextMessage message = session.createTextMessage();
				message.setText(text);
				qSender.send(message);
				log.info("Message sent to Queue, JMSMessageID=" + message.getJMSMessageID());
			} catch(Exception e) {
				log.error("sendMsgToQueue() Exception", e);
				log.info("RE-INITIALIZE QUEUE CONNECTION");
				initializeQueueConnection();
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
	
	private boolean connectAndLogin (String host, String userName, String password)
			throws IOException, UnknownHostException, FTPConnectionClosedException {
		boolean success = false;
		connect(host);
		int reply = getReplyCode();
		if (FTPReply.isPositiveCompletion(reply))
			success = login(userName, password);
		if (!success)
			disconnect();
		return success;
	}

	private Vector listFileNames()
			throws IOException, FTPConnectionClosedException {
		FTPFile[] files = listFiles();
		Vector v = new Vector();
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory())
				v.addElement(files[i].getName());
		}
		return v;
	}

	
}
