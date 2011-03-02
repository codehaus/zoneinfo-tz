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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.classactionpl.tz.Rule.OnType;
import com.classactionpl.tz.Rule.TimeOfDayType;
import com.classactionpl.tz.Rule.YearType;
import com.classactionpl.tz.Rule.YearValueType;

/**
 * Test out the zone factory.
 * 
 * @author huntc
 * 
 */
public class ZoneFactoryTest {

	/**
	 * A date formatter to faciliate setting up and verifying tests.
	 */
	private final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssZ");

	/**
	 * Verify the rules for the EU.
	 * 
	 * @param rules
	 *            the rules.
	 */
	private void checkRulesForEU(List<Rule> rules) {
		int ruleIndex = 0;
		Rule rule;

		rule = rules.get(ruleIndex++);
		assertEquals("EU", rule.getName());
		final int nineteen77 = 1977;
		assertEquals(nineteen77, rule.getFrom());
		final int nineteen80 = 1980;
		assertEquals(nineteen80, rule.getTo());
		assertEquals(YearValueType.value, rule.getToType());
		assertEquals(false, rule.isToTypeOnly());
		assertEquals(YearType.inclusive, rule.getType());
		assertEquals(Calendar.APRIL, rule.getIn());
		assertEquals(Calendar.SUNDAY, rule.getOn());
		assertEquals(OnType.get, rule.getOnType());
		assertEquals(1, rule.getOnTypeValue());
		final int oneHourInSeconds = 1 * 60 * 60;
		assertEquals(oneHourInSeconds, rule.getAt());
		assertEquals(TimeOfDayType.universal, rule.getAtType());
		assertEquals(oneHourInSeconds, rule.getSave());
		assertEquals("S", rule.getLetters());

		rule = rules.get(ruleIndex++);
		assertEquals("EU", rule.getName());
		assertEquals(nineteen77, rule.getFrom());
		assertEquals(YearValueType.value, rule.getToType());
		assertEquals(true, rule.isToTypeOnly());
		assertEquals(YearType.inclusive, rule.getType());
		assertEquals(Calendar.SEPTEMBER, rule.getIn());
		assertEquals(Calendar.SUNDAY, rule.getOn());
		assertEquals(oneHourInSeconds, rule.getAt());
		assertEquals(TimeOfDayType.universal, rule.getAtType());
		assertEquals(0, rule.getSave());
		assertNull(rule.getLetters());

		rule = rules.get(ruleIndex++);
		assertEquals("EU", rule.getName());
		final int nineteen78 = 1978;
		assertEquals(nineteen78, rule.getFrom());
		assertEquals(YearValueType.value, rule.getToType());
		assertEquals(true, rule.isToTypeOnly());
		assertEquals(YearType.inclusive, rule.getType());
		assertEquals(Calendar.OCTOBER, rule.getIn());
		assertEquals(1, rule.getOn());
		assertEquals(OnType.value, rule.getOnType());
		assertEquals(oneHourInSeconds, rule.getAt());
		assertEquals(TimeOfDayType.universal, rule.getAtType());
		assertEquals(0, rule.getSave());
		assertNull(rule.getLetters());

		rule = rules.get(ruleIndex++);
		assertEquals("EU", rule.getName());
		final int nineteen79 = 1979;
		assertEquals(nineteen79, rule.getFrom());
		final int nineteen95 = 1995;
		assertEquals(nineteen95, rule.getTo());
		assertEquals(YearValueType.value, rule.getToType());
		assertEquals(false, rule.isToTypeOnly());
		assertEquals(YearType.inclusive, rule.getType());
		assertEquals(Calendar.SEPTEMBER, rule.getIn());
		assertEquals(Calendar.SUNDAY, rule.getOn());
		assertEquals(oneHourInSeconds, rule.getAt());
		assertEquals(TimeOfDayType.universal, rule.getAtType());
		assertEquals(0, rule.getSave());
		assertNull(rule.getLetters());

		rule = rules.get(ruleIndex++);
		assertEquals("EU", rule.getName());
		final int nineteen81 = 1981;
		assertEquals(nineteen81, rule.getFrom());
		assertEquals(YearValueType.max, rule.getToType());
		assertEquals(false, rule.isToTypeOnly());
		assertEquals(YearType.inclusive, rule.getType());
		assertEquals(Calendar.MARCH, rule.getIn());
		assertEquals(Calendar.SUNDAY, rule.getOn());
		assertEquals(OnType.last, rule.getOnType());
		assertEquals(oneHourInSeconds, rule.getAt());
		assertEquals(TimeOfDayType.universal, rule.getAtType());
		assertEquals(oneHourInSeconds, rule.getSave());
		assertEquals("S", rule.getLetters());

		rule = rules.get(ruleIndex++);
		assertEquals("EU", rule.getName());
		final int nineteen96 = 1996;
		assertEquals(nineteen96, rule.getFrom());
		assertEquals(YearValueType.max, rule.getToType());
		assertEquals(false, rule.isToTypeOnly());
		assertEquals(YearType.inclusive, rule.getType());
		assertEquals(Calendar.OCTOBER, rule.getIn());
		assertEquals(Calendar.SUNDAY, rule.getOn());
		assertEquals(OnType.last, rule.getOnType());
		assertEquals(oneHourInSeconds, rule.getAt());
		assertEquals(TimeOfDayType.universal, rule.getAtType());
		assertEquals(0, rule.getSave());
		assertNull(rule.getLetters());
	}

