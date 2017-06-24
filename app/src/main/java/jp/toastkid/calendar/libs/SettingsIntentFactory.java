package jp.toastkid.calendar.libs;

import android.content.Intent;
import android.provider.Settings;

/**
 * Settings intent factory.
 *
 * @author toastkidjp
 */
public class SettingsIntentFactory {

    /**
     * Make launch settings intent.
     * @return
     */
    public static Intent makeLaunch() {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_SETTINGS);
        return intent;
    }
}
