package org.jengine.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import repos.Report;

public class Utils
{
	final static private Logger log = Logger.getLogger("org.jengine.core");

	final static private String DELIM_PlacerOrdNum = ".";
	final static private String EMPTY_STRING_PRINTABLE = "\"\"";

	static public String buildPlacerOrdNum(String patientId, String accession, String profile)
	{
		final String placerOrdNum;

		StringBuffer sb = new StringBuffer();

		sb.append(getNonNull(patientId));
		sb.append(DELIM_PlacerOrdNum);
		sb.append(getNonNull(accession));
		sb.append(DELIM_PlacerOrdNum);
		sb.append(getNonNull(profile));

		placerOrdNum = sb.toString();

		return placerOrdNum;
	}

	static public Report createOrGetReport(Session session, String patientId, String accession, String profile)
	{
		final Report report;

		String placerOrdNum = buildPlacerOrdNum(patientId, accession, profile);
		log.debug("placerOrdNum=" + placerOrdNum);
		Query qryReport = session.getNamedQuery("ReportByPlacerOrdNum");
		qryReport.setString("placerOrdNum", placerOrdNum);
		Object obj = qryReport.uniqueResult();
		if (obj == null)
		{
			report = new Report();
			report.setPlacerOrdNum(placerOrdNum);
		}
		else
		{
			report = (Report) obj;
		}

		return report;
	}

	
	static public String formatDate (Date date)
	{
		final String dateStr;

		DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
		dateStr = df.format(date);

		return dateStr;
	}
	

	static public String getNonNull(String value)
	{
		final String nonNullValue;

		if (value == null)
		{
			nonNullValue = "";
		}
		else
		{
			nonNullValue = value;
		}

		return nonNullValue;
	}

	static public String getNonNull (String value, String msg)
	{
		String nonNullValue;
		
		if (value == null)
		{
			log.warn(msg + "is null: Using " + EMPTY_STRING_PRINTABLE);
			nonNullValue = "";
		}
		else
		{
			nonNullValue = value;
		}
		
		return nonNullValue;	
	}


	static public String getNonNullInt (Integer value)
	{
		String nonNullIntStr;
		
		if (value == null)
		{
			nonNullIntStr = "";
		}
		else
		{
			nonNullIntStr = value.toString();
		}
		
		return nonNullIntStr;	
	}


	static public String getNonNullInt (Integer value, String msg)
	{
		String nonNullIntStr;
		
		if (value == null)
		{
			log.warn(msg + "is null: Using " + EMPTY_STRING_PRINTABLE);
			nonNullIntStr = "";
		}
		else
		{
			nonNullIntStr = value.toString();
		}
		
		return nonNullIntStr;	
	}

	static public Date getNonNullDate (Date date)
	{
		Date nonNullDate;
		
		if (date == null)
		{
			nonNullDate = new Date();
		}
		else
		{
			nonNullDate = date;
		}
		
		return nonNullDate;	
	}


	static public Date getNonNullDate (Date date, String msg)
	{
		Date nonNullDate;
		
		if (date == null)
		{
			log.warn(msg + "is null: Using current date & time");
			nonNullDate = new Date();
		}
		else
		{
			nonNullDate = date;
		}
		
		return nonNullDate;	
	}
}
