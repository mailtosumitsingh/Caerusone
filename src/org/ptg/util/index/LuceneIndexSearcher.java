package org.ptg.util.index;

import java.util.List;

import org.apache.lucene.document.Document;
import org.ptg.events.Event;

public class LuceneIndexSearcher {
public static void main(String[] args) {
	LuceneEventIndex index = new LuceneEventIndex();
	index.setDirName("luceneindex");
	index.init();
	index.prepareForSearch();
	List<Document> docs=index.query("LogStmt:test 1", 10);
	ILuceneEventDocWriter w = index.getILuceneEventDocWriter("org.ptg.util.events.LogEvent"); 
	for(Document d: docs){
		try {
			Event e = w.docToEvent(d);
			System.out.println(e.getEventProperty("LogEventType"));
			System.out.println(e.getEventProperty("LogTime"));
			//System.out.println(new java.util.Date(Long.parseLong(d.get("LogTime"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
}
