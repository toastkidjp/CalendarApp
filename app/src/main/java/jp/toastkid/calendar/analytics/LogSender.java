package jp.toastkid.calendar.analytics;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;

import jp.toastkid.calendar.BuildConfig;

/**
 * Analytics logger wrapper.
 *
 * @author toastkidjp
 */
public class LogSender {

    /** Firebase analytics log sender. */
    private FirebaseAnalytics sender;

    /**
     * Initialize with {@link Context}.
     * @param context
     */
    public LogSender(final Context context) {
        sender = FirebaseAnalytics.getInstance(context);
    }

    /**
     * Send empty parameter log.
     * @param key
     */
    public void send(@NonNull final String key) {
        send(key, Bundle.EMPTY);
    }

    /**
     * Send log.
     * @param key
     * @param bundle
     */
    public void send(@NonNull final String key, final Bundle bundle) {
        if (BuildConfig.DEBUG) {
            return;
        }
        sender.logEvent(key, bundle);
    }

}
