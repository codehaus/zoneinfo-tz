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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test out the JDK interface.
 * 
 * @author huntc
 * 
 */
public class ZoneinfoTimeZoneTest {

	/**
	 * Class wide setup.
	 */
	@BeforeClass
	public static void setUp() {
		ZoneFactory factory = new ZoneFactory();
		Map<String, AbstractZone> zones = ZoneinfoTimeZone.getZones();
		factory.parse(ZoneinfoTimeZoneTest.class.getResourceAsStream("africa"),
				zones);
		factory.parse(
				ZoneinfoTimeZoneTest.class.getResourceAsStream("antarctica"),
				zones);
		factory.parse(ZoneinfoTimeZoneTest.class.getResourceAsStream("asia"),
				zones);
		factory.parse(
				ZoneinfoTimeZoneTest.class.getResourceAsStream("australasia"),
				zones);
		factory.parse(ZoneinfoTimeZoneTest.class.getResourceAsStream("europe"),
				zones);
		factory.parse(
				ZoneinfoTimeZoneTest.class.getResourceAsStream("northamerica"),
				zones);
		factory.parse(
				ZoneinfoTimeZoneTest.class.getResourceAsStream("pacificnew"),
				zones);
		factory.parse(
				ZoneinfoTimeZoneTest.class.getResourceAsStream("solar87"),
				zones);
		factory.parse(
				ZoneinfoTimeZoneTest.class.getResourceAsStream("solar88"),
				zones);
		factory.parse(
				ZoneinfoTimeZoneTest.class.getResourceAsStream("solar89"),
				zones);
		factory.parse(
				ZoneinfoTimeZoneTest.class.getResourceAsStream("southamerica"),
				zones);
		factory.parse(
				ZoneinfoTimeZoneTest.class.getResourceAsStream("backward"),
				zones);
	}

	/**
	 * A date formatter to faciliate setting up and verifying tests.
	 */
	private final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssZ");

	/**
	 * Test cloning.
	 */
	@Test
	public void testClone() {
		TimeZone tz;

		tz = ZoneinfoTimeZone.getTimeZone("Europe/London");

		TimeZone clonedTz = (ZoneinfoTimeZone) tz.clone();
		assertEquals("Europe/London", clonedTz.getID());

	}

	/**
	 * Test that all of the time zones we expect are read in.
	 */
	@Test
	public void testGetAvailableIDs() {
		final int expectedIdLen = 529;
		assertEquals(expectedIdLen, ZoneinfoTimeZone.getAvailableIDs().length);
	}

	/**
	 * Test obtaining all time zones that sit +10 UTC.
	 */
	@Test
	public void testGetAvailableIDsForUTC10() {
		final int utc10millis = 10 * 60 * 60 * 1000;
		final int expectedIdLen = 21;
		assertEquals(expectedIdLen,
				ZoneinfoTimeZone.getAvailableIDs(utc10millis).length);
	}

	/**
	 * Test that the correct display name is returned for a given zone across
	 * DST boundaries.
	 */
	@Test
	public void testGetDisplayName() {
		TimeZone tz;

		tz = ZoneinfoTimeZone.getTimeZone("Europe/London");

		assertEquals("GMT", tz.getDisplayName(false, TimeZone.SHORT));
		assertEquals("BST", tz.getDisplayName(true, TimeZone.SHORT));

		tz = ZoneinfoTimeZone.getTimeZone("Europe/Andorra");

		assertEquals("CET", tz.getDisplayName(false, TimeZone.SHORT));
		assertEquals("CEST", tz.getDisplayName(true, TimeZone.SHORT));

	}

	/**
	 * Test whether the correct DST offset is returned for a couple of time
	 * zones.
	 */
	@Test
	public void testGetDSTOffset() {
		TimeZone tz;

		tz = ZoneinfoTimeZone.getTimeZone("Europe/London");

		final int oneHour = 1 * 60 * 60 * 1000;
		assertEquals(oneHour, tz.getDSTSavings());

		tz = ZoneinfoTimeZone.getTimeZone("Atlantic/Reykjavik");

		assertEquals(0, tz.getDSTSavings());
	}

