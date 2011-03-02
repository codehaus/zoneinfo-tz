/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package com.classactionpl.tz;

import java.io.InputStream;
import java.util.Map;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.apache.log4j.Logger;

/**
 * Responsible for producing Zone objects from Zoneinfo streams.
 * 
 * @author huntc
 * 
 */
public class ZoneFactory {

	/** */
	private static Logger logger = Logger.getLogger(ZoneFactory.class);

	/**
	 * Given an input stream for a zoneinfo document, populate a map of parsed
	 * zoneinfo data keyed by the zone ids.
	 * 
	 * @param zoneinfoStream
	 *            the input stream to read in from.
	 * @param zones
	 *            the zones to append to.
	 */
	public void parse(InputStream zoneinfoStream,
			Map<String, AbstractZone> zones) {
		try {
			CharStream input = new ANTLRInputStream(zoneinfoStream);
			ZoneinfoLexer lex = new ZoneinfoLexer(input);

			CommonTokenStream tokens = new CommonTokenStream(lex);
			ZoneinfoParser parser = new ZoneinfoParser(tokens);
			ZoneinfoParser.line_return root = parser.line();
			if (logger.isTraceEnabled()) {
				logger.trace("tree=" + ((Tree) root.tree).toStringTree());
			}

			CommonTreeNodeStream nodes = new CommonTreeNodeStream(root.tree);
			nodes.setTokenStream(tokens);
			ZoneinfoWalker walker = new ZoneinfoWalker(nodes);
			walker.setZones(zones);
			walker.line();

		} catch (Throwable t) {
			logger.error("During zoneinfo parsing:", t);
		}

	}
}
