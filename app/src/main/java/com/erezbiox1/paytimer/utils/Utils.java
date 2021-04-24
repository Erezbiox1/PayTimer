/*
 * Copyright (c) 2021. Erez Rotem, All rights reserved.
 */

package com.erezbiox1.paytimer.utils;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.erezbiox1.paytimer.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
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
     * Get the formatted total hours text
     * @param totalHours total shift hours
     * @param tip total tip given in the shift
     * @return the formatted total payout text
     */
    public static String getFormattedTotalHours(Context context, long totalHours, int tip){
        // Create a string builder to avoid concatenating strings
        StringBuilder builder = new StringBuilder();

        // Get the currency symbol ( changes per language )
        String symbol = context.getString(R.string.currency_symbol);

        // If the tip isn't 0, add it to the total hours
        if(tip != 0) {
            // Append the tip
            builder.append(tip);
            builder.append(symbol);

            // Append the separator
            builder.append(" + ");
        }

        // Append the total hours
        builder.append(String.format(Locale.getDefault(), "%02d:%02d", ((int) totalHours / 3600000), ((int) (totalHours % 3600000)/60000)));

        // Return the result
        return builder.toString();
    }

    /**
     * Get the formatted total payout text
     * @param totalPayout total shit's payout
     * @return the formatted total hours text
     */
    public static String getFormattedTotalPayout(Context context, double totalPayout){
        // Create a string builder to avoid concatenating strings
        StringBuilder builder = new StringBuilder();

        // Format the total payout
        String formattedPayout = new DecimalFormat("#,###.#").format(totalPayout);

        // If the formatted payout is bigger than 7 characters then use a slimmed version.
        if(formattedPayout.length() > 7)
            formattedPayout = new DecimalFormat("#,###").format(totalPayout);

        // Add the formatted total payout
        builder.append(formattedPayout);

        // Add the currency symbol
        builder.append(context.getString(R.string.currency_symbol));

        // Return the result
        return builder.toString();
    }

    /**
     * Represents a month and a year
     * (YearMonth is restricted to API level 26. sigh.)
     */
    public final static class Month implements Comparable<Month>, Parcelable {
        private final int year, month;

        public Month(int year, int month) {
            this.year = year;
            this.month = month;
        }

        protected Month(Parcel in) {
            year = in.readInt();
            month = in.readInt();
        }

        public static final Creator<Month> CREATOR = new Creator<Month>() {
            @Override
            public Month createFromParcel(Parcel in) {
                return new Month(in);
            }

            @Override
            public Month[] newArray(int size) {
                return new Month[size];
            }
        };

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(year);
            dest.writeInt(month);
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

        @NonNull
        public static Month getMonth(Long timeInMillis){
            Calendar time = Calendar.getInstance();
            time.setTimeInMillis(timeInMillis);
            return new Month(time.get(Calendar.YEAR), time.get(Calendar.MONTH));
        }
    }

}
