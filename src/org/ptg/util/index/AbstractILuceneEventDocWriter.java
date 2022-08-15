package org.ptg.util.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.ptg.events.Event;

public abstract class AbstractILuceneEventDocWriter implements ILuceneEventDocWriter{
public abstract void  writeEventDoc(IndexWriter w,Event toWrite)throws Exception;
public abstract Event docToEvent(Document w)throws Exception;
}
