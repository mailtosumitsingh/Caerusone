import java.util.Arrays;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import static org.ptg.tests.opencv.OpencvUtils.*;
import org.ptg.tests.opencv.OpencvUtils;
import org.ptg.tests.opencv.dro.simpledro.*;

public class Mapping_TestDRONew_phase2  implements IDROReader {
	private boolean DEBUG = false, TRACE = false;


	public void debug(){
	DEBUG = true;
	TRACE = true;
	}
	
	public  List<Integer> evaluate(SimpleModel model, String imageName) {
		Mat img = Imgcodecs.imread(imageName, Imgcodecs.CV_LOAD_IMAGE_COLOR);
		return evaluateInternal(model,img,imageName);
		} 

	public List<Integer> evaluateInternal(SimpleModel model, Mat img ,String imageName) {
		List<Integer>  ret = new java.util.LinkedList<>();
		if(Math.abs(model.getDisplayAngle()-0)>.0001) {
			img = OpencvUtils.rotateImage(img, new Point(0, 0), model.getDisplayAngle());
		}
		Mat imgDebug = img.clone();
		if (DEBUG) {
			drawRect(imgDebug, new Point(model.getX(), model.getY()), new Point(model.getX() + model.getW(), model.getY() + model.getH()), new Scalar(255, 255, 255, 1), 1);
		}
		int[][] val = new int[model.getDigits().size()][7];
		for (int digitScroll = 0; digitScroll < model.getDigits().size(); digitScroll++) {
			System.out.println("Now evaluating digit: "+digitScroll);
			SimpleDigit d = model.getDigits().get(digitScroll);
			if (DEBUG) {
				Point digitStartLoc = new Point(d.getX(), d.getY());
				drawRect(imgDebug, digitStartLoc, new Point(d.getW(), d.getH()), new Scalar(255, 0, 128, .4), 1);
				Imgproc.putText(imgDebug, d.getLabel(), digitStartLoc, 1, 1, new Scalar(128, 0, 128));
			}
			Mat digitMat = OpencvUtils.getCutImage(img, d.getX(), d.getY(), d.getW(), d.getH() );
			for (int k = 0; k < d.getSegments().size(); k++) {
				SimpleSegment rr = d.getSegments().get(k);
				if (DEBUG) {		
					Point startLoc = new Point(d.getX() + rr.getX(), d.getY() + rr.getY());
					drawRect(imgDebug, startLoc, new Point(d.getX()	 + rr.getX() + rr.getW(), d.getY() + rr.getY() + rr.getH()), new Scalar(128, 128, 128, 1), 1);
					Imgproc.putText(imgDebug, ">" + rr.getLabel(), startLoc, 1, 1, new Scalar(128, 0, 128));
				}
				Mat img2 = OpencvUtils.getCutImage(img, (int) (d.getX() + rr.getX()), (int) (d.getY() + rr.getY()), (int) rr.getW(), (int) rr.getH());
				double count = img2.rows() * img2.cols();
				double countMatch = 0;
				for (int i = 0; i < img2.rows(); i++) {
					for (int l = 0; l < img2.cols(); l++) {
						double[] a = img2.get(i, l);
						if (colorMatch(a, model.getDisplayColor(), model.getColorMatchTolerance())) {
							countMatch++;
						}

					}
				}
				String status = "off";
				double matchPerc = countMatch;
				if (matchPerc > model.getModelSegmentClusterTolerance()) {
					status = "on";
					val[digitScroll][k] = 1;
				} else {
					status = "off";
					val[digitScroll][k] = 0;
				}
				if (TRACE) {
					System.out.println(digitScroll + "(" + k + ")" + " is  " + status + " " + model.getModelSegmentClusterTolerance() + " / " + matchPerc);
				}
			}
		}

		if(DEBUG && imageName!=null){
		Mat view = OpencvUtils.getCutImage(imgDebug, model.getX(), model.getY(), model.getW(), model.getH());
		saveImage(imgDebug, imageName + "_changed.jpg");
		}
		for (int i = 0; i < model.getDigits().size(); i++) {
			int numApprox = numApprox( val [ i ] ) ;
			ret.add ( numApprox ) ;
			System.out.println(Arrays.toString(val[i]) + " : " + numApprox);
		}
		return ret;
	}

