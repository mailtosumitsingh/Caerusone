package org.ptg.util.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.util.StringUtils;
import org.ptg.util.mapper.AnonDefObj;
import org.ptg.util.mapper.FunctionPortObj;
import org.ptg.util.mapper.PortObj;
import org.ptg.util.mapper.v2.FPGraph2;

public class LatLonToGeoHash extends AbstractICompileFunction {

	@Override
	public Object compile(AnonDefObj anon, List<FunctionPortObj> inputs, List<FunctionPortObj> output,FPGraph2 graph) {
		StringBuilder solution = new StringBuilder();
		solution.append("\t\t try{\n");
		String ret = "";
		FunctionPortObj s = inputs.get(0);
		PortObj po = s.getPo();
		String lhs = "";
		if (s.getGrpName().equals("unk")){
			lhs = StringUtils.replace(s.getMyPort().getId(),".","_")+";\n";
		}else{
			lhs = s.getGrpName() + ".get(currIndex).get ( " + s.getIndex()+" );\n ";
		}
		// declare var
		ret += "double lat = 0d;\n";
		ret += "double lon = 0d;\n";
		ret += ("String " + anon.getId() + "  =  " + "(" + " String " + ")");
		ret += lhs;
		// use var
		ret += "Matcher m = locPattern.matcher(" + anon.getId() + ");\n";
		ret += "if(m.matches()){\n";
		ret += "System.out.println(\"Now matching[GroupCount]: \"+m.groupCount());\n";
		ret += "System.out.println(\"Grp1[lat]: \"+m.group(1));\n";
		ret += "System.out.println(\"Grp2[lon]: \"+m.group(2));\n";
		ret += "lat = Double.parseDouble(m.group(1));\n";
		ret += "lon = Double.parseDouble(m.group(2));\n";
		ret += "}\n";

		Map<String, List<FunctionPortObj>> map = new HashMap<String, List<FunctionPortObj>>();
		for (FunctionPortObj p : output) {
			List<FunctionPortObj> obj = map.get(p.getGrpName());
			if (obj == null) {
				obj = new ArrayList<FunctionPortObj>();
				map.put(p.getGrpName(), obj);
			}
			obj.add(p);
		}
		solution.append(ret);
		StringBuilder preDefVars = new StringBuilder();
		for (List<FunctionPortObj> mo : map.values()) {
			for (FunctionPortObj sLat : mo) {
				String part = "lat";
				if (sLat.getMyPort().getPortname().equals("lat")){
					part = "lat";
				}else if (sLat.getMyPort().getPortname().equals("lon")){
					part = "lon";
				}else if (sLat.getMyPort().getPortname().equals("geohash")){
					part = "GeoHash.withCharacterPrecision(lat, lon, 9).toBase32()";
				}
				if (sLat.getGrpName().equals("unk")){
					preDefVars.append("Object "+ StringUtils.replace(sLat.getPo().getId(),".","_")+" = null;\n");
					solution.append(StringUtils.replace(sLat.getPo().getId(),".","_") + " = " + part +"  ; \n");
				}else{
					solution.append(sLat.getGrpName() + getRHSSetterFunction()+"  ( " + sLat.getIndex() + " , " + part + " ) ; \n");
				}
			}
		}
		solution.append("\t}catch(Exception exp){\nlogError(currIndex,\"" + anon.getId() + ":" + anon.getAnonType() + "\",exp.getMessage());\n } \n");
		preDefVars.append(solution);
		return preDefVars.toString();
	}

}
