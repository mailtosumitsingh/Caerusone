import org.ptg.eventloop.*;
import java.util.*;
import org.sikuli.script.Region ;
public class Mapping_QkM4gEfd extends org.ptg.eventloop.AutomationHelper implements Runnable {
public void run(){
		java.util.HashMap ctx = new java.util.HashMap();
System.out.println("Going to execute \"run\" method in Mapping_QkM4gEfd now");
Map<String,org.sikuli.script.Region> regions = new HashMap<>();

{/*sleep1sleepMS(JavaCode)*/
int val=2000;
sleepMS(val);
}{/*"+v.getId()+"*/
String img = "C:\\projects\\images\\s.jpg";
int sleep = 1000;
int tries = 10;
Object loc = findFirstImage(img);
String tryvar = "somevar";
while(loc==null && --tries>0){
    sleepMS(sleep);
    loc = findFirstImage(img);
    ctx.put(tryvar,false);
}
if(loc!=null){
    ctx.put(tryvar,true);
}
if(loc!=null)
{
{/*sout1sout(JavaCode)*/
String msg = "inside wait for image image found";
System.out.println(msg);
}{/*sleep2sleepMS(JavaCode)*/
int val=1000;
sleepMS(val);
}{/*mm1MoveMouse(JavaCode)*/

int mx = 100;
int my = 100;

moveMouse(mx,my);
}{/*sleep3sleepMS(JavaCode)*/
int val=2000;
sleepMS(val);
}
}
}{/*sout3sout(JavaCode)*/
String msg = "wait for image done";
System.out.println(msg);
}{/*mm2MoveMouse(JavaCode)*/

int mx = 800;
int my = 800;

moveMouse(mx,my);
}{/*"+v.getId()+"*/
String var = "somevar";
boolean matchval=true;
boolean v = (boolean)ctx.get(var);
if(v==matchval){
{/*s1sout(JavaCode)*/
String msg = "some var is set to true";
System.out.println(msg);
}
}
}
}

public static void main(String[] args){
Mapping_QkM4gEfd testVar = new Mapping_QkM4gEfd();

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
