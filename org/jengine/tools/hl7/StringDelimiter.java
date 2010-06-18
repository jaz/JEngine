package org.jengine.tools.hl7;

/*
 * JEngine Copyright (C) 2001
 *  Jacek Zagorski, Shasta NetWorks LLC
 *  Jeff Gunther, Intalgent Technologies, LLC
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * $Id: StringDelimiter.java,v 1.2 2005/11/10 22:12:20 jaz Exp $
 * $Revision: 1.2 $
 */

import java.util.NoSuchElementException;

public class StringDelimiter
{
	private String string;
  	private String delimiter;
	private int cursor, delimiterLen;

	public StringDelimiter(String string, String delimiter)
	{
		this.string = new String(string);
		this.delimiter = new String(delimiter);
		delimiterLen = delimiter.length();
		initialize();
  	}

	public StringDelimiter(String string, char delimiter)
	{
		//System.out.println("\nDelim=(" + String.valueOf(delimiter) + ")");
		this.string = new String(string);
		this.delimiter = new String(String.valueOf(delimiter));
		delimiterLen = 1;
		initialize();
		//System.out.println("\nDelim=(" + this.delimiter + "), len=" + delimiterLen);
  	}

	public int totalTokens()
	{
    		int oldCursor = cursor;
	    	cursor = 0;
	    	int total = countTokens();
    		cursor = oldCursor;
	    	return total;
	}

  	public int countTokens()
  	{
    	if (!stateOk())
    		return 0;
    	int count = 0, i = string.indexOf(delimiter, cursor), j = 0;
    	while (i != -1)
    	{
      		j = i;
      		i = string.indexOf(delimiter, i + delimiterLen);
      		count++;
    	}
    	if (j < string.length() && !string.substring(j).equals(delimiter))
    	  count++;
    	return count;
  	}


	public boolean hasMoreTokens()
	{
		if (!stateOk())
			return false;
		return string.indexOf(delimiter, cursor) != -1 ||
		cursor < string.length();
	}


	public boolean hasMoreElements()
	{
		return hasMoreTokens();
	}

	public String nextToken()
	{
		if (!stateOk())
		  throw new NoSuchElementException(
			"StringDelimiter.nextToken(): null or empty string.");
		int i = string.indexOf(delimiter, cursor);
		if (i == -1)
		{
	  		if (cursor < string.length())
	  		{
				String token = string.substring(cursor);
				cursor = string.length();
				return token;
	  		}
	  		else
				throw new NoSuchElementException(
			  "StringDelimiter.nextToken(): no more tokens.");
		}
		else
		{
	  		String token = string.substring(cursor, i);
	  		cursor = i + delimiterLen;
	  		return token;
		}
	}


	public String nextToken(String delimiter)
	{
		if (!stateOk())
		  throw new NoSuchElementException(
		  "StringDelimiter.nextToken(): null or empty string or delimiter.");
		this.delimiter = delimiter;
		delimiterLen = delimiter.length();
		return nextToken();
	}


	public Object nextElement()
	{
		return (Object) nextToken();
	}


	public void reset()
	{
		initialize();
	}

  	private boolean stateOk()
  	{
  		return !(string == null || delimiter == null ||
  		    string.length() == 0 || delimiter.length() == 0 ||
  		    string.equals(delimiter));
  	}

  	private void initialize()
  	{
  		cursor = 0;
  	}

}
