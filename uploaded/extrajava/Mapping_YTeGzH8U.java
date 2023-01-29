import org.ptg.eventloop.*;
import java.util.*;
import org.sikuli.script.Region ;
public class Mapping_YTeGzH8U extends org.ptg.eventloop.AutomationHelper implements Runnable {
public void run(){
		java.util.HashMap ctx = new java.util.HashMap();
System.out.println("Going to execute \"run\" method in Mapping_YTeGzH8U now");
Map<String,org.sikuli.script.Region> regions = new HashMap<>();

{/*TestOutput1(JavaCode)*/
String in1 = (String)(""+"value port 1);
String in2 = (String)(""+"value port 2");
int inint = (Integer)Integer.parseInt(""+"3000");
String out1 = "some initial val1";
String out2 = "some initial val2";
int outint=100;
out1=in1;
out2=in2;
outint=inint;
ctx.put("aux_TestOutput1(JavaCode).TestOutput1(JavaCode)in1",in1);
ctx.put("out_TestOutput1(JavaCode).TestOutput1(JavaCode)outint",outint);
}{/*s1sout(JavaCode)*/
String var0 = "aux_TestOutput1(JavaCode).TestOutput1(JavaCode)in1";
String var1 = "out_TestOutput1(JavaCode).TestOutput1(JavaCode)outint";
String msg = (String)ctx.get(var0);
int valin=(Integer)ctx.get(var1);
System.out.println(msg);
System.out.println("Outputting innt as : "+valin);ctx.put("aux_s1sout(JavaCode).s1sout(JavaCode)msg",msg);
}{/*s3sout(JavaCode)*/
String var0 = "aux_s1sout(JavaCode).s1sout(JavaCode)msg";
String msg = (String)ctx.get(var0);
System.out.println(msg);
}
}

public static void main(String[] args){
Mapping_YTeGzH8U testVar = new Mapping_YTeGzH8U();

testVar.run();
}

public int parseInt(String str) {
		return Integer.parseInt(str);
	}
	public org.ptg.eventloop.Point findImageLoc(String img) {
		System.out.println("Find image loc: "+img);
		return null;
	}
	public org.ptg.eventloop.Point findImage(String img) {
		System.out.println("Find image loc: "+img);
		return null;
	}

}
