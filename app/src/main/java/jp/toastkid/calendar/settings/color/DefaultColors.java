package jp.toastkid.calendar.settings.color;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArraySet;

import jp.toastkid.calendar.R;

/**
 * Default colors.
 *
 * @author toastkidjp
 */
class DefaultColors {

    @NonNull
    public static ArraySet<SavedColor> make(@NonNull final Context context) {
        final ArraySet<SavedColor> models = new ArraySet<>();
        models.add(SavedColors.makeSavedColor(
                fromRes(context, R.color.colorPrimary), fromRes(context, R.color.textPrimary)));
        models.add(SavedColors.makeSavedColor(
                fromRes(context, R.color.colorPrimaryDark), fromRes(context, R.color.textPrimary)));
        models.add(SavedColors.makeSavedColor(
                fromRes(context, R.color.crimson_bg), fromRes(context, R.color.crimson_font)));
        models.add(SavedColors.makeSavedColor(
                fromRes(context, R.color.cyan_bg), fromRes(context, R.color.white)));
        models.add(SavedColors.makeSavedColor(
                fromRes(context, R.color.black), fromRes(context, R.color.textPrimary)));
        models.add(SavedColors.makeSavedColor(
                fromRes(context, R.color.white), fromRes(context, R.color.black)));
        models.add(SavedColors.makeSavedColor(
                fromRes(context, R.color.pinky), fromRes(context, R.color.white)));
        models.add(SavedColors.makeSavedColor(
                fromRes(context, R.color.lime_bg), fromRes(context, R.color.filter_white)));
        models.add(SavedColors.makeSavedColor(
                fromRes(context, R.color.purple_bg), fromRes(context, R.color.white)));
        models.add(SavedColors.makeSavedColor(
                fromRes(context, R.color.darkgray_scale), fromRes(context, R.color.filter_white)));
        models.add(SavedColors.makeSavedColor(
                fromRes(context, R.color.wa_bg), fromRes(context, R.color.wa_font)));
        models.add(SavedColors.makeSavedColor(
                Color.TRANSPARENT, Color.BLACK));
        return models;
    }

    private static @ColorInt int fromRes(
            @NonNull final Context context, @ColorRes final int colorId) {
        return ContextCompat.getColor(context, colorId);
    }
}
