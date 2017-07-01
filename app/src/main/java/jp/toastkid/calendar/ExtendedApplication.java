package jp.toastkid.calendar;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import jp.toastkid.calendar.libs.preference.PreferenceApplier;
import jp.toastkid.calendar.settings.color.SavedColors;

/**
 * For using LeakCanary and so on...
 *
 * @author toastkidjp
 */
public class ExtendedApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        if (new PreferenceApplier(this).isFirstLaunch()) {
            SavedColors.insertDefaultColors(this);
        }
    }

}
