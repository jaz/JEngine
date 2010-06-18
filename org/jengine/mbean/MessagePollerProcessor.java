package org.jengine.mbean;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.TimeZone;
import java.util.Calendar;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jengine.core.IProcessor;
import org.jengine.ex.ShastaException;
import org.jengine.in.Main;

public class MessagePollerProcessor implements IProcessor
{
	final static private String CONNECTION_FACTORY = "ConnectionFactory";

	private String _queueName;
	private int _numberMessages;
	private int _numberMessagesError;
	private String _timeStampLastMsg = "";
	private String _repositorySource;
	private String _repositorySink;
	private int _interval;;

	final static private Logger log = Logger.getLogger("SHASTA");

	final private NewMessagePollerMBean _mbean;

	public MessagePollerProcessor(NewMessagePollerMBean mbean)
	{
		_mbean = mbean;
	}

        public void setRepositorySource(String repos)
        {
                _repositorySource = repos;
        }
        public String getRepositorySource()
        {
                return _repositorySource;
        }
        public void setRepositorySink(String repos)
        {
                _repositorySink = repos;
        }
        public String getRepositorySink()
        {
                return _repositorySink;
        }
        public void setQueue(String q)
        {
                _queueName = q;
        }
        public String getQueue()
        {
                return _queueName;
        }
        public void setInterval(int i)
        {
                _interval = i;
        }
        public int getInterval()
        {
                return _interval;
        }
        public void setNumberMessagesError(int n)
        {
            _numberMessagesError = n;
        }
        public int getNumberMessagesError()
        {
            return _numberMessagesError;
        }
        public void setNumberMessages(int n)
        {
            _numberMessages= n;
        }
        public int getNumberMessages()
        {
            return _numberMessages;
        }
        public String getTimeStampLastMsg()
        {
            return _timeStampLastMsg;
        }

	public void process()
	{
		log.info("Polling . . .");
		_mbean.setLastPollTime(new Date());
		try
		{

			// Get max message date
			String maxMsgTimestamp = getMaxMessageTimestamp();
			log.info("Looking for deals since " + maxMsgTimestamp);
			// Query for deals
			List messages = getNewMains(maxMsgTimestamp);
			int nMessages = messages.size();
			String status = nMessages + " new message(s)";
			_mbean.setLastPollStatus(status);

			// Process hits
			sendMessages(messages);
		}
		catch (Exception ex)
		{
			ShastaException shastaEx = new ShastaException("Error parsing data", ex);
			throw shastaEx;
		}
	}

	private void sendMessages(List messages) throws NamingException, JMSException
	{
		Context ctx = new InitialContext();

		System.out.println("messages.size()=" + messages.size());

		QueueConnectionFactory qcf = (QueueConnectionFactory) ctx.lookup("ConnectionFactory");
		QueueConnection conn = qcf.createQueueConnection();
		Queue queue = (Queue) ctx.lookup("queue/" + _queueName);
		QueueSession session = conn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
		QueueSender sender = session.createSender(queue);
		if (messages != null)
		{
			log.info(messages.size() + " new message(s) found");
			for (int i = 0; i < messages.size(); ++i)
			{
				Main mainRow = (Main) messages.get(i);
				log.info("mainRow.getTestId()=" + mainRow.getTestId());
				ObjectMessage msg = session.createObjectMessage(mainRow);
				log.info("Creation: msg.getClass()=" + msg.getClass());
				sender.send(msg);
				_numberMessages++;
				_timeStampLastMsg = getCurrentTime();
			}
		}
		sender.close();
		session.close();
		conn.close();
	}

