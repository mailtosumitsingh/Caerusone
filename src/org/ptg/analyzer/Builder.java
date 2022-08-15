package org.ptg.analyzer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.ptg.util.CommonUtil;

import core.NameAlgo;
import core.Project;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Feb 4, 2009
 * Time: 9:04:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class Builder {
	ScriptHelper helper = new ScriptHelper();
    int patchingAnalyser = 1;
    int callcollectorindex = 2;
    String dirpath = "C:\\sumit\\projects\\jirareport";
    String deepAnalyzeClasses = "myprojectmanager.test";
    String classListKey = dirpath + "-classs";
    String outputdir = dirpath + "\\output";
    boolean addLibs = false;
    String buildDir = dirpath + "\\build";
    Project p;
    MultiMap advices = new MultiHashMap();
    String libDir = dirpath + "\\lib";
    String nameFrom = "resources\\data\\ClassPatchInfo.xls";
    CodeBuilderAnalyzer t;
    CodeBuilderAnalyzer ana;

    public Builder(String buildPath, String classPattern, String libPath, String outputdir) {
        this.buildDir = buildPath;
        this.deepAnalyzeClasses = classPattern;
        this.libDir = libPath;
        this.outputdir = outputdir;
        this.nameFrom = nameFrom;
        t = createMethodAnalyzer();
        ana = createMethodCollector();
        p = createProject(dirpath);
        p.addAnalyser(t, patchingAnalyser);
        p.addAnalyser(ana, callcollectorindex);
        p.setCurrentAnalyser(patchingAnalyser);


    }


    public void patchClasses(Map<Integer, AdviceInfo> tempAdvices ) {
        buildMap(tempAdvices );
        String[] list = getAllClassesFromProjDir();
        int anaCount = 0;
        for (String classToAnalyze : list) {

            if (cannotFilter(classToAnalyze)) {
                anaCount++;
                System.out.println("Now analyzing class: " + classToAnalyze + "\n");
                analyzeClass(classToAnalyze);
            } else {
                //System.out.println("Dropping class [doesnot match]: " + classToAnalyze + "\n");
            }
        }
        System.out.println("Total classes analyzed = " + anaCount);

    }


    public boolean cannotFilter(String classToAnalyze) {
        return NameAlgo.like(classToAnalyze, deepAnalyzeClasses);
    }

    private void buildMap(Map<Integer, AdviceInfo> tempAdvices ) {
        advices.clear();
         

        Iterator i = tempAdvices.keySet().iterator();
        while (i.hasNext()) {
            AdviceInfo advice = tempAdvices.get(i.next());
            advices.put(advice.ClassName, advice);
        }
    }


    public void analyzeClass(String classToAnalyze) {
        p.analyze(classToAnalyze);
    }

    public Project createProject(String dirpath) {
        Project p = (Project) helper.get(dirpath + "Project");
        if (p == null) {
            p = new Project();
            p.setSaveDir(outputdir);
            p.setSaveFile(true);
            if (addLibs == true) {
                p.addJarInDir(libDir);
            }
            p.addAllowedExtsName("*");
            p.addAllowedImplsName("*");
            p.addAllowedClassName("*");
            p.addClassFilesInDir(buildDir);
            p.setSystemPath();
            p.setSaveFile(true);
            p.setDebug(false);
            p.addAllowedMethodComp("*", "*");
            helper.add(dirpath + "Project", p);
        }
        return p;
    }

    public CodeBuilderAnalyzer createMethodAnalyzer() {
        CodeBuilderAnalyzer t = new CodeBuilderAnalyzer(advices);
        CSVMethodEditor ed = new CSVMethodEditor();
        CSVConstEditor ce = new CSVConstEditor();
        CSVStaticEditor se = new CSVStaticEditor();
        CSVFieldAccesEditor fe = new CSVFieldAccesEditor();
        t.setMethodEditor(ed);
        t.setClassFieldEditor(fe);
        t.setConstructorEditor(ce);
        t.setClassInitEditor(se);
        t.setNoSuper(true);
        return t;
    }

    public CodeBuilderAnalyzer createMethodCollector() {
        CallCollectorAnalyzer t = new CallCollectorAnalyzer(advices);
        CallCollectorEditor ed = new CallCollectorEditor();
        t.setMethodEditor(ed);
        t.setNoSuper(true);
        return t;
    }

    public List getMethodsForClass(String classToAnalyze) {
        CtClass ctClass = p.getClass(classToAnalyze);
        CtConstructor cl = ctClass.getClassInitializer();
        CtMethod[] mtds = ctClass.getDeclaredMethods();
        List mtdSigList = new ArrayList();
        Map m = new HashMap();
        for (CtMethod mtd : mtds) {
            mtdSigList.add(mtd);
        }
        if (cl != null) {
            mtdSigList.add(cl);
        }
        return mtdSigList;
    }

    public String[] getAllClassesFromProjDir() {
        String[] list = (String[]) helper.get(classListKey);
        if (list == null) {
            list = CommonUtil.getClassesFromDir(buildDir);
            helper.add(classListKey, list);
        }
        return list;
    }

    public CtClass getClassFile(String name) {
        return p.getClass(name);
    }

    public List getCallsFromfunctions(String clz, CtMethod mtd) {
        List lst = null;
        try {
            p.setCurrentAnalyser(callcollectorindex);
            CallCollectorEditor ed = (CallCollectorEditor) ana.getMethodEditor();
            ed.setMethod(mtd);
            ed.setList(new ArrayList());
            p.analyze(clz);
            lst = ed.getList();
            ed.setList(new ArrayList());
            ed.setMethod(null);
        }

        finally {
            p.setCurrentAnalyser(patchingAnalyser);
        }
        return lst;
    }

}
