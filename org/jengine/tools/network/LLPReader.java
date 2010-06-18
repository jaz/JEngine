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
 * $Id: LLPReader.java,v 1.2 2005/11/10 22:12:26 jaz Exp $
 * $Revision: 1.2 $
 */

package org.jengine.tools.network;

import java.io.IOException;
import java.io.InputStream;

public class LLPReader
{
    private InputStream in;

    public LLPReader(InputStream input)
    {
        in = input;
    }

    public void close()
    {
        try
        {
            in.close();
        }
        catch (IOException ie)
        {
        }
    }
    
    public String readMsg()
        throws IOException
    {
        int i;
        do
        {
            i = in.read();
            if(i == -1)
                return null;
        } while(i != 11);
        StringBuffer stringbuffer = new StringBuffer();
        do
        {
            int j = in.read();
            if(j == -1)
                return null;
            if(j != 28)
                stringbuffer.append((char)j);
            else
                return stringbuffer.toString();
        } while(true);
    }
}
