package org.ptg.cnc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Maps;

public class CNCElement {
	private int normalizedx;
	private int normalizedy;
	private int depth;
	private String text;
	private String id;
	private String type;
	private int r;
	private int b;
	private int x;
	private int y;
	private int rad;
	private Map<String,String>data=Maps.newHashMap();
	private List<String> layer  = new ArrayList<String>();
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public int getNormalizedx() {
		return normalizedx;
	}
	public void setNormalizedx(int normalizedx) {
		this.normalizedx = normalizedx;
	}
	public int getNormalizedy() {
		return normalizedy;
	}
	public void setNormalizedy(int normalizedy) {
		this.normalizedy = normalizedy;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getR() {
		return r;
	}
	public void setR(int r) {
		this.r = r;
	}
	public int getB() {
		return b;
	}
	public void setB(int b) {
		this.b = b;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	@Override
	public String toString() {
		return "CNCElement [normalizedx=" + normalizedx + ", normalizedy=" + normalizedy + ", text=" + text + ", id=" + id + "]";
	}
	public List<String> getLayer() {
		return layer;
	}
	public void setLayer(List<String> layer) {
		this.layer = layer;
	}
	public int getRad() {
		return rad;
	}
	public void setRad(int rad) {
		this.rad = rad;
	}
	public Map<String, String> getData() {
		return data;
	}
	public void setData(Map<String, String> data) {
		this.data = data;
	}

}
