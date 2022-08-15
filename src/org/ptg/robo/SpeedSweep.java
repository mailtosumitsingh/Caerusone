package org.ptg.robo;

public class SpeedSweep {
	int start=1000;
	int max=200;
	public static void main(String[] args) {
		for(int i=0;i<100;)
		System.out.println(test(100,i));
	}

	public static int test(int steps,int current) {
		int dx = steps-current;
		int pdx = (dx/steps)*100;
		return pdx;
		
	}
}
