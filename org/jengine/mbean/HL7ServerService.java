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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.log4j.Category;
import org.jboss.system.ServiceMBeanSupport;

public class HL7ServerService extends ServiceMBeanSupport implements HL7ServerServiceMBean
{
   private HL7Server server;
   private String host;
   private Category log;

   public HL7ServerService()
   {
      log = Category.getInstance("HL7ServerService");
      this.server = new HL7Server();

      try {
         //host = System.getProperty("java.rmi.server.hostname");
         host = System.getProperty("jboss.bind.address");
      } catch(SecurityException e) {
      }
      try {
         if( host == null )
            host = InetAddress.getLocalHost().getHostName();
      } catch(IOException e) {
         log.error("Failed to get localhost name", e);
      }
   }


   public ObjectName getObjectName(MBeanServer server, ObjectName name)
      throws MalformedObjectNameException
   {
            return new ObjectName(OBJECT_NAME);
   }

   public String getName()
   {
      return "HL7Server";
   }

   public void startService() throws Exception
   {
      server.start();
      log.info("Started server on port: "+server.getPort());
   }

   public void stopService()
   {
      server.stop();
   }

   public void setIFName(String n)
   {
        server.setIFName(n);
   }
   public String getIFName()
   {
        return server.getIFName();
   }

   	public void setPort(int port)
	{
		server.setPort(port);
	}

   public int getPort()
   {
   	return server.getPort();
   }

	public void setHost(String host)
	{
		this.host = host;
	}

   public String getHost()
   {
   	return host;
   }

   public String getBindAddress()
   {
      return server.getBindAddress();
   }

   public void setBindAddress(String host) throws UnknownHostException
   {
      server.setBindAddress(host);
   }

   public int getBacklog()
   {
      return server.getBacklog();
   }

   public void setBacklog(int backlog)
   {
      server.setBacklog(backlog);
   }
   
	public void setQueues(String q)
	{
		server.setQueues(q);
	}
	
	public String getQueues()
	{
		return server.getQueues();
	}

        public boolean getConnectionStatus()
        {
            return server.getConnectionStatus();
        }
        public int getNumberMessages()
        {
            return server.getNumberMessages();
        }
        public String getTimeStampLastMsg()
        {
            return server.getTimeStampLastMsg();
        }

}
