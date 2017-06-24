package jp.toastkid.calendar.advertisement;

import android.content.Context;
import android.support.annotation.NonNull;

import jp.toastkid.calendar.BuildConfig;

/**
 * @author toastkidjp
 */
public class AdInitializers {

    public static AdInitializer find(@NonNull final Context context) {
        if (BuildConfig.DEBUG) {
            return new TestAdInitializer(context);
        }
        return new ProductionAdInitializer(context);
    }
}
