/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
Taken from :
http://sujitpal.blogspot.com/2009/04/xpath-over-html-using-jericho-and-jaxen.html
*/

package org.ptg.util.htmlxpath;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.jaxen.XPath;

public class JerichoNavigatorTest {

  private String testUrl = "http://sujitpal.blogspot.com/2009/04/xpath-over-html-using-jericho-and-jaxen.html";
  private String[] xpaths = new String[] {
    "//ol[@id='rso']/li/h3/a",
    //"/html/head/title/text()",
  };
public static void main(String[] args) throws Exception {
	JerichoNavigatorTest t = new JerichoNavigatorTest();
	t.testParsingVisitor("/vicepa/data/in/test.html");
}
  public void testParsingVisitor(String in) throws Exception {
    FileInputStream s  = new FileInputStream(new File(in));
    DocumentNavigator navigator  =(DocumentNavigator)DocumentNavigator.getInstance(); 
    Source doc = (Source)new Source(s);
    
    doc.fullSequentialParse();
    for (int i = 0; i < xpaths.length; i++) {
      String xpath = xpaths[i];
      System.out.println("*** Evaluating: " + xpath);
      XPath expr = new JerichoXPath(xpath, navigator);
      Object result = expr.evaluate(doc);
      if (result instanceof Element) {
        System.out.println("Element: " + ((Element) result).getName());
      } else if (result instanceof List) {
        System.out.println("List: size=" + ((List) result).size());
        List elements = (List) result;
        for (int j = 0; j < elements.size(); j++) {
          Element element = (Element) elements.get(j);
          System.out.println("Element: " + ((Element) element).getName());
          //System.out.println("Content: "+((Element) element).getTextExtractor().setIncludeAttributes(true).toString());
          System.out.println("Link: "+((Element) element).getAttributeValue("href"));
        }
      } else if (result instanceof String) {
        System.out.println("String: " + ((String) result));
      } else if (result instanceof Number) {
        System.out.println("Number: " + ((Number) result));
      } else if (result instanceof Boolean) {
        System.out.println("Boolean: " + ((Boolean) result));
      } else {
        System.out.println("Unknown: " + result == null ? 
          "NULL" : result.getClass().getName());
      }
    }
  }
private String getContent(Element ele){
	  String temp  = ele.getTextExtractor().toString();
        List elements = ele.getChildElements();	   
        for (int j = 0; j < elements.size(); j++) {
      	  String t = getContent((Element)elements.get(j));
      	  temp +=t;
        }
        return temp;
        }
}