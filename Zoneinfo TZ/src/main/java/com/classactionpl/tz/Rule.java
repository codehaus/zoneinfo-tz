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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Captures a Zoneinfo Rule.
 * 
 * @author huntc
 * 
 */
public class Rule {
	/** */
	enum OnType {
		/**
		 * On the calendar month day specified by value.
		 */
		value,
		/**
		 * The last calendar month day of week specified by the value.
		 */
		last,
		/**
		 * The nth calendar month day of week specified by the value. The nth
		 * value is specified by the comparator i.e. onTypeValue. Either less
		 * than or equal to (let) or greater than or equal to (get).
		 */
		let, get
	}

	/** */
	enum TimeOfDayType {
		/** */
		wallclock, localStandard, universal
	}

	/** */
	enum YearType {
		/** */
		inclusive, even, odd, uspres, nonpres, nonuspres
	}

	/** */
	enum YearValueType {
		/** */
		min, max, value
	}

	/**
	 * Given a year and in, on and at values resolve to a date object.
	 * 
	 * @param year
	 *            the year to resolve things with.
	 * @param in
	 *            value.
	 * @param onType
	 *            value.
	 * @param on
	 *            value.
	 * @param onTypeValue
	 *            value.
	 * @param at
	 *            value.
	 * @param atType
	 *            value.
	 * @param utcOffset
	 *            the utc offset (seconds) to use for at values when using wall
	 *            clock or local standard time.
	 * @param save
	 *            the DST value (seconds) to use for at values when using wall
	 *            clock.
	 * @return the date resolved.
	 */
	public static Date resolveInOnAt(int year, Integer in, OnType onType,
			Integer on, int onTypeValue, Integer at, TimeOfDayType atType,
			int utcOffset, int save) {
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		cal.set(Calendar.YEAR, year);

		cal.set(Calendar.MILLISECOND, 0);

		boolean inSet = false;
		boolean onSet = false;
		boolean atSet = false;

		if (in != null) {
			cal.set(Calendar.MONTH, in);

			inSet = true;

			if (on != null) {
				switch (onType) {
				case last:
					cal.set(Calendar.DAY_OF_WEEK, on);
					cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, -1);
					break;
				case let:
					cal.set(Calendar.DAY_OF_WEEK, on);
					cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, -onTypeValue);
					break;
				case get:
					cal.set(Calendar.DAY_OF_WEEK, on);
					cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, onTypeValue);
					break;
				default: // value
					cal.set(Calendar.DAY_OF_MONTH, on);
				}

				onSet = true;

