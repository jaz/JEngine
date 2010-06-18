package org.jengine.core;

import java.util.Calendar;
import org.apache.log4j.Logger;

public class PollerRunnable implements Runnable
{
	final static private Logger log = Logger.getLogger("SHASTA");

	private boolean _keepRunningFlag;

	private final IProcessor _processor;

	private int _interval;
	private String _repositorySource;
	private String _repositorySink;
	private String _queueName;

	public PollerRunnable(IProcessor processor)
	{
		_keepRunningFlag = true;
		_processor = processor;
	}

	public void run()
	{
		while (_keepRunningFlag)
		{
			process();
			try
			{
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MILLISECOND, getInterval());
				log.info("Sleeping for " + getInterval() + "ms, until " + cal.getTime() + " . . . ");
				Thread.sleep(getInterval());
			}
			catch (InterruptedException ex)
			{
				log.error("!!! SLEEP InterruptedException !!!", ex);
				ex.printStackTrace();
			}
		}
	}

	public void setKeepRunningFlag(boolean keepRunningFlag)
	{
		_keepRunningFlag = keepRunningFlag;
	}

	private void process()
	{
		_processor.process();
	}

	public int getNumberMessages()
	{
		return _processor.getNumberMessages();
	}
	public void setNumberMessages(int n)
	{
		_processor.setNumberMessages(n);
	}
	public int getNumberMessagesError()
	{
		return _processor.getNumberMessagesError();
	}
	public void setNumberMessagesError(int n)
	{
		_processor.setNumberMessagesError(n);
	}
	public int getInterval()
	{
		return _processor.getInterval();
	}
	public void setInterval(int interval)
	{
		_processor.setInterval(interval);
	}

	public String getRepositorySource()
	{
		return _processor.getRepositorySource();
	}
	public void setRepositorySource(String repos)
	{
		_processor.setRepositorySource(repos);
	}
	public String getRepositorySink()
	{
		return _processor.getRepositorySink();
	}
	public void setRepositorySink(String repos)
	{
		_processor.setRepositorySink(repos);
	}
	public String getQueue()
	{
		return _processor.getQueue();
	}
	public void setQueue(String q)
	{
		_processor.setQueue(q);
	}

}
