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

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * Base class for describing Zoneinfo zone objects. Zoneinfo objects differ from
 * regular Java TimeZone objects in that you always need to specify a date
 * object to resolve UTC offsets whether or not there is a DST offset. With the
 * regular Java TimeZone, you can query for various characteristics e.g. does a
 * TZ have DST. With Zoneinfo, the answer to this would depend on when in terms
 * of time.
 * 
 * @author huntc
 * 
 */
public abstract class AbstractZone implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the time zone. This is the name used in creating the time
	 * conversion information file for the zone.
	 */
	private String name;

	/**
	 * @return value.
	 */
	public abstract Collection<ZoneDetail> getDetails();

	public String getName() {
		return name;
	}

	/**
	 * Get a zone detail given a date and time.
	 * 
	 * @param when
	 *            when to return the detail for.
	 * @return the detail object or null if there is none.
	 */
	public abstract ZoneDetail resolveDetail(Date when);

	/**
	 * Get the offset to UTC given a time.
	 * 
	 * @param when
	 *            the time.
	 * @return the offset.
	 */
	public abstract int resolveUtcOffset(Date when);

	public void setName(String name) {
		this.name = name;
	}
}
