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


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.CharacterReference;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTagType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.DefaultNavigator;
import org.jaxen.JaxenConstants;
import org.jaxen.NamedAccessNavigator;
import org.jaxen.Navigator;
import org.jaxen.UnsupportedAxisException;
import org.jaxen.XPath;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.util.SingleObjectIterator;

public class DocumentNavigator extends DefaultNavigator 
    implements NamedAccessNavigator {

  private static final long serialVersionUID = 8640276699026512314L;

  private final Log log = LogFactory.getLog(getClass());
  
  // DocumentNavigator needs to expose a singleton. Strategy adapted from: 
  // http://www.ibm.com/developerworks/java/library/j-dcl.html

  private DocumentNavigator() {
    super();
  }
  private static DocumentNavigator INSTANCE = new DocumentNavigator(); 
  
  /**
   * Return a singleton instance of the DocumentNavigator object.
   * @return a Navigator.
   */
  public static Navigator getInstance() {
    return INSTANCE;
  }
  
  // various abstract isXXX method implementations. These are specified
  // in the Navigator interface but not defined in the abstract class,
  // so they must be implemented in the implementation specific subclass.

  /**
   * Returns true if the node is an Attribute.
   * @param obj the node to consider.
   * @return true if node is an attribute, else false.
   */
  public boolean isAttribute(Object obj) {
    return obj instanceof Attribute;
  }

  /**
   * Returns true if the node is a Comment.
   * @param obj the node to consider.
   * @return true if the node is a comment, else false.
   */
  public boolean isComment(Object obj) {
    if (obj instanceof Element) {
      Element element = (Element) obj;
      return isElementOfType(element, StartTagType.COMMENT);
    }
    return false;
  }

  /**
   * Returns true if the node is a Document.
   * @param obj the node to consider.
   * @return true if the node is a Document, else false.
   */
  public boolean isDocument(Object obj) {
    return (obj instanceof Source);
  }

  /**
   * Returns true if the node is an Element.
   * @param obj the node to consider.
   * @return true if the node is an Element, else false.
   */
  public boolean isElement(Object obj) {
    if (obj instanceof Element) {
      Element element = (Element) obj;
      return isElementOfType(element, StartTagType.NORMAL);
    }
    return false;
  }

  /**
   * Returns true if the node is a Namespace. Since Jericho HTML
   * does not work with Namespaces, this method always returns false.
   * @parm obj the node to consider.
   * @return always false.
   */
  public boolean isNamespace(Object obj) {
    return false;
  }

  /**
   * Returns true if the node is a Processing Instruction.
   * @param the node to consider.
   * @return true if the node is a PI, else false.
   */
  public boolean isProcessingInstruction(Object obj) {
    if (obj instanceof Element) {
      Element element = (Element) obj;
      return isElementOfType(element,
        StartTagType.XML_PROCESSING_INSTRUCTION) || 
        isElementOfType(element, StartTagType.XML_DECLARATION);
    }
    return false;
  }

  /**
   * Returns true if the node is Text.
   * @param obj the node to consider.
   * @return true if the node is text, else false.
   */
  public boolean isText(Object obj) {
    if (obj instanceof CharacterReference || obj instanceof String) {
      return true;
    }
    return false;
  }

  // various abstract getXXX method implementations. These are specified
  // in the Navigator interface but not defined in the abstract class,
  // so they must be implemented in the implementation specific subclass.
  
  /**
   * Return the name of the attribute.
   * @param obj the Attribute object.
   * @return the attribute name.
   */
  public String getAttributeName(Object obj) {
    if (obj instanceof Attribute) {
      Attribute attr = (Attribute) obj;
      return attr.getName();
    } else {
      return "";
    }
  }

  /**
   * Return the name of the attribute's namespace URI. Since there
   * are no Namespaces in Jericho, this returns an empty string.
   * @param obj the Attribute object.
   * @return an empty string.
   */
  public String getAttributeNamespaceUri(Object obj) {
    return "";
  }

  /**
   * Return the attribute's QName. Since there are no Namespaces in
   * Jericho, this is the same as returning the Attribute name.
   * @param obj the Attribute object.
   * @return the attribute name.
   */
  public String getAttributeQName(Object obj) {
    return getAttributeName(obj);
  }

  /**
   * Return the value of the attribute.
   * @param obj the attribute.
   * @return the attribute value as a string.
   */
  public String getAttributeStringValue(Object obj) {
    if (obj instanceof Attribute) {
      Attribute attr = (Attribute) obj;
      return attr.getValue();
    } else {
      return "";
    }
  }

  /**
   * Returns the comment as a string.
   * @param obj the comment element.
   * @return the comment's string value.
   */
  public String getCommentStringValue(Object obj) {
    if (isComment(obj)) {
      Element element = (Element) obj;
      return element.getContent().getTextExtractor().toString();
    } else {
      return "";
    }
  }

  /**
   * Returns the name of the Element.
   * @param obj the Element.
   * @return the name of the element.
   */
  public String getElementName(Object obj) {
    if (obj instanceof Element) {
      Element element = (Element) obj;
      return element.getName();
    } else {
      return "";
    }
  }

  /**
   * Returns the namespace URI for the Element. Since Namespaces are
   * not supported in Jericho, this returns an empty string.
   * @param obj the Element.
   * @return an empty string.
   */
  public String getElementNamespaceUri(Object obj) {
    return "";
  }

  /**
   * Returns the Element's QName. Since Namespaces are not supported
   * in Jericho, this is the same as getElementName().
   * @param obj the Element.
   * @return the Element name.
   */
  public String getElementQName(Object obj) {
    return getElementName(obj);
  }

  /**
   * Returns the text content of the Element.
   * @param obj the Element.
   * @return the text content or String value of the Element.
   */
  public String getElementStringValue(Object obj) {
    if (obj instanceof Element) {
      Element element = (Element) obj;
      return element.getContent().getTextExtractor().toString();
    } else if (obj instanceof String) {
      return ((String) obj);
    } else {
      return String.valueOf(obj);
    }
  }

  /**
   * Get the Namespace prefix for the Document. Always returns an
   * empty string since Jericho does not support Namespaces.
   * @param obj the Document.
   * @return an empty string.
   */
  public String getNamespacePrefix(Object obj) {
    return "";
  }

  /**
   * Returns the namespace string value for the Element. Same as
   * returning the element's string value.
   * @param obj the Element.
   * @return the name of the element.
   */
  public String getNamespaceStringValue(Object obj) {
    return getElementStringValue(obj);
  }

  /**
   * Return the text string value for the Element. Same as returning
   * the element's string value.
   * @param obj the Element.
   * @return the text content of the Element.
   */
  public String getTextStringValue(Object obj) {
    return getElementStringValue(obj);
  }

  // various overrides of incorrect or inefficient default behavior in 
  // parent class. Default behavior is usually returning null or throwing 
  // an UnsupportedOperationException, so we override to provide correct
  // behavior. 

  /**
   * Returns a Document object given a URL. We return a Source object,
   * and automatically do a fullSequentialParse() for performance.
   * @param url the URL for the document.
   * @return the Source object.
   */
  public Object getDocument(String url) {
    try {
      URLConnection conn = new URL(url).openConnection();
      Source source = new Source(conn.getInputStream());
      source.fullSequentialParse();
      return source;
    } catch (MalformedURLException e) {
      log.error("Malformed URL: " + url, e);
      return null;
    } catch (IOException e) {
      log.error("IO Exception for URL: " + url, e);
      return null;
    }
  }

  /**
   * Returns the root element for the Document context node.
   * @param contextNode the Source object.
   * @return the root Element of the document (html).
   */
  public Object getDocumentNode(Object contextNode) {
    if (contextNode instanceof Source) {
      Source source = (Source) contextNode;
      return ((Segment) source).getAllElements("html").get(0);
    } else {
      return contextNode;
    }
  }

  /**
   * Returns the parent node for the contextNode. Default behavior is
   * inefficient. Since a Jericho Element has a pointer to its parent
   * node, we can use that here.
   * @param contextNode the context node.
   * @return the parent node.
   */
  public Object getParentNode(Object contextNode) {
    if (isElement(contextNode)) {
      return ((Element) contextNode).getParentElement();
    } else {
      return null;
    }
  }

  /**
   * Returns an element by id. Default behavior always returns null,
   * but Jericho provides methods to return this object, so we override
   * it.
   * @param contextNode the context node.
   * @param elementId the name of the element being searched for.
   * @return an Element object.
   */
  public Object getElementById(Object contextNode, String elementId) {
    if (isElement(contextNode)) {
      Iterator eit = ((Element) contextNode).getAllElements().iterator();
      List elementsById = new ArrayList();
      while (eit.hasNext()) {
        Element element = (Element) eit.next();
        if (element.getAttributeValue("id") == null ||
            (! element.getAttributeValue("id").equals(elementId))) {
          continue;
        }
        elementsById.add(element);
      }
      return elementsById;
    } else {
      return Collections.emptyList();
    }
  }
  
  // iteration methods

  /**
   * Returns an iterator for the child Elements of the contextNode
   * Element.
   * @param contextNode the context node Element.
   * @return an Iterator over the child objects of this Element.
   */
  public Iterator getChildAxisIterator(Object contextNode) {
    if (isElement(contextNode)) {
      Element element = (Element) contextNode;
      List children = new ArrayList();
      children.addAll(element.getChildElements());
      children.add(element.getTextExtractor().toString());
      return children.iterator();
    } else {
      return JaxenConstants.EMPTY_ITERATOR;
    }
  }

  /**
   * Returns an iterator over the named child elements of this context
   * node. Comes from NamedAccessNavigator.
   * @param contextNode the context node Element.
   * @param localName the name of the element.
   * @param namespacePrefix not used.
   * @param namespaceURI not used.
   * @return an iterator over the named child elements.
   */
  public Iterator getChildAxisIterator(Object contextNode, String localName,
      String namespacePrefix, String namespaceURI)
      throws UnsupportedAxisException {
    if (contextNode instanceof Element) {
      List children = ((Element) contextNode).getAllElements(localName);
      return children.iterator();
    } else {
      return JaxenConstants.EMPTY_ITERATOR;
    }
  }

  /**
   * Jericho does not support Namespaces, so returns an empty iterator.
   * @param contextNode the context node Element.
   * @return an empty iterator.
   */
  public Iterator getNamespaceAxisIterator(Object contextNode) {
    return JaxenConstants.EMPTY_ITERATOR;
  }

  /**
   * Returns an iterator over the parent elements of this Context node.
   * @param contextNode the context node Element.
   * @return an iterator over the parent elements of this Element.
   */
  public Iterator getParentAxisIterator(Object contextNode) {
    if (isDocument(contextNode)) {
      return JaxenConstants.EMPTY_ITERATOR;
    }
    Element parent = null;
    if (isElement(contextNode)) {
      Element element = (Element) contextNode;
      parent = element.getParentElement();
    }
    if (parent == null) {
      return JaxenConstants.EMPTY_ITERATOR;
    } else {
      return new SingleObjectIterator(parent);
    }
  }
  
  /**
   * Returns an iterator over the attribute axis of this context node.
   * @param contextNode the context node Element.
   * @return an iterator over the Element's attributes.
   */
  public Iterator getAttributeAxisIterator(Object contextNode) {
    if (isElement(contextNode)) {
      Element element = (Element) contextNode;
      Attributes attrs = element.getAttributes();
      Iterator ait = attrs.iterator();
      List attrlist = new ArrayList();
      while (ait.hasNext()) {
        attrlist.add((Attribute) ait.next());
      }
      return attrlist.iterator();
    }
    return JaxenConstants.EMPTY_ITERATOR;
  }

  /**
   * Returns an iterator over the named attributes for this context node.
   * Comes from NamedAccessNavigator.
   * @param contextNode the context node Element.
   * @param localName the name of the attribute.
   * @param namespacePrefix not used.
   * @param namespaceURI not used.
   * @return an iterator over the named attributes of this Element.
   */
  public Iterator getAttributeAxisIterator(Object contextNode,
      String localName, String namespacePrefix, String namespaceURI)
      throws UnsupportedAxisException {
    List namedAttrs = new ArrayList();
    if (contextNode instanceof Element) {
      Attributes attrs = ((Element) contextNode).getAttributes();
      Iterator ait = attrs.iterator();
      while (ait.hasNext()) {
        Attribute attr = (Attribute) ait.next();
        if (localName.equals(attr.getName())) {
          namedAttrs.add(attr);
        }
      }
      return namedAttrs.iterator();
    } else {
      return JaxenConstants.EMPTY_ITERATOR;
    }
  }

  /**
   * Makes sure that we return the correct XPath implementation when
   * called.
   * @param xpath the XPath expression.
   * @return the JerichoXPath object wrapping the expression.
   */
  public XPath parseXPath(String xpath) throws SAXPathException {
    return new JerichoXPath(xpath);
  }

  /**
   * Convenience method to return a correct element type.
   * @param element the Element object.
   * @param tagType the correct tag type. 
   * @return true or false.
   */
  private boolean isElementOfType(Element element, StartTagType tagType) {
    if (element == null) {
      return false;
    }
    return element.getStartTag().getTagType().equals(tagType);
  }
}