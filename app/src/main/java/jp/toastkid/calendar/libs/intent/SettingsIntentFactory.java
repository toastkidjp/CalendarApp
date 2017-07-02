package jp.toastkid.calendar.libs.intent;

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
     * @return {@link Intent}
     */
    public static Intent makeLaunch() {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_SETTINGS);
        return intent;
    }

    /**
     * Make launch Wi-Fi settings intent.
     * @return {@link Intent}
     */
    public static Intent wifi() {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_WIFI_SETTINGS);
        return intent;
    }

    /**
     * Make launch wireless settings intent.
     * @return {@link Intent}
     */
    public static Intent wireless() {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
        return intent;
    }

    /**
     * Make launch all apps settings intent.
     * @return {@link Intent}
     */
    public static Intent allApps() {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
        return intent;
    }
}
