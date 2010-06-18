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
 * $Id: HL7MSA.java,v 1.1 2004/06/15 18:48:51 jaz Exp $
 * $Revision: 1.1 $
 */

public class HL7MSA
{
    
    // Required Fields - MSA
    private String acknowledgmentCode;            // MSA.1 - AA|AE|AR|CA|CE|CR
    private String messageControlId;               // MSA.2
    // Optional Fields
    private String textMessage;                    // MSA.3
    private String expectedSequenceNumber;
    private String delayedAcknowledgmentType;
    private String errorCondition;
    
    public HL7MSA(boolean success, String mcId)
    {
        // Required Field Initialization
        if (success == true)
        {
            acknowledgmentCode = "AA";
            textMessage = "jEngine Response : Msg OK";
        }
        else
        {
            acknowledgmentCode = "AE";
            textMessage = "jEngine Response : Msg BAD";
        }
        messageControlId = mcId;
        // Optional Field Initialization
        expectedSequenceNumber = "";
        delayedAcknowledgmentType = "";
        errorCondition = "";
    }
   
    public String getSegment()
    {
            return "MSA"
            + HL7Utils.PIPE
            + acknowledgmentCode
            + HL7Utils.PIPE
            + messageControlId
            + HL7Utils.PIPE
            + textMessage
            + HL7Utils.PIPE
            + expectedSequenceNumber
            + HL7Utils.PIPE
            + delayedAcknowledgmentType
            + HL7Utils.PIPE
            + errorCondition
            ;
    }
    
}

