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
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.BaseXPath;
import org.jaxen.Context;
import org.jaxen.JaxenException;
import org.jaxen.Navigator;
import org.jaxen.util.SingletonList;

public class JerichoXPath extends BaseXPath {

  private static final long serialVersionUID = -6969112785840871593L;

  private final Log log = LogFactory.getLog(getClass());
  
  public JerichoXPath(String xpathExpr, Navigator navigator) 
      throws JaxenException {
    super(xpathExpr, navigator);
  }
  
  public JerichoXPath(String xpathExpr) throws JaxenException {
    super(xpathExpr, DocumentNavigator.getInstance());
  }

  /**
   * Jericho specific method to get the context associated with a node.
   * @param node the current node being visited.
   * @return the Context associated with the node.
   */
  protected Context getContext(Object node) {
    if (node instanceof Context) {
      return (Context) node;
    }
    Context fullContext = new Context(getContextSupport());
    if (node instanceof Source) {
      Element rootNode = 
        (Element) getNavigator().getDocumentNode((Source) node);
      fullContext.setNodeSet(new SingletonList(rootNode));
    } else if (node instanceof List) {
      fullContext.setNodeSet((List) node);
    } else {
      List list = new SingletonList(node);
      fullContext.setNodeSet(list);
    }
    return fullContext;
  }
  /*
   * public String valueOf(Object context) throws JaxenException {
    return stringValueOf(context);
  }

  public boolean booleanValueOf(Object context) throws JaxenException {
    String result = stringValueOf(context);
    return Boolean.valueOf(result);
  }

  public Number numberValueOf(Object context) throws JaxenException {
    String result = stringValueOf(context);
    if (NumberUtils.isNumber(result)) {
      return NumberUtils.createNumber(result);
    } else {
      throw new JaxenException("Value of " + xpathExpr + " is not numeric");
    }
  }

  public String stringValueOf(Object context) throws JaxenException {
    Object result = evaluate(context);
    if (result instanceof String) {
      return (String) result;
    } else {
      throw new JaxenException("Cannot return string value of " + xpathExpr);
    }
  }

  public Object selectSingleNode(Object context) throws JaxenException {
    List nodes = selectNodes(context);
    if (nodes.size() > 0) {
      return nodes.get(0);
    } else {
      return null;
    }
  }

  public List selectNodes(Object context) throws JaxenException {
    Object result = evaluate(context);
    if (result instanceof List) {
      return ((List) result);
    } else {
      return Collections.emptyList();
    }
  }
   * */
}