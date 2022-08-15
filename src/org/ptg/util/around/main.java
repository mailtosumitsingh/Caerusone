package org.ptg.util.around;



public class main {
	public static void main(String []args) {
		System.loadLibrary("avoidtestwrap");
		around.initp();
		around.addRect(20.0, 35.0,40.0, 120.0);
		around.addRect(10.0, 10.0,55,35);
		around.addRect(60,10,115,35);
		SWIGTYPE_p_double  ptr = around.getPath(1, 15,40, 100);
		double count = around.double_array_getitem(ptr, 0);
		System.out.println("No of objects: "+count);
		for(int i=1;i<=count;i++){
			
			double itm0 = around.double_array_getitem(ptr, i*2-1);
			double itm1 = around.double_array_getitem(ptr, i*2);
			System.out.println("Now printing: "+i);
			System.out.println("x: "+itm0);
			System.out.println("y: "+itm1);
		}
		around.finish();
	}

}




