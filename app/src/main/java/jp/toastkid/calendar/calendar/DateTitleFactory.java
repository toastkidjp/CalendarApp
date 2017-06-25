package jp.toastkid.calendar.calendar;

import android.content.Context;

import java.text.MessageFormat;
import java.util.Locale;

import jp.toastkid.calendar.R;

/**
 * Date title factory.
 *
 * @author toastkidjp
 */
class DateTitleFactory {

    /** Format resource ID. */
    private static final int FORMAT_ID = R.string.format_date_title;

    /**
     * Make date title.
     * @param context context
     * @param month 0-11
     * @param dayOfMonth 1-31
     * @return date title
     */
    static String makeDateTitle(final Context context, final int month, final int dayOfMonth) {
        if (month < 0 || month >= 12) {
            return "";
        }
        if (dayOfMonth <= 0 || dayOfMonth >= 31) {
            return "";
        }
        if (Locale.getDefault().getLanguage().equals(Locale.JAPAN.getLanguage())) {
            return MessageFormat.format(context.getString(FORMAT_ID), month + 1, dayOfMonth);
        }
        return MessageFormat.format(context.getString(FORMAT_ID), Month.get(month), dayOfMonth);
    }
}
