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


import org.apache.log4j.Logger;

import com.googlecode.sardine.SardineFactory;
import com.googlecode.sardine.impl.SardineImpl;

public class LearnServer {
	static Logger logger = WebdavBulkDeleterClient.logger;
	
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
        logger.debug("Creating sardine instance with SardineFactory");
        sardine = (SardineImpl) SardineFactory.begin(userName, password);

		if (sardine.exists(url)) {
			logger.info("Successfully connected to " + url);
		} else {
			throw new IOException("Cannot connect to server or access specified path");
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
	
	
}
