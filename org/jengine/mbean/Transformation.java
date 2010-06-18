package org.jengine.mbean;

public interface Transformation {

  /**
  * initialise the transformation engine.
  */
  public void init(String ifName,String props);

  /**
  * perform transformation of an hl7 message.
  */
  public String transform(String message);

}
