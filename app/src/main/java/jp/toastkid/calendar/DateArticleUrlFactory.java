package jp.toastkid.calendar;

import android.support.annotation.NonNull;

import java.text.MessageFormat;

/**
 * @author toastkidjp
 */
class DateArticleUrlFactory {

    /** Wikipedia link format. */
    private static final String WIKIPEDIA_LINK_FORMAT = "https://ja.wikipedia.org/wiki/{0}月{1}日";

    /**
     *
     * @param month 0-11
     * @param date  1-31
     * @return
     */
    @NonNull
    static String make(final int month, final int date) {
        if (month < 0 || month >= 12) {
            return "";
        }
        if (date <= 0 || date >= 31) {
            return "";
        }
        return MessageFormat.format(WIKIPEDIA_LINK_FORMAT, (month + 1), date);
    }
}
