package jp.toastkid.calendar.settings.background;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.io.IOException;

import jp.toastkid.calendar.R;
import jp.toastkid.calendar.databinding.SavedImageBinding;
import jp.toastkid.calendar.libs.ImageLoader;
import jp.toastkid.calendar.libs.Toaster;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;

/**
 * Extended of {@link RecyclerView.ViewHolder}.
 *
 * @author toastkidjp
 */
class ViewHolder extends RecyclerView.ViewHolder {

    /** Binding object. */
    private SavedImageBinding binding;

    /** Preferences wrapper. */
    private PreferenceApplier preferenceApplier;

    /** Action on removed. */
    private Runnable onRemoved;

    /**
     *
     * @param binding
     * @param preferenceApplier
     * @param onRemoved
     */
    ViewHolder(
            final SavedImageBinding binding,
            final PreferenceApplier preferenceApplier,
            final Runnable onRemoved
    ) {
        super(binding.getRoot());
        this.binding = binding;
        this.preferenceApplier = preferenceApplier;
        this.onRemoved = onRemoved;
    }

    /**
     * Apply file content.
     * @param f
     */
    void applyContent(final File f) {
        ImageLoader.setImageToImageView(this.binding.image, f.getPath());
        this.binding.text.setText(f.getName());
        this.binding.remove.setOnClickListener(v -> removeSetImage(f));
        this.binding.getRoot().setOnClickListener(v -> {
            preferenceApplier.setBackgroundImagePath(f.getPath());
            Toaster.snackShort(
                    binding.image,
                    R.string.message_change_background_image,
                    preferenceApplier.getColor(),
                    preferenceApplier.getFontColor()
            );
        });
        this.binding.getRoot().setOnLongClickListener(v -> {
            final Uri uri = Uri.parse(f.toURI().toString());
            try {
                ImageDialog.show(
                        v.getContext(), uri, ImageLoader.readBitmapDrawable(v.getContext(), uri));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        });
    }

    /**
     * Remove set image.
     *
     * @param file
     */
    private void removeSetImage(final File file) {
        final int bgColor   = preferenceApplier.getColor();
        final int fontColor = preferenceApplier.getFontColor();
        if (file == null || !file.exists()) {
            Toaster.snackShort(
                    binding.text,
                    R.string.message_cannot_found_image,
                    bgColor,
                    fontColor
            );
            return;
        }
        final boolean successRemove = file.delete();
        if (!successRemove) {
            Toaster.snackShort(
                    binding.text,
                    R.string.message_failed_image_removal,
                    bgColor,
                    fontColor
            );
            return;
        }
        Toaster.snackShort(
                binding.text,
                R.string.message_success_image_removal,
                bgColor,
                fontColor
        );
        onRemoved.run();
    }
}