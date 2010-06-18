package repos;

import java.util.Set;
import java.util.Collections;

/** A business entity class representing an HL7 Message.
  * 
  * @author Jacek Zagorski
  * @since 1.0
  * @hibernate.class  table="MESSAGE"
  */
public class Message {

    /** This Messages's identifier field.
      */
    private long id;

    private String sendingApp;
    private String receiveApp;
    private String receiveFac;
    private String msgTimestamp;
    private String msgType;
    private String msgControlId;
    private Report report;

    /** The getter method for this Messages's identifier.
      *
      * @hibernate.id  generator-class="native"
      */
    public long getId() {
        return(id);
    }

    /** The setter method for this Messages's identifier.
      */
    public void setId(long lId) {
        id = lId;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getSendingApp() {
        return(sendingApp);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setSendingApp(String s) {
        sendingApp = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getReceiveApp() {
        return(receiveApp);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setReceiveApp(String s) {
        receiveApp = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getReceiveFac() {
        return(receiveFac);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setReceiveFac(String s) {
        receiveFac = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getMsgTimestamp() {
        return(msgTimestamp);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setMsgTimestamp(String s) {
        msgTimestamp = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getMsgType() {
        return(msgType);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setMsgType(String s) {
        msgType = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getMsgControlId() {
        return(msgControlId);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setMsgControlId(String s) {
        msgControlId = s;
    }

    public Report getReport() {
        return(report);
    }
    public void setReport(Report r) {
        report = r;
    }

    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append("Message {id=" + id);
	sb.append(",sendingApp=" + sendingApp);
	sb.append(",receiveApp=" + receiveApp);
	sb.append(",receiveFac=" + receiveFac);
	sb.append(",msgTimestamp=" + msgTimestamp);
	sb.append(",msgType=" + msgType);
	sb.append(",msgControlId=" + msgControlId);
	sb.append("}");
	return sb.toString();
    }
}
