/*
 * 	JEngine Copyright (C) 2003
 *  	Jacek Zagorski, Shasta NetWorks LLC
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


public class HL7FilterService extends ServiceMBeanSupport implements HL7FilterServiceMBean
{

	private HL7Filter filter;
        private String filterClass;
        private String filterProperties;
        private Category log;

	public HL7FilterService()
	{
                log = Category.getInstance("HL7FilterService");
		this.filter = new HL7Filter();
	}


	public ObjectName getObjectName(MBeanServer server, ObjectName name)
	  throws MalformedObjectNameException
	{
            return new ObjectName(OBJECT_NAME);
	}

	public String getName()
	{
	  	return "HL7Filter";
	}

        public void setFilterClass(String filterClass) {
          try {
            Class cls=Class.forName(filterClass);
            Filter trx=(Filter)cls.newInstance();
            filter.setFilter(trx);
            log.info("filterClass set to "+filterClass);
            this.filterClass=filterClass;
          }
          catch(Exception e) {
            this.filterClass="Could not set filterClass class:"+e;
            log.error("Could not set filterClass class",e);
          }
        }

        public String getFilterClass() {
          return filterClass;
        }

        public void setFilterProperties(String properties) {
          this.filterProperties=properties;
          filter.setFilterProperties(properties); 
        }

        public String getFilterProperties() {
          return filterProperties;
        }

	public void startService() throws Exception
	{
	  	filter.start();
	}

	public void stopService()
	{
	  	filter.stop();
	}

        public void setIFName(String n)
        {
                filter.setIFName(n);
        }
        public String getIFName()
        {
                return filter.getIFName();
        }

	public void setInQueue(String q)
	{
		filter.setInQueue(q);
	}

	public String getInQueue()
	{
		return filter.getInQueue();
	}

	public void setOutQueue(String q)
	{
		filter.setOutQueue(q);
	}

	public String getOutQueue()
	{
		return filter.getOutQueue();
	}

}
