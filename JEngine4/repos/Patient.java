package repos;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/** A business entity class representing an HL7 Message.
  * 
  * @author Jacek Zagorski
  * @since 1.0
  * @hibernate.class  table="PATIENT"
  */
public class Patient {

    /** This Patient's identifier field.
      */
    private long id;

    private String hhId;
    private String cciId;
    private String dob;
    private String sex;
    private String accountNum;
    private Set reports;
    private String _labId;

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
    public String getHhId() {
        return(hhId);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setHhId(String s) {
        hhId = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getCciId() {
        return(cciId);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setCciId(String s) {
        cciId = s;
    }

    /** The getter method for this Messages's labId.
     * @hibernate.property
     */
   public String getLabId() {
       return(_labId);
   }
   /** The setter method for this Message's messageControlId.
     */
   public void setLabId(String labId) {
       _labId = labId;
   }
    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getDob() {
        return(dob);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setDob(String s) {
        dob = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getSex() {
        return(sex);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setSex(String s) {
        sex = s;
    }

    /** The getter method for this Messages's messageControlId.
      * @hibernate.property
      */
    public String getAccountNum() {
        return(accountNum);
    }
    /** The setter method for this Message's messageControlId.
      */
    public void setAccountNum(String s) {
        accountNum = s;
    }

    public void setReports(Set s) {
        reports = s;
    }
    public void addReport(Report r) {
        if (reports==null) reports = new HashSet();
        r.setPatient(this);
        reports.add(r);
    }
    public Set getReports() {
        return reports;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Patient {id=" + id);
        sb.append(",hhId=" + hhId);
        sb.append(",cciId=" + cciId);
        sb.append(",dob=" + dob);
        sb.append(",sex=" + sex);
        sb.append(",accountNum=" + accountNum);
        sb.append("}");
        return sb.toString();
    }

}
