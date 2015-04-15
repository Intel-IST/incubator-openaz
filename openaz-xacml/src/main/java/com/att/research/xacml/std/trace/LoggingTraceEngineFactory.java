/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */

/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2013 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package com.att.research.xacml.std.trace;

import java.util.Properties;

import com.att.research.xacml.api.trace.TraceEngine;
import com.att.research.xacml.api.trace.TraceEngineFactory;

/**
 * Extends the {@link com.att.research.xacml.api.trace.TraceEngineFactory} class to implement the <code>getTraceEngine</code> method to return
 * an instance of the {@link LoggingTraceEngine} class.
 * 
 * @author Christopher A. Rath
 * @version $Revision$
 */
public class LoggingTraceEngineFactory extends TraceEngineFactory {
	/**
	 * Creates a new <code>LoggingTraceEngineFactory</code>
	 */
	public LoggingTraceEngineFactory() {
	}

	/**
	 * Creates a new <code>LoggingTraceEngineFactory</code>
	 */
	public LoggingTraceEngineFactory(Properties properties) {
	}

	@Override
	public TraceEngine getTraceEngine() {
		return LoggingTraceEngine.newInstance();
	}

	@Override
	public TraceEngine getTraceEngine(Properties properties) {
		return LoggingTraceEngine.newInstance(properties);
	}

}