package org.ptg.util.aspect;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class AroundAspectAdvice  implements MethodInterceptor{
    public Object invoke(MethodInvocation method) throws Throwable {
        System.out.println("Before Invoking Method");
        Object val = method.proceed();
        System.out.println("After Invoking Method");
        return val + "updated value";
    }
 
}