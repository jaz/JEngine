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

import java.net.UnknownHostException;

import org.jboss.system.ServiceMBean;

public interface HL7ServerServiceMBean extends ServiceMBean
{

	public static final String OBJECT_NAME = ":service=Interface";

	public void setPort(int port);
	public int getPort();

        public void setIFName(String n);
        public String getIFName();

	public String getHost();
	public void setHost(String host);

	public String getBindAddress();
	public void setBindAddress(String host) throws UnknownHostException;

	public int getBacklog();
	public void setBacklog(int backlog);

	public void setQueues(String q);
	public String getQueues();

        public boolean getConnectionStatus();
        public int getNumberMessages();
        public String getTimeStampLastMsg();

}
