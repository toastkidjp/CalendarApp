package jp.toastkid.calendar.calendar.alarm;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Build;

import java.util.Calendar;

import jp.toastkid.calendar.libs.intent.PendingIntentFactory;

import static android.content.Context.ALARM_SERVICE;

/**
 * @author toastkidjp
 */
public class DailyAlarm {

    /** Context. */
    private Context context;

    /** Alarm manager. */
    private AlarmManager alarmManager;

    public DailyAlarm(final Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) this.context.getSystemService(ALARM_SERVICE);
    }

    private void set() {

        final long triggerAtMillis = calcTriggerAtMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC,
                    triggerAtMillis,
                    AlarmManager.INTERVAL_DAY,
                    PendingIntentFactory.daily(context)
            );
        } else {
            alarmManager.setRepeating(
                    AlarmManager.RTC,
                    triggerAtMillis,
                    AlarmManager.INTERVAL_DAY,
                    PendingIntentFactory.daily(context)
            );
        }
    }

    private long calcTriggerAtMillis() {
        final Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.HOUR) >= 8) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        calendar.set(Calendar.HOUR,   8);
        calendar.set(Calendar.MINUTE, 0);
        return calendar.getTimeInMillis();
    }

    public void reset() {
        cancel();
        set();
    }

    public void cancel() {
        alarmManager.cancel(PendingIntentFactory.daily(context));
    }
}
