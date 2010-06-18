package org.jengine.mbean;

public interface Database {

  /**
  * initialise the database engine.
  */
  public void init(String ifName,String props);

  /**
  * perform database update from an hl7 message.
  */
  public String execute(String message);

}
