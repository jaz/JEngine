/*
 * Field.java
 *
 * Created on 05 July 2001, 13:46
 */

package org.jengine.tools.hl7;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Vector;

/**
 * Represents a field.
 * fields may contain multiple components, and each component may contain
 * multiple sub components. In addition the field may repeat multiple times.
 * Does not currently support escape characters.
 * @author  alan shields
 * @version
 */
public class Field extends java.lang.Object {
  
  private Vector fields;
  
  /**
   * Creates new Field
   */
  public Field(String val) {
    fields=new Vector();
    set(val);
  }
  
  /**
   * Creates new Field
   */
  public Field() {
    fields=new Vector();
  }
  
  
  /**
   * Decode a string.
   */
  public void decode(String coded,char encoding[]) {
    fields=new Vector();
    DelimitedString repeats=new DelimitedString(coded,encoding[2],encoding[3]);
    int repeat=1;
    while(repeats.hasValues()) {
      DelimitedString components=new DelimitedString(repeats.getValue(),encoding[1],encoding[3]);
      int component=1;
      while(components.hasValues()) {
        DelimitedString subcomponents=new DelimitedString(components.getValue(),encoding[4],encoding[3]);
        int subcomponent=1;
        while(subcomponents.hasValues()) {
          set(repeat,component,subcomponent,subcomponents.getValue());
          subcomponent++;
        }
        component++;
      }
      repeat++;
    }
  }
  
  /**
   * create an encoded string.
   */
  public String encode(char encoding[]) {
    StringBuffer coded=new StringBuffer();
    try {
      boolean firstField=true;
      for(Iterator fieldit=fields.iterator();fieldit.hasNext();firstField=false) {
        if(!firstField)coded.append(encoding[2]);
        boolean firstComponent=true;
        java.util.Vector components=(java.util.Vector)fieldit.next();
        if(components!=null) {
          for(Iterator componentit=components.iterator();componentit.hasNext();firstComponent=false) {
            if(!firstComponent)coded.append(encoding[1]);
            boolean firstSubComponent=true;
            java.util.Vector subcomponents=(java.util.Vector)componentit.next();
            if (subcomponents!=null) {
              for(Iterator subit=subcomponents.iterator();subit.hasNext();firstSubComponent=false) {
                if(!firstSubComponent)coded.append(encoding[4]);
                coded.append((String)subit.next());
              }
            }
          }
        }
      }
    }
    catch(Exception e) {
      System.out.println("Exception encoding field "+e+" got :"+coded.toString());
    }
    return coded.toString();
  }
  
  
  /**
   * Return the number of field instances.
   */
  public int getNoFields() {
    return(fields.size());
  }
  
  /**
   * Return the number of components for a field instance.
   */
  public int getNoComponents(int field) {
    Vector components=(Vector)fields.elementAt(field);
    if(components!=null) return components.size();
    else return 0;
  }
  
  /**
   * Return the number of subcomponents of a component of a field instance.
   */
  public int getNoSubComponents(int field,int component) {
    Vector components=(Vector)fields.elementAt(field);
    if(components!=null) {
      Vector subcomponents=(Vector)components.elementAt(component);
      if(subcomponents!=null) return subcomponents.size();
    }
    return 0;
  }
  
  /**
   * Set the value of a subcomponent of a component of a field
   */
  public void set(int field,int component,int subcomponent,String value) {
    //System.out.println("set "+field+","+component+","+subcomponent+"="+value);
    if(fields.size()<field) fields.setSize(field);
    Vector components=(Vector)fields.elementAt(field-1);
    if(components==null) {
      components=new Vector();
      fields.setElementAt(components,field-1);
    }
    if(components.size()<component) components.setSize(component);
    Vector subcomponents=(Vector)components.elementAt(component-1);
    if(subcomponents==null) {
      subcomponents=new Vector();
      components.setElementAt(subcomponents,component-1);
    }
    if(subcomponents.size()<subcomponent) subcomponents.setSize(subcomponent);
    subcomponents.setElementAt(value,subcomponent-1);
  }
  
  /**
   * Set the value of a component of a field
   */
  public void set(int field,int component,String value) {
    set(field,component,1,value);
  }
  
  /**
   * set the value of a field
   */
  public void set(int field,String value) {
    set(field,1,1,value);
  }
  
  /**
   * Set the value of the first field
   */
  public void set(String value) {
    set(1,1,1,value);
  }
  
  /**
   * get the value of a subcomponent of a component of a field
   */
  public String get(int field,int component,int subcomponent) {
    if(fields.size()<field) fields.setSize(field);
    Vector components=(Vector)fields.elementAt(field-1);
    if(components==null) {
      components=new Vector();
      fields.setElementAt(components,field-1);
    }
    if(components.size()<component) components.setSize(component);
    Vector subcomponents=(Vector)components.elementAt(component-1);
    if(subcomponents==null) {
      subcomponents=new Vector();
      components.setElementAt(subcomponents,component-1);
    }
    if(subcomponents.size()<subcomponent) subcomponents.setSize(subcomponent);
    String val=(String)subcomponents.elementAt(subcomponent-1);
    if (val==null) val="";
    return val;
  }
  
  /**
   * get the value of a component of a field
   */
  public String get(int field,int component) {
    return get(field,component,1);
  }
  
  /**
   * get the value of a field
   */
  public String get(int field) {
    return get(field,1,1);
  }
  
  /**
   * get the value of the first field
   */
  public String get() {
    return get(1,1,1);
  }
  
  /**
   * Returns the value of the first field.
   */
  public String toString() {
    return get();
  }
  
  public static Date timeStampToDate(String timeStamp) {
    Calendar gc=new GregorianCalendar();
    int len=timeStamp.length();
    
    gc.set(Calendar.YEAR,Integer.parseInt(timeStamp.substring(0,4)));
    if(len>=6) {
      gc.set(Calendar.MONTH,Integer.parseInt(timeStamp.substring(4,6)));
      
      if(len >=8) {
        gc.set(Calendar.DAY_OF_MONTH,Integer.parseInt(timeStamp.substring(6,8)));
        
        if(len >=12) {
          gc.set(Calendar.HOUR,Integer.parseInt(timeStamp.substring(8,10)));
          gc.set(Calendar.MINUTE,Integer.parseInt(timeStamp.substring(10,12)));
          
          if(len >=14) {
            gc.set(Calendar.SECOND,Integer.parseInt(timeStamp.substring(12,14)));
          }
          
        }
      }
    }
    return gc.getTime();
  }
  
  public static String dateToTimeStamp(Date date) {
    return null;
  }
  
}
