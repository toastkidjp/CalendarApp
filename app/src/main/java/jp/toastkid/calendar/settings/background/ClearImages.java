package jp.toastkid.calendar.settings.background;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.Html;

import jp.toastkid.calendar.R;

/**
 * @author toastkidjp
 */
class ClearImages {

    private Context context;

    private Runnable action;

    ClearImages(final Context context, final Runnable action) {
        this.context = context;
        this.action  = action;
    }

    void invoke() {
        new AlertDialog.Builder(context)
                .setTitle(R.string.clear_all)
                .setMessage(Html.fromHtml(context.getString(R.string.confirm_clear_all_settings)))
                .setCancelable(true)
                .setPositiveButton(R.string.ok, (d, i) -> {
                    action.run();
                    d.dismiss();
                })
                .setNegativeButton(R.string.cancel, (d, i) -> d.cancel())
                .show();
    }
}
