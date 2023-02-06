package org.ptg.tests.opencv.dro.simpledro;

import java.util.List;

import org.opencv.core.Mat;

public interface IDROReader {
	List<Integer> read(String imagePath);
	List<Integer> read(Mat img) ;
	List<Integer> read(Mat img,String imageName);

}
