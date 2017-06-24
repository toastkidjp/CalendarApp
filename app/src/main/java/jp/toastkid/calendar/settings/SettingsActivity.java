package jp.toastkid.calendar.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.toastkid.calendar.BaseActivity;
import jp.toastkid.calendar.BuildConfig;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.advertisement.AdInitializers;
import jp.toastkid.calendar.libs.Toaster;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;
import jp.toastkid.calendar.settings.color.ColorSettingActivity;

/**
 * Settings activity.
 *
 * @author toastkidjp
 */
public class SettingsActivity extends BaseActivity {

    @BindView(R.id.settings_toolbar)
    public Toolbar mToolbar;

    @BindView(R.id.settings_enable_suggest_text)
    public TextView mEnableSuggestText;

    @BindView(R.id.settings_clear)
    public TextView clear;

    @BindView(R.id.settings_color_text)
    public TextView colorText;

    @BindView(R.id.settings_licenses)
    public TextView license;

    @BindView(R.id.settings_enable_suggest_check)
    public CheckBox mEnableSuggestCheck;

    @BindView(R.id.ad)
    public AdView adView;

    private PreferenceApplier mPreferenceApplier;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        mPreferenceApplier = new PreferenceApplier(this);
        initToolbar(mToolbar);
        clear.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(R.string.title_clear)
                .setMessage(Html.fromHtml(getString(R.string.confirm_clear_all_settings)))
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, (d, i) -> d.cancel())
                .setPositiveButton(R.string.ok,      (d, i) -> {
                    mPreferenceApplier.clear();
                    refresh();
                    Toaster.snackShort(
                            mToolbar, R.string.done_clear, mPreferenceApplier.getColor());
                })
                .show());
        license.setOnClickListener(v -> new LicenseViewer(this).invoke());

        ((TextView) findViewById(R.id.settings_app_version)).setText(BuildConfig.VERSION_NAME);

        initAdView();
    }

    private void initAdView() {
        AdInitializers.find(getApplicationContext()).invoke(adView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        applyColorToToolbar(
                mToolbar, mPreferenceApplier.getColor(), mPreferenceApplier.getFontColor());

        mEnableSuggestCheck.setChecked(mPreferenceApplier.isEnableSuggest());
    }

    @OnClick(R.id.settings_color)
    public void color() {
        startActivity(ColorSettingActivity.makeIntent(this));
    }

    @OnClick(R.id.settings_enable_suggest)
    public void switchSuggest() {
        mPreferenceApplier.switchEnableSuggest();
        mEnableSuggestCheck.setChecked(mPreferenceApplier.isEnableSuggest());
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