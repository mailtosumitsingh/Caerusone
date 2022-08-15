package org.ptg.processors;

import java.util.List;

import org.apache.camel.Exchange;
import org.ptg.events.Event;
import org.ptg.util.AbstractIProcessor;
import org.ptg.util.index.LuceneEventIndex;

public class LuceneIndexerProcessor extends AbstractIProcessor{
	LuceneEventIndex index;
	@Override
	public void childAttach()  {
		index = new LuceneEventIndex();
		index.setDirName(this.query);
		try {
			index.buildEventIndexer(ed);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void childProcess(Exchange msg) throws Exception {
		Object o = msg.getIn().getBody();
		Event evt  =  null;
		if(o instanceof Event){
			evt  = (Event)o;
			index.addDocNative(evt);
		}else if(o instanceof Object[]){
			evt  = (Event) p.transformRow((Object[]) o);
		}else if(o instanceof java.util.List){
			evt  = (Event) p.transformRow((List) o);
			
		}
		index.addDocNative(evt);
	}
	
}

