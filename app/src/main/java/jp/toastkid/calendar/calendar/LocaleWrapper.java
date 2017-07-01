package jp.toastkid.calendar.calendar;

import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;

/**
 * @author toastkidjp
 */
public class LocaleWrapper {

    private static final String JAPANESE = Locale.JAPAN.getLanguage();

    public static boolean isJa(final Configuration configuration) {
        return getLocale(configuration).equals(JAPANESE);
    }

    public static String getLocale(final Configuration configuration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            final LocaleList locales = configuration.getLocales();
            if (locales.isEmpty()) {
                return Locale.getDefault().getLanguage();
            }
            return locales.get(0).getLanguage();
        }
        return configuration.locale.getLanguage();
    }

    public static void setLocale(final Configuration configuration, final Locale newLocale) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(newLocale);
        }
    }
}
