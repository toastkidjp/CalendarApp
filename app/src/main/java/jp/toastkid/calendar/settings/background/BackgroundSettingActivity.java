package jp.toastkid.calendar.settings.background;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.toastkid.calendar.BaseActivity;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.databinding.ActivityBackgroundSettingBinding;
import jp.toastkid.calendar.databinding.SavedImageBinding;
import jp.toastkid.calendar.libs.ImageLoader;
import jp.toastkid.calendar.libs.IntentFactory;
import jp.toastkid.calendar.libs.Toaster;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;

/**
 * Background settings.
 *
 * @author toastkidjp
 */
public class BackgroundSettingActivity extends BaseActivity {

    /** Request code. */
    private static final int IMAGE_READ_REQUEST = 136;

    /** Data Binding object. */
    private ActivityBackgroundSettingBinding binding;

    /** Preference wrapper. */
    private PreferenceApplier preferenceApplier;

    /** Adapter. */
    private ImagesViewAdapter adapter;

    /** Wrapper of FilesDir. */
    private Storeroom storeroom;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_setting);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_background_setting);
        initToolbar(binding.toolbar);
        binding.toolbar.inflateMenu(R.menu.background_setting_menu);
        preferenceApplier = new PreferenceApplier(this);

        storeroom = new Storeroom(this);

        binding.imagesView.setLayoutManager(new GridLayoutManager(this, 2, LinearLayoutManager.HORIZONTAL, false));
        adapter = new ImagesViewAdapter();
        binding.imagesView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyColorToToolbar(binding.toolbar, preferenceApplier.getColor(), preferenceApplier.getFontColor());
    }

    private class ImagesViewAdapter extends RecyclerView.Adapter<ImagesViewHolder> {

        @Override
        public ImagesViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final SavedImageBinding itemBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.saved_image,
                    parent,
                    false
            );
            return new ImagesViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(final ImagesViewHolder holder, final int position) {
            holder.applyContent(storeroom.get(position));
        }

        @Override
        public int getItemCount() {
            return storeroom.getCount();
        }
    }

    private class ImagesViewHolder extends RecyclerView.ViewHolder {

        private SavedImageBinding binding;

        ImagesViewHolder(SavedImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void applyContent(final File f) {
            ImageLoader.setImageToImageView(this.binding.image, f.getPath());
            this.binding.text.setText(f.getName());
            this.binding.remove.setOnClickListener(v -> removeSetImage(f));
            this.binding.getRoot().setOnClickListener(v -> {
                preferenceApplier.setBackgroundImagePath(f.getPath());
                Toaster.snackShort(
                        binding.image,
                        getString(R.string.message_change_background_image),
                        preferenceApplier.getColor(),
                        preferenceApplier.getFontColor()
                );
            });
            this.binding.getRoot().setOnLongClickListener(v -> {
                final Uri uri = Uri.parse(f.toURI().toString());
                try {
                    showImageDialog(uri, ImageLoader.readBitmapDrawable(v.getContext(), uri));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            });
        }

    }

    @Override
    protected boolean clickMenu(final MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.background_settings_toolbar_menu_add) {
            sendLog("set_bg_img");
            startActivityForResult(IntentFactory.makePickImage(), IMAGE_READ_REQUEST);
            return true;
        }
        if (itemId == R.id.background_settings_toolbar_menu_clear) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.clear_all)
                    .setMessage(Html.fromHtml(getString(R.string.confirm_clear_all_settings)))
                    .setCancelable(true)
                    .setPositiveButton(R.string.ok, (d, i) -> {
                        sendLog("clear_bg_img");
                        storeroom.clean();
                        Toaster.snackShort(
                                binding.toolbar,
                                getString(R.string.message_success_image_removal),
                                preferenceApplier.getColor(),
                                preferenceApplier.getFontColor()
                        );
                        adapter.notifyDataSetChanged();
                        d.dismiss();
                    })
                    .setNegativeButton(R.string.cancel, (d, i) -> d.cancel())
                    .show();
            return true;
        }
        return super.clickMenu(item);
    }

    /**
     * Remove set image.
     */
    private void removeSetImage(final File file) {
        final int bgColor   = preferenceApplier.getColor();
        final int fontColor = preferenceApplier.getFontColor();
        if (file == null || !file.exists()) {
            Toaster.snackShort(
                    binding.toolbar,
                    getString(R.string.message_cannot_found_image),
                    bgColor,
                    fontColor
            );
            return;
        }
        final boolean successRemove = file.delete();
        if (!successRemove) {
            Toaster.snackShort(
                    binding.toolbar,
                    getString(R.string.message_failed_image_removal),
                    bgColor,
                    fontColor
            );
            return;
        }
        Toaster.snackShort(
                binding.toolbar,
                getString(R.string.message_success_image_removal),
                bgColor,
                fontColor
        );
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data
    ) {

        if (requestCode == IMAGE_READ_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                return;
            }
            final Uri uri = data.getData();

            try {
                final Bitmap image = ImageLoader.loadBitmap(this, uri);

                if (image == null) {
                    Toaster.snackShort(
                            binding.toolbar,
                            getString(R.string.message_failed_read_image),
                            preferenceApplier.getColor(),
                            preferenceApplier.getFontColor()
                    );
                    return;
                }

                final File output = storeroom.assignMewFile(uri);
                new PreferenceApplier(this).setBackgroundImagePath(output.getPath());
                image.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(output));

                final BitmapDrawable background = new BitmapDrawable(getResources(), image);

                final Bundle bundle = new Bundle();
                bundle.putInt("width", background.getBitmap().getWidth());
                bundle.putInt("height", background.getBitmap().getHeight());
                sendLog("set_img", bundle);

                adapter.notifyDataSetChanged();

                final Snackbar snackbar = Snackbar.make(
                        binding.toolbar, R.string.message_done_set_image, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.display, v -> showImageDialog(uri, background));
                snackbar.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showImageDialog(final Uri uri, final BitmapDrawable background) {
        final ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setAdjustViewBounds(true);
        imageView.setImageDrawable(background);
        new AlertDialog.Builder(this)
                .setTitle(R.string.image)
                .setMessage(uri.toString())
                .setView(imageView)
                .show();
    }

    @Override
    protected int getTitleId() {
        return R.string.title_background_image_setting;
    }

    /**
     * Make launcher intent.
     * @param context Context
     * @return {@link Intent}
     */
    public static Intent makeIntent(final Context context) {
        final Intent intent = new Intent(context, BackgroundSettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

}
