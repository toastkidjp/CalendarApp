package jp.toastkid.calendar.advertisement;

import android.content.Context;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import jp.toastkid.calendar.R;

/**
 * @author toastkidjp
 */
public class AdViewFactory {

    public static void make(final Context appContext, final ViewGroup ad) {
        final AdView adView = new AdView(appContext);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(appContext.getString(R.string.production_banner_unit_id));
        ad.addView(adView);
        AdInitializers.find(appContext).invoke(adView);
    }
}
