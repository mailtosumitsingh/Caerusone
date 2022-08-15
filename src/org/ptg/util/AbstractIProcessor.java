/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.ptg.events.EventDefinition;
import org.ptg.events.EventDefinitionManager;
import org.ptg.events.PropertyDefinition;
import org.ptg.http2.handlers.compilers.graph.CompileObjectMapping;
import org.ptg.stream.Stream;
import org.ptg.stream.StreamDefinition;
import org.ptg.stream.StreamManager;
import org.ptg.util.mapper.v2.FPGraph2;

public class AbstractIProcessor implements IProcessor,Runnable {
	protected Map c;/*configuration as a map*/
	protected String name;
	protected String streamName;
	protected String query;
	protected String cfg;
	protected Stream stream;
	protected String extra;
	protected IStreamTransformer p;
	protected IEventDBTransformer dbtransformer;
	protected EventDefinition ed;
	protected String base = (String) SpringHelper.get("basedir");
	protected org.ptg.util.IObjectMapper mapper;
	protected boolean hasMapper = false;
	
	public void initMapper(){
		Map<String,String> params  = new HashMap<String,String>();
		FPGraph2 g = CommonUtil.buildDesignMappingGraph2(this.name+"Schema");
		if(g!=null){
			CompileObjectMapping m = new CompileObjectMapping();
			try {
				mapper = m.objectMapperFromGraph(this.name+"Schema",g);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public String getName() {
		return name;
	}

	public String getStreamName() {
		return streamName;
	}

	public IStreamTransformer getTransformer() {
		return p;
	}

	public void setConfigItems(String s) {
		if (s == null || s.length() <= 0)
			return;
		c = CommonUtil.getConfigFromJsonData(s);
		cfg = s;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setQuery(String s) {
		query = s;
	}

	public String getConfigItems() {
		return cfg;
	}

	public String getDoc() {
		return null;
	}

	public void attach(String streamName) throws GenericException {
		try {
			if(hasMapper){
			initMapper();
			} 
			stream = StreamManager.getInstance().getStream(streamName);
			ed = EventDefinitionManager.getInstance().getEventDefinition(stream.getEventType());
			if(stream.getDefs().size()==0){
				for(PropertyDefinition p :	ed.getProps().values()){
					StreamDefinition def = CommonUtil.getStreamPropertyDefinition(p.getName(), p.getType(), p.getIndex());
					stream.getDefs().put(def.getName(), def);
				}
			}
			extra  = stream.getExtra();
			Class c = StreamManager.getInstance().getStreamTransformer(streamName);
			p = (IStreamTransformer) ReflectionUtils.createInstance(c.getName());
			Class dbc = EventDefinitionManager.getInstance().buildDBTransformerDefinition(stream.getEventType());
			dbtransformer = (IEventDBTransformer) ReflectionUtils.createInstance(dbc.getName());
		} catch (Exception e) {
			throw new GenericException("Cannot find stream transformer", e);
		}
		childAttach();
	}

	public void detach() {

	}

	public void process(Exchange msg) throws Exception {
			childProcess( msg);
	}

	public void childAttach() throws GenericException {
	}

	public String getConfigOptions() {
		return "[\"p1\",\"p2\",\"p3\"]";
	}
	public void childProcess(Exchange msg)  throws Exception{
		
	}
	public void run() {
		
	}
	public boolean isHasMapper() {
		return hasMapper;
	}
	public void setHasMapper(boolean hasMapper) {
		this.hasMapper = hasMapper;
	}

}
