package jp.toastkid.calendar.settings.background;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * FilesDir's wrapper.
 *
 * @author toastkidjp
 */
class Storeroom {

    /** Directory name of background images. */
    private static final String DIR_NAME = "background_dir";

    /** Directory object. */
    private final File dir;

    /**
     * Initialize with context.
     * @param context
     */
    Storeroom(final Context context) {
        dir = new File(context.getFilesDir(), DIR_NAME);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Get file object.
     * @param index
     * @return
     */
    @Nullable
    File get(final int index) {
        if (index < 0 || index > listFiles().length) {
            return null;
        }
        return listFiles()[index];
    }

    /**
     * Delete all files.
     */
    void clean () {
        for (final File f : dir.listFiles()) {
            f.delete();
        }
    }

    /**
     * Get file count.
     * @return
     */
    int getCount() {
        return listFiles().length;
    }

    /**
     * Assign new file.
     * @param uri
     * @return
     */
    @NonNull
    File assignMewFile(final Uri uri) {
        return new File(dir, new File(uri.toString()).getName());
    }

    /**
     * Internal method.
     * @return
     */
    private File[] listFiles() {
        return dir.listFiles();
    }

}
