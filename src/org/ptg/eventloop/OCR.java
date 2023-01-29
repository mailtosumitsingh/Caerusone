package org.ptg.eventloop;

import java.util.Iterator;
import java.util.List;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Image;
import org.sikuli.script.Match;
import org.sikuli.script.Region;

public class OCR {
	public String toText(Image img) {
		return img.text();
	}
	public String toText(int x, int y, int w, int h){
		Region r = new Region(x,y,w,h);
		return r.text();
	}
	public List<String> toTextLines(Image img) {
		return img.textLines();
	}

	public List<String> toTextWords(Image img) {
		return img.textWords();
	}

	public List<Match> findAllText(Image img, String txt) {
		return img.findAllText(txt);
	}

	public Iterator<Match> findAllImage(Region r, Image target) throws FindFailed {
		return r.findAll(target);
	}

	public Match findAll(Image img, String txt) throws FindFailed {
		return img.findText(txt);
	}

	public Match findAll(Region r, Image target) throws FindFailed {
		return r.find(target);
	}

}
