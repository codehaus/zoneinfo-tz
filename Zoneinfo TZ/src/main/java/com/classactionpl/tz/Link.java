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

import java.util.Collection;
import java.util.Date;

/**
 * A link is an alias to another zone object.
 * 
 * @author huntc
 * 
 */
public class Link extends AbstractZone {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The zone to link to.
	 */
	private AbstractZone targetZone;

	@Override
	public Collection<ZoneDetail> getDetails() {
		return targetZone.getDetails();
	}

	public AbstractZone getTargetZone() {
		return targetZone;
	}

	@Override
	public ZoneDetail resolveDetail(Date when) {
		return targetZone.resolveDetail(when);
	}

	@Override
	public int resolveUtcOffset(Date when) {
		return targetZone.resolveUtcOffset(when);
	}

	public void setTargetZone(AbstractZone targetZone) {
		this.targetZone = targetZone;
	}

	@Override
	public String toString() {
		return "Link [targetZone=" + targetZone + ", getName()=" + getName()
				+ "]";
	}

}
