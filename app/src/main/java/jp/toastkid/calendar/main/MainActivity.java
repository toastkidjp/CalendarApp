package jp.toastkid.calendar.main;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import jp.toastkid.calendar.BaseActivity;
import jp.toastkid.calendar.BuildConfig;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.about.AboutThisAppActivity;
import jp.toastkid.calendar.advertisement.AdInitializers;
import jp.toastkid.calendar.appwidget.search.Updater;
import jp.toastkid.calendar.calendar.CalendarArticleLinker;
import jp.toastkid.calendar.calendar.CalendarFragment;
import jp.toastkid.calendar.calendar.alarm.DailyAlarm;
import jp.toastkid.calendar.databinding.ActivityMainBinding;
import jp.toastkid.calendar.launcher.LauncherActivity;
import jp.toastkid.calendar.libs.ImageLoader;
import jp.toastkid.calendar.libs.Toaster;
import jp.toastkid.calendar.libs.intent.CustomTabsFactory;
import jp.toastkid.calendar.libs.intent.IntentFactory;
import jp.toastkid.calendar.libs.intent.SettingsIntentFactory;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;
import jp.toastkid.calendar.search.SearchFragment;
import jp.toastkid.calendar.search.favorite.FavoriteSearchActivity;
import jp.toastkid.calendar.settings.background.BackgroundSettingActivity;
import jp.toastkid.calendar.settings.color.ColorSettingActivity;

/**
 * Main of this calendar app.
 *
 * @author toastkidjp
 */
public class MainActivity extends BaseActivity {

    /** Layout ID. */
    private static final int LAYOUT_ID = R.layout.activity_main;

    /** For using daily alarm. */
    private static final String KEY_EXTRA_MONTH = "month";

    /** For using daily alarm. */
    private static final String KEY_EXTRA_DOM = "dom";

    /** Navigation's background. */
    private View navBackground;

    /** Data binding object. */
    private ActivityMainBinding binding;