	/**
	 * Test getting the id of a concrete zone and a link.
	 */
	@Test
	public void testGetID() {
		TimeZone tz;

		// Test a concrete zone.
		tz = ZoneinfoTimeZone.getTimeZone("Europe/London");

		assertEquals("Europe/London", tz.getID());

		// Test a link.
		tz = ZoneinfoTimeZone.getTimeZone("Europe/Vatican");

		assertEquals("Europe/Vatican", tz.getID());
	}

	/**
	 * Test returning the correct offset.
	 * 
	 * @throws ParseException
	 *             if the test is not set up correctly.
	 */
	@Test
	public void testGetOffset() throws ParseException {
		TimeZone tz;

		tz = ZoneinfoTimeZone.getTimeZone("Australia/Sydney");

		final int oneHourInMillis = 1 * 60 * 60 * 1000;
		final int tenHoursInMillis = 10 * oneHourInMillis;
		final int elevenHoursInMillis = 11 * oneHourInMillis;

		final int testYear = 2010;
		final int testDay = 3;
		final int dstTestDayOffsetInMillis = 15 * oneHourInMillis;
		final int noDstTestDayOffsetInMillis = 16 * oneHourInMillis;

		assertEquals(elevenHoursInMillis, tz.getOffset(GregorianCalendar.AD,
				testYear, Calendar.APRIL, testDay, Calendar.SATURDAY,
				dstTestDayOffsetInMillis));

		assertEquals(tenHoursInMillis, tz.getOffset(GregorianCalendar.AD,
				testYear, Calendar.APRIL, testDay, Calendar.SATURDAY,
				noDstTestDayOffsetInMillis));

		assertEquals(elevenHoursInMillis, tz.getOffset(dateFormat.parse(
				"2010-04-03T15:59:59+0000").getTime()));

		assertEquals(tenHoursInMillis, tz.getOffset(dateFormat.parse(
				"2010-04-03T16:00:00+0000").getTime()));

		assertEquals(tenHoursInMillis, tz.getOffset(dateFormat.parse(
				"2010-10-02T15:59:59+0000").getTime()));

		assertEquals(elevenHoursInMillis, tz.getOffset(dateFormat.parse(
				"2010-10-02T16:00:00+0000").getTime()));

	}

	/**
	 * Test getting the raw offset.
	 */
	@Test
	public void testGetRawOffset() {
		TimeZone tz;

		tz = ZoneinfoTimeZone.getTimeZone("Australia/Sydney");

		final int oneHourInMillis = 1 * 60 * 60 * 1000;
		final int tenHoursInMillis = 10 * oneHourInMillis;

		assertEquals(tenHoursInMillis, tz.getRawOffset());

	}

	/**
	 * Test rule comparison.
	 */
	@Test
	public void testHasSameRules() {
		// Compare against a JDK TZ object. Should return false.
		assertFalse(ZoneinfoTimeZone.getTimeZone("Australia/Sydney")
				.hasSameRules(TimeZone.getTimeZone("Australia/Sydney")));

		// Compare two zones sharing the same rule object (EU).
		assertTrue(ZoneinfoTimeZone.getTimeZone("Europe/Tirane").hasSameRules(
				ZoneinfoTimeZone.getTimeZone("Europe/Andorra")));

		// Compare two zones with different rule objects.
		assertFalse(ZoneinfoTimeZone.getTimeZone("Australia/Sydney")
				.hasSameRules(ZoneinfoTimeZone.getTimeZone("Europe/Andorra")));

	}

	/**
	 * Test whether DST or not.
	 * 
	 * @throws ParseException
	 *             if the test is bad.
	 */
	@Test
	public void testInDST() throws ParseException {
		TimeZone tz;

		tz = ZoneinfoTimeZone.getTimeZone("Australia/Sydney");

		assertTrue(tz.inDaylightTime(dateFormat
				.parse("2010-04-03T15:59:59+0000")));

		assertTrue(tz.inDaylightTime(dateFormat
				.parse("2010-04-03T16:00:00+0000")));

	}

	/**
	 * Test whether DST is used or not.
	 */
	@Test
	public void testUsesDST() {
		assertTrue(ZoneinfoTimeZone.getTimeZone("Europe/London")
				.useDaylightTime());

		assertFalse(ZoneinfoTimeZone.getTimeZone("Australia/Perth")
				.useDaylightTime());

	}
}
