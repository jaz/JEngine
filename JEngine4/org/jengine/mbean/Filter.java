package org.jengine.mbean;

public interface Filter {

  /**
  * initialise the filter engine.
  */
  public void init(String ifName,String props);

  /**
  * perform filter of an hl7 message.
  */
  public String execute(String message);

}
