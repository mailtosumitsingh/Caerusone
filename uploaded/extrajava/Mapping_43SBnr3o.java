import org.ptg.eventloop.*;
import java.util.*;
import org.sikuli.script.Region ;
public class Mapping_43SBnr3o extends org.ptg.eventloop.AutomationHelper  {
public void run(java.util.Map<String,Object> ctx){
System.out.println("Going to execute \"run\" method in Mapping_43SBnr3o now");
Map<String,org.sikuli.script.Region> regions = new HashMap<>();

{/*s1sout(JavaCode)*/
String msg = "Start";
System.out.println(msg);
}{/*f2ForLoop(JavaCode)*/
for(int i=0;i<5;i+=1){
{/*s3sout(JavaCode)*/
String msg = "3";
System.out.println(msg);ctx.put("aux_s3sout(JavaCode).s3sout(JavaCode)msg",msg);
}{/*s2sout(JavaCode)*/
String msg =(String) "somemsg";
System.out.println(msg);
}
}
}{/*f1ForLoop(JavaCode)*/
int incr = 1;
for(int j=0;j<5;j+=1){
{/*s7sout(JavaCode)*/
String var0 = "aux_s3sout(JavaCode).s3sout(JavaCode)msg";
String msg = (String)("5"+ ctx.get(var0));
System.out.println(msg);
}
}
}{/*s8sout(JavaCode)*/
String msg = (String)("" + "somemsg");
System.out.println(msg);
}
}

public static void main(String[] args){
Mapping_43SBnr3o testVar = new Mapping_43SBnr3o();
Map<String,Object> map = new HashMap<String,Object>();
testVar.run(map);
}

	public int parseInt(String str) {
		return Integer.parseInt(str);
	}


}
