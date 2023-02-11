import org.ptg.eventloop.*;
import java.util.*;
import org.sikuli.script.Region;

public class Mapping_TestAndroidAppMakey extends org.ptg.eventloop.AutomationHelper {
	public void run(java.util.Map<String, Object> ctx) {
		System.out.println("Going to execute \"run\" method in Mapping_TestAndroidAppMakey now");
		Map<String, org.sikuli.script.Region> regions = new HashMap<>();
		/* region annotations goes here */
		org.sikuli.script.Region region1 = new org.sikuli.script.Region(277, 34, 105, 69);/* match */
		org.sikuli.script.Region region2 = new org.sikuli.script.Region(878, 198, 216, 113);/* null */
		org.sikuli.script.Region region3 = new org.sikuli.script.Region(396, 205, 98, 62);/* null */
		org.sikuli.script.Region shape8 = new org.sikuli.script.Region(310, 9, 1441, 1053);/* match */
		org.sikuli.script.Region region4 = new org.sikuli.script.Region(861, 416, 34, 33);/* null */
		/* END region annotations goes here */

		{/* Init(JavaCode) */
			String textToSend = "region1";
		}
		{/* mr1MouseMoveRegion(JavaCode) */
			mouseMove(region2);
		}
		{/* sout4sout(JavaCode) */
			String msg = (String) "somemsg";
			System.out.println(msg);
		}
		{/* cr1ClickRegion(JavaCode) */
			click(region3);
		}
	}

	public static void main(String[] args) {
		Mapping_TestAndroidAppMakey testVar = new Mapping_TestAndroidAppMakey();
		Map<String, Object> map = new HashMap<String, Object>();
		testVar.run(map);
	}

	public int parseInt(String str) {
		return Integer.parseInt(str);
	}

}
