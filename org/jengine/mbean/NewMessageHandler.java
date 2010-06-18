package org.jengine.mbean;

import java.util.Date;

import org.jboss.system.ServiceMBeanSupport;

public class NewMessageHandler extends ServiceMBeanSupport implements NewMessageHandlerMBean {

	private String _lastPollStatus;
	private int _pollInterval;
	private Date _lastPollTime;
	private NewMessageHandlerRunnable _nmhr;
	
	
	public String getLastPollStatus() {
		return _lastPollStatus;
	}

	public void setLastPollStatus (String lastPollStatus) {
		_lastPollStatus = lastPollStatus;
	}
	
	public Date getLastPollTime() {
		return _lastPollTime;
	}

	public void setLastPollTime(Date lastPollTime) {
		_lastPollTime = lastPollTime;
	}

	public int getPollInterval() {
		return _pollInterval;
	}

	public void setPollInterval(int pollInterval) {
		_pollInterval = pollInterval;

	}

	public void start() throws Exception {
		_nmhr = new NewMessageHandlerRunnable(_pollInterval);
		Thread th = new Thread(_nmhr);
		th.start();		
	}

	public void stop() {
		_nmhr.setKeepRunningFlag(false);
	}
}