    /** Interstitial AD. */
    private InterstitialAd interstitialAd;
    private CalendarFragment calendarFragment;
    private SearchFragment searchFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT_ID);
        binding = DataBindingUtil.setContentView(this, LAYOUT_ID);

        initToolbar(binding.appBarMain.toolbar);

        initDrawer(binding.appBarMain.toolbar);

        initNavigation();

        calendarFragment = new CalendarFragment();
        replaceFragment(calendarFragment);

        initInterstitialAd();

        final Intent calledIntent = getIntent();
        if (calledIntent == null || !calledIntent.hasExtra(KEY_EXTRA_MONTH)) {
            return;
        }
        new CalendarArticleLinker(
                this,
                calledIntent.getIntExtra(KEY_EXTRA_MONTH, -1),
                calledIntent.getIntExtra(KEY_EXTRA_DOM,   -1)
        ).invoke();
    }

    private void replaceFragment(final Fragment fragment) {
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }

    private void initInterstitialAd() {
        if (interstitialAd == null) {
            interstitialAd = new InterstitialAd(getApplicationContext());
        }
        interstitialAd.setAdUnitId(getString(R.string.unit_id_interstitial));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Toaster.snackShort(
                        binding.appBarMain.toolbar,
                        R.string.thank_you_for_using,
                        colorPair()
                );
            }
        });
        AdInitializers.find(this).invoke(interstitialAd);
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
                this::restart,
                (RadioGroup) binding.navView.getMenu().findItem(R.id.nav_locale).getActionView()
                        .findViewById(R.id.locale)
        );

        final DailyAlarm dailyAlarm = new DailyAlarm(this);
        final boolean useDailyAlarm = getPreferenceApplier().doesUseDailyAlarm();
        if (useDailyAlarm) {
            dailyAlarm.reset();
        }
        final MenuItem alarmMenu = binding.navView.getMenu().findItem(R.id.nav_use_daily_alarm);
        final CheckBox checkBox = (CheckBox) alarmMenu.getActionView();
        checkBox.setChecked(useDailyAlarm);

        binding.navView.setNavigationItemSelectedListener(item -> {
            attemptToShowingAd();
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
                    if (searchFragment == null) {
                        searchFragment = new SearchFragment();
                    }
                    replaceFragment(searchFragment);
                    binding.drawerLayout.closeDrawers();
                    return true;
                case R.id.nav_calendar:
                    sendLog("nav_cal");
                    replaceFragment(calendarFragment);
                    binding.drawerLayout.closeDrawers();
                    return true;
                case R.id.nav_favorite_search:
                    sendLog("nav_fav_search");
                    startActivity(FavoriteSearchActivity.makeIntent(MainActivity.this));
                    return true;
                case R.id.nav_tweet:
                    sendLog("nav_twt");
                    IntentFactory.makeTwitter(
                            MainActivity.this,
                            colorPair(),
                            R.drawable.ic_back
                    ).launchUrl(MainActivity.this, Uri.parse("https://twitter.com/share"));
                    return true;
                case R.id.nav_launcher:
                    sendLog("nav_lnchr");
                    startActivity(LauncherActivity.makeIntent(this));
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
                case R.id.nav_settings_date_and_time:
                    sendLog("nav_dat");
                    startActivity(SettingsIntentFactory.dateAndTime());
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
                    sendLog("nav_about");
                    startActivity(AboutThisAppActivity.makeIntent(this));
                    return true;
                case R.id.nav_google_play:
                    sendLog("nav_gplay");
                    startActivity(IntentFactory.googlePlay(BuildConfig.APPLICATION_ID));
                    return true;
                case R.id.nav_privacy_policy:
                    sendLog("nav_prvcy_plcy");
                    CustomTabsFactory.make(this, colorPair(), R.drawable.ic_back)
                            .build()
                            .launchUrl(this, Uri.parse(getString(R.string.link_privacy_policy)));
                    return true;
                case R.id.nav_use_daily_alarm:
                    final boolean newState = !getPreferenceApplier().doesUseDailyAlarm();
                    checkBox.setChecked(newState);
                    if (newState) {
                        getPreferenceApplier().useDailyAlarm();
                        dailyAlarm.reset();
                        Toaster.snackShort(
                                binding.drawerLayout,
                                R.string.message_set_daily_alarm,
                                colorPair()
                        );
                        sendLog("nav_daily_set");
                    } else {
                        getPreferenceApplier().notUseDailyAlarm();
                        dailyAlarm.cancel();
                        Toaster.snackShort(
                                binding.drawerLayout,
                                R.string.message_clear_daily_alarm,
                                colorPair()
                        );
                        sendLog("nav_daily_cancel");
                    }

                    return true;
                case R.id.nav_clear_settings:
                    sendLog("nav_clr_set");
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.title_clear)
                            .setMessage(Html.fromHtml(getString(R.string.confirm_clear_all_settings)))
                            .setCancelable(true)
                            .setNegativeButton(R.string.cancel, (d, i) -> d.cancel())
                            .setPositiveButton(R.string.ok,     (d, i) -> {
                                clearPreferences();
                                refresh();
                                Updater.update(this);
                                Toaster.snackShort(binding.drawerLayout, R.string.done_clear, colorPair());
                            })
                            .show();
                    return true;
            }
            return true;
        });
        navBackground = binding.navView.getHeaderView(0).findViewById(R.id.nav_header_background);
    }

    /**
     * Restart this activity.
     */
    private void restart() {
        final Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
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
        refresh();
    }

    private void refresh() {
        applyColorToToolbar(binding.appBarMain.toolbar);

        applyBackgrounds();
    }

    private void attemptToShowingAd() {
        final PreferenceApplier preferenceApplier = getPreferenceApplier();
        if (interstitialAd.isLoaded() && preferenceApplier.allowShowingAd()) {
            Toaster.snackShort(
                    binding.appBarMain.toolbar,
                    R.string.message_please_view_ad,
                    colorPair()
            );
            interstitialAd.show();
            preferenceApplier.updateLastAd();
        }
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
        binding.appBarMain.image.setImageDrawable(background);
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
    protected @StringRes int getTitleId() {
        return R.string.app_name;
    }

    /**
     * Make launcher intent.
     * @param context
     * @return
     */
    public static Intent makeIntent(final Context context) {
        final Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    /**
     * Make launcher intent.
     * @param context
     * @return
     */
    public static Intent makeIntent(final Context context, final int month, final int dayOfMonth) {
        final Intent intent = makeIntent(context);
        intent.putExtra(KEY_EXTRA_MONTH, month);
        intent.putExtra(KEY_EXTRA_DOM,   dayOfMonth);
        return intent;
    }

}
