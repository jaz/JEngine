/*
 * Segment.java
 *
 * Created on 03 July 2001, 09:17
 */

package org.jengine.tools.hl7;

import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author  alan shields
 * @version 
 */
public class Segment extends Object {

    private String name;
    private Vector fields;
  
    /** Creates new Segment */
    public Segment(String name) {
      this.name=name;
      fields=new Vector();
    }
    
    
    public String getName() {
      return name; 
    }
    
    public void setField(int pos,Field field) {
      if(fields.size()<pos) fields.setSize(pos);
      fields.setElementAt(field,pos-1);
    }

    public void setValue(int pos,String value) {
      Field fld=getField(pos);
      fld.set(value);
    }
    
    public Field getField(int pos) {
      if(fields.size()<pos) fields.setSize(pos);
      return (Field)fields.elementAt(pos-1);
    }
    
    /**
     * Read a segment from an input stream.
     */
    static Segment read(java.io.BufferedReader is,char encoding[]) throws Exception {
      
      String line=is.readLine();
      Segment seg=null;

      seg=new Segment(line.substring(0,3));
      char dlim=line.charAt(3);
      String data=line.substring(4);
        
      int start=1;
        
      if(seg.name.equals("MSH")) {
          
        // get the encoding characters out first
        DelimitedString ds=new DelimitedString(data,dlim,' ');
        String enc=ds.getValue();
          
        encoding[0]=dlim;
        encoding[1]=enc.charAt(0);
        encoding[2]=enc.charAt(1);
        if(enc.length()>2)encoding[3]=enc.charAt(2);
        if(enc.length()>3)encoding[4]=enc.charAt(3);
          
        data=line.substring(5+enc.length());
        start=3;
      }
        
      DelimitedString ds=new DelimitedString(data,encoding[0],encoding[3]);
      
      while(ds.hasValues()) {
        String val=ds.getValue();
        Field fld=new Field();
        fld.decode(val,encoding);
        seg.setField(start,fld);
        start++;
      }
      
      return seg;
    }
    

    /**
     * write a segment to an output stream.
     */
    void write(java.io.Writer os,char encoding[]) throws Exception {
      os.write(name);
      Iterator it=fields.iterator();
      // handling for msh - ignore the first two fields - but write the encoding characters directly
      if(name.equals("MSH")) {
        it.next();
        it.next();
        os.write(encoding);
      }
      
      while(it.hasNext()) {
        os.write(encoding[0]);
        Field fld=(Field)it.next();
        if(fld!=null) os.write(fld.encode(encoding));
      }
    }
    
}
