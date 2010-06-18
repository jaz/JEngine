package org.jengine.internal.bsh;

import org.apache.log4j.Category;

public class Filter implements org.jengine.mbean.Filter {

  private bsh.Interpreter interpreter;
  private String script;
  private String ifName;
  private Category log;

  public Filter() {
  }

  public void init(String ifName,String props) {
    log = Category.getInstance(getClass()+":bsh:"+ifName);
    this.ifName=ifName;
    interpreter=new bsh.Interpreter();
    script=props;
  }

  public String execute(String message) {
    String rv=message;
    try {
      log.debug("in="+message);
      // decode the message 
      org.jengine.tools.hl7.Message msg=org.jengine.tools.hl7.Message.read(new java.io.StringReader(message));
      // pass the decoded message to beanshell
      interpreter.eval("org.jengine.tools.hl7.Message message");
      interpreter.set("message",msg);
      // pass the logging category to beanshell
      interpreter.eval("org.apache.log4j.Category log");
      interpreter.set("log",log);
      // call the script
      interpreter.eval(script);
      // encode the possibly modified message
      java.io.StringWriter sw=new java.io.StringWriter();
      if (msg.messageType.toString().equalsIgnoreCase("KILL") == true)
          rv = null;
      else
      {
          msg.write(sw);
          rv=sw.toString();
      }
      log.debug("out="+rv);
    }
    catch(Exception e) {
      log.error("Problem processing filter ",e);
    }
    return rv;
  }

}
