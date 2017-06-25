package jp.toastkid.calendar.calendar;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

import jp.toastkid.calendar.BaseActivity;
import jp.toastkid.calendar.R;
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

    /** Request code. */
    private static final int IMAGE_READ_REQUEST = 136;

    /** Preference Applier. */
    private PreferenceApplier preferenceApplier;

    /** Navigation's background. */
    private View navBackground;

    /** Data binding object. */
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        preferenceApplier = new PreferenceApplier(this);

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
        binding.navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_gallery:
                    sendLog("nav_set_img");
                    startActivityForResult(IntentFactory.makePickImage(), IMAGE_READ_REQUEST);
                    return true;
                case R.id.nav_gallery_clear:
                    sendLog("nav_rm_img");
                    removeSetImage();
                    return true;
                case R.id.nav_search:
                    sendLog("nav_search");
                    startActivity(SearchActivity.makeIntent(MainActivity.this));
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
                case R.id.nav_share:
                    sendLog("nav_shr");
                    startActivity(IntentFactory.makeShare(makeShareMessage()));
                    return true;
                case R.id.nav_share_twitter:
                    sendLog("nav_twttr");
                    IntentFactory.makeTwitter(
                            MainActivity.this,
                            preferenceApplier.getColor(),
                            preferenceApplier.getFontColor(),
                            R.drawable.ic_back
                    ).launchUrl(
                            MainActivity.this,
                            Uri.parse("https://twitter.com/share?text="
                                    + Uri.encode(makeShareMessage()))
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
        CustomTabsFactory.make(
                this,
                preferenceApplier.getColor(),
                preferenceApplier.getFontColor(),
                R.drawable.ic_back
        ).build().launchUrl(this, Uri.parse(url));
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

        applyBackgrounds(bgColor, fontColor);
    }

    /**
     * Apply background appearance.
     * @param bgColor
     * @param fontColor
     */
    private void applyBackgrounds(@ColorInt final int bgColor, @ColorInt final int fontColor) {
        final String backgroundImagePath = preferenceApplier.getBackgroundImagePath();
        if (backgroundImagePath.length() == 0) {
            setBackgroundImage(null);
            navBackground.setBackgroundColor(bgColor);
            ((TextView) navBackground.findViewById(R.id.nav_header_main)).setTextColor(fontColor);
            return;
        }

        try {
            setBackgroundImage(readBitmapDrawable(
                    Uri.parse(new File(backgroundImagePath).toURI().toString())));
        } catch (IOException e) {
            e.printStackTrace();
            Toaster.snackShort(
                    navBackground,
                    getString(R.string.message_failed_read_image),
                    bgColor,
                    fontColor
            );
            navBackground.setBackgroundColor(bgColor);
        }
        ((TextView) navBackground.findViewById(R.id.nav_header_main)).setTextColor(fontColor);
    }

    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data
    ) {

        if (requestCode == IMAGE_READ_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                return;
            }
            final Uri uri = data.getData();

            try {
                final BitmapDrawable background = readBitmapDrawable(uri);

                final Bundle bundle = new Bundle();
                bundle.putInt("width", background.getBitmap().getWidth());
                bundle.putInt("height", background.getBitmap().getHeight());
                sendLog("set_img", bundle);

                setBackgroundImage(background);

                final Snackbar snackbar = Snackbar.make(
                        binding.navView, R.string.message_done_set_image, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.display, v -> {
                    final View imageView = new View(this);
                    imageView.setBackground(background);
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.image)
                            .setMessage(uri.toString())
                            .setView(imageView)
                            .show();
                });
                snackbar.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Set background image.
     * @param background nullable
     */
    private void setBackgroundImage(@Nullable final BitmapDrawable background) {
        ((ImageView) navBackground.findViewById(R.id.background)).setImageDrawable(background);
        binding.appBarMain.content.image.setImageDrawable(background);
    }

    /**
     * Read uri image content.
     * @param uri Image path uri
     * @return {@link BitmapDrawable}
     * @throws IOException
     */
    @NonNull
    private BitmapDrawable readBitmapDrawable(final Uri uri) throws IOException {
        final ParcelFileDescriptor parcelFileDescriptor
                = getContentResolver().openFileDescriptor(uri, "r");
        final FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        final Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        final File output = new File(getFilesDir(), new File(uri.toString()).getName());
        image.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(output));
        preferenceApplier.setBackgroundImagePath(output.getPath());
        return new BitmapDrawable(getResources(), image);
    }

    /**
     * Remove set image.
     */
    private void removeSetImage() {
        final String backgroundImagePath = preferenceApplier.getBackgroundImagePath();
        final int bgColor = preferenceApplier.getColor();
        final int fontColor = preferenceApplier.getFontColor();
        if (backgroundImagePath.length() == 0) {
            Toaster.snackShort(
                    binding.drawerLayout,
                    getString(R.string.message_cannot_found_image),
                    bgColor,
                    fontColor
            );
            return;
        }
        final boolean successRemove = new File(backgroundImagePath).delete();
        if (!successRemove) {
            Toaster.snackShort(
                    binding.drawerLayout,
                    getString(R.string.message_failed_image_removal),
                    bgColor,
                    fontColor
            );
            return;
        }
        Toaster.snackShort(
                binding.drawerLayout,
                getString(R.string.message_success_image_removal),
                bgColor,
                fontColor
        );
        preferenceApplier.removeBackgroundImagePath();
        applyBackgrounds(bgColor, fontColor);
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

}