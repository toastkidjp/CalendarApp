package jp.toastkid.calendar.libs.intent;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import jp.toastkid.calendar.calendar.alarm.DailyCalendarReceiver;
import jp.toastkid.calendar.calendar.MainActivity;
import jp.toastkid.calendar.search.SearchActivity;
import jp.toastkid.calendar.settings.color.ColorSettingActivity;

/**
 * Factory of {@link PendingIntent}.
 *
 * @author toastkidjp
 */
public class PendingIntentFactory {

    /**
     * Make launch search intent.
     * @param context
     * @return {@link SearchActivity}'s pending intent
     */
    public static PendingIntent makeSearchIntent(final Context context) {
        final Intent intent = SearchActivity.makeIntent(context);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Make launch color settings intent.
     * @param context
     * @return {@link ColorSettingActivity}'s pending intent
     */
    public static PendingIntent makeColorSettingsIntent(final Context context) {
        final Intent intent = ColorSettingActivity.makeIntent(context);
        return PendingIntent.getActivity(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Make launch main activity.
     * @param context
     * @return
     */
    public static PendingIntent main(final Context context) {
        return PendingIntent.getActivity(
                context,
                3,
                MainActivity.makeIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    /**
     * Daily notification intent.
     * @param context
     * @return
     */
    public static PendingIntent daily(final Context context) {
        return PendingIntent.getBroadcast(
                context,
                4,
                new Intent(context, DailyCalendarReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    /**
     * Make calling calendar intent.
     * @param context
     * @param month
     * @param dayOfMonth
     * @return
     */
    public static PendingIntent calendar(
            final Context context, final int month, final int dayOfMonth) {
        return PendingIntent.getActivity(
                context,
                5,
                MainActivity.makeIntent(context, month, dayOfMonth),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }
}
