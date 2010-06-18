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
 * $Id: HL7Utils.java,v 1.3 2006/05/22 21:08:11 jaz Exp $
 * $Revision: 1.3 $
 */


public class HL7Utils
{
	public static final char CARRIAGERETURN = (char)0xd;
	public static final char LINEFEED = (char)0xa;
	public static final char PIPE = '|';
	public static final char CARET = '^';  // component separator
	public static final char TILDE = '~';  // repetition separator
        public static final char AMP = '&';    // subcomponent separator
        public static final String ENCODINGCHARACTERS = "^~\\&";

      	private String segType;
       	private String level1FieldTag = "";
	private String level2FieldTag = "";
	private String level3FieldTag = "";
        
        private String ackCode;
        private String msgControlId;
        private String msgControlIdMsa;
        private String messageType;
        private String eventType;
        
        private boolean ackReceived = false;
        
        public HL7Utils() { }

        public HL7Utils(String msg)
        {
            decomposeSegments(msg);
        }

        public boolean isAck()
        {
            return ackReceived;
        }
        
        public void decomposeSegments(String str)
        {
            int i=0;
            StringDelimiter sd;
            sd = new StringDelimiter(str, HL7Utils.CARRIAGERETURN);
            while (sd.hasMoreTokens())
            {
                String nt = sd.nextToken();
                //System.out.println("Token :: " + nt);
                if (nt.indexOf(HL7Utils.PIPE) >= 0)
                    decomposeFields(nt);
                i++;
            }
        }
        
        public void decomposeFields(String str)
        {
            int i=0;
            StringDelimiter sd;
            sd = new StringDelimiter(str, HL7Utils.PIPE);
            while (sd.hasMoreTokens())
            {
                String nt = sd.nextToken();
                if (i == 0) {
                    segType = nt;
                    // for segments other than MSH,
                    // the second field actually begins at 1
                    if (nt.equals("MSH")) i++;
                }
                level1FieldTag = segType + "." + i;
                //System.out.println(level1FieldTag + "(" + nt.length() + ")" + "\t[" + nt + "]");
                String ptr = level1FieldTag;
                
                // this applies to an MSA segment [[part of an MSH-MSA ack/nak]]
                if (ptr.equalsIgnoreCase("MSA.1"))
                {
                    if (nt.equalsIgnoreCase("AA") || nt.equalsIgnoreCase("CA"))
                        ackReceived = true;
                    else
                        ackReceived = false;
                    ackCode = nt;
                }
                if (ptr.equalsIgnoreCase("MSA.2"))
                {
                    msgControlIdMsa = nt;
                }
                if (ptr.equalsIgnoreCase("MSH.10"))
                {
                    msgControlId = nt;
                }

                if (!isMshDelimiterField(segType, i))
                {
                    if (nt.indexOf(HL7Utils.TILDE) >= 0)
                        decomposeRepeats(nt);
                    else if (nt.indexOf(HL7Utils.CARET) >= 0)
                        decomposeSubfields(nt);
                }
                i++;
            }
        }
        
        public void decomposeSubfields(String str)
        {
//System.out.println("decompose subfields");
            int i=0;
            StringDelimiter sd;
            sd = new StringDelimiter(str, HL7Utils.CARET);
            while (sd.hasMoreTokens())
            {
                String nt = sd.nextToken();
                level2FieldTag = level1FieldTag + "." + (i+1);
                //System.out.println(level2FieldTag + "(" + nt.length() + ")" + "\t[" + nt + "]");
//System.out.println("fieldtag="+level2FieldTag+" nt="+nt);
                if(level2FieldTag.equalsIgnoreCase("MSH.9.1")) {
                  messageType=nt;
                }
                if(level2FieldTag.equalsIgnoreCase("MSH.9.2")) {
                  eventType=nt;
                }
                if (nt.indexOf(HL7Utils.AMP) >= 0)
                    decomposeRepeats(nt);
                i++;
            }

        }
        public void decomposeRepeats(String str)
        {
            int i=1;
            StringDelimiter sd;
            sd = new StringDelimiter(str, HL7Utils.TILDE);
            while (sd.hasMoreTokens())
            {
                String nt = sd.nextToken();
                //System.out.println("\t{" + i + "}" + "(" + nt.length() + ")" + "\t[" + nt + "]");
                if (nt.indexOf(HL7Utils.CARET) >= 0)
                    decomposeSubfields(nt);
                i++;
            }
        }

        public boolean isMshDelimiterField(String segment, int position)
	{
		if ((segment.equals("MSH")) & (position==2))
			return true;
		else
			return false;
	}

        public String getMessageControlIdMsa()
        {
            return msgControlIdMsa;
        }
        public String getMessageControlId()
        {
            return msgControlId;
        }
        public String getAcknowledgementCode()
        {
            return ackCode;
        }
        public String getMessageType() {
            return messageType;
        }
        public String getEventType() {
            //System.out.println("returning event type "+eventType);
            return eventType;
        }

}

