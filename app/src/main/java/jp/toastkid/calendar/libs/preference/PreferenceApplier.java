package jp.toastkid.calendar.libs.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import jp.toastkid.calendar.R;

/**
 * @author toastkidjp
 */
public class PreferenceApplier {

    private enum Key {
        BG_COLOR, FONT_COLOR, ENABLE_SUGGEST;
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

    public boolean isEnableSuggest() {
        return preferences.getBoolean(Key.ENABLE_SUGGEST.name(), true);
    }

    public boolean isDisableSuggest() {
        return !isEnableSuggest();
    }

    public void switchEnableSuggest() {
        preferences.edit().putBoolean(Key.ENABLE_SUGGEST.name(), !preferences.getBoolean(Key.ENABLE_SUGGEST.name(), true)).apply();
    }

    public void clear() {
        preferences.edit().clear().apply();
    }

}
