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
 * @author toastkidjp
 */

class ImagesViewHolder extends RecyclerView.ViewHolder {

    private SavedImageBinding binding;

    private PreferenceApplier preferenceApplier;

    private Runnable notifyDataSetChanged;

    ImagesViewHolder(
            final SavedImageBinding binding,
            final PreferenceApplier preferenceApplier,
            final Runnable notifyDataSetChanged
    ) {
        super(binding.getRoot());
        this.binding = binding;
        this.preferenceApplier = preferenceApplier;
        this.notifyDataSetChanged = notifyDataSetChanged;
    }

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
                ImageDialog.show(v.getContext(), uri, ImageLoader.readBitmapDrawable(v.getContext(), uri));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        });
    }

    /**
     * Remove set image.
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
        notifyDataSetChanged.run();
    }
}