package jp.toastkid.calendar.advertisement.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;

import jp.toastkid.calendar.BaseActivity;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.advertisement.AdInitializer;
import jp.toastkid.calendar.advertisement.AdInitializers;
import jp.toastkid.calendar.libs.Toaster;

/**
 * Interstitial Advertisement activity.
 *
 * @author toastkidjp
 */
public class InterstitialAdActivity extends BaseActivity {

    /** Layout ID. */
    @LayoutRes
    private static final int LAYOUT_ID = R.layout.activity_interstitial;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT_ID);

        final AdInitializer adInitializer = AdInitializers.find(this);
        adInitializer.invoke(makeInterstitialAd());
    }

    /**
     * Make interstitial ad.
     * @return {@link InterstitialAd}
     */
    @NonNull
    private InterstitialAd makeInterstitialAd() {
        final InterstitialAd interstitialAd = new InterstitialAd(getApplicationContext());
        interstitialAd.setAdUnitId(getString(R.string.unit_id_interstitial));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                finish();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                interstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Toaster.tShort(InterstitialAdActivity.this, R.string.message_failed_ad_loading);
                finish();
            }
        });
        return interstitialAd;
    }

    @Override
    protected int getTitleId() {
        return R.string.title_loading_ad;
    }

    /**
     * Make this Activity launching intent.
     * @param context
     * @return
     */
    public static Intent makeIntent(final Context context) {
        final Intent intent = new Intent(context, InterstitialAdActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

}
