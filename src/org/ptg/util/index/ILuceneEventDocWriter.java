package org.ptg.util.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.ptg.events.Event;

public interface ILuceneEventDocWriter {
	void writeEventDoc(IndexWriter w,Event toWrite)throws Exception;
	Event docToEvent(Document w)throws Exception;
}
