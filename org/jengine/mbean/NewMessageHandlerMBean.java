package org.jengine.mbean;

import java.util.Date;

import org.jboss.system.Service;

public interface NewMessageHandlerMBean extends Service{
	public Date getLastPollTime ();
	public void setLastPollTime(Date lastPollTime);
	public String getLastPollStatus ();
	public void setLastPollStatus (String lastPollStatus);
	public int getPollInterval ();
	public void setPollInterval (int interval);

}
