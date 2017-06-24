package jp.toastkid.calendar.libs;

import android.util.Log;

import jp.toastkid.calendar.BuildConfig;

/**
 * @author toastkidjp
 */
public class Logger {

    public static void i(final String s) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        Log.i("Logger", s);
    }
}
