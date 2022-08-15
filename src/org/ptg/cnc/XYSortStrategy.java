package org.ptg.cnc;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import com.google.common.primitives.Ints;

public class XYSortStrategy implements SortStrategy{
	public List<? extends CNCElement> sort(List<? extends CNCElement>  elems){
		Multimap<Integer,CNCElement> pointMap =  TreeMultimap.create(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return Ints.compare(o1, o2);
			}
			
		},new Comparator<CNCElement>(){

			@Override
			public int compare(CNCElement o1, CNCElement o2) {
				int xcompare = Ints.compare(o1.getNormalizedx(), o2.getNormalizedx());
				if(xcompare==0){
					int ycompare = Ints.compare(o1.getNormalizedy(), o2.getNormalizedy());
						if(ycompare==0)
					return 1;
					else
						return ycompare;
				}else{
					return xcompare;
				}
			}
			
		});
		for(CNCElement e: elems){
			pointMap.put(e.getNormalizedx(),e);
		}
		List<CNCElement> ret = Lists.newLinkedList();
		int j = 0;
		for(Integer i : pointMap.keySet()){
			Collection<CNCElement> c = pointMap.get(i);
			if(j%2==0){
				ret.addAll(c);
			}else{
				Iterable<CNCElement> iter = Iterables.reverse(Lists.newArrayList(c));
				for(CNCElement e: iter)
				ret.add(e);
			}
			j++;
		}
		
		return ret;
	}

}
