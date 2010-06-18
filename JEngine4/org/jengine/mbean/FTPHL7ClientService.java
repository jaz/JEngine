/*
 * 	JEngine Copyright (C) 2006
 *  Jacek Zagorski, Shasta NetWorks LLC
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.jengine.mbean;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.log4j.Category;
import org.jboss.system.ServiceMBeanSupport;

public class FTPHL7ClientService extends ServiceMBeanSupport implements FTPHL7ClientServiceMBean
{
	private FTPHL7Client client;
	private Category log;
	
	public FTPHL7ClientService()
	{
		log = Category.getInstance("FTPHL7ClientService");
		client = new FTPHL7Client();
	}
	
	public ObjectName getObjectName(MBeanServer server, ObjectName name)
		throws MalformedObjectNameException
	{
		return new ObjectName(OBJECT_NAME);
	}
	
	public String getName()
	{
		return "FTPHL7Client";
	}
	
	public void startService() throws Exception
	{
		log.debug("startService()");
		client.start();
	}
	
	public void stopService()
	{
		log.debug("stopService()");
		client.stop();
	}
	
	public void setIFName(String n) { client.setIFName(n); }
	public String getIFName() { return client.getIFName(); }
	public void setHostname(String host) { client.setHostname(host); }
	public String getHostname() { return client.getHostname(); }
	public String getIPAddress() { return client.getIPAddress(); }

	public void setUsername(String s) { client.setUsername(s); }
	public String getUsername() { return client.getUsername(); }
	public void setPassword(String s) { client.setPassword(s); }
	public String getPassword() { return client.getPassword(); }
	public void setPollInterval(int n) { client.setPollInterval(n); }
	public int getPollInterval() { return client.getPollInterval(); }

	public void setWorkingDirectory(String d) { client.setWorkingDirectory(d); }
	public String getWorkingDirectory() { return client.getWorkingDirectory(); }
	public void setWorkingDirectoryLocal(String s) { client.setWorkingDirectoryLocal(s); }
	public String getWorkingDirectoryLocal() { return client.getWorkingDirectoryLocal(); }
	public void setMatchFilePattern(String s) { client.setMatchFilePattern(s); }
	public String getMatchFilePattern() { return client.getMatchFilePattern(); }
	public void setPassiveMode(boolean b) { client.setPassiveMode(b); }
	public boolean getPassiveMode() { return client.getPassiveMode(); }
	public void setDefaultDateFormatStr(String s) { client.setDefaultDateFormatStr(s); }
	public String getDefaultDateFormatStr() { return client.getDefaultDateFormatStr(); }
	public void setRecentDateFormatStr(String s) { client.setRecentDateFormatStr(s); }
	public String getRecentDateFormatStr() { return client.getRecentDateFormatStr(); }
	
	public int getBacklog() { return client.getBacklog(); }
	public void setBacklog(int backlog) { client.setBacklog(backlog);	}
	
	public void setQueues(String q) { client.setQueues(q); }
	public String getQueues() { return client.getQueues(); }
	
	public boolean getConnectionStatus() { return client.getConnectionStatus(); }
	public int getNumberMessages()	{ return client.getNumberMessages(); }
	public String getTimeStampLastMsg() { return client.getTimeStampLastMsg(); }
	
}
