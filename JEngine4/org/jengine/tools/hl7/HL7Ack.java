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
 * $Id: HL7Ack.java,v 1.1 2004/06/15 18:48:51 jaz Exp $
 * $Revision: 1.1 $
 */

public class HL7Ack
{
    /*
     * HL7 Ack Structure:
     *     ACK General acknowledgment Chapter
     *     MSH     Message Header        2
     *     MSA     Message acknowledgment      2
     *    [ ERR ]    Error          2
     */

    private boolean success;
    private String messageControlId;
    private String eventType;
    private String theHL7AckMessage;               // the whole shebang
        
    public HL7Ack(boolean success, String msg)
    {
        HL7Utils hl7Obj = new HL7Utils(msg);
        this.success = success;
        this.messageControlId = hl7Obj.getMessageControlId();
        this.eventType=hl7Obj.getEventType();
        buildMessage();
    }
    
    public String getMessage()
    {
        return theHL7AckMessage;
    }

    private String buildMessage()
    {
        HL7MSH msh = new HL7MSH(messageControlId,eventType);
        HL7MSA msa = new HL7MSA(success, messageControlId);
        
        theHL7AckMessage = 
            msh.getSegment()
            //+ "\013"
            + (char)0xd
            + msa.getSegment()
            //+ "\013"
            + (char)0xd
            ;
        return theHL7AckMessage;
    }
         
}
