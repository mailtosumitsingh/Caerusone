package org.ptg.util.closures;

import org.ptg.util.CommonUtil;

public class RunTaskPlanClosure {
public static void  runTaskPlan(String hint){
	System.out.println("RunTaskPlanClosure: "+ " now running "+hint);
	CommonUtil.execTaskPlan(hint);
	
}
}
