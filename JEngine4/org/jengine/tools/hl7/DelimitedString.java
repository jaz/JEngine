/*
 * DelimitedString.java
 *
 * Created on 06 July 2001, 08:19
 */

package org.jengine.tools.hl7;

/**
 * Split up a string using a delimiter character.
 * this is used for decoding hl7 messages.
 * Does not currently support escape characters.
 * @author  alan shields
 * @version 
 */
public class DelimitedString extends Object {
     
      private String val;
      private char del;
      private char esc;
      
      public DelimitedString(String val,char del,char esc) {
        this.val=val;
        this.del=del;
        this.esc=esc;
      }
      
      public boolean hasValues() {
        return(! val.equals("")); 
      }
      
      public String getValue() {
        String rv=null;
        int pos=val.indexOf(del);
        if(pos==-1) {
          rv=val;
          val="";
        }
        else {
          rv=val.substring(0,pos);
          val=val.substring(pos+1);
        }
        return rv;
      }
      

}
