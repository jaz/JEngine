import org.jengine.tools.hl7.Field;
import org.jengine.tools.hl7.Segment;
import java.util.Vector;
import java.util.Iterator;
import org.jengine.tools.hl7.Message;

public class TestBeanShell {

	Message message = new Message();

	public void main(String args[]) {

		String send_facility = message.sendingFacility.toString();
		String rcv_facility = message.receivingFacility.toString();
		String msgType = message.messageType.toString();

		//Pass only ADT^A04 and A08 transactions
		//for Physicians 00023 and 00073
		//  if passing, rewrite the receiving facility 002 -> 97 and 076 -> 2
		if ((msgType.equals("ADT^A04") || msgType.equals("ADT^A08")) == true ) {
			Segment pd1Segment;
			pd1Segment = message.getSegment("PD1");
			if (pd1Segment != null) {
				Field pcDrField = pd1Segment.getField(4);
				String pcDrCode = pcDrField.get(1);
				if ((pcDrCode.equals("00023") || pcDrCode.equals("00073")) == true) {
					if (send_facility.equals("002") == true) {
						message.receivingFacility = new Field("97");
					} else if (send_facility.equals("076") == true) {
						message.receivingFacility = new Field("2");
					}
				} else {
					message.messageType = new Field("KILL");
					return;
				} 
			} else {
				message.messageType = new Field("KILL");
				return;
			}
		}
	}
}
