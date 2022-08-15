package org.ptg.util;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;

public class ShellUploadFile {
	public static void main(String[] args) throws Exception {
		String out = "";
		for (String s : args) {
			System.out.println(s);
			out = s;
		try {
			String outSend = URLEncoder.encode(out, "UTF-8");
			String q = ("item=" + outSend + "&requesttype=" + "train");
			System.out.println(q);
			HTTPClientUtil.openPage("http://localhost:8095/ProcessShellItem", q);
			//String ret = HTTPClientUtil.openPage("http://localhost:8095/ProcessShellItem", q);
			deployFile(out);
			//System.out.println((out + ret));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	}
	public static void deployFile(String path) throws HttpException, IOException{
		String name = StringUtils.substringAfterLast(path,"/");
		if(name==null||name.length()==0)
			name = StringUtils.substringAfterLast(path,"\\");
		HTTPClientUtil.deploy(name, "in", null, "http://localhost:8095/SaveDeploy", path);
	}
}
