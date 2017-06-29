package jp.toastkid.calendar.settings.background;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import jp.toastkid.calendar.R;

/**
 * @author toastkidjp
 */
class ImageDialog {

    static void show(final Context context, final Uri uri, final BitmapDrawable background) {
        final ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setAdjustViewBounds(true);
        imageView.setImageDrawable(background);
        new AlertDialog.Builder(context)
                .setTitle(R.string.image)
                .setMessage(uri.toString())
                .setView(imageView)
                .show();
    }
}
