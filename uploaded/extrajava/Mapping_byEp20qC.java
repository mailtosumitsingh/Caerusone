import org.ptg.eventloop.*;
import java.util.*;
import org.sikuli.script.Region ;
public class Mapping_byEp20qC extends org.ptg.eventloop.AutomationHelper implements Runnable {
public void run(){
		java.util.HashMap ctx = new java.util.HashMap();
System.out.println("Going to execute \"run\" method in Mapping_byEp20qC now");
Map<String,org.sikuli.script.Region> regions = new HashMap<>();
/*region annotations goes here*/
org.sikuli.script.Region region1 = new org.sikuli.script.Region(600,95,105,69);
/*END region annotations goes here*/

{/*Init(JavaCode)*/
String textToSend  = "region1";
}{/*sl1sleepMS(JavaCode)*/
int val=4000;
sleepMS(val);
}{/*script1CustomScriptCode(JavaCode)*/
boolean  result =true  ;
int []loc = getMouseLoc();
sleepMS(1000);
rightClick();
sleepMS(1000);
    moveToImage("openwithicon");
sleepMS(1000);
    clickImage("painticon");
sleepMS(4000);
    clickImage("paintfilemenu");
sleepMS(1000);
    moveToImage("paintsaveas");
sleepMS(1000);
    clickImage("paintjpgpicture");
sleepMS(3000);
clickImage("intsavebutton");
    sleepMS(2000);
if(findFirstImage("paintconfirmsaveas")!=null){
    sleepMS(1000);
    clickImage("paintconfirmsavebutton");
}
sleepMS(1000);
    clickImage("closeappxicon");
sleepMS(1000);
moveMouse(loc[0],loc[1]);
}
}

public static void main(String[] args){
Mapping_byEp20qC testVar = new Mapping_byEp20qC();

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
