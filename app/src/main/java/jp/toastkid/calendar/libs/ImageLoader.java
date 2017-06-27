package jp.toastkid.calendar.libs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.toastkid.calendar.libs.preference.PreferenceApplier;

/**
 * Image file loader.
 *
 * @author toastkidjp
 */
public class ImageLoader {

    /**
     * Read uri image content.
     *
     * <pre>
     * Uri.parse(new File(backgroundImagePath).toURI().toString())
     * </pre>
     *
     * @param context Context
     * @param uri Image path uri
     * @return {@link BitmapDrawable}
     * @throws IOException
     */
    @Nullable
    public static BitmapDrawable readBitmapDrawable(
            final Context context,
            final Uri uri
    ) throws IOException {
        final ParcelFileDescriptor parcelFileDescriptor
                = context.getContentResolver().openFileDescriptor(uri, "r");
        final FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        final Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        if (image == null) {
            return null;
        }
        parcelFileDescriptor.close();
        final File output = new File(context.getFilesDir(), new File(uri.toString()).getName());
        image.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(output));
        new PreferenceApplier(context).setBackgroundImagePath(output.getPath());
        return new BitmapDrawable(context.getResources(), image);
    }

    /**
     * Set image to passed ImageView.
     *
     * @param iv ImageView
     * @param imagePath Image file path
     */
    public static void setImageToImageView(final ImageView iv, final String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            iv.setImageDrawable(null);
            return;
        }

        try {
            iv.setImageDrawable(readBitmapDrawable(
                    iv.getContext(),
                    Uri.parse(new File(imagePath).toURI().toString())
            ));
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
