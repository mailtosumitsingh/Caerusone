/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.processors;

import java.io.File;
import java.io.IOException;

import org.apache.camel.Exchange;
import org.ptg.util.AbstractIProcessor;
import org.ptg.util.CommonUtil;
import org.ptg.util.GenericException;
import org.ptg.util.IProcessor;
import org.ptg.util.SpringHelper;
import org.ptg.util.titan.TitanCompiler;

public class JavaProcess extends AbstractIProcessor{
	String temp = (String) SpringHelper.get("tempdir");
	String base = (String) SpringHelper.get("basedir");
	IProcessor child = null;
	@Override
	public void attach(String streamName) throws GenericException {
		super.attach(streamName);
		String name = this.name;
		String code = query;
			try {
				final String path =base+File.separator+"uploaded"+File.separator+"extraprocess"+File.separator ;
				String dest = name+".java";
				//code = StringEscapeUtils.unescapeJavaScript(code);
				String origCode = code;
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + name+".titan"), origCode.getBytes());
			
				code = CommonUtil.extractTextFromHtmlTitan(code);

				code = TitanCompiler.compile(code);
				org.apache.commons.io.FileUtils.writeByteArrayToFile(new File(path + dest), code.getBytes());
				Class t = CommonUtil.getUploadedProcessClass(path, name,name);
				if(t!=null){
				child = (IProcessor) t.newInstance();
				child.setName(name);
				child.setQuery(query);
				child.setConfigItems(cfg);
				child.attach(streamName);				
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	

	@Override
	public void detach() {
		if(child!=null)
		child.detach();
	}
	@Override
	public void process(Exchange msg) throws Exception {
		if(child !=null)
		child.process(msg);
	}
	

}
