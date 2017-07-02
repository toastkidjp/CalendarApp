package jp.toastkid.calendar;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;

import jp.toastkid.calendar.libs.preference.ColorPair;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;

/**
 * @author toastkidjp
 */
public abstract class BaseActivity extends AppCompatActivity {

    /** Firebase analytics log sender. */
    private FirebaseAnalytics sender;

    /** Preference Applier. */
    private PreferenceApplier preferenceApplier;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sender = FirebaseAnalytics.getInstance(this);
        sendLog("launch");
        preferenceApplier = new PreferenceApplier(this);
    }

    /**
     * Initialize Toolbar.
     * @param toolbar Toolbar
     */
    protected void initToolbar(final Toolbar toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setTitle(getTitleId());
        toolbar.inflateMenu(R.menu.settings_toolbar_menu);
        toolbar.setOnMenuItemClickListener(this::clickMenu);
    }

    protected boolean clickMenu(final MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.settings_toolbar_menu_exit) {
            finish();
            return true;
        }
        return true;
    }

    /**
     * Apply color to Toolbar.
     * @param toolbar Toolbar
     */
    protected void applyColorToToolbar(final Toolbar toolbar) {
        final ColorPair pair = preferenceApplier.colorPair();
        toolbar.setBackgroundColor(pair.bgColor());
        toolbar.setTitleTextColor(pair.fontColor());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ColorUtils.setAlphaComponent(pair.bgColor(), 255));
        }
    }

    /**
     * Send empty parameter log.
     * @param key
     */
    protected void sendLog(@NonNull final String key) {
        sendLog(key, Bundle.EMPTY);
    }

    /**
     * Send log.
     * @param key
     * @param bundle
     */
    protected void sendLog(@NonNull final String key, final Bundle bundle) {
        if (BuildConfig.DEBUG) {
            return;
        }
        sender.logEvent(key, bundle);
    }

    protected final ColorPair colorPair() {
        return preferenceApplier.colorPair();
    }

    protected final String getBackgroundImagePath() {
        return preferenceApplier.getBackgroundImagePath();
    }

    protected final void removeBackgroundImagePath() {
        preferenceApplier.removeBackgroundImagePath();
    }

    protected final void clearPreferences() {
        preferenceApplier.clear();
    }

    /**
     * FIXME: remove it
     * @return {@link PreferenceApplier}
     */
    protected final PreferenceApplier getPreferenceApplier() {
        return preferenceApplier;
    }

    protected abstract @StringRes int getTitleId();
}
