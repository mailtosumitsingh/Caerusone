package org.ptg.http2.handlers.workshop;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.tests.opencv.dro.simpledro.DROValue;
import org.ptg.tests.opencv.dro.simpledro.DynaModelSupport;
import org.ptg.tests.opencv.dro.simpledro.SimpleModel;
import org.ptg.util.CommonUtil;
import org.ptg.util.HTTPClientUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ReadDROOverHTTP extends AbstractHandler implements DynaModelSupport {
	public SimpleModel dynaModel = null; 

	public SimpleModel getDynaModel() {
		return dynaModel;
	}

	public void setDynaModel(SimpleModel dynaModel) {
		this.dynaModel = dynaModel;
	}

	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String imageName = request.getParameter("i");
		String url = "http://localhost:8080/readDro";
		String json = "";
		DROModel droModel = new DROModel();
		droModel.setModel(dynaModel);
		droModel.setFileName(imageName);
		droModel.setFileNameTemplate("c:\\temp\\readdro-{0}.jpg");
		try {
			byte[] bytes = HTTPClientUtil.doPostWithBody(CommonUtil.toJson(droModel), url);
			ObjectMapper mapper = new ObjectMapper();
			DROValue droVal = mapper.readValue(bytes, DROValue.class);
			System.out.println(droVal);
			//do something with image
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		response.getWriter().print("some val");
		((Request) request).setHandled(true);
	}



	}
