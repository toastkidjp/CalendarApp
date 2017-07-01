package jp.toastkid.calendar.calendar;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import jp.toastkid.calendar.BaseActivity;
import jp.toastkid.calendar.BuildConfig;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.databinding.ActivityMainBinding;
import jp.toastkid.calendar.libs.CustomTabsFactory;
import jp.toastkid.calendar.libs.ImageLoader;
import jp.toastkid.calendar.libs.IntentFactory;
import jp.toastkid.calendar.libs.SettingsIntentFactory;
import jp.toastkid.calendar.libs.Toaster;
import jp.toastkid.calendar.search.SearchActivity;
import jp.toastkid.calendar.settings.SettingsActivity;
import jp.toastkid.calendar.settings.background.BackgroundSettingActivity;
import jp.toastkid.calendar.settings.color.ColorSettingActivity;

/**
 * Main of this calendar app.
 *
 * @author toastkidjp
 */
public class MainActivity extends BaseActivity {

    /** Navigation's background. */
    private View navBackground;

    /** Data binding object. */
    private ActivityMainBinding binding;

    /** About this app. */
    private AboutThisApp aboutThisApp;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initToolbar(binding.appBarMain.toolbar);

        initDrawer(binding.appBarMain.toolbar);

        initNavigation();

        initCalendarView();
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
        LocaleRadioGroupInitializer.init(
                (RadioGroup) binding.navView.getMenu().findItem(R.id.nav_locale).getActionView()
                        .findViewById(R.id.locale)
        );

        binding.navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_gallery:
                    sendLog("nav_bg_set");
                    startActivity(BackgroundSettingActivity.makeIntent(this));
                    return true;
                case R.id.nav_reset_bg:
                    sendLog("nav_bg_reset");
                    removeBackgroundImagePath();
                    setBackgroundImage(null);
                    Toaster.snackShort(
                            binding.drawerLayout,
                            R.string.message_reset_bg_image,
                            colorPair()
                    );
                    return true;
                case R.id.nav_search:
                    sendLog("nav_search");
                    startActivity(SearchActivity.makeIntent(MainActivity.this));
                    return true;
                case R.id.nav_tweet:
                    sendLog("nav_twt");
                    IntentFactory.makeTwitter(
                            MainActivity.this,
                            colorPair(),
                            R.drawable.ic_back
                    ).launchUrl(MainActivity.this, Uri.parse("https://twitter.com/share"));
                    return true;
                case R.id.nav_settings:
                    sendLog("nav_set");
                    startActivity(SettingsActivity.makeIntent(MainActivity.this));
                    return true;
                case R.id.nav_color_settings:
                    sendLog("nav_color");
                    startActivity(ColorSettingActivity.makeIntent(MainActivity.this));
                    return true;
                case R.id.nav_settings_device:
                    sendLog("nav_dvc_set");
                    startActivity(SettingsIntentFactory.makeLaunch());
                    return true;
                case R.id.nav_settings_wifi:
                    sendLog("nav_wifi_set");
                    startActivity(SettingsIntentFactory.wifi());
                    return true;
                case R.id.nav_settings_wireless:
                    sendLog("nav_wrls_set");
                    startActivity(SettingsIntentFactory.wireless());
                    return true;
                case R.id.nav_settings_all_apps:
                    sendLog("nav_allapps_set");
                    startActivity(SettingsIntentFactory.allApps());
                    return true;
                case R.id.nav_share:
                    sendLog("nav_shr");
                    startActivity(IntentFactory.makeShare(makeShareMessage()));
                    return true;
                case R.id.nav_share_twitter:
                    sendLog("nav_shr_twt");
                    IntentFactory.makeTwitter(
                            MainActivity.this,
                            colorPair(),
                            R.drawable.ic_back
                    ).launchUrl(
                            MainActivity.this,
                            Uri.parse("https://twitter.com/share?text="
                                    + Uri.encode(makeShareMessage()))
                    );
                    return true;
                case R.id.nav_about_this_app:
                    if (aboutThisApp == null) {
                        aboutThisApp = new AboutThisApp(this, colorPair());
                    }
                    aboutThisApp.invoke();
                    return true;
                case R.id.nav_google_play:
                    startActivity(IntentFactory.googlePlay(BuildConfig.APPLICATION_ID));
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
                    final String dateTitle = DateTitleFactory.makeDateTitle(this, month, dayOfMonth);
                    new AlertDialog.Builder(this)
                            .setTitle(dateTitle)
                            .setItems(R.array.calendar_menu, (d, index) -> {
                                final Bundle bundle = new Bundle();
                                bundle.putString("date", dateTitle);
                                if (index == 0) {
                                    sendLog("cal_wkp", bundle);
                                    openCalendarArticle(month, dayOfMonth);
                                    return;
                                }
                                if (index == 1) {
                                    sendLog("cal_schdl", bundle);
                                    startActivity(IntentFactory.makeCalendar(view.getDate()));
                                    return;
                                }
                                if (index == 2) {
                                    sendLog("cal_srch", bundle);
                                    startActivity(SearchActivity.makeIntent(this, dateTitle));
                                }
                            })
                            .setCancelable(true)
                            .setOnCancelListener(v -> sendLog("cal_x"))
                            .setPositiveButton(R.string.close, (d, i) -> d.dismiss())
                            .show();
        });
    }

    /**
     * Open calendar wikipedia article.
     * @param month
     * @param dayOfMonth
     */
    private void openCalendarArticle(final int month, final int dayOfMonth) {
        final String url = DateArticleUrlFactory.make(this, month, dayOfMonth);
        if (url.length() == 0) {
            return;
        }
        CustomTabsFactory
                .make(this, colorPair(), R.drawable.ic_back)
                .build()
                .launchUrl(this, Uri.parse(url));
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
        applyColorToToolbar(binding.appBarMain.toolbar);

        applyBackgrounds();
    }

    /**
     * Apply background appearance.
     */
    private void applyBackgrounds() {
        final String backgroundImagePath = getBackgroundImagePath();
        final int fontColor = colorPair().fontColor();
        if (backgroundImagePath.length() == 0) {
            setBackgroundImage(null);
            ((TextView) navBackground.findViewById(R.id.nav_header_main))
                    .setTextColor(fontColor);
            return;
        }

        try {
            setBackgroundImage(
                    ImageLoader.readBitmapDrawable(
                            this,
                            Uri.parse(new File(backgroundImagePath).toURI().toString())
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
            Toaster.snackShort(
                    navBackground,
                    getString(R.string.message_failed_read_image),
                    colorPair()
            );
            removeBackgroundImagePath();
            setBackgroundImage(null);
        }
        ((TextView) navBackground.findViewById(R.id.nav_header_main)).setTextColor(fontColor);
    }

    /**
     * Set background image.
     * @param background nullable
     */
    private void setBackgroundImage(@Nullable final BitmapDrawable background) {
        ((ImageView) navBackground.findViewById(R.id.background)).setImageDrawable(background);
        binding.appBarMain.content.image.setImageDrawable(background);
        if (background == null) {
            navBackground.setBackgroundColor(colorPair().bgColor());
        }
    }

    /**
     * Make share message.
     * @return string
     */
    @NonNull
    private String makeShareMessage() {
        return MessageFormat.format(getString(R.string.message_share), getString(R.string.app_name));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (aboutThisApp != null) {
            aboutThisApp.dispose();
        }
    }

    @Override
    protected @StringRes int getTitleId() {
        return R.string.app_name;
    }

}
