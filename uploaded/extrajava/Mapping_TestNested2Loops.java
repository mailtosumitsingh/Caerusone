import org.ptg.eventloop.*;
import java.util.*;
import org.sikuli.script.Region;

public class Mapping_TestNested2Loops extends org.ptg.eventloop.AutomationHelper {
	public void run(java.util.Map<String, Object> ctx) {
		System.out.println("Going to execute \"run\" method in Mapping_TestNested2Loops now");
		Map<String, org.sikuli.script.Region> regions = new HashMap<>();

		{/* s1sout(JavaCode) */
			String msg = "Start";
			System.out.println(msg);
		}
		{/* f2ForLoop(JavaCode) */
			for (int i = 0; i < 5; i += 2) {
				if (i == 2) {/* f2ForLoop(JavaCode)-0 */
					{
						{/* s3sout(JavaCode) */
							String msg = "3";
							System.out.println(msg);
						}
						{/* s2sout(JavaCode) */
							String msg = (String) "dada";
							System.out.println(msg);
							ctx.put("aux_s2sout(JavaCode).s2sout(JavaCode)msg", msg);
						}
						{/* f1ForLoop(JavaCode) */
							int incr = 1;
							for (int j = 0; j < 5; j += 1) {
								if (j == 3) {/* f1ForLoop(JavaCode)-0 */
									{
										{/* s7sout(JavaCode) */
											String msg = (String) ("" + "somemsg");
											System.out.println(msg);
										}
										{/* s9sout(JavaCode) */
											String msg = "somemsg";
											System.out.println(msg);
										}
									}
								} else {/* f1ForLoop(JavaCode)-1 */
									{
										{/* d1sout(JavaCode) */
											String msg = "d1";
											System.out.println(msg);
										}
										{/* d2sout(JavaCode) */
											String msg = "d2";
											System.out.println(msg);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		{/* s8sout(JavaCode) */
			String var0 = "aux_s2sout(JavaCode).s2sout(JavaCode)msg";
			String msg = (String) ("" + ctx.get(var0));
			System.out.println(msg);
		}
	}

	public static void main(String[] args) {
		Mapping_TestNested2Loops testVar = new Mapping_TestNested2Loops();
		Map<String, Object> map = new HashMap<String, Object>();
		testVar.run(map);
	}

	public int parseInt(String str) {
		return Integer.parseInt(str);
	}

}
