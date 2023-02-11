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
public class Mapping_JavaExample3 implements IRunnable{
public void run(){
System.out.println("Going to execute \"run\" method in Mapping_JavaExample3 now");
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
boolean cond$foreach1_foreach_$0 = true;
/*STARTBLOCK*/
boolean condvar32_var_ = true;
/*STARTBLOCK*/
boolean condvar1_var_ = true;
/*DUMMY_VIRTUAL_NODE*/
System.out.println("STARTNODE");
/*var2(var)*/
condvar2_var_ = true;
/*var2(var)*/
var2_var_:
/*var2(var)*/
{
/*var2(var)*/
System.out.println("var2(var)");
/*var2(var)*/
{
/*foreach1(foreach)*/
condforeach1_foreach_ = true;
/*foreach1(foreach)*/
foreach1(foreach):
/*foreach1(foreach)*/
foreach (int i=0;i<10;i++){
/*foreach1(foreach)*/
System.out.println(" i is now: "+i);
/*foreach1(foreach)*/
System.out.println("foreach1(foreach)");
/*var11(var)*/
System.out.println("var11(var)");
/*var12(var)*/
System.out.println("var12(var)");
/*foreach1(foreach)*/
continue foreach1_foreach_;
/*foreach1(foreach)*/
/*foreach1(foreach)*/
}
/*var2(var)*/
/*var2(var)*/
}
/*var2(var)*/
{
/*if1Test(if)*/
condif1Test_if_ = true;
/*if1Test(if)*/
if1Test_if_:
/*if1Test(if)*/
{
/*if1Test(if)*/
System.out.println("if1Test(if)");
/*if1Test(if)*/
 if(condif1Test_if_=="true")
/*if1Test(if)*/
{
/*var32(var)*/
System.out.println("var32(var)");
/*if1Test(if)*/
/*if1Test(if)*/
}
/*if1Test(if)*/
else
/*if1Test(if)*/
{
/*var1(var)*/
System.out.println("var1(var)");
/*if1Test(if)*/
/*if1Test(if)*/
}
/*if1Test(if)*/
/*if1Test(if)*/
}
/*var2(var)*/
/*var2(var)*/
}
/*var2(var)*/
/*var2(var)*/
}

}

public static void main(String[] args){
Mapping_JavaExample3 testVar = new Mapping_JavaExample3();

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
