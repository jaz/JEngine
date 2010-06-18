package org.jengine.tools.hl7;

import java.util.Vector;

public class TestBeanShell {

	Message message = new Message();

	public void main(String args[]) {
		
		String send_facility = message.sendingFacility.toString();
		String rcv_facility = message.receivingFacility.toString();

		if (send_facility.equals("RMC") == true) {
			Field f = new Field("002");
			message.sendingFacility = f;
		}
		if (rcv_facility.equals("RMC") == true) {
			Field f = new Field("002");
			message.receivingFacility = f;
		}


//		do lab result specific mappings below

		String msgType = message.messageType.toString();
		if (msgType.equalsIgnoreCase("ORU^R01") == true) {

			//change msg type in MSH
			message.messageType = new Field("UDM^R03");



			//create URD segment, copy PID info
			Segment urdSegment = message.getSegment("URD");
			Segment pidSegment = message.getSegment("PID");
			Segment obrSegment = message.getSegment("OBR");
			message.addSegment(urdSegment);
			Field urdField3 = urdSegment.getField(3);
			urdField3.set(1,1,pidSegment.getField(18).toString());
			urdField3.set(1,2,pidSegment.getField(3).toString());
			Field urdField5 = urdSegment.getField(5);
			urdField5.set(1,1,obrSegment.getField(2).toString());
			urdField5.set(1,1,obrSegment.getField(4).get(1).toString());
			Field urdField1 = urdSegment.getField(1);
			urdField1.set(1,1,obrSegment.getField(22).toString());

			//strip off PID, ORC, OBR, OBX segments
			Vector pidSegs = message.getSegments("PID");
			message.removeAllSegments(pidSegs);
			Vector orcSegs = message.getSegments("ORC");
			message.removeAllSegments(orcSegs);
			Vector obrSegs = message.getSegments("OBR");
			message.removeAllSegments(obrSegs);
			Vector obxSegs = message.getSegments("OBX");
			message.removeAllSegments(obxSegs);
		}
	}
}