	 public int num(int[] v) {
		int[] z0 = new int[] { 1, 1, 1, 1, 1, 1, 0 };
		int[] z1 = new int[] { 0, 1, 1, 0, 0, 0, 0 };
		int[] z2 = new int[] { 1, 1, 0, 1, 1, 0, 1 };
		int[] z3 = new int[] { 1, 1, 1, 1, 0, 0, 1 };
		int[] z4 = new int[] { 0, 1, 1, 0, 0, 1, 1 };
		int[] z5 = new int[] { 1, 0, 1, 1, 0, 1, 1 };
		int[] z6 = new int[] { 0, 0, 1, 1, 1, 1, 1 };
		int[] z7 = new int[] { 1, 1, 1, 0, 0, 0, 0 };
		int[] z8 = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		int[] z9 = new int[] { 1, 1, 1, 1, 0, 1, 1 };
		int[] sign = new int[] { 0, 0, 0, 0, 0, 0, 1 };
		int[][] m = new int[11][];
		m[0] = z0;
		m[1] = z1;
		m[2] = z2;
		m[3] = z3;
		m[4] = z4;
		m[5] = z5;
		m[6] = z6;
		m[7] = z7;
		m[8] = z8;
		m[9] = z9;
		m[10] = sign;
		int c = 0;
		for (int[] mm : m) {
			boolean match = true;
			for (int i = 0; i < 7; i++) {
				if (v[i] != mm[i])
					match = false;
			}
			if (match)
				return c > 9 ? -1 : c;
			c++;
		}
		return 10;
	}

	 public int numApprox(int[] v) {
		int[] z0 = new int[] { 1, 1, 1, 1, 1, 1, 0 };
		int[] z1 = new int[] { 0, 1, 1, 0, 0, 0, 0 };
		int[] z2 = new int[] { 1, 1, 0, 1, 1, 0, 1 };
		int[] z3 = new int[] { 1, 1, 1, 1, 0, 0, 1 };
		int[] z4 = new int[] { 0, 1, 1, 0, 0, 1, 1 };
		int[] z5 = new int[] { 1, 0, 1, 1, 0, 1, 1 };
		int[] z6 = new int[] { 0, 0, 1, 1, 1, 1, 1 };
		int[] z7 = new int[] { 1, 1, 1, 0, 0, 0, 0 };
		int[] z8 = new int[] { 1, 1, 1, 1, 1, 1, 1 };
		int[] z9 = new int[] { 1, 1, 1, 1, 0, 1, 1 };
		int[] sign = new int[] { 0, 0, 0, 0, 0, 0, 1 };
		int[][] m = new int[11][];
		m[0] = z0;
		m[1] = z1;
		m[2] = z2;
		m[3] = z3;
		m[4] = z4;
		m[5] = z5;
		m[6] = z6;
		m[7] = z7;
		m[8] = z8;
		m[9] = z9;
		m[10] = sign;
		int c = 0;
		int[] cc = new int[11];
		for (int[] mm : m) {
			int count = 0;
			for (int i = 0; i < 7; i++) {
				if (v[i] == mm[i])
					count++;
			}
			cc[c] = count;
			c++;
		}
		int max = 0;
		int idx = -1;
		for (int i = 0; i < 11; i++) {
			if (cc[i] > max) {
				max = cc[i];
				idx = i;
			} else if (cc[i] == max) {
				if (idx > -1 && flags(m[i]) > flags(m[idx])) {
					idx = i;
				}
			}
		}

		return idx;
	}

	private int flags(int[] is) {
		int count = 0;
		for (int i : is) {
			if (i == 1)
				count++;
		}
		return count;
	}

