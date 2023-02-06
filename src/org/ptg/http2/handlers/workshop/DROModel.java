package org.ptg.http2.handlers.workshop;

import org.ptg.tests.opencv.dro.simpledro.SimpleModel;

public class DROModel {
	String fileName;
	String fileNameTemplate;
	SimpleModel model;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileNameTemplate() {
		return fileNameTemplate;
	}

	public void setFileNameTemplate(String fileNameTemplate) {
		this.fileNameTemplate = fileNameTemplate;
	}

	public SimpleModel getModel() {
		return model;
	}

	public void setModel(SimpleModel model) {
		this.model = model;
	}

}
