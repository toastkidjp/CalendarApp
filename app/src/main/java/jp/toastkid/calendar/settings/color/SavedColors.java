package jp.toastkid.calendar.settings.color;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.libs.Toaster;

/**
 * @author toastkidjp
 */
public class SavedColors {

    static void setSaved(final TextView tv, final SavedColor color) {
        tv.setBackgroundColor(color.bgColor);
        tv.setTextColor(color.fontColor);
    }

    @NonNull
    static SavedColor makeSavedColor(
            @ColorInt final int bgColor,
            @ColorInt final int fontColor
    ) {
        final SavedColor color = new SavedColor();
        color.bgColor   = bgColor;
        color.fontColor = fontColor;

        return color;
    }

    static void showClearColorsDialog(
            final Context context,
            final View view,
            final SavedColor_Relation relation
    ) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.title_clear_saved_color)
                .setMessage(Html.fromHtml(context.getString(R.string.confirm_clear_all_settings)))
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, (d, i) -> d.cancel())
                .setPositiveButton(R.string.ok,     (d, i) -> deleteAllAsync(view, relation.deleter(), d))
                .show();
    }

    private static void deleteAllAsync(
            final View view,
            final SavedColor_Deleter deleter,
            final DialogInterface d
    ) {
        deleter.executeAsSingle()
                .subscribeOn(Schedulers.io())
                .subscribe(v -> {
                    Toaster.snackShort(
                            view,
                            R.string.settings_color_delete,
                            ((ColorDrawable) view.getBackground()).getColor()
                    );
                    d.dismiss();
                });
    }

    /**
     * Insert default colors.
     * @param context
     */
    public static void insertDefaultColors(@NonNull final Context context) {

        Completable.create(e -> {
            DbInitter.get(context).relationOfSavedColor()
                    .inserter()
                    .executeAllAsObservable(DefaultColors.make(context))
                    .subscribe();
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public static void insertRandomColor(@NonNull final Context context) {

        final Random random = new Random();
        @ColorInt final int bg = Color.argb(
                random.nextInt(255),
                random.nextInt(255),
                random.nextInt(255),
                random.nextInt(255)
        );
        @ColorInt final int font = Color.argb(
                random.nextInt(255),
                random.nextInt(255),
                random.nextInt(255),
                random.nextInt(255)
        );
        Completable.create(e -> {
            DbInitter.get(context).relationOfSavedColor()
                    .inserter()
                    .executeAsSingle(makeSavedColor(bg, font))
                    .subscribe();
        }).subscribeOn(Schedulers.io()).subscribe();
    }

}
