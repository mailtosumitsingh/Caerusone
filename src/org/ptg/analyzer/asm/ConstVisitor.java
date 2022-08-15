package org.ptg.analyzer.asm;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.ptg.analyzer.model.Ctor;
import org.ptg.analyzer.model.Setter;
import org.ptg.analyzer.model.SimpleMethod;

public class ConstVisitor extends MethodVisitor {
	String name;
	String className;
	Type[] argTypes;
	Type returnType;
	boolean isPublic = false;
	List<Setter> reach;
	boolean isStatic = true;
	Setter curr;
	public ConstVisitor(int api) {
		super(api);
	}

	public ConstVisitor(int api, int access, String className, String name, String desc, String signature, String[] exceptions, List<Setter> reach) {
		super(api);
		this.className = className;
		this.name = name;
		argTypes = Type.getArgumentTypes(desc);
		returnType = Type.getReturnType(desc);
		if ((Opcodes.ACC_PUBLIC & access) > 0) {
			isPublic = true;
		}
		this.reach=reach;
		if(this.name.equals("<init>")){
			curr = new Ctor();
		}else{
			if(name.startsWith("set")){
				curr = new Setter();
				curr.setReturnType(returnType.getClassName());
			}else{
				curr = new SimpleMethod();
				curr.setReturnType(returnType.getClassName());
			}
			
		}
		curr.setName(name);
	}

	@Override
	public void visitCode() {
		super.visitCode();
	}

	@Override
	public void visitEnd() {
		super.visitEnd();
		String className = StringUtils.substringBeforeLast(this.className,".");
		if(isPublic)
			this.reach.add(curr);
	}

	

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		if (name.equals("this")) {
			isStatic = false;
			curr.setStatic(false);
			return;
		}
		int li;
		if (isStatic)
			li = index;
		else
			li = index - 1;

		if (li < this.argTypes.length) {
			Type type = Type.getType(desc);
			curr.getParams().put(name, type.getClassName());
		}
	}
}
