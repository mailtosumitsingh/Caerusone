/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.stream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ptg.admin.AppContext;
import org.ptg.util.StreamTransformerHelper;
import org.ptg.util.db.DBHelper;


public class StreamManager {
	Map<String, Stream>streams = new HashMap<String,Stream>();
	String sqlStr = "select s.name as streamname,s.id as streamid,s.eventtype,s.exceptionstream as exceptionstream,s.processor,s.seda,s.extra as streamextra, sd.* from stream s left outer join stream_definition sd on sd.streamtype=s.id";
	String insertStream = "insert into stream (name, eventtype,processor,seda,extra,exceptionstream) values (?,?,?,?,?,?)";
	String insertStreamDefinition = "insert into stream_definition (name, type,streamtype,accessor,extra,destprop,xmlexpr,index) values (?,?,?,?,?,?,?,?)";
	public Map<String, Stream> getStreams() {
		return streams;
	}

	public Stream getStream(String s){
		return streams.get(s);
	}
	public void addStream(Stream s){
		streams.put(s.getName(), s);
	}
	
	public void saveStream(Stream s){
		saveToDB(s);
		streams.put(s.getName(), s);
	}
	public Stream getEventStream(String event){
		for(Stream s: streams.values()){
			if(s.getEventType().equals(event))
				return s;
		}
		return null;
	}
	public List<Stream> getEventStreams(String event){
		List<Stream> ret = new ArrayList<Stream>();
		for(Stream s: streams.values()){
			if(s.getEventType().equals(event))
				ret.add( s);
		}
		return ret;
	}
	public void saveToDB(Stream s){
		DBHelper inst = DBHelper.getInstance();
		Connection conn = inst.createConnection();
		PreparedStatement stmt =null;
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			stmt = conn.prepareStatement(insertStream);
			stmt.setString(1,s.getName());
			stmt.setString(2,s.getEventType());
			stmt.setString(3,s.getProcessor());
			stmt.setInt(4,s.getSeda());
			stmt.setString(5,s.getExtra());
			stmt.setString(6,s.getExceptionStream());
			stmt.executeUpdate();
			s.setId(inst.getLastId(conn));
			for(StreamDefinition sd : s.getDefs().values()){
				saveToDB(conn,sd,s);
			}
			inst.commit(conn);
		} catch (SQLException e) {
				inst.rollback(conn);
			e.printStackTrace();
		}finally{
			inst.closeStmt(stmt);
			inst.closeConnection(conn);
		}
	}
	private void saveToDB(Connection conn,StreamDefinition sd,Stream s) throws SQLException{
		PreparedStatement stmt = null;
		stmt = conn.prepareStatement(insertStreamDefinition);
		stmt.setString(1, sd.getName());
		stmt.setString(2, sd.getType());
		stmt.setInt(3, sd.getStreamType()==0?s.getId():sd.getStreamType());
		stmt.setString(4, sd.getAccessor());
		stmt.setString(5, sd.getExtra());
		stmt.setString(6, sd.getDestProp());
		stmt.setString(7, sd.getXmlExpr());
		stmt.setInt(8, sd.getIndex());
		stmt.executeUpdate();
	}
	public void loadStreams(){
		DBHelper helper = DBHelper.getInstance();
		StreamsLoader loader = new StreamsLoader(streams);
		helper.forEach(sqlStr, loader);
		AppContext.getInstance().setStat("StreamCount",streams.size());
		System.out.println("Loaded "+ streams.size() +" Streams");
	}
	public void generateStreamTransformers(){
		for(Stream s: streams.values()){
			getStreamTransformer(s,false);
		}
	}
	public Class getStreamTransformer(String name){
		Stream s = streams.get(name);
		Class c = StreamTransformerHelper.getTransformerClass(s,true);
		return c;
	}
	public Class getStreamTransformer(Stream s){
		Class c = StreamTransformerHelper.getTransformerClass(s,true);
		return c;
	}
	public Class getStreamTransformer(String name,boolean update){
		Stream s = streams.get(name);
		Class c = StreamTransformerHelper.getTransformerClass(s,update);
		return c;
	}
	public Class getStreamTransformer(Stream s,boolean update){
		streams.put(s.getName(),s);
		Class c = StreamTransformerHelper.getTransformerClass(s,update);
		return c;
	}
	private  static class SingletonHolder {
		private static final StreamManager INSTANCE = new StreamManager();
		static{
			
		}
	}

	public  static StreamManager getInstance() {
		return SingletonHolder.INSTANCE;
	}	
	public void deleteStream(String s){
		Stream str = getInstance().getStream(s);
		deleteStream(str);
	}
	public void deleteStream(Stream s){
		if(s==null) return;
		DBHelper inst = DBHelper.getInstance();
		Connection conn = inst.createConnection();
		Statement stmt =null;
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			stmt = conn.createStatement();
			String sqlTemp = "delete from stream_definition where streamType in (select id from stream where name =\'"+s.getName()+"\')";
			System.out.println("Now executing : " +sqlTemp);
			stmt.executeUpdate(sqlTemp);
			sqlTemp  = "delete from stream where name=\'"+s.getName()+"\'";
			System.out.println("Now executing : " +sqlTemp);
			stmt.executeUpdate(sqlTemp);
			s.setId(inst.getLastId(conn));
			inst.commit(conn);
			streams.remove(s.getName());
		} catch (SQLException e) {
				inst.rollback(conn);
			e.printStackTrace();
		}finally{
			inst.closeStmt(stmt);
			inst.closeConnection(conn);
		}
	}
	
}
