package jp.toastkid.calendar.appwidget.search;

import android.content.ContextWrapper;
import android.content.Intent;

/**
 * App-Widget updater.
 *
 * @author toastkidjp
 */
public class Updater {

    /** Update app-widget intent. */
    private static final Intent INTENT_UPDATE_WIDGET = new Intent("UPDATE_WIDGET");

    /**
     * Do update app-widget.
     *
     * @param wrapper {@link ContextWrapper}
     */
    public static void update(final ContextWrapper wrapper) {
        wrapper.sendBroadcast(INTENT_UPDATE_WIDGET);
    }
}
