/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class ReflectionUtils {
    /* this function creates instance of a class, it takes class types of params from the objects
         * passed as params
         * */
    public static Object createInstanceNative(String name, Class[] cls, Object[] obj) {
        Object ret = null;
        Constructor cons = null;
        try {
            Class cl = Class.forName(name);
            if (cls.length > 0) {
                cons = cl.getConstructor(cls);
                ret = cons.newInstance(obj);
            } else {
                cons = cl.getConstructor(null);
                ret = cons.newInstance(null);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ret;
    }
    public static Object createInstance(String name) {
    	return createInstance(name, new Object[0]);
    }
    /* this function invokes the INSTANCE of a class, it takes class types of params from the objects
         * passed as params
         * */
    public static Object createInstance(String name, Object[] consParas) {
    	Class cl = null;
        Object ret = null;
        Constructor cons = null;
        try {
        	  cl = Class.forName(name);
            int i = consParas == null ? 0 : consParas.length;
            Class[] cls = new Class[i];
            --i;
            for (; i >= 0; i--) {
                cls[i] = consParas[i].getClass();
            }
            if (consParas != null && consParas.length > 0) {
                cons = cl.getConstructor(cls);
                ret = cons.newInstance(consParas);
            } else {
                cons = cl.getConstructor(null);
                ret = cons.newInstance(null);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ret;
    }
    /* this function invokes the method of a class, it takes class types of params from the objects
         * passed as params
         * */

    public static Object invoke(Object classObj, String methodName, Object[] consParas) {
        Object ret = null;
        Method method = null;
        try {
            int i = consParas.length;
            Object[] objArray = new Object[i];
            Class[] cls = new Class[i];
            --i;
            for (; i >= 0; i--) {
                cls[i] = consParas[i].getClass();
            }
            Class cl = classObj.getClass();
            if (consParas.length > 0) {
                method = cl.getMethod(methodName, cls);
                ret = method.invoke(classObj, consParas);
            } else {
                method = cl.getMethod(methodName, null);
                ret = method.invoke(classObj, null);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /* this function invokes the static method of a class
        * */
    public static Object invokeStatic(String className, String methodName, Object[] consParas) {
        Object ret = null;
        Method method = null;
        try {
            int i = consParas == null ? 0 : consParas.length;
            Class cl = Class.forName(className);
            if (i > 0) {
                Class[] cls = new Class[i];
                --i;
                for (; i >= 0; i--) {
                    cls[i] = consParas[i].getClass();
                }
                method = cl.getMethod(methodName, cls);
                ret = method.invoke(null, consParas);
            } else {
                method = cl.getMethod(methodName, null);
                ret = method.invoke(null, null);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static Object invokeStaticNative(String className, String methodName, Class[] paraClass, Object[] consParas) {
        Object ret = null;
        Method method = null;
        try {
            int i = consParas == null ? 0 : consParas.length;
            Class cl = Class.forName(className);
            if (i > 0) {
                method = cl.getMethod(methodName, paraClass);
                ret = method.invoke(null, consParas);
            } else {
                method = cl.getMethod(methodName, null);
                ret = method.invoke(null, null);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static Object getFieldValueSafely(Object object, String key, Class c) {
        Field fld = null;
        Class tclass = object.getClass();
        try {
            fld = tclass.getField(key);
            if (!fld.getType().equals(c)) {
                return null;
            }
            try {
                fld.setAccessible(true);
                return fld.get(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public static Object getFieldValue(Object object, String key) {
        Field fld = null;
        Class tclass = object.getClass();
        try {
            fld = tclass.getField(key);
            try {
                fld.setAccessible(true);
                return fld.get(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
    public static Object setFieldValue(Object object, String key, Object value) {
        Field fld = null;
        Class tclass = object.getClass();
        Object ret = null;
        try {
            fld = tclass.getField(key);
            try {
                fld.setAccessible(true);
                ret = fld.get(object);
                if (fld.getType().equals(boolean.class) || fld.getType().equals(Boolean.class)) {
                    String temp = value.toString();
                    fld.set(object, (temp.startsWith("t") || temp.startsWith("T")) ? Boolean.TRUE : Boolean.FALSE);
                } else if (fld.getType().equals(Double.class) || fld.getType().equals(double.class)) {
                    fld.set(object, Double.parseDouble(value.toString()));
                } else if (fld.getType().equals(int.class) || fld.getType().equals(Integer.class)) {
                    fld.set(object, (int) Math.round(Double.valueOf(value.toString())));
                } else if (fld.getType().equals(String.class)) {    /*reverse format in this case*/
                    if (value.getClass().equals(Double.class) || value.getClass().equals(double.class) || value.getClass().equals(Integer.class) || value.getClass().equals(int.class)) {
                        fld.set(object, value.toString());
                    } else if (value.getClass().equals(Boolean.class) || value.getClass().equals(boolean.class)) {
                        fld.set(object, (Boolean.valueOf(value.toString()) == true ? "true" : "false"));
                    } else {
                        fld.set(object, value);
                    }
                } else {
                    fld.set(object, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ret;

    }
    public static Object setStaticFieldValue(Object object, String key, Object value) {
        Field fld = null;
        Class tclass = object.getClass();
        Object ret = null;
        try {
        	fld  = tclass.getDeclaredField(key);
            try {
                fld.setAccessible(true);
                ret = fld.get(object);
                if (fld.getType().equals(boolean.class) || fld.getType().equals(Boolean.class)) {
                    String temp = value.toString();
                    fld.set(object, (temp.startsWith("t") || temp.startsWith("T")) ? Boolean.TRUE : Boolean.FALSE);
                } else if (fld.getType().equals(Double.class) || fld.getType().equals(double.class)) {
                    fld.set(object, Double.parseDouble(value.toString()));
                } else if (fld.getType().equals(int.class) || fld.getType().equals(Integer.class)) {
                    fld.set(object, (int) Math.round(Double.valueOf(value.toString())));
                } else if (fld.getType().equals(String.class)) {    /*reverse format in this case*/
                    if (value.getClass().equals(Double.class) || value.getClass().equals(double.class) || value.getClass().equals(Integer.class) || value.getClass().equals(int.class)) {
                        fld.set(object, value.toString());
                    } else if (value.getClass().equals(Boolean.class) || value.getClass().equals(boolean.class)) {
                        fld.set(object, (Boolean.valueOf(value.toString()) == true ? "true" : "false"));
                    } else {
                        fld.set(object, value);
                    }
                } else {
                    fld.set(object, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ret;
    }


    public static Object setFieldValuesFromMap(Object object, Map<String, Object> m) {
        Object ret = null;
        for (Map.Entry<String, Object> en : m.entrySet()) {
            Object value = en.getValue();
            String key = en.getKey();
            Field fld = null;
            Class tclass = object.getClass();
            try {
                fld = tclass.getField(key);
                try {
                    fld.setAccessible(true);
                    ret = fld.get(object);
                    if (fld.getType().equals(boolean.class) || fld.getType().equals(Boolean.class)) {
                        String temp = value.toString();
                        fld.set(object, (temp.startsWith("t") || temp.startsWith("T")) ? Boolean.TRUE : Boolean.FALSE);
                    } else {
                        fld.set(object, value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchFieldException e) {
                System.out.println("Could not found field " + key);
            }
        }
        return ret;
    }

    public static Map getDefinition(Object object) {
        Map m = new TreeMap();
        for (Field fld : object.getClass().getFields()) {
            fld.setAccessible(true);
            m.put(fld.getName(), fld.getType());
        }
        return m;
    }
    public static Map getFldIndexMap(int start, Object object) {
        Map m = new TreeMap();
        for (Field fld : object.getClass().getFields()) {
            fld.setAccessible(true);
            m.put(new Integer(start++), fld.getName());
        }
        return m;
    }
    public static Method getMethod(Object object, String name) {
        for (Method mtd : object.getClass().getMethods()) {
        	if(mtd.getName().equals(name) )
        		return mtd;
        }
        return null;
    }
    public static Method getMethod(Class cls, String name) {
        for (Method mtd : cls.getMethods()) {
        	if(mtd.getName().equals(name) )
        		return mtd;
        }
        return null;
    }
    public static Collection<String> getMethodNames(Class cls) {
    	Collection<String> col  = new ArrayList<String>();
        for (Method mtd : cls.getMethods()) {
        	col.add(mtd.getName());
        		
        }
        return col;
    }

}
