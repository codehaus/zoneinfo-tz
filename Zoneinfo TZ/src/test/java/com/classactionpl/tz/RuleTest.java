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
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.classactionpl.tz.Rule.OnType;
import com.classactionpl.tz.Rule.TimeOfDayType;

/**
 * Test out rules.
 * 
 * @author huntc
 * 
 */
public class RuleTest {

	/**
	 * A date formatter to faciliate setting up and verifying tests.
	 */
	private final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssZ");

	/**
	 * Test DST ending the first Sunday in April at 3am wall clock time.
	 * 
	 * @throws ParseException
	 *             test is wrong.
	 */
	@Test
	public void testResolveInOnAtGet() throws ParseException {
		final int oneHourInSeconds = 60 * 60;
		final int year = 2010;
		final Integer in = Calendar.APRIL;
		final OnType onType = OnType.get;
		final Integer on = Calendar.SUNDAY;
		final int onTypeValue = 1;
		final Integer at = 3 * oneHourInSeconds;
		final TimeOfDayType atType = TimeOfDayType.wallclock;
		final int utcOffset = 10 * oneHourInSeconds;
		final int save = oneHourInSeconds;

		Date resolved = Rule.resolveInOnAt(year, in, onType, on, onTypeValue,
				at, atType, utcOffset, save);

		assertEquals(dateFormat.parse("2010-04-04T03:00:00+1100"), resolved);
	}

	/**
	 * Test DST ending the last Sunday in October at 2am wall clock time.
	 * 
	 * @throws ParseException
	 *             test is wrong.
	 */
	@Test
	public void testResolveInOnAtLast() throws ParseException {
		final int oneHourInSeconds = 60 * 60;
		final int year = 2010;
		final Integer in = Calendar.OCTOBER;
		final OnType onType = OnType.last;
		final Integer on = Calendar.SUNDAY;
		final int onTypeValue = 2;
		final Integer at = 2 * oneHourInSeconds;
		final TimeOfDayType atType = TimeOfDayType.wallclock;
		final int utcOffset = 10 * oneHourInSeconds;
		final int save = 0;

		Date resolved = Rule.resolveInOnAt(year, in, onType, on, onTypeValue,
				at, atType, utcOffset, save);

		assertEquals(dateFormat.parse("2010-10-31T02:00:00+1000"), resolved);
	}

	/**
	 * Test DST ending the 2nd last Saturday in October at 4pm universal time.
	 * 
	 * @throws ParseException
	 *             test is wrong.
	 */
	@Test
	public void testResolveInOnAtLet() throws ParseException {
		final int oneHourInSeconds = 60 * 60;
		final int year = 2010;
		final Integer in = Calendar.OCTOBER;
		final OnType onType = OnType.let;
		final Integer on = Calendar.SATURDAY;
		final int onTypeValue = 2;
		final Integer at = 16 * oneHourInSeconds;
		final TimeOfDayType atType = TimeOfDayType.universal;
		final int utcOffset = 10 * oneHourInSeconds;
		final int save = 0;

		Date resolved = Rule.resolveInOnAt(year, in, onType, on, onTypeValue,
				at, atType, utcOffset, save);

		assertEquals(dateFormat.parse("2010-10-24T02:00:00+1000"), resolved);
	}

	/**
	 * Test DST ending the first Sunday in April at 2am standard time.
	 * 
	 * @throws ParseException
	 *             test is wrong.
	 */
	@Test
	public void testResolveInOnAtValue() throws ParseException {
		final int oneHourInSeconds = 60 * 60;
		final int year = 2010;
		final Integer in = Calendar.APRIL;
		final OnType onType = OnType.value;
		final Integer on = 4;
		final int onTypeValue = 1;
		final Integer at = 2 * oneHourInSeconds;
		final TimeOfDayType atType = TimeOfDayType.localStandard;
		final int utcOffset = 10 * oneHourInSeconds;
		final int save = oneHourInSeconds;

		Date resolved = Rule.resolveInOnAt(year, in, onType, on, onTypeValue,
				at, atType, utcOffset, save);

		assertEquals(dateFormat.parse("2010-04-04T03:00:00+1100"), resolved);
	}

	/**
	 * Test DST ending the first Sunday in April midnight UTC.
	 * 
	 * @throws ParseException
	 *             test is wrong.
	 */
	@Test
	public void testResolveInOnNoAt() throws ParseException {
		final int oneHourInSeconds = 60 * 60;
		final int year = 2010;
		final Integer in = Calendar.APRIL;
		final OnType onType = OnType.get;
		final Integer on = Calendar.SUNDAY;
		final int onTypeValue = 1;
		final Integer at = null;
		final TimeOfDayType atType = TimeOfDayType.wallclock;
		final int utcOffset = 10 * oneHourInSeconds;
		final int save = oneHourInSeconds;

		Date resolved = Rule.resolveInOnAt(year, in, onType, on, onTypeValue,
				at, atType, utcOffset, save);

		assertEquals(dateFormat.parse("2010-04-04T00:00:00+0000"), resolved);
	}

	/**
	 * Test DST ending the first day of January midnight UTC.
	 * 
	 * @throws ParseException
	 *             test is wrong.
	 */
	@Test
	public void testResolveInOnNoIn() throws ParseException {
		final int oneHourInSeconds = 60 * 60;
		final int year = 2010;
		final Integer in = null;
		final OnType onType = OnType.get;
		final Integer on = null;
		final int onTypeValue = 1;
		final Integer at = null;
		final TimeOfDayType atType = TimeOfDayType.wallclock;
		final int utcOffset = 10 * oneHourInSeconds;
		final int save = oneHourInSeconds;

		Date resolved = Rule.resolveInOnAt(year, in, onType, on, onTypeValue,
				at, atType, utcOffset, save);

		assertEquals(dateFormat.parse("2010-01-01T00:00:00+0000"), resolved);
	}

	/**
	 * Test DST ending the first day of April midnight UTC.
	 * 
	 * @throws ParseException
	 *             test is wrong.
	 */
	@Test
	public void testResolveInOnNoOn() throws ParseException {
		final int oneHourInSeconds = 60 * 60;
		final int year = 2010;
		final Integer in = Calendar.APRIL;
		final OnType onType = OnType.get;
		final Integer on = null;
		final int onTypeValue = 1;
		final Integer at = null;
		final TimeOfDayType atType = TimeOfDayType.wallclock;
		final int utcOffset = 10 * oneHourInSeconds;
		final int save = oneHourInSeconds;

		Date resolved = Rule.resolveInOnAt(year, in, onType, on, onTypeValue,
				at, atType, utcOffset, save);

		assertEquals(dateFormat.parse("2010-04-01T00:00:00+0000"), resolved);
	}
}
