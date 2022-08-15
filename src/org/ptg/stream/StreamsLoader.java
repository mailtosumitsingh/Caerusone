/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.stream;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.ptg.util.closures.IStateAwareClosure;


public class StreamsLoader implements IStateAwareClosure {
	Map<String, Stream> streams ;

	public StreamsLoader(Map<String, Stream> streams) {
		this.streams = streams;
	}

	public void execute(ResultSet rs) throws SQLException {
		String type = rs.getString("streamname");
		Stream s = streams.get(type);
		if (s == null) {
			String eventtype = rs.getString("eventtype");
			int seda = rs.getInt("seda");
			String processor = rs.getString("processor");
			String extra = rs.getString("streamextra");
			int id = rs.getInt("streamid");
			String exceptionStream = rs.getString("exceptionStream");
			s = new Stream();
			s.setName(type);
			s.setEventType(eventtype);
			s.setSeda(seda);
			s.setProcessor(processor);
			s.setId(id);
			s.setExtra(extra);
			s.setExceptionStream(exceptionStream);
			streams.put(type, s);
		}
		int sdIndex = s.getSdMaxIndex();
		StreamDefinition sd = new StreamDefinition();
		String temp = rs.getString("name");
		sd.setName(temp);
		temp = rs.getString("type");
		sd.setType(temp);
		temp = rs.getString("accessor");
		sd.setAccessor(temp);
		temp = rs.getString("extra");
		sd.setExtra(temp);
		temp = rs.getString("xmlexpr");
		sd.setXmlExpr(temp);
		temp = rs.getString("destprop");
		sd.setDestProp(temp);
		int tempi = rs.getInt("id");
		sd.setId(tempi);
		tempi = rs.getInt("streamtype");
		sd.setStreamType(tempi);
		int index = rs.getInt("index");
		if(index==-1)
			sd.setIndex(sdIndex);
		else{
			sd.setIndex(index);
			sdIndex = index;
		}
		s.setSdMaxIndex(++sdIndex);
		s.getDefs().put(sd.getName(), sd);
	}

	public void finish() {
	}

	public void init() {
	}

}
