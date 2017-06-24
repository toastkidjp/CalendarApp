package jp.toastkid.calendar.advertisement;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import jp.toastkid.calendar.BuildConfig;
import jp.toastkid.calendar.R;

/**
 * @author toastkidjp
 */
class ProductionAdInitializer implements AdInitializer {

    /**
     * @param context need ApplicationContext
     */
    ProductionAdInitializer(@NonNull final Context context) {
        if (BuildConfig.DEBUG) {
            throw new IllegalStateException();
        }
        final String appAdId = context.getString(R.string.production_app_admob_id);
        MobileAds.initialize(context, appAdId);
    }

    /**
     *
     */
    @Override
    public void invoke(@NonNull final AdView adView) {
        if (BuildConfig.DEBUG) {
            throw new IllegalStateException();
        }
        adView.loadAd(new AdRequest.Builder().build());
    }
}
