package org.ptg.analyzer.asm;

import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.ptg.analyzer.model.Setter;

public class ConstExtractor extends ClassVisitor{
    String cname;
    List<Setter> reach = new LinkedList<Setter>();
	public ConstExtractor(int api) {
		super(api);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		ConstVisitor viewMethodVisitor = new ConstVisitor(api , access, cname,name, desc, signature, exceptions,reach);
		return viewMethodVisitor;
	}

	@Override
	public void visitSource(String source, String debug) {
		super.visitSource(source, debug);
		cname = source;
	}

	public List<Setter> getReach() {
		
		return reach;
	}

	public void setReach(List<Setter> reach) {
		this.reach = reach;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
					super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		// TODO Auto-generated method stub
		return super.visitAnnotation(desc, visible);
	}

	@Override
	public void visitAttribute(Attribute attr) {
		// TODO Auto-generated method stub
		super.visitAttribute(attr);
	}

	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		// TODO Auto-generated method stub
		super.visitInnerClass(name, outerName, innerName, access);
	}

	@Override
	public void visitOuterClass(String owner, String name, String desc) {
		// TODO Auto-generated method stub
		super.visitOuterClass(owner, name, desc);
	}
    
	
}
