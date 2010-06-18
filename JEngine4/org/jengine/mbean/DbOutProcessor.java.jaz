package org.jengine.mbean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Calendar;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jengine.ex.BadDataHandler;
import org.jengine.ex.ShastaException;
import org.jengine.in.Main;

import repos.Patient;
import repos.Report;
import repos.ReportContents;
import bsh.Interpreter;

public class DbOutProcessor implements MessageListener
{
	final static private Logger log = Logger.getLogger("DbOutProcessor");
    final static private String CONNECTION_FACTORY = "ConnectionFactory";
    final static private String PROVIDER_URL = System.getProperty("jboss.bind.address");
	private Interpreter _interpreter;
    private QueueConnection _connection;
    private QueueSession _jmsSession;

	private String _script;
    	private String _inQueueName;
    	private String _outQueueName;
    	private int _numberMessages;
    	private int _numberMessagesFailed;
    	private String _timeStampLastMsg = "";

	public DbOutProcessor ()
	{
		_interpreter = new Interpreter();
		try {
			_interpreter.eval("org.apache.log4j.Logger log");
			_interpreter.set("log",log);
			_interpreter.eval("org.jengine.mbean.DbOutProcessor dbout");
			_interpreter.set("dbout", this);
		} catch (Exception ex) {
			ShastaException shastaEx = new ShastaException( "Error initializing Interpretor", ex);
			BadDataHandler exHandler = new BadDataHandler();
			exHandler.handleThrowable(Thread.currentThread(), shastaEx);
		}
	}
	
	public void setInQueue(String q)
	{
		_inQueueName = q;
	}
	public String getInQueue()
	{
		return _inQueueName;
	}
	public String getScript()
	{
		return _script;
	}
	public void setScript(String _script)
	{
		this._script = _script;
	}
        //added 20070427, jaz
        public void setOutQueue(String q)
        {
                this._outQueueName = q;
        }
        public String getOutQueue()
        {
                return _outQueueName;
        }
        public int getNumberMessages()
        {
            return _numberMessages;
        }
        public int getNumberMessagesFailed()
        {
            return _numberMessagesFailed;
        }
        public String getTimeStampLastMsg()
        {
            return _timeStampLastMsg;
        }



	public void onMessage (Message msg)
	{
		BadDataHandler exHandler = new BadDataHandler();
		try
		{
			try
			{
				Main main = getMainFromMessage(msg);
				log.info(main.toString());
				// Set the message
				_interpreter.eval("org.jengine.in.Main main");
				_interpreter.set("main", main);
				// call the script
				_interpreter.eval(_script);
				_interpreter.unset("main");
				main = null;
				msg.acknowledge();
				_jmsSession.commit();
				_numberMessages++;
        			_timeStampLastMsg = getCurrentTime();
			}
			catch (JMSException ex)
			{
				log.error("Message Exception", ex);
				_jmsSession.rollback();
			}
		}
		catch (ShastaException ex)
		{
			_numberMessagesFailed++;
			exHandler.handleThrowable(Thread.currentThread(), ex);
		}
		catch (Exception ex)
		{
			_numberMessagesFailed++;
			ShastaException shastaEx = new ShastaException( "Error processing message", ex);
			exHandler.handleThrowable(Thread.currentThread(), shastaEx);
		}
	}
	
	public void start()
	{
		try
		{
    		Context ctx = new InitialContext();
            QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup(CONNECTION_FACTORY);
            _connection = factory.createQueueConnection();

            //make this a transacted session
            //only commit the send and recv if both work w/o exception
            //and everything in between (database) works.
            _jmsSession = _connection.createQueueSession(true, javax.jms.Session.CLIENT_ACKNOWLEDGE);

            log.debug("Looking up " + _inQueueName);
            Queue inQueue = (Queue)ctx.lookup("queue/" + _inQueueName);
            QueueReceiver receiver = _jmsSession.createReceiver(inQueue);
            receiver.setMessageListener(this);
            log.debug("Starting JMS connection()");
    	    _connection.start();
		}
		catch (JMSException ex)
		{
			log.error("JMSException", ex);
		}
		catch (NamingException ex)
		{
			log.error("NamingException", ex);
		}
	}

