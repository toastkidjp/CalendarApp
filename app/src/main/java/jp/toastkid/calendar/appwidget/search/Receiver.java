package jp.toastkid.calendar.appwidget.search;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author toastkidjp
 */

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals("UPDATE_WIDGET")) {
            Provider.updateWidget(
                    context.getApplicationContext(),
                    RemoteViewsFactory.make(context)
            );
        }
    }
}