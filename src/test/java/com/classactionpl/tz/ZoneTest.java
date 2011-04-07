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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.classactionpl.tz.Rule.OnType;
import com.classactionpl.tz.Rule.TimeOfDayType;
import com.classactionpl.tz.Rule.YearType;
import com.classactionpl.tz.Rule.YearValueType;

/**
 * Test out the Zone class.
 * 
 * @author huntc
 */
public class ZoneTest {

	/**
	 * A date formatter to faciliate setting up and verifying tests.
	 */
	private final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssZ");

	/**
	 * Test resolving a detail with no until. Should return it.
	 */
	@Test
	public void testResolveDetailNoUntil() {
		ZoneDetail detail1 = new ZoneDetail();
		detail1.setUntil(null);

		Zone zone = new Zone();
		zone.getDetails().add(detail1);

		assertEquals(detail1, zone.resolveDetail(new Date()));
	}

	/**
	 * Test that the correct detail is returned given rules.
	 * 
	 * @throws ParseException
	 *             if the test is incorrectly set up.
	 */
	@Test
	public void testResolveDetailsWithRules() throws ParseException {
		Rule rule1 = new Rule();
		rule1.setIn(Calendar.APRIL);
		rule1.setOn(Calendar.SUNDAY);
		rule1.setOnType(OnType.get);
		rule1.setOnTypeValue(1);
		final int twoOClockSeconds = 2 * 60 * 60;
		rule1.setAt(twoOClockSeconds);
		rule1.setAtType(TimeOfDayType.wallclock);
		final int nineteen99 = 1999;
		rule1.setTo(nineteen99);
		rule1.setToType(YearValueType.value);
		rule1.setToTypeOnly(false);
		final int twoHoursSeconds = 2 * 60 * 60;
		rule1.setSave(twoHoursSeconds);
		rule1.setType(YearType.inclusive);

		List<Rule> rules = new ArrayList<Rule>();
		rules.add(rule1);

		ZoneDetail detail1 = new ZoneDetail();
		detail1.setRules(rules);
		final int twoAmSeconds = 2 * 60 * 60;
		detail1.setUtcOffset(twoAmSeconds);
		detail1.setUntil(null);

		Zone zone = new Zone();
		zone.getDetails().add(detail1);

		assertEquals(detail1, zone.resolveDetail(dateFormat
				.parse("1999-01-31T00:00:00+0000")));

		assertEquals(detail1, zone.resolveDetail(dateFormat
				.parse("2000-01-31T00:00:00+0000")));
	}

	/**
	 * Test that the correct detail is returned given the specification of the
	 * until field and the absence of rules.
	 * 
	 * @throws ParseException
	 *             if the test is incorrectly set up.
	 */
	@Test
	public void testResolveDetailsWithSave() throws ParseException {
		ZoneDetail detail1 = new ZoneDetail();
		final int nineteen99 = 1999;
		detail1.setUntil(nineteen99);
		detail1.setSave(0);

		// 1-jan-2010 midnight utc.
		ZoneDetail detail2 = new ZoneDetail();
		final int twenty10 = 2010;
		detail2.setUntil(nineteen99);
		detail2.setSave(0);

		// 1-feb-2010 midnight utc
		ZoneDetail detail3 = new ZoneDetail();
		detail3.setUntil(twenty10);
		detail3.setUntilIn(Calendar.FEBRUARY);
		detail3.setUntilOnType(OnType.value);
		final int thirtyFirstOfMonth = 1;
		detail3.setUntilOn(thirtyFirstOfMonth);
		detail3.setUntilAt(null);
		final int oneHourInSeconds = 60 * 60;
		detail3.setSave(oneHourInSeconds);

		ZoneDetail detail4 = new ZoneDetail();
		detail4.setSave(0);
		detail4.setUntil(null);

		Collection<ZoneDetail> details = new ArrayList<ZoneDetail>();
		details.add(detail1);
		details.add(detail2);
		details.add(detail3);
		details.add(detail4);

		Zone zone = new Zone();
		Collection<ZoneDetail> sortedDetails = zone.getDetails();
		sortedDetails.addAll(details);

		assertEquals(detail3, zone.resolveDetail(dateFormat
				.parse("2010-01-31T00:00:00+0000")));
	}

	/**
	 * Test that UTC offsets are correctly resolved.
	 * 
	 * @throws ParseException
	 *             if the test is incorrectly set up.
	 */
	@Test
	public void testResolveUtcOffset() throws ParseException {
		Rule ruleOutDST = new Rule();
		final int ruleStartYear = 2008;
		ruleOutDST.setFrom(ruleStartYear);
		ruleOutDST.setFromType(YearValueType.value);
		ruleOutDST.setToType(YearValueType.max);
		ruleOutDST.setType(YearType.inclusive);
		ruleOutDST.setIn(Calendar.APRIL);
		ruleOutDST.setOn(Calendar.SUNDAY);
		ruleOutDST.setOnType(OnType.get);
		ruleOutDST.setOnTypeValue(1);
		final int twoAMInSeconds = 2 * 60 * 60;
		ruleOutDST.setAt(twoAMInSeconds);
		ruleOutDST.setAtType(TimeOfDayType.localStandard);
		ruleOutDST.setSave(0);

		Rule ruleInDST = new Rule();
		ruleInDST.setFrom(ruleStartYear);
		ruleInDST.setFromType(YearValueType.value);
		ruleInDST.setToType(YearValueType.max);
		ruleInDST.setType(YearType.inclusive);
		ruleInDST.setIn(Calendar.OCTOBER);
		ruleInDST.setOn(Calendar.SUNDAY);
		ruleInDST.setOnType(OnType.get);
		ruleInDST.setOnTypeValue(1);
		ruleInDST.setAt(twoAMInSeconds);
		ruleInDST.setAtType(TimeOfDayType.localStandard);
		final int oneHourInSeconds = 1 * 60 * 60;
		ruleInDST.setSave(oneHourInSeconds);

		List<Rule> rules = new ArrayList<Rule>();
		rules.add(ruleOutDST);
		rules.add(ruleInDST);

		ZoneDetail detail = new ZoneDetail();
		detail.setRules(rules);
		final int tenHoursInSeconds = 10 * 60 * 60;
		detail.setUtcOffset(tenHoursInSeconds);

		Zone zone = new Zone();
		zone.getDetails().add(detail);

		final int elevenHoursInSeconds = 11 * 60 * 60;
		assertEquals(elevenHoursInSeconds, zone.resolveUtcOffset(dateFormat
				.parse("2010-04-03T15:59:59+0000")));

		assertEquals(tenHoursInSeconds, zone.resolveUtcOffset(dateFormat
				.parse("2010-04-03T16:00:00+0000")));

		assertEquals(tenHoursInSeconds, zone.resolveUtcOffset(dateFormat
				.parse("2010-10-02T15:59:59+0000")));

		assertEquals(elevenHoursInSeconds, zone.resolveUtcOffset(dateFormat
				.parse("2010-10-02T16:00:00+0000")));
	}
}
