import org.ptg.eventloop.*;
import java.util.*;
import org.sikuli.script.Region ;
public class Mapping_ObjectMapperTest1 extends org.ptg.eventloop.AutomationHelper  {
public void run(java.util.Map<String,Object> ctx){
System.out.println("Going to execute \"run\" method in Mapping_ObjectMapperTest1 now");
Map<String,org.sikuli.script.Region> regions = new HashMap<>();

{/*in*/
org.ptg.events.TraceEvent in =(org.ptg.events.TraceEvent) ctx.get("in");
ctx.put("out_in.in_org.ptg.events.TraceEvent.tracetype",in.getTracetype());
ctx.put("out_in.in_org.ptg.events.TraceEvent.origin",in.getOrigin());
ctx.put("out_in.in_org.ptg.events.TraceEvent.senderSeqNo",in.getSenderSeqNo());
}{/*s2sout(JavaCode)*/
String var0 = "out_in.in_org.ptg.events.TraceEvent.origin";
String msg = (String)ctx.get(var0);
System.out.println(msg);
}{/*s1sout(JavaCode)*/
String var0 = "out_in.in_org.ptg.events.TraceEvent.tracetype";
String msg = (String)ctx.get(var0);
System.out.println(msg);ctx.put("aux_s1sout(JavaCode).s1sout(JavaCode)msg",msg);
}{/*out*/
org.ptg.events.StringEvent out =(org.ptg.events.StringEvent) ctx.get("out");
out.setSenderSeqNo( (long) ctx.get("out_in.in_org.ptg.events.TraceEvent.senderSeqNo" ) );
out.setEventGroup( (java.lang.String) ctx.get("aux_s1sout(JavaCode).s1sout(JavaCode)msg" ) );
}
}

public static void main(String[] args){
Mapping_ObjectMapperTest1 testVar = new Mapping_ObjectMapperTest1();
Map<String,Object> map = new HashMap<String,Object>();
testVar.run(map);
}

	public int parseInt(String str) {
		return Integer.parseInt(str);
	}


}
