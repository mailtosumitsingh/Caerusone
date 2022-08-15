/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.http2.handlers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtils;
import org.ptg.util.SpringHelper;
import org.ptg.util.db.DBHelper;

public class GetDesign extends AbstractHandler {
	static String tempDir = (String) SpringHelper.get("systemtempdir");
	static String phantomjsbin = (String) SpringHelper.get("phantomjsbin");
	static String phantomjswdir = (String) SpringHelper.get("phantomjswdir");
	static String rasterjs = (String) (String) SpringHelper.get("rasterjs");
	static String visjsp=(String)SpringHelper.get("visjsp");
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String cmd = request.getParameter("cmd");
		
		response.setHeader("Access-Control-Allow-Origin", "*");
		try {
			String ip = request.getRemoteAddr();
			String graphid = request.getParameter("graphid");
			String downLoad = request.getParameter("d");
			String format = request.getParameter("f");
			String json = null;
			if (downLoad != null && downLoad.equals("true")) {
				if (format == null || format.length() < 1){
					format = "json";
				sendAsJson(response, graphid);
				}else if(format.equals("pdf")){
					String w = request.getParameter("w");
					String h = request.getParameter("h");
					sendAsPdf(response, graphid,w,h);
				}else if(format.equals("png")){
					String w = request.getParameter("w");
					String h = request.getParameter("h");
					sendAsPng(response, graphid,w,h);
				}
			} else {
				format = "design";
				sendAsDesign(response, graphid);
			}
			response.flushBuffer();
		} catch (Exception e) {
			response.getOutputStream().print("Cannot export design");
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

	private void sendAsPdf(HttpServletResponse response, String graphid, String w, String h) throws ExecuteException, FileNotFoundException, IOException {
		String format = "pdf";
		String imgSave = tempDir+graphid+"."+format;
		export(graphid, w, h,format);
		response.setHeader("content-disposition", "attachment; filename=" + graphid + "-Design."+format);
		byte[] data=IOUtils.toByteArray(new FileInputStream(imgSave));
		response.getOutputStream().write(data);
		response.getOutputStream().flush();
	}
	private void sendAsPng(HttpServletResponse response, String graphid, String w, String h) throws ExecuteException, FileNotFoundException, IOException {
		String format = "png";
		String imgSave = tempDir+graphid+"."+format;
		export(graphid, w, h,format);
		response.setHeader("content-disposition", "attachment; filename=" + graphid + "-Design."+format);
		byte[] data=IOUtils.toByteArray(new FileInputStream(imgSave));
		response.getOutputStream().write(data);
		response.getOutputStream().flush();
	}
	public static void export(String graphId, String w, String h,String format) throws ExecuteException, IOException, FileNotFoundException {
		String imgSave = tempDir+graphId+"."+format;
		String wdir = phantomjswdir;
		String cmd = phantomjsbin+"  "+rasterjs+" ";
		cmd += " "+visjsp+"?graphid=" + graphId  + " "+imgSave+" " + w+"px" + "," + h+"px";
		String[] o = CommonUtils.runCmd(cmd, wdir);
		
	}
	private void sendAsJson(HttpServletResponse response, String graphid) throws IOException {
		String json;
		response.setHeader("content-disposition", "attachment; filename=" + graphid + "-Design.json");
		json = DBHelper.getInstance().getString("select graph,config,name from pageconfig where name='" + graphid + "'");
		response.getOutputStream().print(json);
	}

	private void sendAsDesign(HttpServletResponse response, String graphid) throws IOException {
		String json;
		json = DBHelper.getInstance().getResultJson("select graph,config,name from pageconfig where name='" + graphid + "'");
		response.getOutputStream().print(json);
	}

}
