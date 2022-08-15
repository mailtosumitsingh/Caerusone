/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ptg.analyzer;

import java.util.HashMap;

import org.ptg.util.CommonUtil;

/**
 * @author ssingh
 */
public class ScriptHelper {
    HashMap registry = new HashMap();

    public void add(String name, Object value) {
        registry.put(name, value);
    }

    public Object get(String name) {
        return registry.get(name);
    }

    public Object remove(String key) {
        return registry.remove(key);
    }

    public String getRandomString(int len) {
        return CommonUtil.getRandomString(len);
    }

    public String[] getClassesFromDir(String dir) {
        return CommonUtil.getClassesFromDir(dir);
    }

    public String[] getClassesFromJar(String name) {
        return CommonUtil.getClassesFromJar(name);
    }

    public void loadProperties(String filename) {
        CommonUtil.loadProperties(filename, registry);
    }

    public void reset() {
        registry.clear();
    }

}


