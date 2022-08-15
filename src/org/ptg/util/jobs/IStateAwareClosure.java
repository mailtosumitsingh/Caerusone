/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/

package org.ptg.util.jobs;

import org.apache.commons.collections.Closure;

public interface IStateAwareClosure extends Closure {
    void init();
    void finish();
    
}
