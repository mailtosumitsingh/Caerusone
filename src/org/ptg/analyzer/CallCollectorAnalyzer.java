package org.ptg.analyzer;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;

import org.apache.commons.collections.MultiMap;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: Feb 8, 2009
 * Time: 11:18:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class CallCollectorAnalyzer extends CodeBuilderAnalyzer {
    public CallCollectorAnalyzer(MultiMap advices) {
        super(advices);
    }

    public void applyAdvice(CtClass cClass, CtMethod cMtd, CSVMethodEditor methodEditor) throws CannotCompileException {
        methodEditor.setMethod(cMtd, cClass);
        cMtd.instrument(methodEditor);
    }

    public void applyAdvice(CtClass cClass, CtConstructor cMtd, CSVConstEditor methodEditor) throws CannotCompileException {
    	System.out.println("inside applyAdvice constant");
    }

    public void applyAdvice(CtClass cClass, CtConstructor cMtd, CSVStaticEditor methodEditor) throws CannotCompileException {
    	System.out.println("inside applyAdvice static" );
    }

    public void applyMethodAdvice(CtClass cClass, CtMethod cMtd) throws CannotCompileException {
      System.out.println("inside applyMethodAdvice");
    }
    
}
