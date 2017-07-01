package jp.toastkid.calendar.libs.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import java.io.File;

import jp.toastkid.calendar.R;

/**
 * @author toastkidjp
 */
public class PreferenceApplier {

    private enum Key {
        BG_COLOR, FONT_COLOR, ENABLE_SUGGEST, BG_IMAGE;
    }

    private SharedPreferences preferences;

    private Context context;

    public PreferenceApplier(final Context c) {
        preferences = c.getSharedPreferences(getClass().getCanonicalName(), Context.MODE_PRIVATE);
        context = c;
    }

    public int getColor() {
        return preferences.getInt(Key.BG_COLOR.name(), ContextCompat.getColor(context, R.color.colorPrimary));
    }

    public void setColor(final int color) {
        preferences.edit().putInt(Key.BG_COLOR.name(), color).apply();
    }

    public int getFontColor() {
        return preferences.getInt(Key.FONT_COLOR.name(), Color.WHITE);
    }

    public void setFontColor(final int color) {
        preferences.edit().putInt(Key.FONT_COLOR.name(), color).apply();
    }

    public ColorPair colorPair() {
        return new ColorPair(getColor(), getFontColor());
    }

    public boolean isEnableSuggest() {
        return preferences.getBoolean(Key.ENABLE_SUGGEST.name(), true);
    }

    public boolean isDisableSuggest() {
        return !isEnableSuggest();
    }

    public void switchEnableSuggest() {
        preferences.edit().putBoolean(Key.ENABLE_SUGGEST.name(), !preferences.getBoolean(Key.ENABLE_SUGGEST.name(), true)).apply();
    }

    public void setBackgroundImagePath(@NonNull final String path) {
        preferences.edit().putString(Key.BG_IMAGE.name(), path).apply();
    }

    public String getBackgroundImagePath() {
        return preferences.getString(Key.BG_IMAGE.name(), "");
    }

    public void removeBackgroundImagePath() {
        preferences.edit().remove(Key.BG_IMAGE.name()).apply();
    }

    public boolean isFirstLaunch() {
        File firstLaunch = new File(context.getFilesDir(), "firstLaunch");
        if (firstLaunch.exists()) {
            return false;
        }
        context.getFilesDir().mkdirs();
        return true;
    }

    public void clear() {
        preferences.edit().clear().apply();
    }

}
