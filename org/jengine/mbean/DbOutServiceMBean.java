package org.jengine.mbean;

import org.jboss.system.ServiceMBean;

public interface DbOutServiceMBean extends ServiceMBean
{
	public static final String OBJECT_NAME = ":service=Interface";
	
	public String getInQueue();
	public void setInQueue(String q);
	public String getScript ();
	public void setScript (String script);

        public void setOutQueue(String q);
        public String getOutQueue();
        public int getNumberMessages();
        public int getNumberMessagesFailed();
        public String getTimeStampLastMsg();

}
