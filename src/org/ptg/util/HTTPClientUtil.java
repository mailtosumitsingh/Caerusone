/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
 */

package org.ptg.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.io.FileUtils;

public class HTTPClientUtil {
	public static String openPage(String urlStr, String page) {
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		String url = "";
		if (urlStr == null)
			return null;
		else
			url = urlStr;

		HttpMethod method = null;
		method = new GetMethod(url);
		// method.setFollowRedirects(true);
		String responseBody = null;
		try {
			if (page != null) {

				method.setQueryString(page);
			}
			client.executeMethod(method);
			responseBody = method.getResponseBodyAsString();

		} catch (HttpException he) {
			System.err.println("Http error connecting to '" + url + "'");
			System.err.println(he.getMessage());
		} catch (IOException ioe) {
			System.err.println("Unable to connect to '" + url + "'");
		}

		method.releaseConnection();
		return responseBody;
	}

	public static String getText(String url) {
		// System.out.println("URL: "+url);
		String ret = null;
		HttpClient client = new HttpClient();
		//client.getHttpConnectionManager().getParams().setConnectionTimeout(2000);
		// client.getHostConfiguration().setProxy(url, proxyPort);
		// Credentials creds = new UsernamePasswordCredentials()
		// client.getState().setProxyCredentials(AuthScope.ANY, creds);

		GetMethod method = new GetMethod(url);
		// method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new
		// DefaultHttpMethodRetryHandler(3, false));
		method.setFollowRedirects(true);
		try {
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}
			ret = method.getResponseBodyAsString();
			// System.out.println(ret);
		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}
		return ret;
	}

	public static byte[] getBinary(String url) {
		// System.out.println("URL: "+url);
		byte[] ret = null;
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		// client.getHostConfiguration().setProxy(url, proxyPort);
		// Credentials creds = new UsernamePasswordCredentials()
		// client.getState().setProxyCredentials(AuthScope.ANY, creds);

		GetMethod method = new GetMethod(url);
		// method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new
		// DefaultHttpMethodRetryHandler(3, false));
		method.setFollowRedirects(true);
		try {
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}
			ret = method.getResponseBody();
			// System.out.println(ret);
		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}
		return ret;
	}

	public static byte[] openBinaryPage(String urlStr, String page) {
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

		String url = "";
		if (urlStr == null)
			return null;
		else
			url = urlStr;

		HttpMethod method = null;
		method = new GetMethod(url);
		// method.setFollowRedirects(true);
		byte[] responseBody = null;
		try {
			if (page != null) {

				method.setQueryString(page);
			}
			client.executeMethod(method);
			responseBody = method.getResponseBody();

		} catch (HttpException he) {
			System.err.println("Http error connecting to '" + url + "'");
			System.err.println(he.getMessage());
		} catch (IOException ioe) {
			System.err.println("Unable to connect to '" + url + "'");
		}

		method.releaseConnection();
		return responseBody;
	}

	public static String getTextUnEncodedQuery(String url, String query) {
		String queryParse = URLEncoder.encode(query);
		String s = getText(url + queryParse);
		return s;
	}
	public static byte[] getBinaryUnEncodedQuery(String url, String query) {
		String queryParse = URLEncoder.encode(query);
		byte[] s = getBinary(url + queryParse);
		return s;
	}
public static void deploy(String name,String type,String dest,String url,String path) throws HttpException, IOException{
	  File f = new File(path);
	  PostMethod filePost = new PostMethod(url);
	  Part[] parts = {
	      new StringPart("deployName", name),
	      new StringPart("deployType", type),
	      new StringPart("dest", dest==null?"":dest),
	      new FilePart(f.getName(), f)
	  };
	  filePost.setRequestEntity(
	      new MultipartRequestEntity(parts, filePost.getParams())
	      );
	  HttpClient client = new HttpClient();
	  int status = client.executeMethod(filePost);
	  
}
public static byte[] doPost(Map<String,String>params,String url)throws Exception{
	  PostMethod filePost = new PostMethod(url);
	  for(Map.Entry<String, String>en: params.entrySet()){
		  filePost.addParameter(en.getKey(), en.getValue());
	  }
	  HttpClient client = new HttpClient();
	  int status = client.executeMethod(filePost);
	  return filePost.getResponseBody();
}
public static String getPostString(Map<String,String>params,String url)throws Exception{
	byte [] ret = doPost(params, url);
	return new String(ret);
}
public static byte[] doPostWithBody(String body,String url)throws Exception{
	  PostMethod filePost = new PostMethod(url);
	  filePost.setRequestHeader("Content-Type", "application/json");
	  filePost.setRequestEntity(new StringRequestEntity(body));
	  HttpClient client = new HttpClient();
	  int status = client.executeMethod(filePost);
	  return filePost.getResponseBody();
}
public static void wget(String url,String file) throws Exception{
	FileOutputStream out = new FileOutputStream(new File(file));
	HttpClient client = new HttpClient();
	client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

	GetMethod method = new GetMethod(url);
	method.setFollowRedirects(true);
	try {
		int statusCode = client.executeMethod(method);
		if (statusCode != HttpStatus.SC_OK) {
			System.err.println("Method failed: " + method.getStatusLine());
		}
		InputStream str = method.getResponseBodyAsStream();
		BufferedInputStream buf = new BufferedInputStream(str);
		byte[] buffer = new byte[1024];
		int len = 1;
		while(len>0){
			len = buf.read(buffer);
			out.write(buffer,0,len);
		}
		// System.out.println(ret);
	} catch (HttpException e) {
		System.err.println("Fatal protocol violation: " + e.getMessage());
		e.printStackTrace();
	} catch (IOException e) {
		System.err.println("Fatal transport error: " + e.getMessage());
		e.printStackTrace();
	} finally {
		method.releaseConnection();
	}
}
public static String saveUrlTxtToFile(String url, String fname) throws IOException {
	String str = HTTPClientUtil.getText(url);
	File file = new File(fname);
	FileUtils.writeStringToFile(file, str);
	return str;
}
public static String saveUrlTxtToFile(String url, String fname,Map<String,String>form) throws IOException {
	String str = HTTPClientUtil.getText(url,form);
	File file = new File(fname);
	FileUtils.writeStringToFile(file, str);
	return str;
}
public static String getText(String url,Map<String,String>form) {
	// System.out.println("URL: "+url);
	String ret = null;
	HttpClient client = new HttpClient();
	GetMethod method = new GetMethod(url);
	method.setFollowRedirects(true);
	
	NameValuePair[] p = new NameValuePair[form.size()];
	int i = 0;
	for(Map.Entry<String, String>en:form.entrySet()){
		p [i] = new NameValuePair();
		p[i].setName(en.getKey());
		p[i].setValue(en.getValue());
		i++;
	}
	method.setQueryString(p);
	try {
		int statusCode = client.executeMethod(method);
		if (statusCode != HttpStatus.SC_OK) {
			System.err.println("Method failed: " + method.getStatusLine());
		}
		ret = method.getResponseBodyAsString();
		// System.out.println(ret);
	} catch (HttpException e) {
		System.err.println("Fatal protocol violation: " + e.getMessage());
		e.printStackTrace();
	} catch (IOException e) {
		System.err.println("Fatal transport error: " + e.getMessage());
		e.printStackTrace();
	} finally {
		method.releaseConnection();
	}
	return ret;
}
}


