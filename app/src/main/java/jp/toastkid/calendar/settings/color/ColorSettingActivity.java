package jp.toastkid.calendar.settings.color;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.gfx.android.orma.Relation;
import com.github.gfx.android.orma.widget.OrmaRecyclerViewAdapter;

import io.reactivex.schedulers.Schedulers;
import jp.toastkid.calendar.BaseActivity;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.databinding.ActivitySettingsColorBinding;
import jp.toastkid.calendar.libs.Toaster;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;

/**
 * Color setting activity.
 *
 * @author toastkidjp
 */
public class ColorSettingActivity extends BaseActivity {

    private int initialBgColor;

    private int initialFontColor;

    private ActivitySettingsColorBinding binding;

    private PreferenceApplier mPreferenceApplier;

    private OrmaRecyclerViewAdapter<SavedColor, SavedColorHolder> adapter;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_color);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings_color);
        binding.setActivity(this);

        mPreferenceApplier = new PreferenceApplier(this);

        initialFontColor = mPreferenceApplier.getFontColor();
        binding.settingsColorPrev.setTextColor(initialFontColor);

        initialBgColor = mPreferenceApplier.getColor();
        binding.settingsColorPrev.setBackgroundColor(initialBgColor);

        initPalette();
        initToolbar(binding.settingsColorToolbar);
        initSavedColors();
    }

    private void initPalette() {
        binding.backgroundPalette.addSVBar(binding.backgroundSvbar);
        binding.backgroundPalette.addOpacityBar(binding.backgroundOpacitybar);
        binding.backgroundPalette.setOnColorChangedListener(c -> {
            binding.settingsColorToolbar.setBackgroundColor(c);
            binding.settingsColorOk.setBackgroundColor(c);
        });

        binding.fontPalette.addSVBar(binding.fontSvbar);
        binding.fontPalette.addOpacityBar(binding.fontOpacitybar);
        binding.fontPalette.setOnColorChangedListener(c -> {
            binding.settingsColorToolbar.setTitleTextColor(c);
            binding.settingsColorOk.setTextColor(c);
        });

        setPreviousColor();
    }

    private void setPreviousColor() {
        binding.backgroundPalette.setColor(initialBgColor);
        binding.fontPalette.setColor(initialFontColor);
        applyColorToToolbar(binding.settingsColorToolbar, initialBgColor, initialFontColor);
    }

    private void initSavedColors() {

        adapter = new SavedColorAdapter(this, DbInitter.get(this).relationOfSavedColor());
        binding.savedColors.setAdapter(adapter);
        binding.savedColors.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.clearSavedColor.setOnClickListener(v ->
                Colors.showClearColorsDialog(
                        this,
                        binding.settingsColorToolbar,
                        (SavedColor_Relation) adapter.getRelation()
                )
        );
    }

    /**
     * Bind value and action to holder's view.
     * @param holder Holder
     * @param color  {@link SavedColor} object
     */
    private void bindView(final SavedColorHolder holder, final SavedColor color) {
        Colors.setSaved(holder.textView, color);
        holder.textView.setOnClickListener(v -> commitNewColor(color.bgColor, color.fontColor));
        holder.remove.setOnClickListener(v -> {
            adapter.removeItemAsMaybe(color)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
            Toaster.snackShort(binding.settingsColorToolbar, R.string.settings_color_delete, mPreferenceApplier.getColor());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        final int bgColor   = mPreferenceApplier.getColor();
        final int fontColor = mPreferenceApplier.getFontColor();
        applyColorToToolbar(binding.settingsColorToolbar, bgColor, fontColor);
        Colors.setBgAndText(binding.settingsColorOk, bgColor, fontColor);
    }

    public void ok(final View view) {
        final int bgColor   = binding.backgroundPalette.getColor();
        final int fontColor = binding.fontPalette.getColor();

        commitNewColor(bgColor, fontColor);

        final Bundle bundle = new Bundle();
        bundle.putString("bg",   Integer.toHexString(bgColor));
        bundle.putString("font", Integer.toHexString(fontColor));
        sendLog("color_set", bundle);

        final SavedColor savedColor = new SavedColor();
        savedColor.bgColor   = bgColor;
        savedColor.fontColor = fontColor;
        adapter.addItemAsSingle(savedColor).subscribeOn(Schedulers.io()).subscribe();
    }

    private void commitNewColor(final int bgColor, final int fontColor) {
        mPreferenceApplier.setColor(bgColor);

        mPreferenceApplier.setFontColor(fontColor);

        refresh();
        Toaster.snackShort(binding.settingsColorToolbar, R.string.settings_color_done_commit, bgColor);
    }

    public void reset(final View view) {
        setPreviousColor();
        Toaster.snackShort(binding.settingsColorToolbar, R.string.settings_color_done_reset, binding.backgroundPalette.getColor());
    }

    @Override
    protected int getTitleId() {
        return R.string.title_settings_color;
    }

    /**
     * Make launcher intent.
     * @param context Context
     * @return {@link Intent}
     */
    public static Intent makeIntent(final Context context) {
        final Intent intent = new Intent(context, ColorSettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    private class SavedColorAdapter extends OrmaRecyclerViewAdapter<SavedColor, SavedColorHolder> {

        public SavedColorAdapter(@NonNull Context context, @NonNull Relation<SavedColor, ?> relation) {
            super(context, relation);
        }

        @Override
        public SavedColorHolder onCreateViewHolder(
                final ViewGroup parent,
                final int viewType
            ) {
            final LayoutInflater inflater = LayoutInflater.from(ColorSettingActivity.this);
            return new SavedColorHolder(inflater.inflate(R.layout.saved_color, parent, false));
        }

        @Override
        public void onBindViewHolder(final SavedColorHolder holder, final int position) {
            bindView(holder, getRelation().get(position));
        }

        @Override
        public int getItemCount() {
            return getRelation().count();
        }
    };

}
