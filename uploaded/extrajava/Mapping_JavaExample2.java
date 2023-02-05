import java.util.*;
import org.apache.camel.*;
import org.ptg.events.*;
import org.ptg.util.events.*;
import org.ptg.stream.*;
import org.ptg.util.*;
import java.io.*;
import java.lang.reflect.*;
import java.sql.*;
import java.text.*;
import java.util.regex.*;
import javassist.*;
import javassist.bytecode.annotation.*;
import javax.xml.bind.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import net.htmlparser.jericho.*;
import net.sf.ezmorph.*;
import net.sf.ezmorph.bean.*;
import net.sf.json.*;
import net.sf.json.util.*;
import org.apache.camel.builder.*;
import org.apache.camel.impl.*;
import org.apache.commons.collections.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.jexl2.*;
import org.apache.commons.jxpath.*;
import org.apache.commons.lang.*;
import org.apache.commons.lang.exception.*;
import org.apache.commons.lang.math.*;
import org.apache.commons.vfs.*;
import org.ptg.admin.*;
import org.ptg.cep.*;
import org.ptg.processors.*;
import org.ptg.script.ScriptEngine;
import org.ptg.stream.Stream;
import org.ptg.stream.StreamDefinition;
import org.ptg.stream.StreamManager;
import org.ptg.timer.QuartzEvent;
import org.ptg.timer.QuartzImmidiateEvent;
import org.ptg.util.db.DBHelper;
import org.ptg.util.thread.ThreadManager;
import org.ptg.util.titan.ITitanClass;
import org.ptg.util.titan.TitanCompiler;
import org.ptg.velocity.VelocityHelper;
import org.rosuda.REngine.Rserve.RserveException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.yaml.snakeyaml.Yaml;
import com.google.protobuf.*;
import com.sun.tools.xjc.XJC2Task;
import org.quartz.Job;
import org.quartz.*;
import org.ptg.util.IRunnable;
import ch.ethz.ssh2.*;
import org.apache.tools.ant.taskdefs.optional.ssh.Scp;
public class Mapping_JavaExample2 implements IRunnable{
public void run(){
System.out.println("Going to execute \"run\" method in Mapping_JavaExample2 now");
/*STARTBLOCK*/
boolean cond = false;
/*STARTBLOCK*/
boolean condSTARTNODE = true;
/*STARTBLOCK*/
boolean condvar2_var_ = true;
/*STARTBLOCK*/
boolean condif1Test_if_ = true;
/*STARTBLOCK*/
boolean condforeach1_foreach_ = true;
/*STARTBLOCK*/
boolean condvar11_var_ = true;
/*STARTBLOCK*/
boolean condvar12_var_ = true;
/*STARTBLOCK*/
boolean condjava1_JavaCode_ = true;
/*STARTBLOCK*/
boolean cond$foreach1_foreach_$0 = true;
/*STARTBLOCK*/
boolean condvar32_var_ = true;
/*STARTBLOCK*/
boolean condvar1_var_ = true;
/*DUMMY_VIRTUAL_NODE*/
/*handleGenerateCode*/
/*DUMMY_VIRTUAL_NODE*/
/*handleNormalBlock*/
/*DUMMY_VIRTUAL_NODE*/
/*var2(var)*/
/*handleFork*/
/*var2(var)*/
condvar2_var_ = true;
/*var2(var)*/
var2_var_:
/*var2(var)*/
{
/*var2(var)*/
/*handleGenerateCode*/
/*var2(var)*/
boolean item2 = true;
/*var2(var)*/
/*handleForkCondition*/
/*var2(var)*/
/*handleUnkFork*//*unknown fork type*/
/*var2(var)*/
/*handleForkBlockBegin*/
/*var2(var)*/
{
/*foreach1(foreach)*/
/*handleLoopBackTarget*/
/*foreach1(foreach)*/
condforeach1_foreach_ = true;
/*foreach1(foreach)*/
foreach1_foreach_:
 for (int item : new int[]{1,2,3,4,5,6,7})/*foreach1(foreach)*/
{
/*foreach1(foreach)*/
/*handleGenerateCode*/
/*foreach1(foreach)*/
/*handleNormalBlock*/
/*foreach1(foreach)*/
/*var11(var)*/
/*handleGenerateCode*/
/*var11(var)*/
int item11 = 0;
/*var11(var)*/
/*handleNormalBlock*/
/*var11(var)*/
/*var12(var)*/
/*handleGenerateCode*/
/*var12(var)*/
int item0 = 0;
/*var12(var)*/
/*handleNormalBlock*/
/*var12(var)*/
/*java1(JavaCode)*/
/*handleGenerateCode*/
/*java1(JavaCode)*/
System.out.println(item);
class a{
    
    
    
}
/*java1(JavaCode)*/
/*handleNormalBlock*/
/*java1(JavaCode)*/
/*foreach1(foreach)*/
/*handleGenerateCode*/
/*foreach1(foreach)*/
continue foreach1_foreach_;
/*foreach1(foreach)*/
/*handleLoopBackTargetFinish*/
/*foreach1(foreach)*/
/*foreach1(foreach)*/
}
/*foreach1(foreach)*/
/*var2(var)*/
/*handleEndChild*/
/*var2(var)*/
/*var2(var)*/
}
/*var2(var)*/
/*var2(var)*/
/*handleForkCondition*/
/*var2(var)*/
/*handleUnkFork*//*unknown fork type*/
/*var2(var)*/
/*handleForkBlockBegin*/
/*var2(var)*/
{
/*if1Test(if)*/
/*handleFork*/
/*if1Test(if)*/
condif1Test_if_ = item2;
/*if1Test(if)*/
if1Test_if_:
/*if1Test(if)*/
{
/*if1Test(if)*/
/*handleGenerateCode*/
/*if1Test(if)*/
/*handleForkCondition*/
/*if1Test(if)*/
/*handleIfFork*/
/*if1Test(if)*/
 if(item2)
/*if1Test(if)*/
/*handleForkBlockBegin*/
/*if1Test(if)*/
{
/*var32(var)*/
/*handleGenerateCode*/
/*var32(var)*/
int item23 = 0;
/*if1Test(if)*/
/*handleEndChild*/
/*if1Test(if)*/
/*if1Test(if)*/
}
/*if1Test(if)*/
/*if1Test(if)*/
/*handleForkCondition*/
/*if1Test(if)*/
/*handleIfFork*/
/*if1Test(if)*/
else
/*if1Test(if)*/
/*handleForkBlockBegin*/
/*if1Test(if)*/
{
/*var1(var)*/
/*handleGenerateCode*/
/*var1(var)*/
int item = 0;
/*if1Test(if)*/
/*handleEndChild*/
/*if1Test(if)*/
/*if1Test(if)*/
}
/*if1Test(if)*/
/*if1Test(if)*/
/*handleForkFinish*/
/*if1Test(if)*/
/*if1Test(if)*/
}
/*if1Test(if)*/
/*var2(var)*/
/*handleEndChild*/
/*var2(var)*/
/*var2(var)*/
}
/*var2(var)*/
/*var2(var)*/
/*handleForkFinish*/
/*var2(var)*/
/*var2(var)*/
}
/*var2(var)*/

}

public static void main(String[] args){
Mapping_JavaExample2 testVar = new Mapping_JavaExample2();

testVar.run();
}
    public boolean runScript(String server, String code) {
            return runScript(server, code) ;
    }

    public boolean echo(String txt) {
        System.out.println("echo: " + txt);
        return false;
    }
        public boolean antScpTo(String file, String toDir, String context) {
       return AutomateUtils.antScpTo(file, toDir, context); 
    }
    public boolean sendToUi(String s) {
               return AutomateUtils.sendToUi(s);
    }
    
}
