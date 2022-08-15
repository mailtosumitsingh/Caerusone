/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.util.procintr.closure;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ptg.util.CommonUtil;
import org.ptg.util.PropInfo;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.PortObj;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;

public class CompileSpringConfigList implements ICompileSpringConfig {
	String targetName =null;
	public CompileSpringConfigList(String targetName) {
		this.targetName = targetName;
	}

	public void realCompileConfig(PropInfo<PropInfo> p) {
		System.out.println("realCompileConfig:" + p.getName());
		if (p.getChilds().size() > 0) {
			processPropInfoWithChilds(p);
		} else {
			System.out.println(p.getPropClass() + " currently only processing classes with members");
		}
	}

	private void processPropInfoWithChilds(PropInfo<PropInfo> p) {
		defaultProcessPropInfoWithChilds(p);
	}

	private void defaultProcessPropInfoWithChilds(PropInfo<PropInfo> p) {
		GenericBeanDefinition bean = new GenericBeanDefinition();
		boolean isSecondLevel = isSecondLastLevel(p);
		List values = new LinkedList();
		if (isSecondLevel) {
			for (PropInfo child : p.getChilds()) {
				String name = child.getName();
				handleSecondLevelProperties(values, child, name, p);
			}
		} else {
			for (PropInfo<PropInfo> child : p.getChilds()) {
				String name = child.getName();
				if (child.getChilds().size() > 0) {
					Object ref = CommonUtil.getDynamicSpringConfig().getBean(child.getGroup() + "_" + name);
					values.add(ref);
				} else {
					handleSecondLevelProperties(values, child, name, p);
				}

			}
		}
		// generic processing for all bean types
		for (PropInfo child : p.getChilds()) {
			if (child.getName().equals("destroyMethodName")) {
				bean.setDestroyMethodName((String) p.getVal());
			}
			if (child.getName().equals("initMethodName")) {
				bean.setInitMethodName((String) p.getVal());
			}
		}
		MutablePropertyValues firstProps = new MutablePropertyValues();
		firstProps.addPropertyValue("sourceList", values);
		try {
			Class targetClass =(Class.forName(targetName));
			firstProps.addPropertyValue("targetListClass", targetClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		bean.setPropertyValues(firstProps);
		bean.setBeanClass(ListFactoryBean.class);
		GenericApplicationContext ctx = CommonUtil.getDynamicSpringConfig();
		if (p.getParent() == null) {// sumit : this might break
			System.out.println("Now registering: "+p.getGroup());
			ctx.registerBeanDefinition(p.getGroup(), bean);
			Object ovj = ctx.getBean(p.getGroup());
			System.out.println(ovj);
		} else {
			System.out.println("Now registering: "+p.getGroup() + "_" + p.getName());
			ctx.registerBeanDefinition(p.getGroup() + "_" + p.getName(), bean);
			Object ovj = ctx.getBean(p.getGroup() + "_" + p.getName());
			System.out.println(ovj);
		}
	}

	private boolean isCreateOrDestroy(String name) {
		return name.equals("destroyMethodName") || name.equals("initMethodName");
	}

	private void handleSecondLevelProperties(List values, PropInfo child, String name, PropInfo<PropInfo> parent) {
		if (!(child.getVal() instanceof java.util.List)) {
			values.add(child.getVal());
		} else {
			java.util.List lst = (List) child.getVal();
			if (lst.size() == 1) {
				Object obj = lst.get(0);
				if (obj instanceof PortObj) {
					PortObj tobj = (PortObj) obj;
					String beanName = tobj.getPortname();
					if(beanName.contains(".")&&!(tobj.getGrp()+"_"+tobj.getDtype()).equals(tobj.getPortname())){
							beanName = StringUtils.substringAfterLast(beanName, ".");
					}
					if (!(tobj.getGrp()+"_"+tobj.getDtype()).equals(tobj.getPortname())) {
						Object ref = CommonUtil.getDynamicSpringConfig().getBean(tobj.getGrp() + "_" + beanName);
						values.add(ref);
						} else {
							Object ref = CommonUtil.getDynamicSpringConfig().getBean(tobj.getGrp() );
							values.add(ref);					
						}
				} else {
					if (obj instanceof FunctionPoint) {
						FunctionPoint tobj = (FunctionPoint) obj;
						values.add(tobj.getVal());
					}
				}

			} else if (lst.size() == 0) {
				System.out.println(name + " Property is not set just declared");
			} else {
				for (Object obj : lst) {
					if (obj instanceof PortObj) {
						PortObj tobj = (PortObj) obj;
						String beanName = tobj.getPortname();
						if(beanName.contains(".")&&!(tobj.getGrp()+"_"+tobj.getDtype()).equals(tobj.getPortname())){
								beanName = StringUtils.substringAfterLast(beanName, ".");
						}
						if (!(tobj.getGrp()+"_"+tobj.getDtype()).equals(tobj.getPortname())) {
							Object ref = CommonUtil.getDynamicSpringConfig().getBean(tobj.getGrp() + "_" + beanName);
							values.add(ref);
						} else {
							Object ref = CommonUtil.getDynamicSpringConfig().getBean(tobj.getGrp() );
							values.add(ref);
						}
					} else {
						if (obj instanceof FunctionPoint) {
							FunctionPoint tobj = (FunctionPoint) obj;
							values.add(tobj.getVal());
						}
					}
				}
			}
		}
	}

	private boolean isSecondLastLevel(PropInfo<PropInfo> p) {
		boolean isSecondLL = true;
		for (PropInfo child : p.getChilds()) {
			if (child.getChilds() == null || child.getChilds().size() == 0) {

			} else {
				isSecondLL = false;
				break;
			}
		}
		return isSecondLL;
	}

}