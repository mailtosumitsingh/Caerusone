package org.ptg.cnc;

import java.util.List;

public interface SortStrategy {
	List<? extends CNCElement> sort(List<? extends CNCElement>  elems);

}
