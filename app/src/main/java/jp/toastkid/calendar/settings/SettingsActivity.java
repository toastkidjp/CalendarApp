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
import jp.toastkid.calendar.advertisement.AdInitializers;
import jp.toastkid.calendar.databinding.ActivitySettingsBinding;
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

    /** Preference applier. */
    private PreferenceApplier mPreferenceApplier;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_settings);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.setActivity(this);

        mPreferenceApplier = new PreferenceApplier(this);
        initToolbar(binding.settingsToolbar);
        binding.settingsClear.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(R.string.title_clear)
                .setMessage(Html.fromHtml(getString(R.string.confirm_clear_all_settings)))
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, (d, i) -> d.cancel())
                .setPositiveButton(R.string.ok,      (d, i) -> {
                    mPreferenceApplier.clear();
                    refresh();
                    Toaster.snackShort(
                            binding.settingsToolbar, R.string.done_clear, mPreferenceApplier.getColor());
                })
                .show());

        ((TextView) findViewById(R.id.settings_app_version)).setText(BuildConfig.VERSION_NAME);

        initAdView();
    }

    private void initAdView() {
        AdInitializers.find(getApplicationContext()).invoke(binding.ad);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        applyColorToToolbar(
                binding.settingsToolbar,
                mPreferenceApplier.getColor(),
                mPreferenceApplier.getFontColor()
        );

        binding.settingsEnableSuggestCheck.setChecked(mPreferenceApplier.isEnableSuggest());
    }

    public void color(final View view) {
        startActivity(ColorSettingActivity.makeIntent(this));
    }

    public void switchSuggest(final View view) {
        mPreferenceApplier.switchEnableSuggest();
        binding.settingsEnableSuggestCheck.setChecked(mPreferenceApplier.isEnableSuggest());
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