package org.ptg.migrate;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.ptg.util.HTTPClientUtil;

import com.google.common.collect.HashMultimap;



public class MigrateDroppables {
	
	
	public static void main(String[] args) throws Exception{
		String url ="http://192.168.1.9:8095/site/GetDesign?graphid=Groups";
		String fname = "migrate/groups.json";
		HTTPClientUtil.saveUrlTxtToFile(url, fname);
		String json  =HTTPClientUtil.saveUrlTxtToFile("http://192.168.1.9:8095/site/GetStaticComponents", "migrate/components.json");
		JSONArray js  = JSONArray.fromObject(json);
		for(int i = 0;i<js.size();i++){
			JSONObject j = (JSONObject) js.get(i);
			String scname = j.getString("name");
			System.out.println(scname);
			String scUrl = "http://192.168.1.9:8095/site/GetStaticComponent" ;
			Map<String,String> params  = new HashMap<String,String>();
			params.put("name", scname);
			
			String scjson  =HTTPClientUtil.saveUrlTxtToFile(scUrl,"migrate/comp/"+scname+".json",params);
			String targetLoc ="http://localhost:8095/SaveStaticComponent";
			Map<String,String> params2  = new HashMap<String,String>();
			params2.put("name", scname);
			params2.put("doc", scname);
			params2.put("tosave", scjson);
			byte[] ret  = HTTPClientUtil.doPost(params2,targetLoc);
			System.out.println(new String(ret));
			Thread.sleep(250);
				
		}
		//
	}
	private static void processDroppables() throws IOException {
		HashMultimap<String, String> groups = HashMultimap.create();
		Set<String> hashSet = new HashSet<String>();
		String graphjson = FileUtils.readFileToString(new File("migrate/groups.json"));
		JSONArray jo = JSONArray.fromObject(graphjson);
		JSONObject ja = jo.getJSONObject(0).getJSONObject("graph");
		JSONArray objs = ja.getJSONArray("data");
		for (int i = 0; i < objs.size(); i++) {
			JSONObject jobj = objs.getJSONObject(i);
			if (jobj.getString("type").equals("group")) {
				String id = jobj.getString("id");
				JSONArray items = jobj.getJSONArray("items");
				for (int j = 0; j < items.size(); j++) {
					String item = items.getString(j);
					System.out.println(item);
					groups.put(id, item);
					hashSet.add(item);
				}
			}
		}
		File[] files = new File("migrate/comp/").listFiles();
		Map<String, String> comps = new HashMap<String, String>();
		for (File f : files) {
			if (f.getName().endsWith(".json")) {
				String json = FileUtils.readFileToString(f);
				String name = getCompName(f.getName());
				System.out.println(name);
				if (hashSet.contains(name)) {
					comps.put(name, json);
				}
			}
		}

	}

	private static String getCompName(String name) {
		if (name.startsWith("comp-")) {
			return name.substring(5, name.length());
		} else {
			return name;
		}
	}
}
