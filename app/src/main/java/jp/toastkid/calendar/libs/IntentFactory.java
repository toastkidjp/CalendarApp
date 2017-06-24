package jp.toastkid.calendar.libs;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;

/**
 * Common {@link android.content.Intent} factory.
 *
 * @author toastkidjp
 */
public class IntentFactory {

    /**
     * Make sharing message intent.
     * @param message
     * @return
     */
    @NonNull
    public static Intent makeShare(@NonNull final String message) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        return intent;
    }

    /**
     * Make intent of sharing with twitter.
     * @return
     */
    @NonNull
    public static CustomTabsIntent makeTwitter(
            @NonNull final Context context,
            @ColorInt final int bgColor,
            @ColorInt final int fontColor,
            @DrawableRes final int iconId
    ) {
        return CustomTabsFactory.make(context, bgColor, fontColor, iconId).build();
    }

    /**
     * Make opening image intent.
     * @return
     */
    @NonNull
    public static Intent makeImage() {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setType("image/*");
        return intent;
    }
}
