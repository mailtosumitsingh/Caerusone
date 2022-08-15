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

public class CartesianJoin {
	public static void main(String[] args) {
		Collection<String> a = new ArrayList<String>();
		a.add("a");
		a.add("b");
		Collection<Integer> b = new ArrayList<Integer>();
		b.add(1);
		b.add(2);
		Collection<Double> c = new ArrayList<Double>();
		c.add(-10.0d);
		c.add(-20.0d);
		CartesianJoin join = new CartesianJoin();
		Collection<Collection<Object>>  cp=		join.cartesianProduct(a,b,c);
		for(Collection<Object> cart: cp){
			System.out.println(cart);
		}
	}
	public Collection<Collection<Object>> cartesianProduct(Collection<?>... sets) {
	    if (sets.length < 2)
	        throw new IllegalArgumentException(
	                "Can't have a product of fewer than two sets (got " +
	                sets.length + ")");

	    return _cartesianProduct(0, sets);
	}

	private Collection<Collection<Object>> _cartesianProduct(int index, Collection<?>... sets) {
		Collection<Collection<Object>> ret = new ArrayList<Collection<Object>>();
	    if (index == sets.length) {
	        ret.add(new ArrayList<Object>());
	    } else {
	        for (Object obj : sets[index]) {
	            for (Collection<Object> set : _cartesianProduct(index+1, sets)) {
	                set.add(obj);
	                ret.add(set);
	                System.out.println(set);
	                if(set.size()==sets.length){
	                //	System.out.println("Done:"+set);
	                //	test(set.toArray());
	                }
	            }
	        }
	    }
	    return ret;
	}
	public void test(Object ...val){
		for(Object o:val)
		System.out.print("\t"+o);
		System.out.println();
	}
}
