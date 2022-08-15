/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.processors.etl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.ptg.events.Event;
import org.ptg.stream.Stream;
import org.ptg.stream.StreamManager;
import org.ptg.util.GenericException;
import org.ptg.util.IProcessor;
import org.ptg.util.IStreamTransformer;
import org.ptg.util.ReflectionUtils;
import org.ptg.util.db.DBHelper;

public class DBInstance implements IProcessor{
	private String streamName;
	private 	Stream stream ;
	private String name;
	private String dbUrl;
	private IStreamTransformer dbtransformer ;
	private org.apache.commons.dbcp.BasicDataSource ds ;
	private DBHelper dbhelper;
	public String getStreamName() {
		return streamName;
	}
	public IStreamTransformer getTransformer() {
		return null;
	}

	public void process(Exchange msg) throws Exception {
		Event e = (Event)msg.getIn().getBody();
		String q = e.getEventStringProperty("query");
		ResultSet rs = dbhelper.executeQuery(q);
		List<Event> events = new ArrayList<Event>();
		while(rs.next()){
		Event evt = (Event) dbtransformer.transformResultSet(rs);
		events.add(evt);
		}
		msg.getIn().setHeader("query", q);
		msg.getIn().setBody(events);
	}
	public void attach(String streamName)throws GenericException{
		
		this.streamName = streamName;
		try{
		Class c = StreamManager.getInstance().getStreamTransformer(streamName);
		stream = StreamManager.getInstance().getStream(streamName);
		Class dbc = StreamManager.getInstance().getStreamTransformer(stream);
		dbtransformer  = (IStreamTransformer) ReflectionUtils.createInstance(dbc.getName());
		ds = new org.apache.commons.dbcp.BasicDataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl(dbUrl);
		dbhelper = new DBHelper(ds);
		}catch(Exception e){
			throw new GenericException("Cannot find stream transformer",e);
		}
	}
	public void setQuery(String s) {
		dbUrl = s;
		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
	 this.name = name;	
	}
	@Override
	public String getDoc() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void detach() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getConfigItems() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setConfigItems(String s) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getConfigOptions() {
		// TODO Auto-generated method stub
		return null;
	}

}
