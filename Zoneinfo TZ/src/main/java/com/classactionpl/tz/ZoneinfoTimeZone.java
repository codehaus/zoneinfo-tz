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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A concrete implementation of TimeZone that provides zoneinfo capabilities.
 * This class is entirely thread safe and the internal zone structure can be
 * mutated at any time.
 * 
 * @author huntc
 * 
 */
public class ZoneinfoTimeZone extends TimeZone {

	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * Cache the time zone representing UTC for greater efficiency.
	 */
	private static final TimeZone UTC_TZ = TimeZone.getTimeZone("GMT");

	/**
	 * The default number of time zones expecting to be handled.
	 */
	private static final int ZONES_ALLOCATION = 500;

	/** */
	private static final int MILLIS_PER_SECOND = 1000;

	/**
	 * A map of time zone names to time zones that are available.
	 */
	private static final Map<String, AbstractZone> ZONES = new ConcurrentHashMap<String, AbstractZone>(
			ZONES_ALLOCATION);

	/**
	 * Get the available ids in a thread safe manner.
	 * 
	 * @return {@inheritDoc}
	 */
	public static String[] getAvailableIDs() {
		Set<String> keySet = ZONES.keySet();
		String[] ids = new String[keySet.size()];
		keySet.toArray(ids);
		return ids;
	}

	/**
	 * Get the available ids given an offset, in a thread safe manner.
	 * 
	 * @param rawOffset
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	public static String[] getAvailableIDs(int rawOffset) {
		int rawOffsetSeconds = rawOffset / MILLIS_PER_SECOND;
		Collection<String> availableIds = new ArrayList<String>(ZONES.size());
		Date when = new Date();
		for (AbstractZone zone : ZONES.values()) {
			ZoneDetail zoneDetail = zone.resolveDetail(when);
			if (zoneDetail != null
					&& zoneDetail.getUtcOffset() == rawOffsetSeconds) {
				availableIds.add(zone.getName());
			}
		}
		String[] availableIdsArray = new String[availableIds.size()];
		return availableIds.toArray(availableIdsArray);
	}

	/**
	 * Get a time zone in a thread safe manner.
	 * 
	 * @param id
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	public static TimeZone getTimeZone(String id) {
		ZoneinfoTimeZone timeZone = null;

		AbstractZone zone = ZONES.get(id);
		if (zone != null) {
			timeZone = new ZoneinfoTimeZone();
			timeZone.setZone(zone);
		}

		return timeZone;
	}

	/**
	 * Get all of the zone objects.
	 * 
	 * @return the zone objects.
	 */
	public static Map<String, AbstractZone> getZones() {
		return ZONES;
	}

	/**
	 * The associated zoneinfo object.
	 */
	private AbstractZone zone;

	@Override
	public Object clone() {
		ZoneinfoTimeZone timeZone = new ZoneinfoTimeZone();
		timeZone.setZone(zone);
		return timeZone;
	}

	@Override
	public String getDisplayName(boolean daylight, int style, Locale locale) {
		// This method always returns a short name as there is no concept of a
		// long name in Zoneinfo. Locale isn't considered in the Zoneinfo
		// specification either.
		String displayName;

		Date when = new Date();
		ZoneDetail zoneDetail = zone.resolveDetail(when);
		assert (zoneDetail != null);

		if (daylight) {
			displayName = zoneDetail.getDstFormat();
		} else {
			displayName = null;
		}

		if (displayName == null) {
			displayName = zoneDetail.getFormat();
		}

		Rule rule = resolveRule(zoneDetail, when, daylight);

		if (rule != null) {
			String letters = rule.getLetters();
			if (letters == null) {
				letters = "";
			}
			displayName = String.format(displayName, letters);
		}

		return displayName;
	}

	@Override
	public int getDSTSavings() {
		return getDSTSavings(new Date());
	}

	/**
	 * Return the DST savings for a given point in time.
	 * 
	 * @param when
	 *            the time.
	 * @return the savings in milliseconds.
	 */
	public int getDSTSavings(Date when) {
		ZoneDetail zoneDetail = zone.resolveDetail(when);
		assert (zoneDetail != null);

		Rule rule = resolveRule(zoneDetail, when, true);

		int save;
		if (rule != null) {
			save = rule.getSave() * MILLIS_PER_SECOND;
		} else {
			save = 0;
		}

		return save;
	}

