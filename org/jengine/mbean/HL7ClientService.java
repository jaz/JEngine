/*
 * 	JEngine Copyright (C) 2001
 *  	Jacek Zagorski, Shasta NetWorks LLC
 *  	Jeff Gunther, Intalgent Technologies, LLC
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

import javax.management.ObjectName;
import javax.management.MalformedObjectNameException;
import javax.management.MBeanServer;

import org.jboss.system.ServiceMBeanSupport;

public class HL7ClientService extends ServiceMBeanSupport implements HL7ClientServiceMBean
{

	private HL7Client client;
	
	public HL7ClientService()
	{
		this.client = new HL7Client();
	}
	
	public ObjectName getObjectName(MBeanServer server, ObjectName name)
	  throws MalformedObjectNameException
	{
            return new ObjectName(OBJECT_NAME);
	}

	public String getName()
	{
	  	return "HL7Client";
	}

	public void startService() throws Exception
	{
	  	client.start();
	}

	public void stopService()
	{
	  	client.stop();
	}

        public void setIFName(String n)
        {
                client.setIFName(n);
        }
        public String getIFName()
        {
                return client.getIFName();
        }

        public void setPort(int port)
	{
		client.setPort(port);
	}

	public int getPort()
	{
		return client.getPort();
	}

        public void setResendFailedCount(int t)
	{
		client.setResendFailedCount(t);
	}

	public int getResendFailedCount()
	{
		return client.getResendFailedCount();
	}

        public void setResendRetryInterval(int t)
	{
		client.setResendRetryInterval(t);
	}

	public int getResendRetryInterval()
	{
		return client.getResendRetryInterval();
	}

        public void setConnectRetryInterval(int t)
	{
		client.setConnectRetryInterval(t);
	}

	public int getConnectRetryInterval()
	{
		return client.getConnectRetryInterval();
	}

	public void setIPAddress(String ip)
	{
		client.setIPAddress(ip);
	}

	public String getIPAddress()
	{
		return client.getIPAddress();
	}

	public void setQueue(String q)
	{
		client.setQueue(q);
	}

	public String getQueue()
	{
		return client.getQueue();
	}

	public void setQueueError(String q)
	{
		client.setQueueError(q);
	}

	public String getQueueError()
	{
		return client.getQueueError();
	}
        public boolean getConnectionStatus()
        {
            return client.getConnectionStatus();
        }
        public int getNumberMessages()
        {
            return client.getNumberMessages();
        }
        public int getNumberMessagesFailed()
        {
            return client.getNumberMessagesFailed();
        }
        public String getTimeStampLastMsg()
        {
            return client.getTimeStampLastMsg();
        }

        public boolean getOnDemandTCPConnect()
        {
            return client.getOnDemandTCPConnect();
        }
        public void setOnDemandTCPConnect(boolean c)
        {
                client.setOnDemandTCPConnect(c);
        }

}
