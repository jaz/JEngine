package org.jengine.mbean;

import java.util.Date;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.jboss.system.ServiceMBeanSupport;
import org.jengine.core.PluggableThreadGroup;
import org.jengine.core.PollerRunnable;
import org.jengine.core.ThrowableHandler;
import org.jengine.ex.BadDataHandler;

public class NewMessagePoller extends ServiceMBeanSupport implements NewMessagePollerMBean
{

	final static private Logger log = Logger.getLogger("SHASTA");

	private String _lastPollStatus;

	private Date _lastPollTime;

	private PollerRunnable _nmpr = new PollerRunnable(new MessagePollerProcessor(this));


	public ObjectName getObjectName(MBeanServer server, ObjectName name) throws MalformedObjectNameException
	{
		return new ObjectName(OBJECT_NAME);
	}

	public String getLastPollStatus()
	{
		return _lastPollStatus;
	}

	public void setLastPollStatus(String lastPollStatus)
	{
		_lastPollStatus = lastPollStatus;
	}

	public Date getLastPollTime()
	{
		return _lastPollTime;
	}

	public void setLastPollTime(Date lastPollTime)
	{
		_lastPollTime = lastPollTime;
	}

	public int getPollInterval()
	{
		return _nmpr.getInterval();
	}
	public void setPollInterval(int pollInterval)
	{
		log.info("Interval will change to " + pollInterval + "ms at next opportunity");
		_nmpr.setInterval(pollInterval);

	}

	public String getQueue()
	{
		return _nmpr.getQueue();
	}
	public void setQueue(String q)
	{
		_nmpr.setQueue(q);
	}
	public String getRepositorySource()
	{
		return _nmpr.getRepositorySource();
	}
	public void setRepositorySource(String repos)
	{
		_nmpr.setRepositorySource(repos);
	}
	public String getRepositorySink()
	{
		return _nmpr.getRepositorySink();
	}
	public void setRepositorySink(String repos)
	{
		_nmpr.setRepositorySink(repos);
	}
	public int getNumberMessages()
	{
		return _nmpr.getNumberMessages();
	}
	public void setNumberMessages(int n)
	{
		_nmpr.setNumberMessages(n);
	}
	public int getNumberMessagesError()
	{
		return _nmpr.getNumberMessagesError();
	}
	public void setNumberMessagesError(int n)
	{
		_nmpr.setNumberMessagesError(n);
	}
	public String getTimestampLastMsg()
	{
		return _nmpr.getTimestampLastMsg();
	}
	public void setTimestampLastMsg(String ts)
	{
		_nmpr.setTimestampLastMsg(ts);
	}



	public void startService()
	{

		log.info("=== Starting " + this.getName() + " ===");

		ThrowableHandler exHandler = new BadDataHandler();
		ThreadGroup group = new PluggableThreadGroup("Pollers", exHandler);
		_nmpr.setKeepRunningFlag(true);
		Thread th = new Thread(group, _nmpr);
		th.start();
	}

	public void stopService()
	{

		log.info("=== Stopping " + this.getName() + " ===");

		_nmpr.setKeepRunningFlag(false);
	}
}
