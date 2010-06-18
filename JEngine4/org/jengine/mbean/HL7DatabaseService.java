/*
 * 	JEngine Copyright (C) 2004
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


public class HL7DatabaseService extends ServiceMBeanSupport implements HL7DatabaseServiceMBean
{

	private HL7Database database;
        private String databaseClass;
        private String databaseProperties;
        private Category log;

	public HL7DatabaseService()
	{
                log = Category.getInstance("HL7DatabaseService");
		this.database = new HL7Database();
	}


	public ObjectName getObjectName(MBeanServer server, ObjectName name)
	  throws MalformedObjectNameException
	{
            return new ObjectName(OBJECT_NAME);
	}

	public String getName()
	{
	  	return "HL7Database";
	}

        public void setDatabaseClass(String databaseClass) {
          try {
            Class cls=Class.forName(databaseClass);
            Database trx=(Database)cls.newInstance();
            database.setDatabase(trx);
            log.info("databaseClass set to "+databaseClass);
            this.databaseClass=databaseClass;
          }
          catch(Exception e) {
            this.databaseClass="Could not set databaseClass class:"+e;
            log.error("Could not set databaseClass class",e);
          }
        }

        public String getDatabaseClass() {
          return databaseClass;
        }

        public void setDatabaseProperties(String properties) {
          this.databaseProperties=properties;
          database.setDatabaseProperties(properties); 
        }

        public String getDatabaseProperties() {
          return databaseProperties;
        }

	public void startService() throws Exception
	{
	  	database.start();
	}

	public void stopService()
	{
	  	database.stop();
	}

        public void setIFName(String n)
        {
                database.setIFName(n);
        }
        public String getIFName()
        {
                return database.getIFName();
        }

	public void setInQueue(String q)
	{
		database.setInQueue(q);
	}

	public String getInQueue()
	{
		return database.getInQueue();
	}

	public void setOutQueue(String q)
	{
		database.setOutQueue(q);
	}

	public String getOutQueue()
	{
		return database.getOutQueue();
	}

        public int getNumberMessages()
        {
            return database.getNumberMessages();
        }
        public int getNumberMessagesFailed()
        {
            return database.getNumberMessagesFailed();
        }
        public String getTimeStampLastMsg()
        {
            return database.getTimeStampLastMsg();
        }

}
