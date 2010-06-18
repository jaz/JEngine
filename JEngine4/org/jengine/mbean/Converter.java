package org.jengine.mbean;

public interface Converter {

  /**
  * initialize the converter engine used to convert a text message to HL7
  */
  public void init(String ifName,String props);

  /**
  * perform conversion processing text to hl7 message.
  */
  public String execute(String message);

}
