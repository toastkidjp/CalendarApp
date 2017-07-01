package jp.toastkid.calendar.advertisement;

import android.support.annotation.NonNull;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;

/**
 * @author toastkidjp
 */
public interface AdInitializer {
    public void invoke(@NonNull final AdView adView);

    public void invoke(@NonNull final InterstitialAd interstitialAd);

    public void invoke(@NonNull final NativeExpressAdView adView);
}
