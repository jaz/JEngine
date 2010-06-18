/*
 * 	JEngine Copyright (C) 2008
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
import au.com.bytecode.opencsv.CSVReader;

import org.jengine.internal.bsh.Converter;
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

public class FTPTxtClient implements Runnable {
	
	private static String CONNECTION_FACTORY = "ConnectionFactory";
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
	private String converterProperties;
	private String delimiter = null;
	private String protocol;
	
	private Converter converter;
	private boolean performDiff;
	
	public FTPClient ftp;
	
	public FTPTxtClient(String protocol)
		throws java.security.NoSuchAlgorithmException
	{
		connectionStatus = false;
		numberMessages = 0;
		timeStampLastMsg = getCurrentTime();
		performDiff = false;

		if (protocol.equals("SSL") == true)
			ftp = new FTPSClient();
		else
			ftp = new FTPClient();
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
	public void setProtocol(String p) { protocol = p; }
	public String getProtocol() { return protocol; }
	public void setDelimiter(String dl) { delimiter = dl; }
	public String getDelimiter() { return delimiter; }
	
	public void setConverterProperties(String properties) {
        converter = new Converter();
    	this.converter.init(ifName,properties);
        converterProperties = properties;
        //System.out.println("setConverterProperties: [" + converterProperties + "]");
    }
    public String getConverterProperties() {
    	//System.out.println("getConverterProperties: [" + converterProperties + "]");
    	return converterProperties;
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
            ftp.configure(conf);
			
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
			if (ftp.isConnected() == true)
				ftp.disconnect();
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
				if (ftp.changeWorkingDirectory(getWorkingDirectory()) != true) {
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
						log.info("About to processFile: " + localFileName);
						if (processFile(localFileName) == true) {
							ftp.deleteFile(file);
						}
					} else {
						log.info("Bypass file: " + file);
						//deleteFile(file);
					}
				}
			} else {
				connectionStatus = false;
			}
		} catch (IOException ie) {
			log.error("IOException", ie);
			try {
				ftp.disconnect();
				connectionStatus = false;
			} catch (Exception e) {
				log.error("Exception", e);
			}
		}
		try {
			log.debug("Disconnect");
			ftp.disconnect();
			connectionStatus = false;
		} catch (Exception e) {
			log.error("Exception", e);
		}
	}

	private boolean fileMatchesPattern(String filename) { // "^.*rad.dat$"  OR  "^.*lab.dat$"
		log.info("Does [" + filename + "] match patterns [" + matchFilePattern + "]");
		String [] patterns = matchFilePattern.split(",");
		for (int i=0 ; i<patterns.length ; i++) {
			if (filename.matches(patterns[i]) == true) {
				log.info("Match! " + patterns[i]);
				return true;
			} else
				return false;
		}
		return false;
	}

	private boolean downloadFile (String serverFile, String localFile)
	throws IOException, FTPConnectionClosedException {
		FileOutputStream out = new FileOutputStream(localFile);
		boolean result = ftp.retrieveFile(serverFile, out);
		out.close();
		return result;
	}

	private java.util.List diffProcessUsingDb(java.util.List messageList) {
		//0. initialize DB connection
		Context cxt = null;
		javax.sql.DataSource cxtDs = null;
		java.util.Vector newMessageList = new java.util.Vector();

		try {
			cxt = new InitialContext();
			log.info("Got InitialContext()");
			cxtDs = (javax.sql.DataSource)cxt.lookup("java:/" + "ShastaDataDirectorDS");
			log.info("Got DataSource");
		} catch (NamingException ne) {
			log.error("NamingException: " + ne.toString());
			ne.printStackTrace();
		}
		java.sql.Connection conn = null;
		try {
			conn = cxtDs.getConnection();
			log.debug("Got DB Connection");
			conn.setAutoCommit(false);
			log.debug("conn.setAutoCommit() true");
			String sql = "drop table diffOld; select * into diffOld from diffNew; delete from diffNew";
			java.sql.PreparedStatement ps = conn.prepareStatement(sql);
			log.debug("prepared statement to execute sql - drop and move");
			ps.execute();
			log.info("Executed: " + sql);
			sql = "insert into diffNew values(?,?)";
			ps = conn.prepareStatement(sql);
			log.debug("prepared statement to execute sql - inserts");
			//String delimiter = "\\|";
			//String delimiter = ",";
			String delimiter = "\\" + getDelimiter();
			log.info("Delimiter is: " + delimiter);
			try {
				for (int i=0 ; i<messageList.size() ; i++) {
					String message = (String)messageList.get(i);
					String[] field = message.split(delimiter);
	
					// SAMPLE MESSAGE:
					// "007946"|"SA"|"058271"|"FARRELL"|"ESTELLE"|"6/9/1917"|"F"|"107283649"|"04"|416|"A "|"7/3/2006"|""|0|||"URBON"|"WILLIAM"|"35 PACHAUG ROAD"||"TRUMBULL"|"CT"|"06611"|"2033780319"|"00000000000000"|0
					//log.debug("message[" + (i+1) + "] : " + message);
					//Strip off quotes from 1st 3 fields - BUILDING KEY
					for (int j=0 ; j<3 ; j++) {
					        int len = field[j].length();
					        //log.debug("field[" + j + "]  : " + field[j] + ", len=" + len);
					        if (len>0 && field[j].charAt(0)=='"')
					                field[j] = field[j].substring(1,len-1);
					        field[j].trim();
					}
	
					String key = field[0] + "-" + field[1] + "-" + field[2];
			        //log.debug("f0-f1-f2 KEY : " + key);
					ps.setString(1, key);
					String dateDischarge = field[12];
					log.debug("Discharge Date = " + dateDischarge);
					ps.setString(2, message);
					ps.execute();
				}
			} catch (Exception e) {
				log.error("Exception in building keys and inserting into DB");
				e.printStackTrace();
			}
			log.info("Executed: " + sql);
			//compare for CHANGE
			sql = "select n.data as data from diffOld o, diffNew n where o.key=n.key and n.data<>o.data";
			ps = conn.prepareStatement(sql);
			java.sql.ResultSet rs = ps.executeQuery();
			log.debug("executeQuery: " + sql);
			while (rs.next()) {
				String diffData = rs.getString("data");
				newMessageList.add("CHANGE" + delimiter + diffData);
			}
			//compare for NEW
			sql = "select * from diffNew where key not in (select key from diffOld)";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			log.debug("executeQuery: " + sql);
			while (rs.next()) {
				String diffData = rs.getString("data");
				newMessageList.add("NEW" + delimiter + diffData);
			}
			//compare for DELETE
			/* DO NOT SEND DELETES TO BE PROCESSED AS DISCHARGES, just drop off
			sql = "select * from diffOld where key not in (select key from diffNew)";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			log.debug("executeQuery: " + sql);
			while (rs.next()) {
				String diffData = rs.getString("data");
				newMessageList.add("DELETE" + delimiter + diffData);
			}
			*/
			conn.commit();
			rs.close();
			ps.close();
			conn.close();
			
		} catch (java.sql.SQLException se) {
			log.error("SQLException: " + se.toString());
			se.printStackTrace();
		}

		log.info("newMessageList size = " + newMessageList.size());
		return newMessageList;
	}

	private boolean processFile(String fileName) {
		log.debug("new File: " + fileName);
		File file = new File(fileName);
		log.debug("got new File: " + fileName);
		
		//BufferedReader inF = null;
		try {
				//inF = new BufferedReader(new FileReader(fileName));
				log.info("Get CSVReader with delimiter: [" + getDelimiter() + "]");
				CSVReader reader = new CSVReader(new FileReader(fileName), getDelimiter().charAt(0));
				log.info("Got CSVReader: " + reader.toString());
				
				//char[] cbuf = new char[500000];
				//int numRead = inF.read(cbuf);
				//String messages = new String(cbuf).trim();
				java.util.List messageListCsv = reader.readAll();
				
				log.debug("Got list of messages: " + messageListCsv.size());
				
				//String[] messageList = messages.split(System.getProperty("line.separator"));
				
				/*
				if (numRead == -1) {
					log.info("File [" + fileName + "] contains ZERO messages.");
					try { file.delete(); } catch(Exception e) { log.error("Exception deleting " + fileName, e); }
					return true;
				}
				*/
				if (messageListCsv.size() == 0) {
					log.info("File [" + fileName + "] contains ZERO messages.");
					try { file.delete(); } catch(Exception e) { log.error("Exception deleting " + fileName, e); }
					return true;
				}

				//log.info("File " + fileName + " (size:" + numRead + ") contains " + messageList.length + " messages.");
				log.info("CSV File " + fileName + " (size:" + (new File(fileName)).length() + ") contains " + messageListCsv.size() + " messages.");
				//log.info(messages);

				//DO DB COMPARE, ASSUMING new contains old data
				if (performDiff == true)
					//messageList = diffProcessUsingDb(messageList);
					messageListCsv = diffProcessUsingDb(messageListCsv);
				
				for (int i=0 ; i<messageListCsv.size() ; i++) {
					String[] fields = (String[])messageListCsv.get(i);
					String message = null;
					for (int j=0 ; j<fields.length ; j++) {
						if (j!=0) message = message + "|" + fields[j];
						else message = fields[j];
					}
					log.debug("Message " + (i+1) + ": " + message);
					
					if (message.startsWith("#") != true) {
						//create HL7 Message for script
						//run script to populate Message
						//org.jengine.tools.hl7.Message msg = new org.jengine.tools.hl7.Message();
						//send HL7 message to Queue
						//String message = messageList[i];
		                if (converter != null) message=converter.execute(message);
		                else log.warn("No conversion performed on data in text file: " + fileName);

						if (sendMsgToQueue(message) != true) {
							log.error("sendMsgToQueue() error");
							return false;
						} else {
							numberMessages++;
							timeStampLastMsg = getCurrentTime();
						}
					}
				}
		} catch (IOException e) {
			return false;
		}
		
		/*
		try {
			//inF.close();
			reader.close();
		} catch(IOException e) {
			log.error("Can't close reaader, " + e.getMessage());
			e.printStackTrace();
		}
		*/
		
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
		ftp.connect(host);
		int reply = ftp.getReplyCode();
		log.info("FTP Connect Reply Code: " + reply);
		
		if (FTPReply.isPositiveCompletion(reply))
			success = ftp.login(userName, password);
		if (!success)
			ftp.disconnect();
		return success;
	}

	private Vector listFileNames()
			throws IOException, FTPConnectionClosedException {
		FTPFile[] files = ftp.listFiles();
		Vector v = new Vector();
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory())
				v.addElement(files[i].getName());
		}
		return v;
	}

	
}
