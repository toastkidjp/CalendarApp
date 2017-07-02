package jp.toastkid.calendar.libs.intent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;

import jp.toastkid.calendar.libs.preference.ColorPair;

/**
 * @author toastkidjp
 */
public class CustomTabsFactory {

    @NonNull
    public static CustomTabsIntent.Builder make(
            @NonNull final Context context,
            @NonNull final ColorPair pair,
            @DrawableRes final int iconId) {
        return make(context, pair.bgColor(), pair.fontColor(), iconId);
    }

    /**
     *
     * @param context
     * @param backgroundColor
     * @param fontColor
     * @param iconId
     * @return
     */
    @NonNull
    public static CustomTabsIntent.Builder make(
            @NonNull final Context context,
            @ColorInt final int backgroundColor,
            @ColorInt final int fontColor,
            @DrawableRes final int iconId) {
        return new CustomTabsIntent.Builder()
                    .setToolbarColor(backgroundColor)
                    .setCloseButtonIcon(decodeResource(context, iconId))
                    .setSecondaryToolbarColor(fontColor)
                    .setStartAnimations(context, 0, 0)
                    .setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .addDefaultShareMenuItem();
    }

    /**
     *
     * @param context
     * @param id
     * @return
     */
    private static Bitmap decodeResource(
            @NonNull final Context context,
            @DrawableRes final int id
    ) {
        return BitmapFactory.decodeResource(context.getResources(), id);
    }
}
