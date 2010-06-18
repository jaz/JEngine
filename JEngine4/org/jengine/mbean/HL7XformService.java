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

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.log4j.Category;
import org.jboss.system.ServiceMBeanSupport;


public class HL7XformService extends ServiceMBeanSupport implements
		HL7XformServiceMBean {

	private HL7Xform xform;

	private String transformation;

	private String transformationProperties;

	private Category log;

	public HL7XformService() {
		log = Category.getInstance("HL7XformService");
		this.xform = new HL7Xform();
	}

	public ObjectName getObjectName(MBeanServer server, ObjectName name)
			throws MalformedObjectNameException {
		return new ObjectName(OBJECT_NAME);
	}

	public String getName() {
		return "HL7Xform";
	}

	public void setTransformation(String transformation) {
		try {
			Class cls = Class.forName(transformation);
			Transformation trx = (Transformation) cls.newInstance();
			xform.setTransformation(trx);
			log.info("transformation set to " + transformation);
			this.transformation = transformation;
		} catch (Exception e) {
			this.transformation = "Could not set transformation class:" + e;
			log.error("Could not set transformation class", e);
		}
	}

	public String getTransformation() {
		return transformation;
	}

	public void setTransformationProperties(String properties) {
		this.transformationProperties = properties;
		xform.setTransformationProperties(properties);
	}

	public String getTransformationProperties() {
		return transformationProperties;
	}

	public void startService() throws Exception {
		xform.start();
	}

	public void stopService() {
		xform.stop();
	}

	public void setIFName(String n) {
		xform.setIFName(n);
	}

	public String getIFName() {
		return xform.getIFName();
	}

	public void setInQueue(String q) {
		xform.setInQueue(q);
	}

	public String getInQueue() {
		return xform.getInQueue();
	}

	public void setOutQueue(String q) {
		xform.setOutQueue(q);
	}

	public String getOutQueue() {
		return xform.getOutQueue();
	}

}
