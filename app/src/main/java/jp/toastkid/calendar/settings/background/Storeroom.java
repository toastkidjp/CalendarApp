package jp.toastkid.calendar.settings.background;

import android.content.Context;
import android.content.ContextWrapper;
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

    private static final String DIR_NAME = "background_dir";

    private final File dir;

    Storeroom(final Context context) {
        dir = new File(context.getFilesDir(), DIR_NAME);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Nullable
    File get(final int index) {
        if (index < 0 || index > listFiles().length) {
            return null;
        }
        return listFiles()[index];
    }

    void clean () {
        for (final File f : dir.listFiles()) {
            f.delete();
        }
    }

    int getCount() {
        return listFiles().length;
    }

    @NonNull
    File assignMewFile(final Uri uri) {
        return new File(dir, new File(uri.toString()).getName());
    }

    private File[] listFiles() {
        return dir.listFiles();
    }

}
