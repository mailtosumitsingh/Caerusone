/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.util.procintr.closure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ptg.util.CommonUtil;
import org.ptg.util.PropInfo;
import org.ptg.util.mapper.FunctionPoint;
import org.ptg.util.mapper.PortObj;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;

public class CompileSpringConfig implements ICompileSpringConfig {
	protected Map<String, ICompileSpringConfig> closureMap = new HashMap<String, ICompileSpringConfig>();

	public CompileSpringConfig() {
		closureMap.put("java.util.List", new CompileSpringConfigList("java.util.LinkedList"));
		closureMap.put("java.util.ArrayList", new CompileSpringConfigList("java.util.ArrayList"));
		closureMap.put("java.util.Map", new CompileSpringConfigMap("java.util.LinkedHashMap"));
		closureMap.put("java.util.TreeMap", new CompileSpringConfigMap("java.util.TreeMap"));
		closureMap.put("java.util.LinkedHashMap", new CompileSpringConfigMap("java.util.LinkedHashMap"));
		closureMap.put("java.util.HashMap", new CompileSpringConfigMap("java.util.HashMap"));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ptg.util.procintr.closure.ICompileSpringConfig#realCompileConfig(
	 * org.ptg.util.PropInfo)
	 */
	public void realCompileConfig(PropInfo<PropInfo> p) {
		System.out.println("realCompileConfig:" + p.getName());
		if (p.getChilds().size() > 0) {
			processPropInfoWithChilds(p);
		} else {
			System.out.println(p.getPropClass() + " currently only processing classes with members");
		}
	}

	private void processPropInfoWithChilds(PropInfo<PropInfo> p) {
		ICompileSpringConfig cl = closureMap.get(p.getPropClass());
		if (cl != null) {
			cl.realCompileConfig(p);
		} else {
			defaultProcessPropInfoWithChilds(p);
		}
	}

