package org.ptg.util.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.ptg.events.Event;
import org.ptg.events.EventDefinition;
import org.ptg.events.EventDefinitionManager;
import org.ptg.events.PropertyDefinition;
import org.ptg.util.CommonUtil;

public class LuceneEventIndex {
	protected String dirName;
	protected Directory index;
	protected StandardAnalyzer sa;
	protected IndexWriterConfig config;
	protected IndexWriter w;
	protected IndexReader ireader;
	protected IndexSearcher isearcher;
	protected Map<String, ILuceneEventDocWriter> eventIndexers = new LinkedHashMap<String, ILuceneEventDocWriter>();

	public void init() {
		try {
			index = new NIOFSDirectory(new File(dirName));
			sa = new StandardAnalyzer(Version.LUCENE_35);
			config = new IndexWriterConfig(Version.LUCENE_35, sa);
			w = new IndexWriter(index, config);
			buildEventIndexers(true);
		} catch (Exception exp) {
			exp.printStackTrace();
		}
	}

	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	public void prepareForSearch() {
		try {
			w.close();
			ireader = IndexReader.open(index);
			isearcher = new IndexSearcher(ireader);

		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Deprecated
	public void addDoc(Event evt, String name) {
		EventDefinition eventDef = EventDefinitionManager.getInstance().getEventDefinition(name);
		Document doc = new Document();
		for (PropertyDefinition d : eventDef.getProps().values()) {
			if (d.getSearchable() > 0) {
				// doc.add(new NumericField(d.getName(), Field.Store.YES,
				// true).set);
				doc.add(new Field(d.getName(), evt.getEventProperty(d.getName()).toString(), Field.Store.YES, Field.Index.ANALYZED));
			}
		}
		try {
			w.addDocument(doc);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<Document> query(String queryString, int maxResult) {
		List<Document> result = new ArrayList<Document>();
		QueryParser parser = new QueryParser(Version.LUCENE_35, "title", sa);
		try {
			Query query = parser.parse(queryString);
			TopDocs hits = isearcher.search(query, null, maxResult);
			for (int i = 0; i < hits.scoreDocs.length; i++) {
				Document hitDoc = isearcher.doc(hits.scoreDocs[i].doc);
				result.add(hitDoc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public void addDocNative(Event evt) throws Exception {
		ILuceneEventDocWriter indx = eventIndexers.get(evt.getEventType());
		if (indx != null) {
			indx.writeEventDoc(w, evt);
		} else {
			throw new Exception("Event type not registered with lucene indexer");
		}
	}

	public IndexWriter getIndexWriter() {
		return w;
	}

	public void setIndexWriter(IndexWriter w) {
		this.w = w;
	}

	public void buildEventIndexers(boolean contOnError) throws Exception {
		for (EventDefinition edef : EventDefinitionManager.getInstance().getEventDefinitions().values()) {
			try {
				ILuceneEventDocWriter writer = eventIndexers.get(edef.getType());
				if (writer == null) {
					writer = CommonUtil.getLuceneIndexFunc(edef);
					eventIndexers.put(edef.getType(), writer);
					System.out.println(writer.getClass().getSimpleName());
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (!contOnError) {
					throw new Exception("Failed to load Indexer for: " + edef.getType());
				}
			}
		}
	}

	public void buildEventIndexer(EventDefinition edef) throws Exception {
		ILuceneEventDocWriter writer = eventIndexers.get(edef.getType());
		if (writer == null) {
			writer = CommonUtil.getLuceneIndexFunc(edef);
			System.out.println(writer.getClass().getSimpleName());
			eventIndexers.put(edef.getType(), writer);
		}
	}

	public static void main(String[] args) {
		LuceneEventIndex i = new LuceneEventIndex();
		i.setDirName("luceneindex");
		i.init();
	}

	public ILuceneEventDocWriter getILuceneEventDocWriter(String s) {
		ILuceneEventDocWriter indx = eventIndexers.get(s);
		return indx;
	}
}
