package org.jengine.mbean;

import java.util.Date;

import org.jboss.system.ServiceMBean;

public interface NewMessagePollerMBean extends ServiceMBean{
	public static final String OBJECT_NAME = ":service=Interface";
	
	public Date getLastPollTime ();
	public void setLastPollTime(Date lastPollTime);
	public String getLastPollStatus ();
	public void setLastPollStatus (String lastPollStatus);
	public int getPollInterval ();
	public void setPollInterval (int interval);
	public int getNumberMessages();
	public void setNumberMessages(int num);
	public String getTimestampLastMsg();
	public void setTimestampLastMsg(String ts);

	public String getRepositorySource ();
	public void setRepositorySource (String repos);
	public String getRepositorySink ();
	public void setRepositorySink (String repos);
	public String getQueue();
	public void setQueue(String q);

}