				if (at != null) {
					int offset;
					switch (atType) {
					case localStandard:
						offset = utcOffset;
						break;
					case universal:
						offset = 0;
						break;
					default: // wallclock
						offset = utcOffset + save;
					}
					final int secondsInMinutes = 60;
					cal.set(Calendar.SECOND, at % secondsInMinutes);
					cal.set(Calendar.MINUTE, at / secondsInMinutes
							% secondsInMinutes);
					final int secondsInHour = 60 * 60;
					cal.set(Calendar.HOUR_OF_DAY, at / secondsInHour);

					cal.add(Calendar.SECOND, -offset);

					atSet = true;
				}
			}
		}

		if (!inSet) {
			cal.set(Calendar.MONTH, Calendar.JANUARY);
		}
		if (!onSet) {
			cal.set(Calendar.DAY_OF_MONTH, 1);
		}
		if (!atSet) {
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.HOUR_OF_DAY, 0);
		}

		return cal.getTime();
	}

	/**
	 * Gives the (arbitrary) name of the set of rules this rule is part of.
	 */
	private String name;

	/**
	 * Gives the first year in which the rule applies. Any integer year can be
	 * supplied; the Gregorian calendar is assumed. The word minimum (or an
	 * abbreviation) means the minimum year representable as an integer. The
	 * word maximum (or an abbreviation) means the maximum year representable as
	 * an integer. Rules can describe times that are not representable as time
	 * values, with the unrepresentable times ignored; this allows rules to be
	 * portable among hosts with differing time value types.
	 */
	private YearValueType fromType;

	/**
	 * If the year type is value then here is the value.
	 */
	private int from;

	/**
	 * Gives the final year in which the rule applies. In addition to minimum
	 * and maximum (as above), the word only (or an abbreviation) may be used to
	 * repeat the value of the FROM field. When only is required, value must be
	 * specified here.
	 */
	private YearValueType toType;

	/**
	 * When set to true the value represents one and only one value.
	 */
	private boolean toTypeOnly;

	/**
	 * If the year type is value then here is the value.
	 */
	private int to;

	/**
	 * Gives the type of year in which the rule applies.
	 */
	private YearType type;

	/**
	 * The calendar month the rule applies from.
	 */
	private int in;

	/**
	 * The day of the month the rule applies to.
	 * <p>
	 * Patterns include:
	 * 
	 * <p>
	 * 5 the fifth of the month
	 * <p>
	 * lastSun the last Sunday in the month
	 * <p>
	 * lastMon the last Monday in the month
	 * <p>
	 * Sun>=8 first Sunday on or after the eighth
	 * <p>
	 * Sun<=25 last Sunday on or before the 25th
	 */
	private OnType onType;

	/**
	 * The value associated with the above.
	 */
	private int on;

	/**
	 * Any value associated with the type e.g. the comparator value.
	 */
	private int onTypeValue;

	/**
	 * The time of day that the rule applies to or null for midnight. The time
	 * is expressed as seconds.
	 * <p>
	 * Patterns include:
	 * 
	 * <p>
	 * 2 time in hours
	 * <p>
	 * 2:00 time in hours and minutes
	 * <p>
	 * 15:00 24-hour format time (for times after noon)
	 * <p>
	 * 1:28:14 time in hours, minutes, and seconds
	 * 
	 * where hour 0 is midnight at the start of the day, and hour 24 is midnight
	 * at the end of the day.
	 */
	private int at;

	/**
	 * A qualifier for the rule at time expressing the type of time to be
	 * interpreted as.
	 */
	private TimeOfDayType atType;

	/**
	 * The amount of seconds to be added to local standard time when the rule is
	 * in effect.
	 */
	private int save;

	/**
	 * Gives the "variable part" (for example, the "S" or "D" in "EST" or "EDT")
	 * of time zone abbreviations to be used when this rule is in effect. If
	 * this field is -, the variable part is null.
	 */
	private String letters;

	public int getAt() {
		return at;
	}

	public TimeOfDayType getAtType() {
		return atType;
	}

	public int getFrom() {
		return from;
	}

	public YearValueType getFromType() {
		return fromType;
	}

	public int getIn() {
		return in;
	}

	public String getLetters() {
		return letters;
	}

	public String getName() {
		return name;
	}

	public int getOn() {
		return on;
	}

	public OnType getOnType() {
		return onType;
	}

	public int getOnTypeValue() {
		return onTypeValue;
	}

	public int getSave() {
		return save;
	}

	public int getTo() {
		return to;
	}

	public YearValueType getToType() {
		return toType;
	}

	public YearType getType() {
		return type;
	}

	public boolean isToTypeOnly() {
		return toTypeOnly;
	}

	/**
	 * Determine a value for the from field based on the value of other fields.
	 * 
	 * @return a value representing from.
	 */
	public int resolveFrom() {
		int resolvedFrom;
		switch (fromType) {
		case min:
			resolvedFrom = new GregorianCalendar().getMinimum(Calendar.YEAR);
			break;
		case max:
			resolvedFrom = new GregorianCalendar().getMaximum(Calendar.YEAR) - 1;
			break;
		default:
			resolvedFrom = from;
			break;
		}
		return resolvedFrom;
	}

	/**
	 * Determine a value for the to field based on the value of other fields.
	 * 
	 * @return A value representing to.
	 */
	public int resolveTo() {
		int resolvedTo;
		switch (toType) {
		case min:
			resolvedTo = new GregorianCalendar().getMinimum(Calendar.YEAR);
			break;
		case max:
			resolvedTo = new GregorianCalendar().getMaximum(Calendar.YEAR) - 1;
			break;
		default:
			if (!toTypeOnly) {
				resolvedTo = to;
			} else {
				resolvedTo = resolveFrom();
			}
			break;
		}
		return resolvedTo;
	}

	public void setAt(int at) {
		this.at = at;
	}

	public void setAtType(TimeOfDayType atType) {
		this.atType = atType;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public void setFromType(YearValueType fromType) {
		this.fromType = fromType;
	}

	public void setIn(int in) {
		this.in = in;
	}

	public void setLetters(String letters) {
		this.letters = letters;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOn(int on) {
		this.on = on;
	}

	public void setOnType(OnType onType) {
		this.onType = onType;
	}

	public void setOnTypeValue(int onTypeValue) {
		this.onTypeValue = onTypeValue;
	}

	public void setSave(int save) {
		this.save = save;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public void setToType(YearValueType toType) {
		this.toType = toType;
	}

	public void setToTypeOnly(boolean toTypeOnly) {
		this.toTypeOnly = toTypeOnly;
	}

	public void setType(YearType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Rule [name=" + name + ", fromType=" + fromType + ", from="
				+ from + ", in=" + in + ", onType=" + onType + ", on=" + on
				+ ", onTypeValue=" + onTypeValue + ", at=" + at + ", atType="
				+ atType + ", save=" + save + "]";
	}

	/**
	 * Determine if a year supplied is as specified as being required.
	 * 
	 * @param year
	 *            the year to test.
	 * @return true if it is.
	 */
	public boolean yearIsType(int year) {
		final int uspresTerm = 4;
		switch (type) {
		case even:
			return (year % 2 == 0);
		case odd:
			return (year % 2 > 0);
		case uspres:
			return (year % uspresTerm == 0);
		case nonpres:
		case nonuspres:
			return (year % uspresTerm > 0);
		default:
			return true;
		}
	}
}
