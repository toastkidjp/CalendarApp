package jp.toastkid.calendar.calendar;

import java.util.Calendar;

/**
 * For using convert {@link java.util.Calendar}'s month to String.
 *
 * @author toastkidjp
 */
class Month {

    /**
     * Return month string form.
     * @param month 1-11
     * @return string form
     */
    static String get(final int month) {
        switch (month) {
            case Calendar.JANUARY:
                return "January";
            case Calendar.FEBRUARY:
                return "February";
            case Calendar.MARCH:
                return "March";
            case Calendar.APRIL:
                return "April";
            case Calendar.MAY:
                return "May";
            case Calendar.JUNE:
                return "June";
            case Calendar.JULY:
                return "July";
            case Calendar.AUGUST:
                return "August";
            case Calendar.SEPTEMBER:
                return "September";
            case Calendar.OCTOBER:
                return "October";
            case Calendar.NOVEMBER:
                return "November";
            case Calendar.DECEMBER:
                return "December";
        }
        return "";
    }
}
