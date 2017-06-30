package jp.toastkid.calendar.interstitial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;

import jp.toastkid.calendar.BaseActivity;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.advertisement.AdInitializers;

/**
 * @author toastkidjp
 */
public class InterstitialAdActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);
        final InterstitialAd interstitialAd = new InterstitialAd(getApplicationContext());
        interstitialAd.setAdUnitId(getString(R.string.production_interstitial_ad_id));
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
        });
        AdInitializers.find(this).invoke(interstitialAd);
    }

    @Override
    protected int getTitleId() {
        return R.string.title_ad;
    }

    public static Intent makeIntent(final Context context) {
        final Intent intent = new Intent(context, InterstitialAdActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }
}