	@Override
	public String getID() {
		return zone.getName();
	}

	@Override
	public int getOffset(int era, int year, int month, int day, int dayOfWeek,
			int milliseconds) {
		Calendar when = new GregorianCalendar(UTC_TZ);
		when.set(Calendar.ERA, era);
		when.set(Calendar.YEAR, year);
		when.set(Calendar.MONTH, month);
		when.set(Calendar.DAY_OF_MONTH, day);
		// when.set(Calendar.DAY_OF_WEEK, dayOfWeek); // redundant and confuses
		// the calendar object.
		when.set(Calendar.HOUR_OF_DAY, 0);
		when.set(Calendar.MINUTE, 0);
		when.set(Calendar.SECOND, 0);
		when.set(Calendar.MILLISECOND, milliseconds);
		return getOffset(when.getTimeInMillis());
	}

	@Override
	public int getOffset(long date) {
		return zone.resolveUtcOffset(new Date(date)) * MILLIS_PER_SECOND;
	}

	@Override
	public int getRawOffset() {
		Date when = new Date();
		ZoneDetail zoneDetail = zone.resolveDetail(when);
		assert (zoneDetail != null);

		return zoneDetail.getUtcOffset() * MILLIS_PER_SECOND;
	}

	public AbstractZone getZone() {
		return zone;
	}

	@Override
	public boolean hasSameRules(TimeZone other) {
		// In order to keep things simple, and also questioning the utility of
		// this method, we only potentially return true if both zones point to
		// the same rule object.
		boolean sameRules;

		if (other instanceof ZoneinfoTimeZone) {
			Date when = new Date();

			ZoneDetail zoneDetail1 = zone.resolveDetail(when);
			assert (zoneDetail1 != null);
			ZoneDetail zoneDetail2 = ((ZoneinfoTimeZone) other).getZone()
					.resolveDetail(when);
			assert (zoneDetail2 != null);

			Rule rule1 = zoneDetail1.resolveRule(when);
			Rule rule2 = zoneDetail2.resolveRule(when);

			sameRules = (rule1 == rule2);

		} else {
			sameRules = false;
		}

		return sameRules;
	}

	@Override
	public boolean inDaylightTime(Date when) {
		return (getDSTSavings(when) != 0);
	}

	/**
	 * Determine a rule that matches dst criteria.
	 * 
	 * @param zoneDetail
	 *            the detail to use.
	 * @param when
	 *            the time to use.
	 * @param daylight
	 *            whether or not the rule should comply with DST.
	 * @return the rule found or null if none found.
	 */
	private Rule resolveRule(ZoneDetail zoneDetail, Date when, boolean daylight) {
		Rule resolvedRule = null;

		List<Rule> rules = zoneDetail.getRules();
		if (rules != null) {
			Rule rule = zoneDetail.resolveRule(when);
			if (rule != null) {
				if ((daylight && rule.getSave() != 0)
						|| (!daylight && rule.getSave() == 0)) {
					resolvedRule = rule;
				} else {
					// The rule we have doesn't match the request to return
					// format details given the when date. We therefore need to
					// track back and find a rule adjacent to the resolved rule
					// that does match the criteria.
					boolean rulePassed = false;
					int rulesSize = rules.size();
					if (rulesSize > 0) {
						for (int i = rulesSize - 1; i >= 0; --i) {
							Rule nextRule = rules.get(i);
							if (nextRule.equals(rule)) {
								rulePassed = true;
							} else if ((daylight && nextRule.getSave() != 0)
									|| (!daylight && nextRule.getSave() == 0)) {
								resolvedRule = nextRule;
							}

							if (rulePassed && resolvedRule != null) {
								break;
							}
						}
					}
				}
			}
		} else {
			resolvedRule = null;
		}
		return resolvedRule;
	}

	/**
	 * Makes no sense in this class. Call setZone() instead.
	 * 
	 * @param offsetMillis
	 *            Meaningless.
	 */
	@Override
	public void setRawOffset(int offsetMillis) {
	}

	public void setZone(AbstractZone zone) {
		this.zone = zone;
	}

	@Override
	public boolean useDaylightTime() {
		return (getDSTSavings(new Date()) != 0);
	}

}
