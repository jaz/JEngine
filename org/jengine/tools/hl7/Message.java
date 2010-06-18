/*
 * Message.java
 *
 * Created on 03 July 2001, 09:16
 */

package org.jengine.tools.hl7;

import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author  alan shields
 * @version 
 */
public class Message extends Object {

  
    /**
     * encoding characters.
     * We always encode outgoing messages with '|^~\&' and only change
     * this value if we read a different set of encoding characters from an 
     * incoming message.
     */
    private char encoding[]={'|','^','~','\\','&'};
        
    public boolean headerExists = false;
    public Field sendingApplication;
    public Field sendingFacility;
    public Field receivingApplication;
    public Field receivingFacility;
    public Field dateTimeOfMessage;
    public Field Security;
    public Field messageType;
    public Field messageControlID;
    public Field processingID;
    public Field versionID;
    public Field sequenceNumber;
    public Field continuationPointer;
    public Field acceptAcknowledgementType;
    public Field applicationAcknowledgementType;
    public Field countryCode;
    public Field characterSet;
    public Field principalLanguageOfMessage;
    
    private Vector segments;
  
    /** Creates new Message */
    public Message() {
      segments=new Vector();
      sendingApplication=new Field();
      sendingFacility=new Field();
      receivingApplication=new Field();
      receivingFacility=new Field();
      dateTimeOfMessage=new Field();
      Security=new Field();
      messageType=new Field();
      messageControlID=new Field();
      processingID=new Field();
      versionID=new Field();
      sequenceNumber=new Field();
      continuationPointer=new Field();
      acceptAcknowledgementType=new Field();
      applicationAcknowledgementType=new Field();
      countryCode=new Field();
      characterSet=new Field();
      principalLanguageOfMessage=new Field();
    }
    
    public void addSegment(Segment segment) {
      segments.add(segment);
    }
  
    public void removeAllSegments(Vector _segments) {
        for(Iterator it=_segments.iterator(); it.hasNext();) {
            Segment seg=(Segment)it.next();
	    removeSegment(seg);
        }
    }

    public void removeSegment(Segment segment) {
        for(Iterator it=segments.iterator(); it.hasNext();) {
            Segment seg=(Segment)it.next();
            if(seg.getName().equals(segment.getName()) == true) {
                if (segments.remove(seg) == true)
                    break;
            }
        }
        segments.trimToSize();
    }

    public void kill() {
    	suicide();
    }
    
    public void suicide() {
        for(Iterator it=segments.iterator(); it.hasNext();) {
            Segment seg=(Segment)it.next();
            segments.remove(seg);
        }
    }
    
    public Iterator getSegments() {
      return segments.iterator(); 
    }
    
    /**
     * Returns the first segment of the specified type.
     * If you need to access multiple segments of the same type use the 
     * getSegments() function below this one.
     */
    public Segment getSegment(String segment) {
      for(Iterator it=segments.iterator();it.hasNext();) { 
        Segment seg=(Segment)it.next(); 
        if(seg.getName().equals(segment)) return seg;
      }
      return null;
    }
    
    /*
     * return Iterator of Segments type "segment"
     */
    public Vector getSegments(String segment) {
        Vector v = new Vector();
        for(Iterator it=segments.iterator();it.hasNext();) {
            Segment seg=(Segment)it.next();
            if(seg.getName().equals(segment)) v.add(seg);
        }
        System.out.println("# segments of type " + segment + " = " + v.size());
        return v;
    }

    public char[] getEncoding() {
      return encoding; 
    }
    
    public char getFieldSeparator() {
      return encoding[0]; 
    }
    
    public char getComponentSeparator() {
      return encoding[1]; 
    }
    
    public char getSubComponentSeparator() {
      return encoding[4];  
    }
    
    public char getRepetitionSeparator() {
      return encoding[2]; 
    }
    
    public char getEscapeCharacter() {
      return encoding[3];
    }
  
    /**
     * Write this message to a stream.
     */
    public void write(java.io.Writer os) throws Exception {
        //os.write('\u000b');
        if (headerExists == true) {
            generateHeader().write(os,encoding);
            os.write(13);
        }
        for(Iterator it=segments.iterator();it.hasNext();) {
            Segment seg=(Segment)it.next();
            seg.write(os,encoding);
            os.write(13);
        }
        //os.write(28);
        //os.write('\u001c' + "\r");
        os.flush();            
    }
    
    /**
     * A kludge method so that a org.jengine.internal.bsh.Database object
     * can kill a message once it's done dealing with the database.
     */
    public void destroy() {
    	messageType = new Field("KILL");    	
    }
    
    /**
     * read a message from a stream.
     */
    public static Message read(java.io.Reader is) throws Exception {
      // read the Header first
      java.io.BufferedReader br=new java.io.BufferedReader(is);
      Message msg=new Message();
      Segment seg=Segment.read(br,msg.encoding);
      if (seg.getName().equals("MSH") == true)
      {
          msg.setHeaderExists();
          msg.processHeader(seg);
      }
      else
          msg.addSegment(seg);
      try {
        while(br.ready()) {
          msg.addSegment(Segment.read(br,msg.encoding));
        }
      }
      catch(Exception e) {
      }
      return msg;
    }

    private void setHeaderExists()
    {
        headerExists = true;
    }
    
    /**
     * Setup message name & decoding rules for an incoming message.
     */
    private void processHeader(Segment msh) {

      sendingApplication=msh.getField(3);
      sendingFacility=msh.getField(4);
      receivingApplication=msh.getField(5);
      receivingFacility=msh.getField(6);
      dateTimeOfMessage=msh.getField(7);
      Security=msh.getField(8);
      messageType=msh.getField(9);
      messageControlID=msh.getField(10);
      processingID=msh.getField(11);
      versionID=msh.getField(12);
      sequenceNumber=msh.getField(13);
      continuationPointer=msh.getField(14);
      acceptAcknowledgementType=msh.getField(15);
      applicationAcknowledgementType=msh.getField(16);
      countryCode=msh.getField(17);
      characterSet=msh.getField(18);
      principalLanguageOfMessage=msh.getField(19);
       
    }
    
    /**
     * Generate a header for the current message
     */
    private Segment generateHeader() {
      Segment hdr=new Segment("MSH");
      
      hdr.setField(3,sendingApplication);
      hdr.setField(4,sendingFacility);
      hdr.setField(5,receivingApplication);
      hdr.setField(6,receivingFacility);
      hdr.setField(7,dateTimeOfMessage);
      hdr.setField(8,Security);
      hdr.setField(9,messageType);
      hdr.setField(10,messageControlID);
      hdr.setField(11,processingID);
      hdr.setField(12,versionID);
      hdr.setField(13,sequenceNumber);
      hdr.setField(14,continuationPointer);
      hdr.setField(15,acceptAcknowledgementType);
      hdr.setField(16,applicationAcknowledgementType);
      hdr.setField(17,countryCode);
      hdr.setField(18,characterSet);
      hdr.setField(19,principalLanguageOfMessage);
      
      return hdr;
    }
    
}