	private  SimpleModel getModel() {
		SimpleModel model = new SimpleModel();
		int xoff = 5, yoff = 153;
		model.setX(398 + xoff);
		model.setY(173 + yoff);
		model.setW(413);
		model.setH(51);
		model.setDisplayAngle(0);
		model.setDigitWidth(43);
		model.setDigitHeight(56);
		model.setDisplayColor(new Scalar(251, 255, 249, 1));
		model.setColorMatchTolerance(new Scalar(50, 50, 30, 1));
		model.setModelSegmentClusterTolerance(4);
		
		SimpleDigit digit0 = new SimpleDigit(723,401,41,59,0);
SimpleSegment sm00 = new SimpleSegment(23,9,5,5,0);
digit0.getSegments().set(0,sm00 );
SimpleSegment sm01 = new SimpleSegment(32,17,6,6,1);
digit0.getSegments().set(1,sm01 );
SimpleSegment sm02 = new SimpleSegment(29,35,5,5,2);
digit0.getSegments().set(2,sm02 );
SimpleSegment sm03 = new SimpleSegment(18,47,5,5,3);
digit0.getSegments().set(3,sm03 );
SimpleSegment sm04 = new SimpleSegment(9,35,5,5,4);
digit0.getSegments().set(4,sm04 );
SimpleSegment sm05 = new SimpleSegment(12,17,6,6,5);
digit0.getSegments().set(5,sm05 );
SimpleSegment sm06 = new SimpleSegment(20,27,5,5,6);
digit0.getSegments().set(6,sm06 );
model.getDigits().add(digit0);
SimpleDigit digit1 = new SimpleDigit(771,402,41,59,1);
SimpleSegment sm10 = new SimpleSegment(22,8,6,6,0);
digit1.getSegments().set(0,sm10 );
SimpleSegment sm11 = new SimpleSegment(32,17,6,6,1);
digit1.getSegments().set(1,sm11 );
SimpleSegment sm12 = new SimpleSegment(30,37,6,6,2);
digit1.getSegments().set(2,sm12 );
SimpleSegment sm13 = new SimpleSegment(17,46,6,6,3);
digit1.getSegments().set(3,sm13 );
SimpleSegment sm14 = new SimpleSegment(9,36,6,6,4);
digit1.getSegments().set(4,sm14 );
SimpleSegment sm15 = new SimpleSegment(12,17,6,6,5);
digit1.getSegments().set(5,sm15 );
SimpleSegment sm16 = new SimpleSegment(19,27,5,5,6);
digit1.getSegments().set(6,sm16 );
model.getDigits().add(digit1);
SimpleDigit digit2 = new SimpleDigit(822,401,41,59,2);
SimpleSegment sm20 = new SimpleSegment(23,6,5,5,0);
digit2.getSegments().set(0,sm20 );
SimpleSegment sm21 = new SimpleSegment(32,17,6,6,1);
digit2.getSegments().set(1,sm21 );
SimpleSegment sm22 = new SimpleSegment(30,37,6,6,2);
digit2.getSegments().set(2,sm22 );
SimpleSegment sm23 = new SimpleSegment(19,48,5,5,3);
digit2.getSegments().set(3,sm23 );
SimpleSegment sm24 = new SimpleSegment(9,36,6,6,4);
digit2.getSegments().set(4,sm24 );
SimpleSegment sm25 = new SimpleSegment(12,17,6,6,5);
digit2.getSegments().set(5,sm25 );
SimpleSegment sm26 = new SimpleSegment(21,26,5,5,6);
digit2.getSegments().set(6,sm26 );
model.getDigits().add(digit2);
SimpleDigit digit3 = new SimpleDigit(874,400,41,59,3);
SimpleSegment sm30 = new SimpleSegment(22,8,6,6,0);
digit3.getSegments().set(0,sm30 );
SimpleSegment sm31 = new SimpleSegment(32,17,6,6,1);
digit3.getSegments().set(1,sm31 );
SimpleSegment sm32 = new SimpleSegment(29,38,5,5,2);
digit3.getSegments().set(2,sm32 );
SimpleSegment sm33 = new SimpleSegment(17,45,5,5,3);
digit3.getSegments().set(3,sm33 );
SimpleSegment sm34 = new SimpleSegment(9,36,6,6,4);
digit3.getSegments().set(4,sm34 );
SimpleSegment sm35 = new SimpleSegment(10,17,5,5,5);
digit3.getSegments().set(5,sm35 );
SimpleSegment sm36 = new SimpleSegment(18,27,5,5,6);
digit3.getSegments().set(6,sm36 );
model.getDigits().add(digit3);
SimpleDigit digit4 = new SimpleDigit(924,401,41,59,4);
SimpleSegment sm40 = new SimpleSegment(22,5,5,5,0);
digit4.getSegments().set(0,sm40 );
SimpleSegment sm41 = new SimpleSegment(32,17,6,6,1);
digit4.getSegments().set(1,sm41 );
SimpleSegment sm42 = new SimpleSegment(30,37,6,6,2);
digit4.getSegments().set(2,sm42 );
SimpleSegment sm43 = new SimpleSegment(19,47,5,5,3);
digit4.getSegments().set(3,sm43 );
SimpleSegment sm44 = new SimpleSegment(9,36,6,6,4);
digit4.getSegments().set(4,sm44 );
SimpleSegment sm45 = new SimpleSegment(13,17,5,5,5);
digit4.getSegments().set(5,sm45 );
SimpleSegment sm46 = new SimpleSegment(21,27,5,5,6);
digit4.getSegments().set(6,sm46 );
model.getDigits().add(digit4);
SimpleDigit digit5 = new SimpleDigit(978,399,41,59,5);
SimpleSegment sm50 = new SimpleSegment(24,8,5,5,0);
digit5.getSegments().set(0,sm50 );
SimpleSegment sm51 = new SimpleSegment(32,18,5,5,1);
digit5.getSegments().set(1,sm51 );
SimpleSegment sm52 = new SimpleSegment(31,37,5,5,2);
digit5.getSegments().set(2,sm52 );
SimpleSegment sm53 = new SimpleSegment(21,48,5,5,3);
digit5.getSegments().set(3,sm53 );
SimpleSegment sm54 = new SimpleSegment(10,38,5,5,4);
digit5.getSegments().set(4,sm54 );
SimpleSegment sm55 = new SimpleSegment(12,19,5,5,5);
digit5.getSegments().set(5,sm55 );
SimpleSegment sm56 = new SimpleSegment(21,26,5,5,6);
digit5.getSegments().set(6,sm56 );
model.getDigits().add(digit5);
SimpleDigit digit6 = new SimpleDigit(1031,401,41,59,6);
SimpleSegment sm60 = new SimpleSegment(25,5,5,5,0);
digit6.getSegments().set(0,sm60 );
SimpleSegment sm61 = new SimpleSegment(32,17,6,6,1);
digit6.getSegments().set(1,sm61 );
SimpleSegment sm62 = new SimpleSegment(31,39,5,5,2);
digit6.getSegments().set(2,sm62 );
SimpleSegment sm63 = new SimpleSegment(19,45,5,5,3);
digit6.getSegments().set(3,sm63 );
SimpleSegment sm64 = new SimpleSegment(9,36,6,6,4);
digit6.getSegments().set(4,sm64 );
SimpleSegment sm65 = new SimpleSegment(13,17,5,5,5);
digit6.getSegments().set(5,sm65 );
SimpleSegment sm66 = new SimpleSegment(21,27,5,5,6);
digit6.getSegments().set(6,sm66 );
model.getDigits().add(digit6);







	




		return model;
	}

