/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.processors;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.camel.Exchange;
import org.apache.commons.lang.ArrayUtils;
import org.ptg.admin.AppContext;
import org.ptg.events.Event;
import org.ptg.events.ObjectWrapperEvent;
import org.ptg.events.TraceEvent;
import org.ptg.stream.Stream;
import org.ptg.util.CommonUtil;
import org.ptg.util.ContPublisherWriter;
import org.ptg.util.GenericException;
import org.ptg.util.IProcessor;
import org.ptg.util.IStreamTransformer;

public class ProxyProcessor implements IProcessor {
	IProcessor _inner;
	String stream;
	String name;
	String q;
	AtomicBoolean l = new AtomicBoolean(false);
	AtomicBoolean detached = new AtomicBoolean(false);

	static AtomicLong totals = new AtomicLong(0);
	String namecalls;
	String namecallsExp;

	public ProxyProcessor(IProcessor _inner) {
		this._inner = _inner;
	}

	public IProcessor getInnerProcessor() {
		return this._inner;
	}

	public void setInnerProcessor(IProcessor p) {
		this._inner = p;
	}

	@Override
	public void attach(String streamName) throws GenericException {
		detached.getAndSet(false);
		_inner.attach(streamName);
		this.stream = streamName;
		namecalls = "Processor_" + _inner.getName() + "_Calls";
		namecallsExp = "Processor_" + _inner.getName() + "_CallsExp";
	}

	@Override
	public String getStreamName() {
		return stream;
	}

	public void setStreamName(String stream) {
		this.stream = stream;
	}

	@Override
	public IStreamTransformer getTransformer() {
		return _inner.getTransformer();
	}

	@Override
	public void process(Exchange msg) throws Exception {
		try {
			if (_inner != null && l.get() == false) {
				String v = (String) AppContext.getInstance().getVar("processtrace_" + this.getInnerName());
				if (v != null && "true".equalsIgnoreCase(v)) {
					Event evt = new TraceEvent("Recieved Event", getInnerName(), "T");
					ContPublisherWriter.getInstance().loadEvent(evt);
				}
				AppContext.getInstance().incrStat("TotalProcessorCalls");
				AppContext.getInstance().incrStat(namecalls);
				preprocess(msg);
				_inner.process(msg);
			}
		} catch (Exception e) {
			AppContext.getInstance().incrStat("TotalExceptionCalls");
			AppContext.getInstance().incrStat(namecallsExp);
			e.printStackTrace();
			Stream s = CommonUtil.getStream(_inner.getStreamName());
			String expstream = s.getExceptionStream();
			msg.getIn().setHeader("LastException", new ObjectWrapperEvent(e));
			if (expstream != null || expstream.length() > 0) {
				CommonUtil.sendAndWait(expstream, msg);
			}
			CommonUtil.sendException(e.toString(), getInnerName());
		}
	}

	@Override
	public void setQuery(String s) {
		q = s;
		_inner.setQuery(s);
	}

	@Override
	public String getName() {
		return this.name;
	}

	public String getInnerName() {
		return this.name.substring(6);
	}

	@Override
	public void setName(String name) {
		this.name = "Proxy_" + name;
		_inner.setName(name);
	}

	public void lock() {
		l.getAndSet(true);
	}

	public void unlock() {
		l.getAndSet(false);
	}

	@Override
	public String getDoc() {
		return null;
	}

	@Override
	public void detach() {
		if (_inner != null && !detached.getAndSet(true)) {
			_inner.detach();
		}

	}

	private void preprocess(Exchange msg) {
		Object doc = msg.getIn().getBody();
		Object[] items = null;
		if (doc instanceof Object[]) {
			items = (Object[]) doc;
		} else if (doc instanceof Event[]) {
			items = (Event[]) doc;
		} else if (doc instanceof List) {
			items = ((List) doc).toArray();
		} else if (doc instanceof Collection) {
			items = ((Collection) doc).toArray();
		} else if (doc instanceof Map) {
			items = ((Map) doc).values().toArray();
		} else if (doc instanceof Object) {
			items = new Object[] { doc };
		} else if (doc instanceof double[]) {
			items = ArrayUtils.toObject((double[]) doc);
		} else if (doc instanceof int[]) {
			items = ArrayUtils.toObject((int[]) doc);
		} else if (doc instanceof long[]) {
			items = ArrayUtils.toObject((long[]) doc);
		} else if (doc instanceof float[]) {
			items = ArrayUtils.toObject((float[]) doc);
		} else if (doc instanceof short[]) {
			items = ArrayUtils.toObject((short[]) doc);
		} else if (doc instanceof char[]) {
			items = ArrayUtils.toObject((char[]) doc);
		}

		if (items != null) {
			for (Object obj : items) {
				if (obj instanceof Event) {
					Event temp = (Event) obj;
					temp.addPath(this.getInnerName());
					temp.setWhere(this.stream + ">" + this.getInnerName());
				}
			}
		}

	}

	@Override
	public String getConfigItems() {
		return _inner.getConfigItems();
	}

	@Override
	public void setConfigItems(String s) {
		_inner.setConfigItems(s);

	}

	@Override
	public String getConfigOptions() {
		return _inner.getConfigOptions();
	}

	@Override
	public String toString() {
		return _inner.getName();
	}

}
