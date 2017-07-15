package jp.toastkid.calendar.calendar;

import android.content.Context;
import android.support.annotation.NonNull;

import java.text.MessageFormat;

import jp.toastkid.calendar.R;

/**
 * Factory of Wikipedia article's url.
 *
 * @author toastkidjp
 */
class DateArticleUrlFactory {

    /** Format resource ID. */
    private static final int FORMAT_ID = R.string.format_date_link;

    /**
     * Make Wikipedia article's url.
     * @param context context
     * @param month 0-11
     * @param dayOfMonth 1-31
     * @return Wikipedia article's url
     */
    @NonNull
    static String make(@NonNull final Context context, final int month, final int dayOfMonth) {
        if (month < 0 || month >= 12) {
            return "";
        }
        if (dayOfMonth <= 0 || dayOfMonth >= 31) {
            return "";
        }
        if (LocaleWrapper.isJa(context.getResources().getConfiguration())) {
            return MessageFormat.format(context.getString(FORMAT_ID), month + 1, dayOfMonth);
        }
        return MessageFormat.format(context.getString(FORMAT_ID), Month.get(month), dayOfMonth);
    }
}
