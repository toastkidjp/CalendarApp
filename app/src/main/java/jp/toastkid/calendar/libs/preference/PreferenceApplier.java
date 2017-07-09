package jp.toastkid.calendar.libs.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import jp.toastkid.calendar.R;

/**
 * @author toastkidjp
 */
public class PreferenceApplier {

    private enum Key {
        BG_COLOR, FONT_COLOR, ENABLE_SUGGEST, BG_IMAGE, LAST_AD_DATE, USE_DAILY_ALARM;
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
        final File firstLaunch = new File(context.getFilesDir(), "firstLaunch");
        if (firstLaunch.exists()) {
            return false;
        }
        firstLaunch.mkdirs();
        return true;
    }

    public void updateLastAd() {
        preferences.edit()
                .putInt(Key.LAST_AD_DATE.name(), Calendar.getInstance(Locale.getDefault()).get(Calendar.DAY_OF_YEAR))
                .apply();
    }

    public boolean allowShowingAd() {
        final int today = Calendar.getInstance(Locale.getDefault()).get(Calendar.DAY_OF_YEAR);
        return today != preferences.getInt(Key.LAST_AD_DATE.name(), -1);
    }

    public void useDailyAlarm() {
        preferences.edit().putBoolean(Key.USE_DAILY_ALARM.name(), true).apply();
    }

    public void notUseDailyAlarm() {
        preferences.edit().putBoolean(Key.USE_DAILY_ALARM.name(), false).apply();
    }

    public boolean doesUseDailyAlarm() {
        return preferences.getBoolean(Key.USE_DAILY_ALARM.name(), false);
    }

    public void clear() {
        preferences.edit().clear().apply();
    }

}
