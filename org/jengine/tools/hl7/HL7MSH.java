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
 * $Id: HL7MSH.java,v 1.2 2006/05/22 21:08:11 jaz Exp $
 * $Revision: 1.2 $
 */

public class HL7MSH
{
    /*
     * HL7 Ack Structure:
     *     ACK General acknowledgment Chapter
     *     MSH     Message Header        2
     *     MSA     Message acknowledgment      2
     *    [ ERR ]    Error          2
     */
    
    // Required Fields
    private char fieldSeparator;      // MSH.1
    private String encodingCharacters;             // MSH.2
    private String messageType;                 // MSH.9.1 ACK - General acknowledgement message
    private String eventType;                      // MSH.9.2 - comes from incoming HL7 ?
    private String messageControlId;               // MSH.10 - comes from incoming HL7 ?
    private String processingId;                  // MSH.11
    private String versionId;                   // MSH.12
    // Optional
    private String sendingApplication;
    private String sendingFacility;
    private String receivingApplication;
    private String receivingFacility;
    private String dateTimeOfMessage;
    private String security;
    private String sequenceNumber;
    private String continuationPointer;
    private String acceptAcknowledgementType;
    private String applicationAcknowledgementType;
    private String countryCode;
    private String characterSet;
    private String principalLanguageOfMessage;

    private String getCurrentDateTime()
    {
        return "";
    }
    
    public HL7MSH(String mcId,String event)
    {
        //Required fields initialization
        messageControlId = mcId;
        fieldSeparator = HL7Utils.PIPE;
        encodingCharacters = HL7Utils.ENCODINGCHARACTERS;
        messageType = "ACK";
        eventType = event;
	if (mcId != null)
        	messageControlId = mcId;
	else
        	messageControlId = "";
        processingId = "P";
        
        //Optional fields INIT
        sendingApplication = "ShastaDataDirector";
        sendingFacility = "Engine";
        receivingApplication = "";
        receivingFacility = "";
        dateTimeOfMessage = getCurrentDateTime();
        security = "";
        processingId = "";
        versionId = "2.3";
        sequenceNumber = "";
        continuationPointer = "";
        acceptAcknowledgementType = "";
        applicationAcknowledgementType = "";
        countryCode = "";
        characterSet = "";
        principalLanguageOfMessage = "";
    }
    
    public String getSegment()
    {
            return "MSH"
            + fieldSeparator
            + encodingCharacters
            + HL7Utils.PIPE
            + sendingApplication
            + HL7Utils.PIPE
            + sendingFacility
            + HL7Utils.PIPE
            + receivingApplication
            + HL7Utils.PIPE
            + receivingFacility
            + HL7Utils.PIPE
            + dateTimeOfMessage
            + HL7Utils.PIPE
            + security
            + HL7Utils.PIPE
            + messageType
            + HL7Utils.CARET
            + eventType
            + HL7Utils.PIPE
            + messageControlId
            + HL7Utils.PIPE
            + processingId
            + HL7Utils.PIPE
            + versionId
            + HL7Utils.PIPE
            + sequenceNumber
            + HL7Utils.PIPE
            + continuationPointer
            + HL7Utils.PIPE
            + acceptAcknowledgementType
            + HL7Utils.PIPE
            + applicationAcknowledgementType
            + HL7Utils.PIPE
            + countryCode
            + HL7Utils.PIPE
            + characterSet
            + HL7Utils.PIPE
            + principalLanguageOfMessage
            ;
    }
}
