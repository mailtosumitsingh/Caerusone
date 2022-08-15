/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;

public class FindSimpleGraphPattern implements Predicate{
   List<String> search ;
   
	public FindSimpleGraphPattern(List<String> search) {
	this.search = search;
}
	public FindSimpleGraphPattern() {
		this.search = new ArrayList<String>();
	}
	@Override
	public boolean apply(Object arg0) {
		if(search.size()==0)
			return false;
		Graph g = (Graph) arg0;
		if(g.fullPathExists(search)){
			System.out.println("found path  "+search+"  in   "+g.getName());
		return true;
		}else{
			return false;
		}
		
	}

	public List<String> getSearch() {
		return search;
	}

	public void setSearch(List<String> search) {
		this.search = search;
	}
	
}