	public void stop()
	{
		try
		{
//			// pass the logging category to beanshell
//			_interpreter.eval("org.apache.log4j.Logger log");
//			_interpreter.set("log",log);
//			// Set the message
//			_interpreter.eval("String message");
//			_interpreter.set("message", "stop");
//			// call the script
//			_interpreter.eval(_script);
			if (_connection != null)
			{
				_connection.close();
			}
		}
		catch (JMSException ex)
		{
			log.error("JMSException", ex);
		}
	}

	
//	private Context getInitialContext() throws NamingException {
//        log.info("--- getInitialContext() ---");
//        Properties env = new Properties();
//        env.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
//        env.put("java.naming.provider.url", PROVIDER_URL);
//        env.put("java.naming.provider.url.pkgs", "org.jnp.interfaces");
//        return new InitialContext(env);
//    }

	
	private Main getMainFromMessage(Message msg) throws JMSException
	{
		log.info("getMainFromMessage()");

		final Main main;
		{
			System.out.println("msg.getClass()=" + msg.getClass());
			ObjectMessage objMsg = (ObjectMessage) msg;
			Object obj = objMsg.getObject();
			log.info("obj.getClass()=" + obj.getClass());
			main = (Main) obj;
			String testId = main.getTestId();
			log.info("testId=" + testId);
		}
		return main;
	}
	public void dummy ()
	{
	}

	private Patient getOrCreatePatient (Main main, Session session)
	{
		Patient patient;

		// Try and look up the patient
		String labId = main.getPatId();
		log.debug("labId=" + labId);
		Query qryPatient = session.getNamedQuery("ByLabId");
		qryPatient.setString("labId", labId);
//			Patient existingPatient = (Patient)qryPatient.uniqueResult();
		List patients = qryPatient.list();

		if (patients != null  &&  patients.size() > 0)
		{
			log.info("Using existing patient");
			patient = (Patient)patients.get(0);
		}
		else
		{
			log.info("Creating new patient");
			patient = new Patient();
			patient.setLabId(labId);
			patient.setDob(main.getDob().toString());
			patient.setSex(main.getSex());
			patient.setAccountNum("");
			session.save(patient);
			session.flush();
		}
		// If not there, create a new one . . . and save it? Or does that come later

		return patient;
	}

	/**
	 * Get SessionFactory, for Shasta-Out sessions
	 * 
	 * @return
	 * @throws NamingException
	 */
	private SessionFactory getShastaOutSessionFactory() throws NamingException
	{
		InitialContext ctx = new InitialContext();
		Object obj = ctx.lookup("java:/hibernate/HibernateReposOut");
		SessionFactory sf = (SessionFactory) obj;
		return sf;
	}


	private Report createReport(Main main)
	{
		Report report = new Report();
		String fillerOrdNum = main.getAccno();
		if (fillerOrdNum == null)
		{
			String errmsg = "Main.Accno is null";
			log.warn(errmsg);
			fillerOrdNum = "";
		}
		report.setFillerOrdNum(fillerOrdNum);

		Date runDate = main.getRunDate();
		if (runDate == null)
		{
			String errmsg = "Main.RunDate is null";
			log.warn(errmsg);
			// Exception exCause = new IllegalArgumentException(
			// "Main.RunDate is null");
			// ShastaException exShasta = new ShastaException(errmsg,
			// exCause);
//					log.warn("Rolling back persistence");
			// session.getTransaction().rollback();
			// throw exShasta;
			runDate = new Date();
		}
		report.setObsTimestamp(formatDate(runDate));

		// Dr Name  -- expecting Lastname, Firstname, M.D.
		String drName = main.getOrdPhys();
		int iComma = drName.indexOf(',');
		if (iComma == -1)
		{
			throw new IllegalArgumentException("Illegal DrName format -- " + drName);
		}
		String lastName = drName.substring(0, iComma);
		String firstName = drName.substring(iComma+1);
		String drCode = "-1";
		report.setOrdProvId(drCode);
		report.setOrdProvFirst(firstName);
		report.setOrdProvLast(lastName);

		return report;
	}

	private String formatDate (Date date)
	{
		String dateStr;

		DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
		dateStr = df.format(date);

		return dateStr;
	}


	private String getReceivingFacility (Main main)
	{
		String location;

		if (main.getLocation() == null){
			log.warn("Main.Location is null: Using \"\"");
			location = "";
		}
		else
		{
			location = main.getLocation();
		}

		return location;
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
