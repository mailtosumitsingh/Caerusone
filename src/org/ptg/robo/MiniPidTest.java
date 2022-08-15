package org.ptg.robo;

public class MiniPidTest {
public static void main(String[] args) {
	MiniPid miniPID; 
	
	miniPID = new MiniPid(0.1, 0.01, 0.2);
	double outputLimits = 5;
	miniPID.setOutputLimits(outputLimits);
	//miniPID.setMaxIOutput(2);
	//miniPID.setOutputRampRate(3);
	//miniPID.setOutputFilter(.3);
	miniPID.setSetpointRange(0);

	double target=100;
	
	double actual=0;
	double output=0;
	
	miniPID.setSetpoint(target);
	
	System.err.printf("Target\tActual\tOutput\tError\n");
	for (int i = 0; i < 50; i++){
		output = miniPID.getOutput(actual, target);
		actual = actual + output;
		if((target-actual)<(.01*target)&& (outputLimits>.01)) {
			outputLimits = .01;
			miniPID.setOutputLimits(outputLimits);
			System.out.println("Changing") ;
		}
		System.err.printf("%d : %3.2f\t%3.2f\t%3.2f\t%3.2f\n",i, target, actual, output, (target-actual));
		if((target-actual)<.001)break;
	}		
}
}

