package org.jengine.mbean;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.jboss.system.ServiceMBeanSupport;

public class DbOutService extends ServiceMBeanSupport implements
		DbOutServiceMBean
{
	final private DbOutProcessor _dbOutPx;
	
	public DbOutService ()
	{
		_dbOutPx = new DbOutProcessor();
	}
	
	public ObjectName getObjectName(MBeanServer server, ObjectName name) throws MalformedObjectNameException
	{
		return new ObjectName(OBJECT_NAME);
	}


	public void startService ()
	{
		log.info("=== Starting " + this.getName() + " ===");
		_dbOutPx.start();
	}

	
	public void stopService ()
	{
		log.info("=== Stopping " + this.getName() + " ===");
		_dbOutPx.stop();
	}

	public String getScript ()
	{
		return _dbOutPx.getScript();
	}

	public void setScript(String script)
	{
		_dbOutPx.setScript(script);
	}

	
	public void setInQueue(String q)
	{
		_dbOutPx.setInQueue(q);
	}

	public String getInQueue()
	{
		return _dbOutPx.getInQueue();
	}

	//added 20070427, jaz
        public void setOutQueue(String q)
        {
                _dbOutPx.setOutQueue(q);
        }
        public String getOutQueue()
        {
                return _dbOutPx.getOutQueue();
        }
        public int getNumberMessages()
        {
            return _dbOutPx.getNumberMessages();
        }
        public int getNumberMessagesFailed()
        {
            return _dbOutPx.getNumberMessagesFailed();
        }
        public String getTimeStampLastMsg()
        {
            return _dbOutPx.getTimeStampLastMsg();
        }

}
