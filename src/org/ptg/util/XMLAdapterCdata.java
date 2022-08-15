package org.ptg.util;
import javax.xml.bind.annotation.adapters.XmlAdapter;
public class XMLAdapterCdata extends XmlAdapter<String, String> {

	    @Override
	    public String marshal(String arg0) throws Exception {
	        return "<![CDATA[\n"  + arg0 + "\n]]>";
	    }
	    @Override
	    public String unmarshal(String arg0) throws Exception {
	        return arg0;
	    }

	}