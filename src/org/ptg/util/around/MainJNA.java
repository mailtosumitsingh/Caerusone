package org.ptg.util.around;
import com.sun.jna.Library;
import com.sun.jna.Native;


public class MainJNA {
	 public interface CLibrary extends Library {
	        CLibrary INSTANCE = (CLibrary)
	            Native.loadLibrary("avoidtestwrap"/*"libavoidtest"*/,
	                               CLibrary.class);
	        void inito();
	        void initp();
	        void finish();
	    }
	 public static void main(String[] args) {
		 CLibrary.INSTANCE.inito();
		 CLibrary.INSTANCE.finish();
	}
}
