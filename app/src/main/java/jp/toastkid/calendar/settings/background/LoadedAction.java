package jp.toastkid.calendar.settings.background;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.toastkid.calendar.R;
import jp.toastkid.calendar.libs.ImageLoader;
import jp.toastkid.calendar.libs.Toaster;
import jp.toastkid.calendar.libs.preference.ColorPair;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;

/**
 * Action of loaded new background image.
 *
 * @author toastkidjp
 */
class LoadedAction {

    /** Image file URI. */
    @NonNull
    private final Uri uri;

    /** Snackbar's parent. */
    @NonNull
    private final View parent;

    /** Color pair. */
    @NonNull
    private final ColorPair colorPair;

    /** On loaded action. */
    @NonNull
    private final Runnable onLoadedAction;

    /**
     *
     * @param data
     * @param parent
     * @param colorPair
     * @param onLoadedAction
     */
    LoadedAction(
            @NonNull final Intent data,
            @NonNull final View parent,
            @NonNull final ColorPair colorPair,
            @NonNull final Runnable onLoadedAction
                 ) {
        this.uri            = data.getData();
        this.parent         = parent;
        this.colorPair      = colorPair;
        this.onLoadedAction = onLoadedAction;
    }

    /**
     * Invoke action.
     */
    void invoke() {

        try {
            final Context context = parent.getContext();

            final Bitmap image = ImageLoader.loadBitmap(context, uri);

            if (image == null) {
                informFailed();
                return;
            }

            storeImageToFile(context, image);

            onLoadedAction.run();

            informDone(context, image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Store image file.
     * @param context
     * @param image
     * @throws FileNotFoundException
     */
    private void storeImageToFile(final Context context, final Bitmap image) throws FileNotFoundException {
        final File output = new Storeroom(context).assignMewFile(uri);
        new PreferenceApplier(context).setBackgroundImagePath(output.getPath());
        image.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(output));
    }

    /**
     * Inform failed.
     */
    private void informFailed() {
        Toaster.snackShort(parent, R.string.message_failed_read_image, colorPair);
    }

    /**
     * Inform done with action.
     * @param context
     * @param image
     */
    private void informDone(final Context context, final Bitmap image) {
        Toaster.snackLong(
                parent, R.string.message_done_set_image, R.string.display,
                v -> ImageDialog.show(context, uri, new BitmapDrawable(context.getResources(), image)),
                colorPair
        );
    }

}
