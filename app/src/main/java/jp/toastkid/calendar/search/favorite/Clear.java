package jp.toastkid.calendar.search.favorite;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;

import com.github.gfx.android.orma.Deleter;

import io.reactivex.schedulers.Schedulers;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.libs.Toaster;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;

/**
 * Action of clear favorite search.
 *
 * @author toastkidjp
 */
class Clear {

    private final Context context;

    private final View view;

    private final Deleter<FavoriteSearch, ?> deleter;

    Clear(@NonNull final View snackbarParent, final Deleter<FavoriteSearch, ?> deleter) {
        this.view    = snackbarParent;
        this.context = snackbarParent.getContext();
        this.deleter = deleter;
    }

    void invoke() {
        new AlertDialog.Builder(context)
                .setTitle(R.string.title_delete_all)
                .setMessage(Html.fromHtml(context.getString(R.string.confirm_clear_all_settings)))
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, (d, i) -> d.cancel())
                .setPositiveButton(R.string.ok,     (d, i) -> {
                    deleter.executeAsSingle()
                            .subscribeOn(Schedulers.io())
                            .subscribe(v -> {
                                        Toaster.snackShort(
                                                view,
                                                R.string.settings_color_delete,
                                                new PreferenceApplier(context).colorPair()
                                        );
                                        d.dismiss();
                                    }
                            );
                })
                .show();
    }
}