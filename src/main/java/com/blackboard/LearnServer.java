/*
 *  Copyright 2013 Friedrich Clausen <friedrich.clausen@blackboard.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
*/
package com.blackboard;

import java.io.IOException;
import java.security.KeyStore;


import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import com.blackboard.TrustAllSSLSocketFactory;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;
import com.googlecode.sardine.impl.SardineImpl;

public class LearnServer {
	static Logger logger = Logger.getLogger(LearnServer.class);
	
	private String url = null;
	private String password = null;
	private String userName = null;
	private SardineImpl sardine = null;
	private boolean verifyCertStatus = true;

	public LearnServer(String userName, String password, String url, boolean verifyCertStatus) 
			throws IllegalArgumentException, IOException {
		if (url == null) {
			throw new IllegalArgumentException("URL cannot be null");
		}
		if (url.startsWith("https://")) {
			this.url = url;
		} else {
			throw new IllegalArgumentException("Only https:// URLs accepted");
		}
		if (password == null) {
			throw new IllegalArgumentException("Password cannot be null");
		}
		if (userName == null) {
			throw new IllegalArgumentException("User name cannot be null");
		}
		if (verifyCertStatus == true) {
			this.verifyCertStatus = true;
			logger.debug("Verifying certificates");
		} else {
			logger.debug("Accepting all SSL certificates");
		}
		
		logger.info("Connecting to " + url + " as user " + userName);
		// Create a custom httpclient object that accepts all SSL certs
		// Protocol acceptAllCerts = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
		// URI uri = new URI(url, true);
		// GetMethod method = new GetMethod(uri.getPathQuery());
		// HostConfiguration hc = new HostConfiguration();
		// hc.setHost(uri.getHost(), uri.getPort(), acceptAllCerts);
		//AbstractHttpClient client = new HttpClient();
		if (verifyCertStatus == false) {
			AbstractHttpClient httpclient = getTrustAllSSLHttpClient();
			logger.debug("Creating new sardine instance via custom httpclient");
			sardine = new SardineImpl(httpclient, userName, password);
		} else {
			logger.debug("Creating sardine instance with SardineFactory");
			sardine = (SardineImpl) SardineFactory.begin(userName, password);
		}
		
		
		
		if (sardine.exists(url)) {
			logger.info("Successfully connected to " + url);
		} else {
			throw new IOException("Cannot connect to server or access specified path");
		}
	}
	
	private AbstractHttpClient getTrustAllSSLHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			
			SSLSocketFactory sf = new TrustAllSSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));
			
			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			System.out.println("WARNING: Could not create Trust All SSL client, using default" + e.getMessage());
			return new DefaultHttpClient();
		}
	}
	

	
	public boolean exists(String courseId) {
		logger.debug("Check for course at : " + url + "/" + courseId);
		boolean found = false;
		if (courseId == null) {
			throw new IllegalArgumentException("courseId cannot be null");
		}
		
		String finalUrl = url + "/" + courseId; 
		try { 
			if (sardine.exists(finalUrl)) {
				found = true;
			} else {
				found = false;
			}
		} catch (IOException e) {
				logger.debug("Error Deleting Course : " + e.getMessage());
		}
		return found;
	}
	
	public void deleteCourse(String courseId) throws IOException {
		if (courseId == null) {
			logger.error("Please specify a course ID for deletion");
		}
		String finalUrl = url + "/" + courseId; 
		logger.info("Deleting via URL : " + finalUrl);
		sardine.delete(finalUrl);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if (password != null) {
			this.password = password;
		} else {
			throw new IllegalArgumentException("Password cannot be null");
		}
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		if (userName != null) {
			this.userName = userName;
		} else {
			throw new IllegalArgumentException("User name cannot be null");
		}
	}
	
	public void setUrl(String url) {
		if (url == null) {
			throw new IllegalArgumentException("URL cannot be null");
		}
		if (url.startsWith("https://")) {
			this.url = url;
		} else {
			throw new IllegalArgumentException("Only https:// URLs accepted");
		}
	}
	
	public String getUrl() {
		return url;
	}

	public boolean getVerifyCertStatus() {
		return verifyCertStatus;
	}

	public void setVerifyCertStatus(boolean verifyCertStatus) {
		this.verifyCertStatus = verifyCertStatus;
	}
	
	
}
