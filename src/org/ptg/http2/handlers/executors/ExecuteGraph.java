package org.ptg.http2.handlers.executors;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.Closure;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.ptg.util.CommonUtil;
import org.ptg.util.IOpencvProcessor;
import org.ptg.util.IRunnable;

public class ExecuteGraph extends AbstractHandler {

	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		Class cls = CommonUtil.forName("Mapping_" + name);
		try {
			Object o = cls.newInstance();
			if (o instanceof IRunnable) {
				IRunnable r = (IRunnable) o;
				r.run();
			} else if (o instanceof org.apache.commons.collections.Closure) {
				org.apache.commons.collections.Closure c = (Closure) o;
				c.execute(null);
			} else if (o instanceof IOpencvProcessor) {
				final IOpencvProcessor p = (IOpencvProcessor) o;
				new Thread(new Runnable() {
					@Override
					public void run() {
						p.restart();
						p.process();
					}
				}).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		((Request) request).setHandled(true);
	}

}
