package jp.toastkid.calendar.calendar.alarm;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.Calendar;

import jp.toastkid.calendar.R;
import jp.toastkid.calendar.calendar.DateTitleFactory;
import jp.toastkid.calendar.libs.intent.PendingIntentFactory;

/**
 * @author toastkidjp
 */
public class DailyCalendarReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        showNotification(context);
    }

    /**
     * Invole notification launching.
     * @param context
     */
    public static void showNotification(final Context context) {
        final NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(1, makeNotification(context));
    }

    /**
     * Make notification.
     * @param context
     * @return
     */
    private static Notification makeNotification(final Context context) {
        final Calendar cal = Calendar.getInstance();
        final int month = cal.get(Calendar.MONTH);
        final int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        final String title = DateTitleFactory.makeDateTitle(context, month, dayOfMonth);

        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_calendar)
                .setContentTitle(title)
                .setSubText(context.getString(R.string.subtext_notification))
                .setWhen(cal.getTimeInMillis())
                .setTicker(title)
                .setContentIntent(PendingIntentFactory.calendar(context, month, dayOfMonth))
                .build();
    }
}
