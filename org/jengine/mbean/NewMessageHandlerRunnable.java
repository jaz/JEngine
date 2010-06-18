package org.jengine.mbean;

public class NewMessageHandlerRunnable implements Runnable {

	private boolean _keepRunningFlag;
	private int _interval;
	
	public NewMessageHandlerRunnable (int interval)
	{
		_keepRunningFlag = true;
		_interval = interval;
	}
	
	public void run() {
		while (_keepRunningFlag)
		{
			process();
			try
			{
				Thread.sleep(_interval);
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	public void setKeepRunningFlag (boolean keepRunningFlag)
	{
		_keepRunningFlag = keepRunningFlag;
	}
	
	
	private void process ()
	{
		System.out.println("Handling new messages . . . ");
		// Lookup shasta_out
		// Get max date of messages
		// If no result,
		//    Get all rows from Main
		// Else
		//    Get all rows from Main after the date
		// For each row from main,
		//    Send a message to the JMS queue with the id
	}
}
