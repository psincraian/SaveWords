package com.psincraian.savewords;

import android.content.ContentValues;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class Utility {
    private static final String LOG_TAG = Utility.class.getSimpleName();
    private final static String DATE_FORMAT = "dd-MM-yyyy";


    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        GregorianCalendar time = new GregorianCalendar();
        time.setTimeInMillis(startDate);
        time.clear(Calendar.HOUR);
        time.clear(Calendar.MINUTE);
        time.clear(Calendar.SECOND);
        time.clear(Calendar.MILLISECOND);

        return time.getTimeInMillis();
    }

    public static String getNiceDayFormat(long time, Context context){
        String date = null;
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(time);
        GregorianCalendar now = new GregorianCalendar();
        now.setTimeInMillis(normalizeDate(now.getTimeInMillis()));

        if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
            if (calendar.get(Calendar.DATE) == now.get(Calendar.DATE))
                date = context.getString(R.string.today);
            else {
                now.add(Calendar.DATE, -1);
                if (calendar.get(Calendar.DATE) == now.get(Calendar.DATE))
                    date = context.getString(R.string.yesterday);
            }
        }

        if (date != null)
            return date;
        else
            return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
                    .format(calendar.getTimeInMillis());
    }


    // return true if word is a valid word or translation
    public static boolean isValidWord(String word){
        return word != null &&
                !word.isEmpty() &&
                !word.replaceAll("\\s", "").isEmpty();
    }

    public static String normalizeWord(String word){
        word = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
        return word.trim().replaceAll(" +", " ");
    }

}