	private List getNewMains(String maxMsgTimestamp) throws NamingException, ParseException
	{
		Context ctx = new InitialContext();

		List messages;
		{
			log.info("Source ctx.lookup: " + _repositorySource);
			Object obj = ctx.lookup(_repositorySource);
			SessionFactory sf = (SessionFactory) obj;
			Session session = sf.openSession();

			session.beginTransaction();

			final Query query;
			if (maxMsgTimestamp == null)
			{
				query = session.getNamedQuery("shasta.in.getAllRows");
			}
			else
			{
				DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
				Date date = df.parse(maxMsgTimestamp);

				SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				StringBuffer sb = new StringBuffer();
				String dateString = df2.format(date,sb,new java.text.FieldPosition(0)).toString();

				query = session.getNamedQuery("shasta.in.getRowsSinceDate");
				query.setString(0, dateString);
				log.info("--------------> query.setDate(0, " + dateString + ")");
			}
			messages = query.list();

			session.getTransaction().commit();
			session.close();
		}
		return messages;
	}

	private String getMaxMessageTimestamp() throws NamingException, JMSException
	{
		final String maxMsgTimestamp;
		
		String maxSavedMsgTimestamp = getMaxSavedMessageTimestamp();
		log.debug("maxSavedMsgTimestamp=" + maxSavedMsgTimestamp);
		String maxQueuedMsgTimestamp = getMaxQueuedMessageTimestamp();
		log.debug("maxQueuedMsgTimestamp=" + maxQueuedMsgTimestamp);
		maxMsgTimestamp = getMaxString(maxSavedMsgTimestamp, maxQueuedMsgTimestamp);

		return maxMsgTimestamp;
	}
	
	
	private String getMaxSavedMessageTimestamp () throws NamingException
	{
		Context ctx = new InitialContext();
		String maxMsgTimestamp = null;
		{
			log.info("Sink ctx.lookup: " + _repositorySink);
			Object obj = ctx.lookup(_repositorySink);
			SessionFactory sf = (SessionFactory) obj;
			Session session = sf.openSession();

			session.beginTransaction();

			Query query = session.getNamedQuery("shasta.out.MaxMessageTimestamp");
			maxMsgTimestamp = (String) query.uniqueResult();
			System.out.println("Polling: maxMsgTimestamp=" + maxMsgTimestamp);

			session.getTransaction().commit();
			session.close();
		}
		return maxMsgTimestamp;
	}

	
	private String getMaxQueuedMessageTimestamp () throws NamingException, JMSException
	{
		final String maxQueuedMsgTimestamp;
		
		Context ctx = new InitialContext();

		// Get reference to queue factory
		QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup(CONNECTION_FACTORY);
		QueueConnection connection = factory.createQueueConnection();
		QueueSession jmsSession = connection.createQueueSession(true, javax.jms.Session.CLIENT_ACKNOWLEDGE);

		// Get reference to queue
		Object obj = ctx.lookup("queue/" + _queueName);
		Queue queue = (Queue) obj;
		QueueBrowser qb = jmsSession.createBrowser(queue);
		Enumeration enm = qb.getEnumeration();
		String currMax = "0000000000000000";
		while (enm.hasMoreElements())
		{
			Object objNext = enm.nextElement();
			System.out.println("objNext.getClass()=" + objNext.getClass());
			ObjectMessage msg = (ObjectMessage)objNext;
			Object objPayload = msg.getObject();
			Main main = (Main)objPayload;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date postDate = sdf.parse(main.getPostDt(), new java.text.ParsePosition(0));
			//Date postDate = main.getPostDt();
			DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			String postDateString = df.format(postDate);
			currMax = getMaxString(currMax, postDateString);
		}
		
		maxQueuedMsgTimestamp = currMax;

		qb.close();
		jmsSession.close();
		
		// Browse over queue, getting max timestamp
		return maxQueuedMsgTimestamp;
	}
	
	
	private String getMaxString (String s1, String s2)
	{
		final String sMax;
		
		if (s1 == null  &&  s2 == null)
		{
			sMax = null;
		}
		else if (s1 == null) {
			sMax = s2;
		
		}
		else if (s2 == null) {
			sMax = s1;
		}
		else if (s1.compareTo(s2) < 1)
		{
			sMax = s2;
		}
		else
		{
			sMax = s1;
		}
		
		return sMax;
	}
        private String getCurrentTime()
        {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
                sdf.setTimeZone(TimeZone.getDefault());
                return sdf.format(cal.getTime());
        }


}
