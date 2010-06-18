package org.jengine.core;

public interface IProcessor
{
	public void process ();

	public void setRepositorySink (String r);
	public String getRepositorySink ();

	public void setRepositorySource (String r);
	public String getRepositorySource ();

	public void setQueue(String q);
	public String getQueue();

	public void setInterval (int i);
	public int getInterval ();

	public void setNumberMessages (int n);
	public int getNumberMessages ();

	public void setNumberMessagesError (int n);
	public int getNumberMessagesError ();
}
