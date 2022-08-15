package org.ptg.proptest;


import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ptg.util.PropCollType;
import org.ptg.util.PropInfo;

public class Generate {
	public static void main(String[] args) {
		String name = "org.ptg.proptest.b";
		//String name = "org.ptg.util.PropInfo";
		String id = "id1";
		String force = "output";
		Map<String,String> stack = new LinkedHashMap<String,String>();
		PropGraph pg = new PropGraph();
		PropInfo<PropInfo> prop = pg.analyze(name, null,stack,0);
		Generate g = new Generate();
		
		String s = g.getHtml(id,name, prop,force,false);
		System.out.println(s);
	}

	public String getHtml(String id, String name, PropInfo prop,String force, boolean addRemapPort) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div>\n");
		sb.append("<ul  class=\"treeview\" id=\"t_"+id+"\" grp=\""+name+"\">\n");
		generateChildHTML(id,prop, sb,0,force);
		sb.append("</ul>"+"\n");
		sb.append("</div>"+"\n");
		if(addRemapPort){
			String pClass  = prop.getPropClass();
			sb.append("<div class=\"portable dojoDndItem \" pval=\"\" grpid=\""+id+"\" id=\""+id+"_"+"remapport"+"\" index=\""+100+"\" ptype=\"input\" dtype=\""+pClass+"\" >"+"inp1"+"</div");	
		}
		return sb.toString();
	}

	public void generateChildHTML(String id, PropInfo<PropInfo> prop, StringBuilder sb,int indx,String force ) {
		String s = prop.getName();
		String proptype = getPropType(prop,force);
		{
			String myid = getPropId(prop, indx);
			String pClass  = prop.getPropClass();
			PropCollType coll = prop.getCollType();
			int i = indx;
			if(coll!=null){
				pClass = pClass+"/"+coll.name();
			}
			sb.append("<li class=\"submenu\" style=\"background-image: url(&quot;open.gif&quot;);\">\n");
			Object val = prop.getVal();
			sb.append("<div rel=\"open\" style=\"display: block;\" class=\"portable dojoDndItem \" pval=\""+(val==null?"":val.toString())+"\" grpid=\""+id+"\" id=\""+id+"_"+myid+"\" index=\""+i+"\" ptype=\""+proptype+"\" dtype=\""+pClass+"\">");
			sb.append(s+"\n");
			sb.append("</div>"+"\n");
			sb.append("<ul rel=\"open\"  ulid=\""+id+"_"+myid+"\" id=\""+id+"_"+myid+"\" style=\"display: block;\">"+"\n");
			
			
			
			for (PropInfo l : prop.getChilds()) {
				i++;
				if(l.getChilds().size()>0){
					generateChildHTML(id,l,sb,i,force);
				}else{
				sb.append("<li>"+"\n");
				String ptype = "prop";
				ptype = getPropType(l,force);
				String mid = getPropId(l, i);
				pClass = l.getPropClass();
				coll = l.getCollType();
				val = prop.getVal();
				if(coll!=null){
					pClass = pClass+"/"+coll.name();
				}
				sb.append("<div class=\"portable dojoDndItem \" style=\"width:100%;\" pval=\""+(val==null?"":val.toString())+"\" grpid=\""+id+"\" id=\""+id+"_"+mid+"\" index=\""+i+"\" ptype=\""+ptype+"\" dtype=\""+pClass+"\">");
				sb.append(l.getName()+"\n");
				sb.append("</div>"+"\n");
				sb.append("</li>"+"\n");
				}
				
			}
			sb.append("</ul>"+"\n");
			sb.append("</li>"+"\n");
		}
	}

	public String getPropId(PropInfo<PropInfo> prop, int indx) {
		String myid = prop.getName();
		if(prop.getPathExpr()!=null && prop.getPathExpr().length()>0){
			myid = prop.getPathExpr();
			myid = StringUtils.replace(myid, "/", ".");
		}
		return myid;
	}

	public String getPropType(PropInfo l,String force) {
		String ptype;
		if(force!=null)
			return force;
		if(l.getType()==null)
			return "output";
		switch (l.getType()){
		case READ:
			ptype = "output";
			break;
		case WRITE:
			ptype = "input";
			break;
		case READWRITE:
			ptype = "prop";
			break;
		default:
			ptype = "prop";
			break;

		}
		return ptype;
	}
	
}
