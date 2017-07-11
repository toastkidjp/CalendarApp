package jp.toastkid.calendar.calendar.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * For using reset daily alarm.
 * @author toastkidjp
 */
public class TimeChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        new DailyAlarm(context).reset();
    }
}
