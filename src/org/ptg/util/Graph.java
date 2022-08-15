/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.ptg.events.EventDefinition;
import org.ptg.processors.ConnDef;
import org.ptg.processors.ProcessorDef;
import org.ptg.stream.Stream;

import edu.uci.ics.jung.graph.DelegateForest;

public class Graph {
	
	String name  ;
	Map <String, ConnDef> forward = new HashMap<String,ConnDef>(); 
	Map<String,Stream> streams = new HashMap<String,Stream>();
	Map<String,ProcessorDef> procs = new HashMap<String,ProcessorDef>();
	Map<String,EventDefinition> eventDefs = new HashMap<String,EventDefinition>();
	Map<String,Group> groups = new HashMap<String,Group>();
	DelegateForest<Stream,ConnDef> graph = null;//new DirectedSparseGraph<Stream,ConnDef>();
	Map<String,JSONObject> orphans = new HashMap<String,JSONObject>();
	

	List<Stream> starts = new ArrayList<Stream>();
	List<Stream> ends = new ArrayList<Stream>();
	public Graph(){
	init();
	}
	public void init(){
		try {
			graph = null;//new DirectedSparseGraph<Stream,ConnDef>();
			forward = new HashMap<String,ConnDef>();
			 streams = new HashMap<String,Stream>();
			 procs = new HashMap<String,ProcessorDef>();
			 starts = new ArrayList<Stream>();
			 ends = new ArrayList<Stream>();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	public void fromGraphJson(String name,String json){
		final Map<String, Object> l = CommonUtil.getGraphObjectsFromJsonData(name,json);
		setName(name);
		fromObjectMap(l,null);
	}
	public void fromJson(String name,String graphjson,String[] filters){
		Set<String> filter = CommonUtil.getSet(filters);
		try{ 
			this.name  = name;
			init();
				graph = new DelegateForest<Stream,ConnDef>();
				List ret = CommonUtil.getStreamsFromProcess(graphjson);
				for (Object obj : ret) {
					if (obj instanceof ProcessorDef) {
						ProcessorDef def = (ProcessorDef) obj;
						procs.put(def.getName(), def);
					}
					if (obj instanceof ConnDef) {
						ConnDef def = (ConnDef) obj;
						forward.put(def.getId(), def);
					}
					if (obj instanceof Stream) {
						Stream def = (Stream) obj;
						streams.put(def.getName(), def);
					}
					if (obj instanceof EventDefinition) {
						EventDefinition ed = (EventDefinition) obj;
						eventDefs.put(ed.getType(), ed);
					}
					if(obj instanceof Group) {
						Group ed = (Group) obj;
						groups.put(ed.getId(), ed);
					}
				}
				//first add vertexes
				for(Stream s: streams.values()){
					if(filter.contains(s.getName())==false)
						graph.addVertex(s);	
				}
				//then add nodes
				for(ConnDef def: forward.values()){
					Stream from = streams.get(def.getFrom());
					Stream to = streams.get(def.getTo());
					if(from!=null && to!=null){
					graph.addEdge(def,new edu.uci.ics.jung.graph.util.Pair(from,to));
					}
				}
				prepareStarts();
				prepareEnds();
				processDynamicPaths();
			} catch (Throwable e) {
				e.printStackTrace();
			}
	}
	public void fromObjectMap(Map<String,Object>map,String[] filters){
		Set<String> filter = CommonUtil.getSet(filters);
		try{ 
			init();
				graph = new DelegateForest<Stream,ConnDef>();
				Collection ret = map.values();
				for (Object obj : ret) {
					if (obj instanceof ProcessorDef) {
						ProcessorDef def = (ProcessorDef) obj;
						procs.put(def.getName(), def);
					}
					if (obj instanceof ConnDef) {
						ConnDef def = (ConnDef) obj;
						forward.put(def.getId(), def);
						
					}
					if (obj instanceof Stream) {
						Stream def = (Stream) obj;
						streams.put(def.getName(), def);
					}
					if (obj instanceof EventDefinition) {
						EventDefinition ed = (EventDefinition) obj;
						eventDefs.put(ed.getType(), ed);
					}
					if(obj instanceof Group) {
						Group ed = (Group) obj;
						groups.put(ed.getId(), ed);
					}
					if(obj instanceof JSONObject) {
						JSONObject jo = (JSONObject)obj;
						String id = jo.getString("id");
						orphans.put(id,jo);
					}
				}
				//first add vertexes
				for(Stream s: streams.values()){
					if(filter.contains(s.getName())==false)
						graph.addVertex(s);	
				}
				//then add nodes
				for(ConnDef def: forward.values()){
					Stream from = streams.get(def.getFrom());
					Stream to = streams.get(def.getTo());
					if(from!=null && to!=null){
					graph.addEdge(def,new edu.uci.ics.jung.graph.util.Pair(from,to));
					}
				}
				processDynamicPaths();
				prepareStarts();
				prepareEnds();

		} catch (Throwable e) {
				e.printStackTrace();
			}
	}
	public void fromJson(String name,String graphjson){
		fromJson(name, graphjson,null);
	}
	
	public void prepareEnds(){
		for(Stream s :streams.values()){
			if(graph.getChildCount(s)==0){
				ends.add(s);
			}
		}
	}
	public void prepareStarts(){
		for(Stream s :streams.values()){
			if(graph.getPredecessorCount(s)==0){
				starts.add(s);
			}
		}
	}

	public Map<String, ConnDef> getForward() {
		return forward;
	}
	public void setForward(Map<String, ConnDef> forward) {
		this.forward = forward;
	}
	public Map<String, Stream> getStreams() {
		return streams;
	}
	public void setStreams(Map<String, Stream> streams) {
		this.streams = streams;
	}
	public Map<String, ProcessorDef> getProcs() {
		return procs;
	}
	public void setProcs(Map<String, ProcessorDef> procs) {
		this.procs = procs;
	}
	public DelegateForest<Stream, ConnDef> getGraph() {
		return graph;
	}
	public void setGraph(DelegateForest<Stream, ConnDef> graph) {
		this.graph = graph;
	}
	public List<Stream> getStarts() {
		return starts;
	}
	public void setStarts(List<Stream> starts) {
		this.starts = starts;
	}
	public Collection<Stream> getEnds() {
		return ends;
	}
	public void setEnds(List<Stream> ends) {
		this.ends = ends;
	}
	public Map<String, EventDefinition> getEventDefs() {
		return eventDefs;
	}
	public void setEventDefs(Map<String, EventDefinition> eventDefs) {
		this.eventDefs = eventDefs;
	}

	public Collection<Stream> getLongestPath(){
		Collection <Stream> longest = null; 
		List<List<Stream>> rets =  getAllPaths();
		for(Stream child:ends){
			List <Stream> ret = graph.getPath(child);
		rets.add(ret);
		}
		int max = Integer.MIN_VALUE;
		
		for(Collection <Stream> ret:rets){
			if(max<ret.size()){
				max = ret.size();
				longest = ret;
			}
		}
		return longest;
	}
	public Collection<Stream> getShortestPath(){
		Collection <Stream> shortest = null; 
		List<List<Stream>> rets =  getAllPaths();
		for(Stream child:ends){
			List <Stream> ret = graph.getPath(child);
		rets.add(ret);
		}
		int max = Integer.MAX_VALUE;
		
		for(Collection <Stream> ret:rets){
			if(max>ret.size()){
				max = ret.size();
				shortest = ret;
			}
		}
		return shortest;
	}
	/*
	 * later this should be done via strategy located 
	 * via loopup /configuration/ choosen by user
	 * */
	public List<List<Stream>> getAllSubPaths(){
		List<List<Stream>> rets = new ArrayList<List<Stream>>();
		Set<Stream> endnodes = new HashSet<Stream>();
		for(Stream child:graph.getVertices()){
			if(graph.getChildCount(child)>1||graph.getPredecessorCount(child)>1||graph.getPredecessorCount(child)==0){
				endnodes.add(child);
			}
		}
		for(Stream s: endnodes){
			List<List<Stream>> subs = getAllPaths(s,endnodes);
			for(List<Stream> subpath : subs){
				rets.add(subpath);
			}
		}
	
		return rets;
	}
	public List<List<Stream>> getAllPaths(){
		List<List<Stream>> rets = new ArrayList<List<Stream>>();
		Collection<Stream>rootNodes  = graph.getRoots();
		for(Stream s: rootNodes){
			List<List<Stream>> subs = getAllPaths(s,null);
			for(List<Stream> subpath : subs){
				rets.add(subpath);
			}
		}
		
		
		return rets;
	}
	public List<List<Stream>> getAllPathsv3(String stream,Collection<Stream> terminals){
		Stream s = streams.get(stream);
		return getAllPaths(s,terminals);
	}
	public List<List<Stream>> getAllPathsv2(String stream,Collection<String> terminals){
		Collection<Stream> t = new ArrayList<Stream>();
		Stream s = streams.get(stream);
		for(String st: terminals){
			Stream temp  = streams.get(st);
			t.add(temp);
		}
		return getAllPaths(s,t);
	}
	public List<List<Stream>> getAllPaths(Stream s,Collection<Stream> terminals){
		List<List<Stream>> rets = new ArrayList<List<Stream>>();
		List<Stream> path = new ArrayList<Stream>();
		Collection<Stream> ss = graph.getChildren(s);
		path.add(s);
		while(ss.size()>0){
			
		if(ss.size()>1){
			for(Stream sub : ss){
				if(terminals!=null&&terminals.contains(sub))
					break;
			List<List<Stream>> subpaths = getAllPaths( sub,terminals );
			for(List<Stream> subsub : subpaths){
				List<Stream> subsubpath = new ArrayList<Stream>();
				for(Stream pathstreams : path){
					subsubpath.add(pathstreams);
				}
				for(Stream subsubstream: subsub){
					subsubpath.add(subsubstream);
					if(terminals!=null&&terminals.contains(subsubstream))
						break;
				}
				rets.add(subsubpath);
			}
			}
			return rets;
		}else{
			Stream current = ss.iterator().next();
			path.add(current);
			/*if we want to include the next stream then do
			after adding otherwise before adding*/
			if(terminals!=null&&terminals.contains(current))
				break;
			ss = graph.getChildren(current);
		}
		}
		rets.add(path);
		return rets;
	}

	public boolean pathHasDeteor(Collection <Stream>  path){
			for(Stream s: path){
				if(graph.getChildCount(s)>1||graph.getPredecessorCount(s)>1){
					return true;
				}
			}
			return false;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, Group> getGroups() {
		return groups;
	}
	public void setGroups(Map<String, Group> groups) {
		this.groups = groups;
	}
	public List<Boolean> pathStatus(List <ConnDef>sp){
		List<Boolean> finding  = new ArrayList<Boolean>();
		for(ConnDef s:sp){
			boolean b = false;
			for(ConnDef def: graph.getEdges()){
				if(def.getFrom().equals(s.getFrom())&&
						def.getTo().equals(s.getTo())){
					b = true;
				}
			}
			finding.add(b);
		}
		
	return finding;	
	}
	public boolean fullPathExists(List <String>sp){
		List <ConnDef>cdefs = new ArrayList<ConnDef>();
		for(int i =0;i<sp.size()-1 ;i++){
			ConnDef def = new ConnDef();
			String s1= sp.get(i);
			String s2 = sp.get(i+1);
			def.setFrom(s1);
			def.setTo(s2);
			def.setNodes(new String[]{s1,s2});
			cdefs.add(def);
		}
		List<Boolean> finding  = pathStatus(cdefs);
	return CommonUtil.allTrue(finding);	
	}
	public boolean findConnPathExists(List <ConnDef>sp){
		List<Boolean> finding  = pathStatus(sp);
		return CommonUtil.allTrue(finding);	
		}
	public void processDynamicPaths(){
		for(Stream s:streams.values()){
			String ex=s.getExtra();
			if(ex!=null){
			if(ex.contains(":")){
			String []outs = ex.split(":");
			for(String out:outs){
				Stream str1 = streams.get(s.getName());
				Stream str2 = streams.get(out);
				if(str1!=null&&str2!=null){
					ConnDef def = createConnection(s.getName(),out);
					graph.addEdge(def, str1,str2);
				}
			}
			}else{
				Stream str1 = streams.get(s.getName());
				Stream str2 = streams.get(s.getExtra());
				if(str1!=null&&str2!=null){
					ConnDef def = createConnection(str1.getName(),str2.getName());
					def.setCtype("synthetic");
					graph.addEdge(def, str1,str2);
					forward.put(def.getId(),def);
				}
					
			}
			}
		}
	}
	public ConnDef createConnection(String s1,String s2){
		ConnDef def = new ConnDef();
		def.setId("Random_"+CommonUtil.getRandomString(6));
		def.setFrom(s1);
		def.setTo(s2);
		def.setNodes(new String[]{s1,s2});
		return def;
	}
	@Override
	public String toString() {
		return "Graph [name=" + name + "]";
	}
	public Map<String, JSONObject> getOrphans() {
		return orphans;
	}
	public void setOrphans(Map<String, JSONObject> orphans) {
		this.orphans = orphans;
	}
}
/**/