	/**
	 * Verify zone details for London.
	 * 
	 * @param zoneDetails
	 *            the details.
	 */
	private void checkZoneDetailsForLondon(List<ZoneDetail> zoneDetails) {
		final int minutesInSeconds = 60;
		final int hourInSeconds = 60 * minutesInSeconds;
		final int fifteenSeconds = 15;
		final int oneMin15Seconds = (1 * minutesInSeconds) + fifteenSeconds;

		int detailIndex = 0;
		ZoneDetail detail;
		List<Rule> rules;

		detail = zoneDetails.get(detailIndex++);
		assertEquals(-oneMin15Seconds, detail.getUtcOffset());
		assertEquals(0, detail.getSave());
		assertNull(detail.getRules());
		assertEquals("LMT", detail.getFormat());
		assertNull(detail.getDstFormat());
		final Integer eighteen47 = 1847;
		assertEquals(eighteen47, detail.getUntil());
		assertEquals(Integer.valueOf(Calendar.DECEMBER), detail.getUntilIn());
		assertEquals(Integer.valueOf(1), detail.getUntilOn());
		assertEquals(OnType.value, detail.getUntilOnType());
		assertEquals(Integer.valueOf(0), detail.getUntilAt());
		assertEquals(TimeOfDayType.localStandard, detail.getUntilAtType());

		detail = zoneDetails.get(detailIndex++);
		assertEquals(0, detail.getUtcOffset());
		rules = detail.getRules();
		final int expectedGBEireRuleCount = 65;
		assertEquals("GB-Eire", rules.get(0).getName());
		assertEquals(expectedGBEireRuleCount, rules.size());
		assertEquals("%s", detail.getFormat());
		assertNull(detail.getDstFormat());
		final Integer nineteen68 = 1968;
		assertEquals(nineteen68, detail.getUntil());
		assertEquals(Integer.valueOf(Calendar.OCTOBER), detail.getUntilIn());
		final int twentySeventh = 27;
		assertEquals(Integer.valueOf(twentySeventh), detail.getUntilOn());
		assertEquals(OnType.value, detail.getUntilOnType());
		assertNull(detail.getUntilAt());

		detail = zoneDetails.get(detailIndex++);
		assertEquals(1 * hourInSeconds, detail.getUtcOffset());
		assertEquals(0, detail.getSave());
		assertNull(detail.getRules());
		assertEquals("BST", detail.getFormat());
		assertNull(detail.getDstFormat());
		final Integer nineteen71 = 1971;
		assertEquals(nineteen71, detail.getUntil());
		assertEquals(Integer.valueOf(Calendar.OCTOBER), detail.getUntilIn());
		final int thirtyFirst = 31;
		assertEquals(Integer.valueOf(thirtyFirst), detail.getUntilOn());
		assertEquals(OnType.value, detail.getUntilOnType());
		assertEquals(Integer.valueOf(2 * hourInSeconds), detail.getUntilAt());
		assertEquals(TimeOfDayType.universal, detail.getUntilAtType());

		detail = zoneDetails.get(detailIndex++);
		assertEquals(0, detail.getUtcOffset());
		rules = detail.getRules();
		assertEquals("GB-Eire", rules.get(0).getName());
		assertEquals(expectedGBEireRuleCount, rules.size());
		assertEquals("%s", detail.getFormat());
		assertNull(detail.getDstFormat());
		final Integer nineteen96 = 1996;
		assertEquals(nineteen96, detail.getUntil());
		assertNull(detail.getUntilIn());
		assertNull(detail.getUntilOn());
		assertNull(detail.getUntilAt());

		detail = zoneDetails.get(detailIndex++);
		assertEquals(0, detail.getUtcOffset());
		rules = detail.getRules();
		assertEquals("EU", rules.get(0).getName());
		final int expectedEUEireRuleCount = 6;
		assertEquals(expectedEUEireRuleCount, rules.size());
		assertEquals("GMT", detail.getFormat());
		assertEquals("BST", detail.getDstFormat());
		assertNull(detail.getUntil());

		checkRulesForEU(rules);
	}

	/**
	 * Test parsing the European zoneinfo file and verify that Europe/London is
	 * as per its rules etc.
	 * 
	 * @throws ParseException
	 *             if the test is not set up correctly.
	 */
	@Test
	public void testParseEurope() throws ParseException {
		ZoneFactory factory = new ZoneFactory();
		Map<String, AbstractZone> zones = new HashMap<String, AbstractZone>();
		factory.parse(ZoneFactoryTest.class.getResourceAsStream("europe"),
				zones);

		Zone zone = (Zone) zones.get("Europe/London");
		List<ZoneDetail> zoneDetails = zone.getDetails();
		final int expectedZoneDetails = 5;
		assertEquals(zoneDetails.size(), expectedZoneDetails);
		assertEquals("Europe/London", zone.getName());

		checkZoneDetailsForLondon(zoneDetails);

		Link link = (Link) zones.get("Europe/Jersey");
		assertEquals(zoneDetails, link.getDetails());
		assertEquals("Europe/Jersey", link.getName());
		assertEquals(zone, link.getTargetZone());

		// For an extra sanity check, test out some UTC offsets for 2010
		final int oneHourInSeconds = 1 * 60 * 60;

		assertEquals(0, zone.resolveUtcOffset(dateFormat
				.parse("2010-03-28T00:59:59+0000")));

		assertEquals(oneHourInSeconds, zone.resolveUtcOffset(dateFormat
				.parse("2010-03-28T01:00:00+0000")));

		assertEquals(oneHourInSeconds, zone.resolveUtcOffset(dateFormat
				.parse("2010-10-31T00:59:59+0000")));

		assertEquals(0, zone.resolveUtcOffset(dateFormat
				.parse("2010-10-31T01:00:00+0000")));
	}

}
