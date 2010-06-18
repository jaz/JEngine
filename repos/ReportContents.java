package repos;

import java.util.Set;
import java.util.Collections;

/** A business entity class representing an HL7 Message.
  * 
  * @author Jacek Zagorski
  * @since 1.0
  * @hibernate.class  table="REPORT_CONTENTS"
  */
public class ReportContents {

    /** This ReportContents's identifier field.
      */
    private long id;

    private Integer position;
    private String valueType;
    private String obsrvId;
    private String obsrvShort;
    private String subId;
    private String obsrvValue;
    private String units;
    private String refRange;
    private String abnormFlags;
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
    public Integer getPosition() {
        return(position);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setPosition(Integer n) {
        position = n;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getValueType() {
        return(valueType);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setValueType(String s) {
        valueType = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getObsrvId() {
        return(obsrvId);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setObsrvId(String s) {
        obsrvId = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getObsrvShort() {
        return(obsrvShort);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setObsrvShort(String s) {
        obsrvShort = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getSubId() {
        return(subId);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setSubId(String s) {
        subId = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getObsrvValue() {
        return(obsrvValue);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setObsrvValue(String s) {
        obsrvValue = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getUnits() {
        return(units);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setUnits(String s) {
        units = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getRefRange() {
        return(refRange);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setRefRange(String s) {
        refRange = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getAbnormFlags() {
        return(abnormFlags);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setAbnormFlags(String s) {
        abnormFlags = s;
    }

    public void setReport(Report r) {
	report = r;
    }
    public Report getReport() {
	return report;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ReportContents {id=" + id);
        sb.append(",position=" + position);
        sb.append(",valueType=" + valueType);
        sb.append(",obsrvShort=" + obsrvShort);
        sb.append(",subId=" + subId);
        sb.append(",obsrvValue=" + obsrvValue);
        sb.append(",units=" + units);
        sb.append(",refRange=" + refRange);
        sb.append(",abnormFlags=" + abnormFlags);
        sb.append("}");
        return sb.toString();
    }

}
