package org.jengine.ex;

import org.apache.log4j.Logger;
import org.jengine.core.ThrowableHandler;

public class BadDataHandler implements ThrowableHandler
{
	final static private Logger log = Logger.getLogger("SHASTA");
	final static private Logger errorlog = Logger.getLogger("ERRORLOG");
	
	public void handleThrowable(Thread thread, Throwable throwable)
	{
		if (throwable instanceof ShastaException)
		{
			ShastaException ex = (ShastaException)throwable;
			log.error("!!! ShastaException: " + ex.getMessage());
			log.error("!!! Check error log for detail");
			errorlog.error("Unhandled Exception", ex.getCause());
		}
		else
		{
			log.error("Unhandled Exception", throwable);
			errorlog.error("Unhandled Exception", throwable);
		}
	}
}
