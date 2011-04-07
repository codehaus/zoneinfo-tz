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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A concrete zone info zone.
 * 
 * @author huntc
 * 
 */
public class Zone extends AbstractZone {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * A set of details describing the zone. There must be at least one. Details
	 * are expected in earliest detail first.
	 */
	private final List<ZoneDetail> details = new ArrayList<ZoneDetail>();

	@Override
	public List<ZoneDetail> getDetails() {
		return details;
	}

	@Override
	public ZoneDetail resolveDetail(Date when) {
		ZoneDetail activeDetail = null;
		int detailsSize = details.size();
		if (detailsSize > 0) {
			Date activeDetailUntil = null;
			for (int i = detailsSize - 1; i >= 0; --i) {
				ZoneDetail detail = details.get(i);
				Date until = detail.resolveUntil(when);
				int compare = until.compareTo(when);
				if (compare < 0) {
					break;
				}
				if (compare == 0 && activeDetail != null) {
					break;
				}
				if (activeDetail == null || until.before(activeDetailUntil)) {
					activeDetail = detail;
					activeDetailUntil = until;
				}
			}
		}

		return activeDetail;
	}

	@Override
	public int resolveUtcOffset(Date when) {
		int offset;
		ZoneDetail detail = resolveDetail(when);
		if (detail != null) {
			offset = detail.getUtcOffset();
			int save;
			if (detail.getRules() != null) {
				Rule rule = detail.resolveRule(when);
				if (rule != null) {
					save = rule.getSave();
				} else {
					save = 0;
				}
			} else {
				save = detail.getSave();
			}
			offset += save;
		} else {
			offset = 0;
		}
		return offset;
	}

	@Override
	public String toString() {
		return "Zone [details=" + details + ", getName()=" + getName() + "]";
	}

}
