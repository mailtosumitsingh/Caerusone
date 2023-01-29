import org.ptg.eventloop.*;
import java.util.*;
import org.sikuli.script.Region;

public class Mapping_rPgLXYYY extends org.ptg.eventloop.AutomationHelper implements Runnable {
	public void run() {
		java.util.HashMap ctx = new java.util.HashMap();
		System.out.println("Going to execute \"run\" method in Mapping_rPgLXYYY now");
		Map<String, org.sikuli.script.Region> regions = new HashMap<>();

		{/* sleep1sleepMS(JavaCode) */
			int val = 2000;
			sleepMS(val);
		}
		{/* s1sout(JavaCode) */
			String msg = "started";
			System.out.println(msg);
		}
		{/* "+v.getId()+" */
			String txt = "data";
			int sleep = 1000;
			int tries = 3;

			Object loc = findFirstText(txt);
			String tryvar = "somevar";
			while (loc == null && --tries > 0) {
				sleepMS(sleep);
				loc = findFirstText(txt);
				ctx.put(tryvar, false);
				String s = toText();
				System.out.println(s);
			}
			if (loc != null) {
				ctx.put(tryvar, true);
			}

			if (loc != null) {
				{/* s2sout(JavaCode) */
					String msg = "found text";
					System.out.println(msg);
				}
			}
		}
		{/* s3sout(JavaCode) */
			String msg = "done with text find";
			System.out.println(msg);
		}
	}

	public static void main(String[] args) {
		Mapping_rPgLXYYY testVar = new Mapping_rPgLXYYY();

		testVar.run();
	}

	public int parseInt(String str) {
		return Integer.parseInt(str);
	}

	public org.ptg.eventloop.Point findImageLoc(String img) {
		System.out.println("Find image loc: " + img);
		return null;
	}

	public org.ptg.eventloop.Point findImage(String img) {
		System.out.println("Find image loc: " + img);
		return null;
	}

}
