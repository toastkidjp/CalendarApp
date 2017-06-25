package jp.toastkid.calendar;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import jp.toastkid.calendar.databinding.ActivityMainBinding;
import jp.toastkid.calendar.libs.CustomTabsFactory;
import jp.toastkid.calendar.libs.IntentFactory;
import jp.toastkid.calendar.libs.SettingsIntentFactory;
import jp.toastkid.calendar.libs.Toaster;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;
import jp.toastkid.calendar.search.SearchActivity;
import jp.toastkid.calendar.settings.SettingsActivity;
import jp.toastkid.calendar.settings.color.ColorSettingActivity;

/**
 * Main of this calendar app.
 *
 * @author toastkidjp
 */
public class MainActivity extends BaseActivity {

    /** Preference Applier. */
    private PreferenceApplier preferenceApplier;

    /** Navigation's background. */
    private View navBackground;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        preferenceApplier = new PreferenceApplier(this);

        initToolbar(binding.appBarMain.toolbar);

        initFab(preferenceApplier, binding.appBarMain.toolbar);

        initDrawer(binding.appBarMain.toolbar);

        initNavigation();

        initCalendarView();
    }

    /**
     * Initialize Floating Action Button.
     * @param preferenceApplier
     * @param toolbar
     */
    private void initFab(
            final PreferenceApplier preferenceApplier,
            final Toolbar toolbar
    ) {
        binding.appBarMain.fab.setOnClickListener(view -> Toaster.snackShort(
                toolbar,
                "Replace with your own action",
                preferenceApplier.getColor(),
                preferenceApplier.getFontColor()
        ));
    }

    /**
     * Initialize drawer.
     * @param toolbar
     */
    private void initDrawer(final Toolbar toolbar) {
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Initialize navigation.
     */
    private void initNavigation() {
        binding.navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_gallery:
                    startActivity(IntentFactory.makeImage());
                    return true;
                case R.id.nav_search:
                    startActivity(SearchActivity.makeIntent(MainActivity.this));
                    return true;
                case R.id.nav_settings:
                    startActivity(SettingsActivity.makeIntent(MainActivity.this));
                    return true;
                case R.id.nav_color_settings:
                    startActivity(ColorSettingActivity.makeIntent(MainActivity.this));
                    return true;
                case R.id.nav_settings_device:
                    startActivity(SettingsIntentFactory.makeLaunch());
                    return true;
                case R.id.nav_share:
                    startActivity(IntentFactory.makeShare(getString(R.string.message_share)));
                    return true;
                case R.id.nav_share_twitter:
                    IntentFactory.makeTwitter(
                            MainActivity.this,
                            preferenceApplier.getColor(),
                            preferenceApplier.getFontColor(),
                            R.drawable.ic_back
                    ).launchUrl(
                            MainActivity.this,
                            Uri.parse("https://twitter.com/share?text="
                                    + Uri.encode(getResources().getString(R.string.message_share)))
                    );
                    return true;
            }
            return true;
        });
        navBackground = binding.navView.getHeaderView(0).findViewById(R.id.nav_header_background);
    }

    /**
     * Initialize calendar view.
     */
    private void initCalendarView() {
        binding.appBarMain.content.calendar.setDate(System.currentTimeMillis());
        binding.appBarMain.content.calendar.setOnDateChangeListener(
                (view, year, month, dayOfMonth) -> {
            final String url = DateArticleUrlFactory.make(month, dayOfMonth);
            if (url.length() == 0) {
                return;
            }
            CustomTabsFactory.make(
                    this,
                    preferenceApplier.getColor(),
                    preferenceApplier.getFontColor(),
                    R.drawable.ic_back
            ).build().launchUrl(this, Uri.parse(url));
        });
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final int bgColor   = preferenceApplier.getColor();
        final int fontColor = preferenceApplier.getFontColor();
        applyColorToToolbar(binding.appBarMain.toolbar, bgColor, fontColor);
        navBackground.setBackgroundColor(bgColor);
        ((TextView) navBackground.findViewById(R.id.nav_header_main)).setTextColor(fontColor);
    }

    @Override
    protected @StringRes int getTitleId() {
        return R.string.app_name;
    }

}
