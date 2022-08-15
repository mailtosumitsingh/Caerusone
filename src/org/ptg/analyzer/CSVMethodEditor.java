package org.ptg.analyzer;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

import org.ptg.util.CommonUtil;

import core.BytecodeHelper;
import core.NameAlgo;
import extra.MethodEditor;


public class CSVMethodEditor extends MethodEditor {
    CtMethod cMtd;
    CtClass cClass;
    AdviceInfo info;

    public CSVMethodEditor() {
    }

    public void setAdvice(AdviceInfo info) {
        this.info = info;

    }

    public void setMethod(CtMethod ctMethod, CtClass ctClass) {
        cMtd = ctMethod;
        cClass = ctClass;
    }

    public void editMethodCall(MethodCall methodCall) throws CannotCompileException {
        {
            if (checkDoubles(MatchTypes.classNameMatch, info.MatchType)) {
                if (NameAlgo.like(methodCall.getClassName(), info.CalledClass)) {
                    if (NameAlgo.like((methodCall.getMethodName() + methodCall.getSignature()), info.CalledSig)) {
                        doEdition(methodCall, info);
                    }
                }
            }

            if (checkDoubles(MatchTypes.extendsMatch, info.MatchType)) {
                try {
                    CtClass c = methodCall.getMethod().getDeclaringClass();
                    if (BytecodeHelper.extendsClass(c, info.CalledExtends)) {
                        if (NameAlgo.like((methodCall.getMethodName() + methodCall.getSignature()), info.CalledSig)) {
                            doEdition(methodCall, info);
                        }
                    }
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }

            if (checkDoubles(MatchTypes.implementsMatch, info.MatchType)) {
                try {
                    CtClass c = methodCall.getMethod().getDeclaringClass();
                    if (BytecodeHelper.implementsInterface(c, info.CalledImplements)) {
                        if (NameAlgo.like((methodCall.getMethodName() + methodCall.getSignature()), info.CalledSig)) {
                            doEdition(methodCall, info);
                        }
                    }
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }


            if (checkDoubles(MatchTypes.lineMatch, info.MatchType)) {
                if ((methodCall.getLineNumber() - info.LineNo) < 1) {
                    doEdition(methodCall, info);
                }
            }
        }
    }

    private boolean checkDoubles(double d, double d2) {
        return ((int) d == (int) d2);
    }

    private void doEdition(MethodCall methodCall, AdviceInfo info) {
        System.out.println("Applying advice ");
        CommonUtil.dump(info);
        try {
            if ("a".equals(info.Type)) {
                methodCall.replace("{ $_ = $proceed($$);" + info.Code + "}");
            } else if ("b".equals(info.Type)) {
                methodCall.replace("{" + info.Code + " $_ = $proceed($$);}");
            } else if ("r".equals(info.Type)) {
                methodCall.replace("{" + info.Code + "}");
            }

        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }

    private void doFieldEdition(FieldAccess fieldAccess, AdviceInfo info) {
        System.out.println("Applying advice ");
        CommonUtil.dump(info);
        try {
            if ("a".equals(info.Type)) {
                if (fieldAccess.isReader()) {
                    fieldAccess.replace("{ $_ = $proceed($$);" + info.Code + "}");
                } else {
                    fieldAccess.replace("{ $proceed($$);" + info.Code + "}");
                }
            } else if ("b".equals(info.Type)) {
                if (fieldAccess.isReader()) {
                    fieldAccess.replace("{" + info.Code + " $_ = $proceed($$);}");
                } else {
                    fieldAccess.replace("{" + info.Code + " $proceed($$);}");
                }
            } else if ("r".equals(info.Type)) {
                fieldAccess.replace("{" + info.Code + "}");
            }

        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }

    public void editFieldAccess(FieldAccess fieldAccess) throws CannotCompileException {
        if (checkDoubles(MatchTypes.fieldAccessMatch, info.MatchType)) {
            if (NameAlgo.like(fieldAccess.getClassName(), info.CalledClass)) {
                if (NameAlgo.like(fieldAccess.getFieldName(), info.CalledSig)) {
                    doFieldEdition(fieldAccess, info);
                }
            }
        }


    }

}
