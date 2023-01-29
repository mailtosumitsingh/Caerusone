import org.ptg.eventloop.*;
import java.util.*;
import org.sikuli.script.Region ;
public class Mapping_iZX6TbGa extends org.ptg.eventloop.AutomationHelper implements Runnable {
public void run(){
		java.util.HashMap ctx = new java.util.HashMap();
System.out.println("Going to execute \"run\" method in Mapping_iZX6TbGa now");
Map<String,org.sikuli.script.Region> regions = new HashMap<>();

{/*sleep1sleepMS(JavaCode)*/
int val=2000;
sleepMS(val);
}{/*mttMoveToText(JavaCode)*/
String text = "Mapper";
Point p  = findFirstText(text);
moveMouse(p.getX(),p.getY());
}{/*sleep6sleepMS(JavaCode)*/
int val=2000;
sleepMS(val);
}{/*mti1MoveToImage(JavaCode)*/
String text = "vscode.jpg";
text = "C:\\projects\\images\\"+text;
Point p  = findFirstImage(text);
moveMouse(p.getX(),p.getY());
}{/*slp8sleepMS(JavaCode)*/
int val=2000;
sleepMS(val);
}{/*fIfImageMaches(JavaCode)*/

String img = "some.jpg";

findImage(img);
}{/*aMoveMouse(JavaCode)*/

int mx = 200;
int my = 200;

moveMouse(mx,my);
}{/*"+v.getId()+"*/
for(int i=0;i<4;i+=2){
{/*mouvemouse2MoveMouse(JavaCode)*/

int mx = 100;
int my = 100;

moveMouse(mx,my);
}{/*bFindTextLoc(JavaCode)*/
String img = "some2.jpg";

int declarePortOut1 = testlocx.out.value;
String tempval = "Something";
findImageLoc(img);
ctx.put("testlocx.out.value",tempval);
}{/*sleep3sleepMS(JavaCode)*/
int val=2000;
sleepMS(val);
}{/*sSendKey(JavaCode)*/

String str = "sendkeystringval";


sendKeys(str);
}{/*movemouse1MoveMouse(JavaCode)*/

int mx = 200;
int my = 200;

moveMouse(mx,my);
}{/*sleep4sleepMS(JavaCode)*/
int val=1000;
sleepMS(val);
}
}
}
}

public static void main(String[] args){
Mapping_iZX6TbGa testVar = new Mapping_iZX6TbGa();

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
