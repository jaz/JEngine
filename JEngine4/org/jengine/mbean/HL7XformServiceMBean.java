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


public interface HL7XformServiceMBean extends org.jboss.system.ServiceMBean
{
	public static final String OBJECT_NAME = ":service=Interface";

	public void setInQueue(String q);
	public String getInQueue();
	public void setOutQueue(String q);
	public String getOutQueue();
        public void setIFName(String n);
        public String getIFName();
        public void setTransformation(String transformation);
        public String getTransformation();
        public void setTransformationProperties(String properties);
        public String getTransformationProperties();

}
