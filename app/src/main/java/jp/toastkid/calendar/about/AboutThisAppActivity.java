package jp.toastkid.calendar.about;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.NativeExpressAdView;

import jp.toastkid.calendar.BaseActivity;
import jp.toastkid.calendar.BuildConfig;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.advertisement.AdInitializer;
import jp.toastkid.calendar.advertisement.AdInitializers;
import jp.toastkid.calendar.advertisement.NativeAdFactory;
import jp.toastkid.calendar.databinding.ActivityAboutBinding;
import jp.toastkid.calendar.libs.ImageLoader;
import jp.toastkid.calendar.libs.LicenseViewer;
import jp.toastkid.calendar.libs.Toaster;

/**
 * About this app.
 *
 * @author toastkidjp
 */
public class AboutThisAppActivity extends BaseActivity {

    /** Layout ID. */
    private static final int LAYOUT_ID = R.layout.activity_about;

    /** Native AD view. */
    private NativeExpressAdView nativeAd;

    /** Data Binding. */
    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(LAYOUT_ID);
        binding = DataBindingUtil.setContentView(this, LAYOUT_ID);
        binding.setActivity(this);

        initToolbar(binding.toolbar);

        binding.settingsAppVersion.setText(BuildConfig.VERSION_NAME);

        final Context appContext = getApplicationContext();

        final AdInitializer adInitializer = AdInitializers.find(appContext);
        nativeAd = NativeAdFactory.make(appContext);
        nativeAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Toaster.snackShort(
                        nativeAd,
                        R.string.message_done_load_ad,
                        colorPair()
                );
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Toaster.snackShort(
                        nativeAd,
                        R.string.message_failed_ad_loading,
                        colorPair()
                );
            }
        });
        binding.ad.addView(nativeAd);
        adInitializer.invoke(nativeAd);
    }

    @Override
    protected void onResume() {
        super.onResume();

        applyColorToToolbar(binding.toolbar);
        ImageLoader.setImageToImageView(binding.backgroundImage, getBackgroundImagePath());
    }

    /**
     * Show licenses dialog.
     * @param view
     */
    public void licenses(final View view) {
        new LicenseViewer(this).invoke();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nativeAd.destroy();
    }

    @Override
    protected int getTitleId() {
        return R.string.title_about_this_app;
    }

    /**
     * Make launcher intent.
     * @param context
     * @return {@link Intent}
     */
    public static Intent makeIntent(final Context context) {
        final Intent intent = new Intent(context, AboutThisAppActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }
}