	private  void prepareDigit(SimpleDigit d1, int xd, int yd, SimpleSegment... sg) {
		d1.getSegments().clear();
		for (SimpleSegment g : sg) {
			SimpleSegment sgg = g.clone();
			sgg.setX(sgg.getX() + xd);
			sgg.setY(sgg.getY() + yd);
			d1.getSegments().add(sgg);
		}
	}

	private  SimpleSegment getSegment(SimpleDigit d1, int i, int j, int k, int l, String label) {
		int w = k - i;
		int h = l - j;
		int x = i - d1.getX();
		int y = j - d1.getY();
		SimpleSegment sg = new SimpleSegment(x, y, w, h, label);

		return sg;
	}

	private  boolean colorMatch(double[] a, Scalar testColor, Scalar colorMatchTolerance) {
		if ((Math.abs(a[0] - testColor.val[0]) < colorMatchTolerance.val[0]) && (Math.abs(a[1] - testColor.val[1]) < colorMatchTolerance.val[1]) && (Math.abs(a[2] - testColor.val[2]) < colorMatchTolerance.val[2]))
			return true;

		return false;
	}
	
	public List<Integer> read(String imagePath) {
		return evaluate(getModel(), imagePath);
	}
	public List<Integer> read(Mat img) {
		return evaluateInternal(getModel(), img,null);
	}
	public List<Integer> read(Mat img,String imageName) {
		return evaluateInternal(getModel(), img, imageName);
		
	}
}
