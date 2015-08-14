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
/**
 * 
 * Friedrich Clausen <friedrich.clausen@blackboard.com>
 * 
 * NEXT:
 *      * Use standard sardine methods in LearnServer
 *      * Require valid certificate for now
 *      ** Look at https://code.launchpad.net/syncany for SSL possibly
 *      
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.*;

public class WebdavBulkDeleterClient {
	
	static Logger logger = Logger.getLogger(WebdavBulkDeleterClient.class);

	// Stuff all the options into the options object for later use 
	// About the static access warning - http://stackoverflow.com/a/1933573/1300307
	// Seems specific to commons cli. Suppressing.
	@SuppressWarnings("static-access")
	private static Options addAllOptions(Options options, Map<String, String> optionsAvailable) {
		Iterator<Entry<String, String>> it = optionsAvailable.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pairs = it.next();
			String name = pairs.getKey();
			String description = pairs.getValue();
			options.addOption( OptionBuilder.withLongOpt(name)
					.withDescription(description)
					.hasArg()
					.withArgName(name)
					.create() );			
		}
		return(options);
	}
	
	// Make sure all the command line options are present
	private static void verifyOptions(CommandLine line, Map<String, String> options) throws ParseException {
		Iterator<Entry<String, String>> it = options.entrySet().iterator();
		boolean error = false; // Let's be optimistic 
		
		while (it.hasNext()) {
			Map.Entry<String, String> pairs = it.next();
			if (!line.hasOption(pairs.getKey())) {
				logger.error("Please specify option --" + pairs.getKey());
				error = true;
			}
		}
		if (error) {
			throw new ParseException("Required arguments missing");
		}
	}

	public static void main(String[] args) {
		if (System.getProperty("log4j.configuration") != null) {
			PropertyConfigurator.configure(System.getProperty("log4j.configuration"));
		} else {
            ConsoleAppender console = new ConsoleAppender();
            String PATTERN = "%d [%p|%c|%C{1}] %m%n";
            console.setLayout(new PatternLayout(PATTERN));
            console.setThreshold(Level.DEBUG);
            console.activateOptions();
            logger.setAdditivity(false);
            logger.addAppender(console);
            //Logger.getRootLogger().addAppender(console);
		}
		
		// Perform command line parsing in an as friendly was as possible. 
		// Could be improved
		CommandLineParser parser = new PosixParser();
		Options options = new Options();

		// Use a map to store our options and loop over it to check and parse options via
		// addAllOptions() and verifyOptions() below
		Map<String, String> optionsAvailable = new HashMap<String, String>();
		optionsAvailable.put("deletion-list", "The file containing the list of courses to delete");
		optionsAvailable.put("user", "User with deletion privileges, usually bbsupport");
		optionsAvailable.put("password", "Password - ensure you escape any shell characters");
		optionsAvailable.put("url", "The Learn URL - usually https://example.com/bbcswebdav/courses");
		
		options = addAllOptions (options, optionsAvailable);

		CommandLine line = null;
		try {
			line = parser.parse(options, args);
			verifyOptions(line, optionsAvailable);
		} catch (ParseException e) {
			// Detailed reason will be printed by verifyOptions above
			logger.fatal("Incorrect options specified, exiting...");
			System.exit(1);
		}
		
		Scanner scanner = null;
		try {
			scanner = new Scanner( new File(line.getOptionValue("deletion-list")));
		} catch (FileNotFoundException e) {
			logger.fatal("Cannot open file : " + e.getLocalizedMessage());
			System.exit(1);
		}
		
		// By default we verify SSL certs
		boolean verifyCertStatus = true;
		if (line.hasOption("no-verify-ssl")) {
			verifyCertStatus = false;
		}
		
		// Loop through deletion list and delete courses if they exist.
		LearnServer instance;
		try {
			logger.debug("Attempting to open connection");
			instance = new LearnServer(line.getOptionValue("user"), line.getOptionValue("password"), line.getOptionValue("url"), verifyCertStatus);
			String currentCourse = null;
			logger.debug("Connection open");
			while(scanner.hasNextLine()) {
				currentCourse = scanner.nextLine();
				if (instance.exists(currentCourse)) {
					try {
						instance.deleteCourse(currentCourse);
						logger.info("Processing " + currentCourse + " : Result - Deletion Successful");
					} catch (IOException ioe) {
						logger.error("Processing " + currentCourse + " : Result - Could not Delete (" + ioe.getLocalizedMessage() + ")");
					}
				} else {
					logger.info("Processing " + currentCourse + " : Result - Course does not exist");
				}
			}
		} catch (IllegalArgumentException e) {
			logger.fatal(e.getLocalizedMessage());
            if (logger.getLevel() == Level.DEBUG) {
                e.printStackTrace();
            }
			System.exit(1);
		} catch (IOException ioe) {
			logger.debug(ioe);
			logger.fatal(ioe.getMessage());
            if (logger.getLevel() == Level.DEBUG) {
                ioe.printStackTrace();
            }
		}
		
	}
}
