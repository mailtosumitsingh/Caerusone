package org.ptg.processors;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.Exchange;
import org.apache.commons.lang.StringUtils;
import org.ptg.util.AbstractIProcessor;
import org.ptg.util.CommonUtil;
import org.ptg.util.GenericException;

public class SchemaMapperProcessor extends AbstractIProcessor{
	String []outs =null;
	String [] ins = null;
	Map<String,String> map = new LinkedHashMap<String,String>();
	@Override
	public void childAttach() throws GenericException {
		outs = StringUtils.split(this.extra,":");
		ins = StringUtils.split(this.query,":");
		for(int i=0;i<outs.length;i++){
			map.put(ins[i],outs[i]);
		}
	}

	@Override
	public void childProcess(Exchange msg) throws Exception {
		List<Map>results  = (List<Map>) msg.getIn().getBody();
		for(Map res: results){
			if(res.keySet().size()!=outs.length){
				throw new Exception("Key size and outputs donot match");
			}else{
				int i= 0;
				for(Object en : res.entrySet()){
					try {
						Map.Entry<String,Object> entry =(Entry) en;
						CommonUtil.sendAndWait(map.get(entry.getKey()), entry);
					} catch (Exception e) {
						System.out.println("SchemaMapper child failed at : "+outs[i]);
						e.printStackTrace();
					}
					i++;
				}
			}
		}
	}

}
