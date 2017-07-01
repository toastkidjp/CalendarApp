package jp.toastkid.calendar.calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.NativeExpressAdView;

import jp.toastkid.calendar.R;
import jp.toastkid.calendar.advertisement.AdInitializer;
import jp.toastkid.calendar.advertisement.AdInitializers;
import jp.toastkid.calendar.advertisement.NativeAdFactory;
import jp.toastkid.calendar.advertisement.activity.InterstitialAdActivity;
import jp.toastkid.calendar.libs.Toaster;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;

/**
 * About this app.
 *
 * @author toastkidjp
 */
class AboutThisApp {

    /** Application context. */
    private final Context context;

    /** AD initializer. */
    private final AdInitializer adInitializer;

    /** Native AD view. */
    private final NativeExpressAdView nativeAd;

    /** Dialog. */
    private AlertDialog dialog;

    /**
     *
     * @param context AppCompat context
     * @param preferenceApplier
     */
    AboutThisApp(
            @NonNull final Context context,
            @NonNull final PreferenceApplier preferenceApplier
    ) {
        this.context = context;
        adInitializer = AdInitializers.find(context.getApplicationContext());
        nativeAd = NativeAdFactory.make(context.getApplicationContext());
        nativeAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Toaster.snackShort(
                        nativeAd,
                        R.string.message_done_load_ad,
                        preferenceApplier.getColor(),
                        preferenceApplier.getFontColor()
                );
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Toaster.snackShort(
                        nativeAd,
                        R.string.message_failed_ad_loading,
                        preferenceApplier.getColor(),
                        preferenceApplier.getFontColor()
                );
            }
        });
    }

    /**
     * Show dialog.
     */
    public void invoke() {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.title_about_this_app)
                    .setMessage(R.string.message_about_this_app)
                    .setView(nativeAd)
                    .setCancelable(true)
                    .setNegativeButton(
                            R.string.message_introduce_ad,
                            (d, i) -> context.startActivity(InterstitialAdActivity.makeIntent(context)))
                    .setPositiveButton(R.string.close, (d, i) -> d.dismiss())
                    .create();
        }

        dialog.show();
        adInitializer.invoke(nativeAd);
    }

    void dispose() {
        nativeAd.destroy();
        dialog.dismiss();
    }
}
