package org.jengine.core;

public class PluggableThreadGroup extends ThreadGroup
{
	final private ThrowableHandler _exHandler;
	
	
	public PluggableThreadGroup (String name, ThrowableHandler exHandler)
	{
		super(name);
		_exHandler = exHandler;
	}


	public PluggableThreadGroup (ThreadGroup parent, String name, ThrowableHandler exHandler)
	{
		super(parent, name);
		_exHandler = exHandler;
	}


	public void uncaughtException(Thread t, Throwable e)
	{
		_exHandler.handleThrowable(t, e);
	}
}