	private void defaultProcessPropInfoWithChilds(PropInfo<PropInfo> p) {
		GenericBeanDefinition bean = new GenericBeanDefinition();
		MutablePropertyValues firstProps = new MutablePropertyValues();
		boolean isSecondLevel = isSecondLastLevel(p);
		if (isSecondLevel) {
			for (PropInfo child : p.getChilds()) {
				String name = child.getName();
				if (isCreateOrDestroy(name)) {
					continue;
				}
				if (name.equals("ctor")) {
					handleConstructor(bean, child);
					continue;
				}
				handleSecondLevelProperties(firstProps, child, name, p);
			}
		} else {
			for (PropInfo<PropInfo> child : p.getChilds()) {
				String name = child.getName();
				if (isCreateOrDestroy(name)) {
					continue;
				}
				if (name.equals("ctor")) {
					handleConstructor(bean, child);
					continue;
				}
				if (child.getChilds().size() > 0) {
					RuntimeBeanReference ref = new RuntimeBeanReference(child.getGroup() + "_" + name);
					firstProps.addPropertyValue(name, ref);
				} else {
					handleSecondLevelProperties(firstProps, child, name, p);
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
		bean.setPropertyValues(firstProps);
		bean.setBeanClassName(p.getPropClass());
		GenericApplicationContext ctx = CommonUtil.getDynamicSpringConfig();
		if (p.getParent() == null) {// sumit : this might break
			System.out.println("Now registering: "+p.getGroup() );
			ctx.registerBeanDefinition(p.getGroup(), bean);
			Object ovj = ctx.getBean(p.getGroup() );
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

	private void handleSecondLevelProperties(MutablePropertyValues firstProps, PropInfo child, String name, PropInfo<PropInfo> parent) {
		if (!(child.getVal() instanceof java.util.List)) {
			firstProps.addPropertyValue(name, child.getVal());
		} else {
			java.util.List lst = (List) child.getVal();
			if (lst.size() == 1) {
				Object obj = lst.get(0);
				if (obj instanceof PortObj) {
					PortObj tobj = (PortObj) obj;
					String beanName = tobj.getPortname();
					if (beanName.contains(".") && !(tobj.getGrp()+"_"+tobj.getDtype()).equals(tobj.getPortname())) {
						beanName = StringUtils.substringAfterLast(beanName, ".");
					}
					if (!(tobj.getGrp()+"_"+tobj.getDtype()).equals(tobj.getPortname())) {
						RuntimeBeanReference ref = new RuntimeBeanReference(tobj.getGrp() + "_" + beanName);
						firstProps.addPropertyValue(name, ref);
					} else {
						RuntimeBeanReference ref = new RuntimeBeanReference(tobj.getGrp());
						firstProps.addPropertyValue(name, ref);
					}
				} else {
					if (obj instanceof FunctionPoint) {
						FunctionPoint tobj = (FunctionPoint) obj;
						firstProps.addPropertyValue(name, tobj.getVal());
					}
				}

			} else if (lst.size() == 0) {
				System.out.println(name + " Property is not set just declared");
			} else {
				java.util.List ref = new java.util.LinkedList();
				for (Object obj : lst) {
					if (obj instanceof PortObj) {
						PortObj tobj = (PortObj) obj;
						String beanName = tobj.getPortname();
						if (beanName.contains(".") && !(tobj.getGrp()+"_"+tobj.getDtype()).equals(tobj.getPortname())) {
							beanName = StringUtils.substringAfterLast(beanName, ".");
						}
						if (!(tobj.getGrp()+"_"+tobj.getDtype()).equals(tobj.getPortname())) {
							RuntimeBeanReference tref = new RuntimeBeanReference(tobj.getGrp() + "_" + beanName);
							ref.add(tref);
						} else {
							RuntimeBeanReference tref = new RuntimeBeanReference(tobj.getGrp());
							ref.add(tref);
						}

					} else {
						if (obj instanceof FunctionPoint) {
							FunctionPoint tobj = (FunctionPoint) obj;
							ref.add(tobj.getVal());
						}
					}
				}
				firstProps.addPropertyValue(name, ref);
			}
		}
	}

	private void handleConstructor(GenericBeanDefinition constBean, PropInfo<PropInfo> parent) {
		ConstructorArgumentValues firstProps = new ConstructorArgumentValues();

		for (PropInfo child : parent.getChilds()) {
			String name = child.getName();
			if (!(child.getVal() instanceof java.util.List)) {
				firstProps.addGenericArgumentValue(child.getVal());
			} else {
				java.util.List lst = (List) child.getVal();
				if (lst.size() == 1) {
					Object obj = lst.get(0);
					if (obj instanceof PortObj) {
						PortObj tobj = (PortObj) obj;
						String beanName = tobj.getPortname();
						if (beanName.contains(".")) {
							beanName = StringUtils.substringAfterLast(beanName, ".");
						}
						RuntimeBeanReference ref = new RuntimeBeanReference(tobj.getGrp() + "_" + beanName);
						firstProps.addGenericArgumentValue(ref);
					} else {
						if (obj instanceof FunctionPoint) {
							FunctionPoint tobj = (FunctionPoint) obj;
							firstProps.addGenericArgumentValue(tobj.getVal());
						}
					}

				} else if (lst.size() == 0) {
					System.out.println(name + " Property is not set just declared");
				} else {
					java.util.List ref = new java.util.LinkedList();
					for (Object obj : lst) {
						if (obj instanceof PortObj) {
							PortObj tobj = (PortObj) obj;
							String beanName = tobj.getPortname();
							if (beanName.contains(".")) {
								beanName = StringUtils.substringAfterLast(beanName, ".");
							}
							RuntimeBeanReference tref = new RuntimeBeanReference(tobj.getGrp() + "_" + beanName);
							ref.add(tref);
						} else {
							if (obj instanceof FunctionPoint) {
								FunctionPoint tobj = (FunctionPoint) obj;
								ref.add(tobj.getVal());
							}
						}
					}
					firstProps.addGenericArgumentValue(ref);
				}
			}
		}
		// TODO Auto-generated method stub
		constBean.setConstructorArgumentValues(firstProps);

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