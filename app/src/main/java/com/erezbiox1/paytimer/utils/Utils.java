/*
 * Copyright (c) 2021. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class Utils {


    /**
     * Parses a long time in the chosen pattern
     * @param pattern time pattern used to update the text view
     * @param time time in epoch format to update the text view
     * @param timeZone time zone to parse the time in.
     */
    public static String format(String pattern, long time, String timeZone){
        // Create a simple date format with the supplied pattern ( and default locale. )
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());

        // Set the time zone to the specified time zone ( if null use the default time zone )
        dateFormat.setTimeZone(timeZone == null ? TimeZone.getDefault() : TimeZone.getTimeZone(timeZone));

        // Format the time and return the formatted time.
        return dateFormat.format(time);
    }

    /**
     * Represents a month and a year
     * (YearMonth is restricted to API level 26. sigh.)
     */
    public final static class Month implements Comparable<Month> {
        private final int year, month;

        public Month(int year, int month) {
            this.year = year;
            this.month = month;
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        @NonNull
        public static Month getMonth(Long timeInMillis){
            Calendar time = Calendar.getInstance();
            time.setTimeInMillis(timeInMillis);
            return new Month(time.get(Calendar.YEAR), time.get(Calendar.MONTH));
        }

        @NonNull
        @Override
        public String toString() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, 1);
            return format("MMMM yyyy", calendar.getTimeInMillis(), null);
        }

        @Override
        public int hashCode() {
            return Objects.hash(year, month);
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (o == this)
                return true;

            if (!(o instanceof Month))
                return false;

            Month other = (Month) o;
            return other.year == this.year && other.month == this.month;
        }

        @Override
        public int compareTo(Month o) {
            return (o.year*12 + o.month) - (this.year*12 + this.month);
        }
    }

}
