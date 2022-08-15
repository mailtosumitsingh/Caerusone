package org.ptg.analyzer;

import java.util.ArrayList;

import javassist.CannotCompileException;
import javassist.CtMethod;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

public class CallCollectorEditor extends CSVMethodEditor {
    ArrayList list = new ArrayList();
    CtMethod ct;

    public void edit(MethodCall m) throws CannotCompileException {
        if (ct.getName().equals(cMtd.getName()) && ct.getSignature().equals(cMtd.getSignature()))
            list.add(m);

    }

    public void edit(FieldAccess f) throws CannotCompileException {
        if (ct.getName().equals(cMtd.getName()) && ct.getSignature().equals(cMtd.getSignature()))
            list.add(f);
    }


    public ArrayList getList() {
        return list;
    }

    public void setList(ArrayList list) {
        this.list = list;
    }

    public void setMethod(CtMethod ct) {
        this.ct = ct;
    }
}
