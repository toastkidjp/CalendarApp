package jp.toastkid.calendar.settings.background;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.view.MenuItem;

import jp.toastkid.calendar.BaseActivity;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.advertisement.AdViewFactory;
import jp.toastkid.calendar.databinding.ActivityBackgroundSettingBinding;
import jp.toastkid.calendar.libs.intent.IntentFactory;
import jp.toastkid.calendar.libs.Toaster;

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

    /** Adapter. */
    private Adapter adapter;

    /** Wrapper of FilesDir. */
    private Storeroom storeroom;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_setting);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_background_setting);
        initToolbar(binding.toolbar);
        binding.toolbar.inflateMenu(R.menu.background_setting_menu);

        storeroom = new Storeroom(this);

        initImagesView();
        AdViewFactory.make(getApplicationContext(), binding.adArea.ad);
    }

    private void initImagesView() {
        binding.imagesView.setLayoutManager(
                new GridLayoutManager(this, 2, LinearLayoutManager.HORIZONTAL, false));
        adapter = new Adapter(getPreferenceApplier(), storeroom);
        binding.imagesView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyColorToToolbar(binding.toolbar);
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
                                colorPair()
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

    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data
    ) {

        if (requestCode == IMAGE_READ_REQUEST && resultCode == RESULT_OK) {
            new LoadedAction(data, binding.toolbar, colorPair(), adapter::notifyDataSetChanged)
                    .invoke();
            sendLog("set_img");
        }
        super.onActivityResult(requestCode, resultCode, data);
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
