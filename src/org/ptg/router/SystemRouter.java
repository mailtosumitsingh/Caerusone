/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.router;

import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang.StringUtils;
import org.ptg.processors.OneTimeProcess;
import org.ptg.processors.ProcessorManager;
import org.ptg.processors.ProxyProcessor;
import org.ptg.stream.Stream;
import org.ptg.stream.StreamManager;
import org.ptg.util.CommonUtil;
import org.ptg.util.Constants;
import org.ptg.util.IProcessor;

public class SystemRouter extends RouteBuilder {
	boolean wireTapping = false;

	@Override
	public void configure() throws Exception {

		Map<String, IProcessor> routes = ProcessorManager.getInstance().getProcessorRoutingTable();
		for (Map.Entry<String, IProcessor> en : routes.entrySet()) {
			Stream s = StreamManager.getInstance().getStream(en.getKey());
			String directfrm = null;
			String frmOut = null;
			if (s.getSeda() == 1) {/* seda begin */
				directfrm = "direct:" + s.getName();
				frmOut = "direct:" + s.getName() + "-out";
				String frm = "seda:" + s.getName() + "?concurrentConsumers=2";
				if (en.getValue() instanceof ProxyProcessor) {
					IProcessor tp = ((ProxyProcessor) en.getValue()).getInnerProcessor();
					if (OneTimeProcess.class.isAssignableFrom(tp.getClass())) {
						CommonUtil.saveLog(en.getValue().getStreamName() + " 's processor is one time dropping.");
						continue;
					}
				}
				CommonUtil.saveLog("Starting stream: <" + s.getName() + "> , Desc: <" + frm + ">");
				if (wireTapping && !s.getName().equals(Constants.WireTapStream)) {
					from(directfrm).setHeader(Constants.STREAMIN, constant(s.getName())).wireTap("direct:" + Constants.WireTapStream).to(frm).process(en.getValue()).to(frmOut)
							.setId(en.getValue().toString());
				} else {
					from(directfrm).to(frm).process(en.getValue()).to(frmOut).setId(en.getValue().toString());

				}

			}/* seda end */else {
				directfrm = "direct:" + s.getName();
				frmOut = "direct:" + s.getName() + "-out";
				if (en.getValue() instanceof ProxyProcessor) {
					IProcessor tp = ((ProxyProcessor) en.getValue()).getInnerProcessor();
					if (OneTimeProcess.class.isAssignableFrom(tp.getClass())) {
						CommonUtil.saveLog(en.getValue().getStreamName() + " 's processor is one time dropping.");
						continue;
					}
				}
				CommonUtil.saveLog("Starting stream: <" + s.getName() + "> , Desc: <" + directfrm + ">");
				if (wireTapping && !s.getName().equals(Constants.WireTapStream)) {
					from(directfrm).setHeader(Constants.STREAMIN, constant(s.getName())).wireTap("direct:" + Constants.WireTapStream).process(en.getValue()).to(frmOut).setId(en.getValue().toString());
				} else {
					from(directfrm).process(en.getValue()).to(frmOut).setId(en.getValue().toString());
				}

			}
			IProcessor tempProcess = en.getValue();
			if (tempProcess instanceof ProxyProcessor) {
				tempProcess = ((ProxyProcessor) tempProcess).getInnerProcessor();
			}

		}
	}

}
