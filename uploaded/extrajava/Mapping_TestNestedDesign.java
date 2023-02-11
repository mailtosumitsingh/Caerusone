import org.ptg.eventloop.*;
import java.util.*;
import org.sikuli.script.Region;

public class Mapping_TestNestedDesign extends org.ptg.eventloop.AutomationHelper {
	public void run(java.util.Map<String, Object> ctx) {
		System.out.println("Going to execute \"run\" method in Mapping_TestNestedDesign now");
		Map<String, org.sikuli.script.Region> regions = new HashMap<>();

		{/* s1sout(JavaCode) */
			String msg = "Start";
			System.out.println(msg);
		}
		{/* f2ForLoop(JavaCode) */
			for (int i = 0; i < 5; i += 1) {
				{/* f2ForLoop(JavaCode)-0 */
					{
						{/* s3sout(JavaCode) */
							String msg = "3";
							System.out.println(msg);
						}
						{/* s2sout(JavaCode) */
							String msg = (String) "somemsg";
							System.out.println(msg);
						}
					}
				}
			}
		}
		{/* f1ForLoop(JavaCode) */
			int incr = 1;
			for (int j = 0; j < 5; j += 1) {
				{/* f1ForLoop(JavaCode)-0 */
					{
						{/* s7sout(JavaCode) */
							String msg = (String) ("5" + "somemsg");
							System.out.println(msg);
						}
						{/* s9sout(JavaCode) */
							String msg = "somemsg";
							System.out.println(msg);
						}
					}
				}
			}
		}
		{/* s8sout(JavaCode) */
			String msg = (String) ("" + "somemsg");
			System.out.println(msg);
		}
	}

	public static void main(String[] args) {
		Mapping_TestNestedDesign testVar = new Mapping_TestNestedDesign();
		Map<String, Object> map = new HashMap<String, Object>();
		testVar.run(map);
	}

	public int parseInt(String str) {
		return Integer.parseInt(str);
	}

}
