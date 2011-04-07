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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import com.classactionpl.tz.Rule.OnType;
import com.classactionpl.tz.Rule.TimeOfDayType;

/**
 * A single instance of detail for a zone.
 * 
 * @author huntc
 * 
 */
public class ZoneDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Cache the time zone representing UTC for greater efficiency.
	 */
	private static final TimeZone UTC_TZ = TimeZone.getTimeZone("GMT");

	/**
	 * The amount of time to add to UTC to get standard time in this zone. This
	 * field has the same format as the AT and SAVE fields of rule lines; begin
	 * the field with a minus sign if time must be subtracted from UTC.
	 */
	private int utcOffset;

	/**
	 * The name of the rule(s) that apply in the time zone or, alternately, an
	 * amount of time to add to local standard time. If this field is null then
	 * standard time always applies in the time zone. Rules are expected in an
	 * ascending order of processing i.e. earliest rule first.
	 */
	private List<Rule> rules;

	/**
	 * If rules are null then a DST offset can be specified instead.
	 */
	private int save;

	/**
	 * The format for time zone abbreviations in this time zone. The pair of
	 * characters %s is used to show where the "variable part" of the time zone
	 * abbreviation goes.
	 */
	private String format;

	/**
	 * The daylight savings time format if available or null if not.
	 */
	private String dstFormat;

	/**
	 * The time at which the UTC offset or the rule(s) change for a location. If
	 * this is not null, the time zone information is generated from the given
	 * UTC offset and rule change until the time specified. The month, day, and
	 * time of day have the same format as the IN, ON, and AT fields of a rule;
	 * trailing fields can be omitted, and default to the earliest possible
	 * value for the missing fields
	 */
	private Integer until;

	/**
	 * The calendar month the rule applies from or null if midnight January UTC
	 * is to be assumed.
	 */
	private Integer untilIn;

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
	private OnType untilOnType;

	/**
	 * The value associated with the above.
	 */
	private Integer untilOn;

	/**
	 * Any value associated with the type e.g. the comparator value.
	 */
	private int untilOnTypeValue;

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
	private Integer untilAt;

	/**
	 * A qualifier for the rule at time expressing the type of time to be
	 * interpreted as.
	 */
	private TimeOfDayType untilAtType;

	public String getDstFormat() {
		return dstFormat;
	}

	public String getFormat() {
		return format;
	}

	public List<Rule> getRules() {
		return rules;
	}

	public int getSave() {
		return save;
	}

	public Integer getUntil() {
		return until;
	}

	public Integer getUntilAt() {
		return untilAt;
	}

	public TimeOfDayType getUntilAtType() {
		return untilAtType;
	}

	public Integer getUntilIn() {
		return untilIn;
	}

	public Integer getUntilOn() {
		return untilOn;
	}

	public OnType getUntilOnType() {
		return untilOnType;
	}

	public int getUntilOnTypeValue() {
		return untilOnTypeValue;
	}

	public int getUtcOffset() {
		return utcOffset;
	}

	/**
	 * Go through our rules and determine which rule falls upon the time
	 * supplied.
	 * 
	 * @param when
	 *            the time to use.
	 * @return the rule found or null if there is none.
	 */
	public Rule resolveRule(Date when) {
		Calendar whenCal = new GregorianCalendar(UTC_TZ);
		whenCal.setTime(when);

		int whenYear = whenCal.get(Calendar.YEAR);
		int whenFromYear;
		if (whenYear > whenCal.getMinimum(Calendar.YEAR)) {
			whenFromYear = whenYear - 1;
		} else {
			whenFromYear = whenYear;
		}

		SortedMap<Date, Rule> resolvedRules = resolveRules(whenFromYear,
				whenYear, when);
		Rule activeRule = resolvedRules.get(whenCal.getTime());
		if (activeRule == null) {
			SortedMap<Date, Rule> matchedRules = resolvedRules.headMap(when);
			if (matchedRules.size() > 0) {
				activeRule = resolvedRules.get(matchedRules.lastKey());
			}
		}

		return activeRule;
	}

	/**
	 * Given a start and end year (inclusive), return a sorted map of dates of
	 * when rules start and associate them with the rule.
	 * 
	 * @param fromYear
	 *            from year inclusive.
	 * @param toYear
	 *            to year inclusive.
	 * @param limit
	 *            any rule date expiring later than this date will be ignored.
	 *            Also rules starting after this time are ignored.
	 * @return a set of dates mapping to rules. The dates will correspond to
	 *         resolved from values for rules between the from and to years.
	 */
	public SortedMap<Date, Rule> resolveRules(int fromYear, int toYear,
			Date limit) {

		SortedMap<Date, Rule> resolvedRules = new TreeMap<Date, Rule>();
		int rulesSize = rules.size();
		if (rulesSize > 0) {
			for (int year = fromYear; year <= toYear; ++year) {
				for (int i = rulesSize - 1; i >= 0; --i) {
					Rule rule = rules.get(i);
					if (rule.yearIsType(year)) {
						int resolvedTo = rule.resolveTo();
						Date resolvedToDate = Rule.resolveInOnAt(resolvedTo,
								rule.getIn(), rule.getOnType(), rule.getOn(),
								rule.getOnTypeValue(), rule.getAt(),
								rule.getAtType(), utcOffset, rule.getSave());
						int compare = resolvedToDate.compareTo(limit);
						if (compare <= 0) {
							break;
						}

						int resolvedFrom = rule.resolveFrom();
						Date resolvedFromDate = Rule.resolveInOnAt(
								resolvedFrom, rule.getIn(), rule.getOnType(),
								rule.getOn(), rule.getOnTypeValue(),
								rule.getAt(), rule.getAtType(), utcOffset,
								rule.getSave());

						compare = resolvedFromDate.compareTo(limit);

						if (compare <= 0) {
							// We have a candidate rule so let us now see if it
							// applies when put in terms of the year associated
							// with the year we're interested in.

							Date normalisedResolvedFromDate = Rule
									.resolveInOnAt(year, rule.getIn(),
											rule.getOnType(), rule.getOn(),
											rule.getOnTypeValue(),
											rule.getAt(), rule.getAtType(),
											utcOffset, rule.getSave());

							resolvedRules.put(normalisedResolvedFromDate, rule);
						}
					}
				}
			}
		}
		return resolvedRules;
	}

	/**
	 * Resolve the until value.
	 * 
	 * @param when
	 *            the date to use when rules are to be resolved.
	 * @return the date determined from the until values.
	 */
	public Date resolveUntil(Date when) {
		Date resolvedUntil;
		if (until == null) {
			resolvedUntil = new Date(Long.MAX_VALUE);
		} else {
			int resolvedSave;
			if (rules == null) {
				resolvedSave = save;
			} else {
				Rule resolvedRule = resolveRule(when);
				if (resolvedRule != null) {
					resolvedSave = resolvedRule.getSave();
				} else {
					resolvedSave = 0;
				}
			}
			resolvedUntil = Rule.resolveInOnAt(until, untilIn, untilOnType,
					untilOn, untilOnTypeValue, untilAt, untilAtType, utcOffset,
					resolvedSave);
		}
		return resolvedUntil;
	}

	public void setDstFormat(String dstFormat) {
		this.dstFormat = dstFormat;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * Assign a collection of rules.
	 * 
	 * @param rules
	 *            the rules.
	 */
	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

	public void setSave(int save) {
		this.save = save;
	}

	public void setUntil(Integer until) {
		this.until = until;
	}

	public void setUntilAt(Integer untilAt) {
		this.untilAt = untilAt;
	}

	public void setUntilAtType(TimeOfDayType untilAtType) {
		this.untilAtType = untilAtType;
	}

	public void setUntilIn(Integer untilIn) {
		this.untilIn = untilIn;
	}

	public void setUntilOn(Integer untilOn) {
		this.untilOn = untilOn;
	}

	public void setUntilOnType(OnType untilOnType) {
		this.untilOnType = untilOnType;
	}

	public void setUntilOnTypeValue(int untilOnTypeValue) {
		this.untilOnTypeValue = untilOnTypeValue;
	}

	public void setUtcOffset(int utcOffset) {
		this.utcOffset = utcOffset;
	}

	@Override
	public String toString() {
		return "ZoneDetail [until=" + until + ", untilIn=" + untilIn
				+ ", untilOnType=" + untilOnType + ", untilOn=" + untilOn
				+ ", untilOnTypeValue=" + untilOnTypeValue + ", untilAt="
				+ untilAt + ", untilAtType=" + untilAtType + ",save=" + save
				+ "]";
	}
}
