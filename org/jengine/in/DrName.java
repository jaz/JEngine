package org.jengine.in;

import java.io.Serializable;

public class DrName implements Serializable
{
	private String _drName;
	private String _drCode;
	private String _epin;

	
	public String getDrCode()
	{
		return _drCode;
	}
	
	
	public void setDrCode(String code)
	{
		_drCode = code;
	}
	
	
	public String getDrName()
	{
		return _drName;
	}
	
	
	public void setDrName(String name)
	{
		_drName = name;
	}
	
	
	public String getEpin()
	{
		return _epin;
	}
	
	
	public void setEpin(String epin)
	{
		_epin = epin;
	}
}