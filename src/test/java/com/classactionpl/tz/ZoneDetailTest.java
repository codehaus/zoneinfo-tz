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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import com.classactionpl.tz.Rule.OnType;
import com.classactionpl.tz.Rule.TimeOfDayType;
import com.classactionpl.tz.Rule.YearType;
import com.classactionpl.tz.Rule.YearValueType;

/**
 * Exercise the zone detail.
 * 
 * @author huntc
 * 
 */
public class ZoneDetailTest {

	/**
	 * A date formatter to faciliate setting up and verifying tests.
	 */
	private final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssZ");

	/**
	 * Test resolving a rule falls within a given date.
	 * 
	 * @throws ParseException
	 *             if the test is set up wrong.
	 */
	@Test
	public void testResolveRule() throws ParseException {
		Rule rule1 = new Rule();
		rule1.setIn(Calendar.APRIL);
		rule1.setOn(Calendar.SUNDAY);
		rule1.setOnType(OnType.get);
		rule1.setOnTypeValue(1);
		final int threeOClockSeconds = 3 * 60 * 60;
		rule1.setAt(threeOClockSeconds);
		rule1.setAtType(TimeOfDayType.wallclock);
		rule1.setFromType(YearValueType.min);
		final int nineteen99 = 1999;
		rule1.setTo(nineteen99);
		rule1.setToType(YearValueType.value);
		rule1.setToTypeOnly(false);
		final int oneHourSeconds = 1 * 60 * 60;
		rule1.setSave(oneHourSeconds);
		rule1.setType(YearType.inclusive);

		List<Rule> rules = new ArrayList<Rule>();
		rules.add(rule1);

		ZoneDetail detail = new ZoneDetail();
		detail.setRules(rules);
		final int tenHoursSeconds = 10 * 60 * 60;
		detail.setUtcOffset(tenHoursSeconds);
		detail.setUntil(null);

		assertEquals(rule1, detail.resolveRule(dateFormat
				.parse("1999-01-31T00:00:00+0000")));

		assertEquals(rule1, detail.resolveRule(dateFormat
				.parse("1999-04-04T02:59:59+1100")));

		assertNull(detail.resolveRule(dateFormat
				.parse("1999-04-04T03:00:00+1100")));
	}

}
