package jp.toastkid.calendar.advertisement;

import android.content.Context;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoOptions;

import jp.toastkid.calendar.R;

/**
 * Native AD factory.
 *
 * @author toastkidjp
 */
public class NativeAdFactory {

    /** Video Options. */
    private static final VideoOptions VIDEO_OPTIONS
            = new VideoOptions.Builder().setStartMuted(true).build();

    /**
     * Make native ad view.
     * @param appContext Application context
     * @return {@link NativeExpressAdView}
     */
    public static NativeExpressAdView make(final Context appContext) {
        final NativeExpressAdView nAd = new NativeExpressAdView(appContext);
        nAd.setAdUnitId(appContext.getString(R.string.unit_id_native_large_ad));
        nAd.setAdSize(AdSize.MEDIUM_RECTANGLE);
        nAd.setVideoOptions(VIDEO_OPTIONS);
        return nAd;
    }
}
