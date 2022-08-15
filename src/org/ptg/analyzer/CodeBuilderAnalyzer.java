package org.ptg.analyzer;



import java.util.Collection;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;

import org.apache.commons.collections.MultiMap;

import core.AnalyserAdapter;
import core.BytecodeHelper;
import core.MyTranslator;
import core.NameAlgo;
import extra.FieldEditor;

public class CodeBuilderAnalyzer extends AnalyserAdapter {
    CSVMethodEditor methodEditor = null;
    CSVStaticEditor classInitEditor = null;
    CSVConstEditor constructorEditor = null;
    FieldEditor classFieldEditor = null;
    boolean noSuper = true;
    MultiMap advices;

    public CodeBuilderAnalyzer(MultiMap advices) {
        this.advices = advices;
    }

    public void setNoSuper(boolean declaredMethodsOnly) {
        this.noSuper = declaredMethodsOnly;
    }

    public void setClassFieldEditor(FieldEditor classFieldEditor) {
        this.classFieldEditor = classFieldEditor;
    }

    public void setMethodEditor(CSVMethodEditor methodEditor) {
        this.methodEditor = methodEditor;
    }

    public void setClassInitEditor(CSVStaticEditor classInitEditor) {
        this.classInitEditor = classInitEditor;
    }

    public void setConstructorEditor(CSVConstEditor constructorEditor) {
        this.constructorEditor = constructorEditor;
    }

    public void analyze(CtMethod cm, Object[] mc, MyTranslator t) {
    }


    public void applyAdvice(CtClass cClass, CtConstructor cMtd, CSVConstEditor methodEditor) throws CannotCompileException {
        {/*i can edit this*/
            Collection ad = (Collection) advices.get(cClass.getName());
            AdviceInfo info = null;
            if (ad != null) {
                for (Object o : ad) {
                    info = (AdviceInfo) o;
                    if (!BytecodeHelper.extendsClass(cClass, info.Extends)) {
                        continue;
                    }
                    if (!BytecodeHelper.implementsInterface(cClass, info.Implements)) {
                        continue;
                    }
                    String mtdSig = info.MethodSignature == null ? "*" : info.MethodSignature;
                    if (NameAlgo.like(cMtd.getName() + cMtd.getSignature(), mtdSig)) {
                        methodEditor.setAdvice(info);
                        methodEditor.setCons(cMtd, cClass);
                        cMtd.instrument(methodEditor);
                    }
                }
            }
        }
    }

    public void applyAdvice(CtClass cClass, CtConstructor cMtd, CSVStaticEditor methodEditor) throws CannotCompileException {
        {/*i can edit this*/
            Collection ad = (Collection) advices.get(cClass.getName());
            AdviceInfo info = null;
            if (ad != null) {
                for (Object o : ad) {
                    info = (AdviceInfo) o;
                    if (!BytecodeHelper.extendsClass(cClass, info.Extends)) {
                        continue;
                    }
                    if (!BytecodeHelper.implementsInterface(cClass, info.Implements)) {
                        continue;
                    }
                    String mtdSig = info.MethodSignature == null ? "*" : info.MethodSignature;
                    if (NameAlgo.like(cMtd.getName() + cMtd.getSignature(), mtdSig)) {
                        methodEditor.setAdvice(info);
                        methodEditor.setCons(cMtd, cClass);
                        cMtd.instrument(methodEditor);
                    }
                }
            }
        }
    }

    public void applyAdvice(CtClass cClass, CtMethod cMtd, CSVMethodEditor methodEditor) throws CannotCompileException {
        {/*i can edit this*/
            Collection ad = (Collection) advices.get(cClass.getName());
            AdviceInfo info = null;
            if (ad != null) {
                for (Object o : ad) {
                    info = (AdviceInfo) o;
                    if (!BytecodeHelper.extendsClass(cClass, info.Extends)) {
                        continue;
                    }
                    if (!BytecodeHelper.implementsInterface(cClass, info.Implements)) {
                        continue;
                    }
                    String mtdSig = info.MethodSignature == null ? "*" : info.MethodSignature;
                    if (NameAlgo.like(cMtd.getName() + cMtd.getSignature(), mtdSig)) {
                        methodEditor.setAdvice(info);
                        methodEditor.setMethod(cMtd, cClass);
                        cMtd.instrument(methodEditor);
                    }
                }
            }
        }
    }

    public void applyConstuctorAdvice(CtClass cClass, CtConstructor cMtd) throws CannotCompileException {
        {/*i can edit this*/
            Collection ad = (Collection) advices.get(cClass.getName());
            AdviceInfo info = null;
            if (ad != null) {
                for (Object o : ad) {
                    info = (AdviceInfo) o;
                    if (!BytecodeHelper.extendsClass(cClass, info.Extends)) {
                        continue;
                    }
                    if (!BytecodeHelper.implementsInterface(cClass, info.Implements)) {
                        continue;
                    }
                    String mtdSig = info.MethodSignature == null ? "*" : info.MethodSignature;
                    if (NameAlgo.like(cMtd.getName() + cMtd.getSignature(), mtdSig)) {
                        if (((int) info.MatchType == MatchTypes.methodReplace)) {
                            cMtd.setBody(info.Code);
                        }
                        if (((int) info.MatchType == MatchTypes.methodBefore)) {
                            cMtd.insertBefore(info.Code);
                        }
                        if ((int) info.MatchType == MatchTypes.methodAfter) {
                            cMtd.insertAfter(info.Code, false);
                        }
                        if ((int) info.MatchType == MatchTypes.methodAfterFinal) {
                            cMtd.insertAfter(info.Code, true);
                        }
                        if ((int) info.MatchType == MatchTypes.methodLine) {
                            cMtd.insertAt((int) info.LineNo, info.Code);
                        }
                    }
                }
            }
        }
    }

