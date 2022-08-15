package org.ptg.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class PluginClassLoader extends ClassLoader {
	List<String> paths = new LinkedList<String>();
	ClassPool p = null;
	public PluginClassLoader() {
		p = new ClassPool();
		p.childFirstLookup=true;
		p.appendSystemPath();
	}

	public void add(String string) throws NotFoundException {
		paths.add(string);
		p.insertClassPath(string);
	}

	public Class loadClass(String name) {
		try {
			Class temp = null;
			try {
				temp = super.loadClass(name);
			} catch (ClassNotFoundException e) {
				System.out.println("class Not Found : "+name + "Will explicit load");
			}
			if(temp!=null)return temp;
			CtClass c = p.getCtClass(name);
			byte[]d= c.toBytecode();
			 return super.defineClass(name,d, 0, d.length);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (CannotCompileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public URL getResource(String name) {
		// TODO Auto-generated method stub
		return super.getResource(name);
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		// TODO Auto-generated method stub
		return super.getResources(name);
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		// TODO Auto-generated method stub
		return super.getResourceAsStream(name);
	}

}
