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

import static android.os.Bundle.EMPTY;

/**
 * @author toastkidjp
 */
public abstract class BaseActivity extends AppCompatActivity {

    /** Firebase analytics log sender. */
    private FirebaseAnalytics sender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sender = FirebaseAnalytics.getInstance(this);
        sendLog("launch");
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
        if (item.getItemId() == R.id.settings_toolbar_menu_exit) {
            finish();
        }
        return true;
    }

    /**
     * Apply color to Toolbar.
     * @param toolbar Toolbar
     * @param bgColor Toolbar's background color
     * @param fontColor Toolbar's font color
     */
    protected void applyColorToToolbar(final Toolbar toolbar, final int bgColor, final int fontColor) {
        toolbar.setTitleTextColor(fontColor);
        toolbar.setBackgroundColor(bgColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ColorUtils.setAlphaComponent(bgColor, 255));
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

    protected abstract @StringRes int getTitleId();
}
