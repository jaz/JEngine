package repos;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/** A business entity class representing an Report - HL7 OBR.
  * 
  * @author Jacek Zagorski
  * @since 1.0
  * @hibernate.class  table="REPORT"
  */
public class Report {

    /** This Report's identifier field.
      */
    private long id;

    private String placerOrdNum;
    private String fillerOrdNum;
    private String usId;
    private String usIdLong;
    private String usIdShort;
    private String obsTimestamp;
    private String ordProvId;
    private String ordProvLast;
    private String ordProvFirst;
    private String interpId;
    private String interpLast;
    private String interpFirst;
    private Patient patient;
    private Set reportContents;
    private Set messages;

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
    public String getPlacerOrdNum() {
        return(placerOrdNum);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setPlacerOrdNum(String s) {
        placerOrdNum = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getFillerOrdNum() {
        return(fillerOrdNum);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setFillerOrdNum(String s) {
        fillerOrdNum = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getUsId() {
        return(usId);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setUsId(String s) {
        usId = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getUsIdShort() {
        return(usIdShort);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setUsIdShort(String s) {
        usIdShort = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getUsIdLong() {
        return(usIdLong);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setUsIdLong(String s) {
        usIdLong = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getObsTimestamp() {
        return(obsTimestamp);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setObsTimestamp(String s) {
        obsTimestamp = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getOrdProvId() {
        return(ordProvId);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setOrdProvId(String s) {
        ordProvId = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getOrdProvLast() {
        return(ordProvLast);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setOrdProvLast(String s) {
        ordProvLast = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getOrdProvFirst() {
        return(ordProvFirst);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setOrdProvFirst(String s) {
        ordProvFirst = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getInterpId() {
        return(interpId);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setInterpId(String s) {
        interpId = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getInterpLast() {
        return(interpLast);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setInterpLast(String s) {
        interpLast = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getInterpFirst() {
        return(interpFirst);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setInterpFirst(String s) {
        interpFirst = s;
    }

    public void setPatient(Patient p) {
	patient = p;
    }
    public Patient getPatient() {
	return patient;
    }
    public void setReportContents(Set s) {
	reportContents = s;
    }
    public void addReportContents(ReportContents rc) {
	if (reportContents==null) reportContents = new HashSet();
	rc.setReport(this);
	reportContents.add(rc);
    }
    public Set getReportContents() {
	return reportContents;
    }

    public void setMessages(Set s) {
	messages = s;
    }
    public void addMessage(Message m) {
	if (messages==null) messages = new HashSet();
	m.setReport(this);
	messages.add(m);
    }
    public Set getMessages() {
	return messages;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Report {id=" + id);
        sb.append(",placerOrdNum=" + placerOrdNum);
        sb.append(",fillerOrdNum=" + fillerOrdNum);
        sb.append(",usId=" + usId);
        sb.append(",usIdLong=" + usIdLong);
        sb.append(",usIdShort=" + usIdShort);
        sb.append(",obsTimestamp=" + obsTimestamp);
        sb.append(",ordProvId=" + ordProvId);
        sb.append(",ordProvLast=" + ordProvLast);
        sb.append(",ordProvFirst=" + ordProvFirst);
        sb.append(",interpId=" + interpId);
        sb.append(",interpLast=" + interpLast);
        sb.append(",interpFirst=" + interpFirst);
        sb.append("}");
        return sb.toString();
    }

}
