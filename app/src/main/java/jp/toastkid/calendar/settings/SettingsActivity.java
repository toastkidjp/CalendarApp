package jp.toastkid.calendar.settings;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import jp.toastkid.calendar.BaseActivity;
import jp.toastkid.calendar.BuildConfig;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.advertisement.AdViewFactory;
import jp.toastkid.calendar.databinding.ActivitySettingsBinding;
import jp.toastkid.calendar.libs.ImageLoader;
import jp.toastkid.calendar.libs.Toaster;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;
import jp.toastkid.calendar.settings.color.ColorSettingActivity;

/**
 * Settings activity.
 *
 * @author toastkidjp
 */
public class SettingsActivity extends BaseActivity {

    /** Binding instance. */
    private ActivitySettingsBinding binding;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_settings);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.setActivity(this);

        initToolbar(binding.settingsToolbar);
        binding.settingsClear.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(R.string.title_clear)
                .setMessage(Html.fromHtml(getString(R.string.confirm_clear_all_settings)))
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, (d, i) -> d.cancel())
                .setPositiveButton(R.string.ok,      (d, i) -> {
                    clearPreferences();
                    refresh();
                    Toaster.snackShort(binding.settingsToolbar, R.string.done_clear, colorPair());
                })
                .show());

        ((TextView) findViewById(R.id.settings_app_version)).setText(BuildConfig.VERSION_NAME);

        AdViewFactory.make(getApplicationContext(), binding.adArea.ad);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
        ImageLoader.setImageToImageView(binding.settingBackground, getBackgroundImagePath());
    }

    private void refresh() {
        applyColorToToolbar(binding.settingsToolbar);

        binding.settingsEnableSuggestCheck.setChecked(getPreferenceApplier().isEnableSuggest());
    }

    public void color(final View view) {
        startActivity(ColorSettingActivity.makeIntent(this));
    }

    public void switchSuggest(final View view) {
        final PreferenceApplier applier = getPreferenceApplier();
        applier.switchEnableSuggest();
        binding.settingsEnableSuggestCheck.setChecked(applier.isEnableSuggest());
    }

    public void licenses(final View view) {
        new LicenseViewer(this).invoke();
    }

    @Override
    protected int getTitleId() {
        return R.string.title_settings;
    }

    /**
     * Make launcher intent.
     * @param context
     * @return
     */
    @NonNull
    public static Intent makeIntent(final Context context) {
        final Intent intent = new Intent(context, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

}