    public void applyMethodAdvice(CtClass cClass, CtMethod cMtd) throws CannotCompileException {
        {/*i can edit this*/
            Collection ad = (Collection) advices.get(cClass.getName());
            AdviceInfo info = null;
            if (ad != null) {
                for (Object o : ad) {
                    info = (AdviceInfo) o;
                    if (!BytecodeHelper.extendsClass(cClass, info.Extends)) {
                        continue;
                    }
                    if (!BytecodeHelper.implementsInterface(cClass, info.Implements)) {
                        continue;
                    }
                    String mtdSig = info.MethodSignature == null ? "*" : info.MethodSignature;
                    if (NameAlgo.like(cMtd.getName() + cMtd.getSignature(), mtdSig)) {
                        if (((int) info.MatchType == MatchTypes.methodReplace)) {
                            cMtd.setBody(info.Code);
                        }
                        if (((int) info.MatchType == MatchTypes.methodBefore)) {
                            cMtd.insertBefore(info.Code);
                        }
                        if ((int) info.MatchType == MatchTypes.methodAfter) {
                            cMtd.insertAfter(info.Code, false);
                        }
                        if ((int) info.MatchType == MatchTypes.methodAfterFinal) {
                            cMtd.insertAfter(info.Code, true);
                        }
                        if ((int) info.MatchType == MatchTypes.methodLine) {
                            cMtd.insertAt((int) info.LineNo, info.Code);
                        }
                    }
                }
            }
        }
    }

    public void applyStaticAdvice(CtClass cClass, CtConstructor cMtd) throws CannotCompileException {
        {/*i can edit this*/
            Collection ad = (Collection) advices.get(cClass.getName());
            AdviceInfo info = null;
            if (ad != null) {
                for (Object o : ad) {
                    info = (AdviceInfo) o;
                    if (!BytecodeHelper.extendsClass(cClass, info.Extends)) {
                        continue;
                    }
                    if (!BytecodeHelper.implementsInterface(cClass, info.Implements)) {
                        continue;
                    }
                    String mtdSig = info.MethodSignature == null ? "*" : info.MethodSignature;
                    if (NameAlgo.like(cMtd.getName() + cMtd.getSignature(), mtdSig)) {
                        if (((int) info.MatchType == MatchTypes.methodReplace)) {
                            cMtd.setBody(info.Code);
                        }
                        if (((int) info.MatchType == MatchTypes.methodBefore)) {
                            cMtd.insertBefore(info.Code);
                        }
                        if ((int) info.MatchType == MatchTypes.methodAfter) {
                            cMtd.insertAfter(info.Code, false);
                        }
                        if ((int) info.MatchType == MatchTypes.methodAfterFinal) {
                            cMtd.insertAfter(info.Code, true);
                        }
                        if ((int) info.MatchType == MatchTypes.methodLine) {
                            cMtd.insertAt((int) info.LineNo, info.Code);
                        }
                    }
                }
            }
        }
    }

    public void analyze(CtClass cc, MyTranslator t) {
        if (canPatch(cc, t)) {
            handleClassFields(cc, t);
            handleClassInit(cc, t);
            handleClassConstructors(cc, t);
            handleClassMethods(cc, t);
        }
    }

    public void handleClassFields(CtClass cc, MyTranslator t) {
        if (classFieldEditor != null) {
            classFieldEditor.setTranslator(t);
            classFieldEditor.setClass(cc);
            CtField[] cf;
            if (noSuper) {
                cf = cc.getDeclaredFields();
            } else {
                cf = cc.getFields();
            }
            for (int i = 0; i < cf.length; i++) {
                classFieldEditor.analyseField(cf[i]);
            }
        }
    }

    public void handleClassInit(CtClass cc, MyTranslator t) {
        try {
            if (classInitEditor != null) {
                CtConstructor classConst = cc.getClassInitializer();
                if (classConst != null) {
                    classInitEditor.setTranslator(t);
                    classInitEditor.setClass(cc);
                    applyStaticAdvice(cc, classConst);
                    applyAdvice(cc, classConst, classInitEditor);
                }
            }
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }

    public void handleClassConstructors(CtClass cc, MyTranslator t) {
        try {
            if (constructorEditor != null) {
                constructorEditor.setTranslator(t);
                CtConstructor[] cons;
                if (noSuper) {
                    cons = cc.getDeclaredConstructors();
                } else {
                    cons = cc.getConstructors();
                }
                for (int i = 0; i < cons.length; i++) {
                    applyConstuctorAdvice(cc, cons[i]);
                    applyAdvice(cc, cons[i], constructorEditor);
                }
            }
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }

    public void handleClassMethods(CtClass cc, MyTranslator t) {
        try {
            if (methodEditor != null) {
                methodEditor.setTranslator(t);
                CtMethod[] methods;
                if (noSuper) {
                    methods = cc.getDeclaredMethods();
                } else {
                    methods = cc.getMethods();
                }
                for (int i = 0; i < methods.length; i++) {
                    applyMethodAdvice(cc, methods[i]);
                    applyAdvice(cc, methods[i], methodEditor);
                }
            }
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }


    public CSVMethodEditor getMethodEditor() {
        return methodEditor;
    }
}

