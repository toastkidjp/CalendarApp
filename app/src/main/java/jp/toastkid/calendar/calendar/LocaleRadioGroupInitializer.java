package jp.toastkid.calendar.calendar;

import android.os.Build;
import android.view.View;
import android.widget.RadioGroup;

import java.util.Locale;

import jp.toastkid.calendar.R;
import jp.toastkid.calendar.libs.Toaster;

/**
 * @author toastkidjp
 */
class LocaleRadioGroupInitializer {

    /**
     * Initialize Locale selector.
     *
     * @param runnable
     * @param radioGroup
     */
    static void init(final Runnable runnable, final RadioGroup radioGroup) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            radioGroup.setVisibility(View.GONE);
            return;
        }
        radioGroup.setOnCheckedChangeListener((group, id) -> {
            final Locale newLocale = id == R.id.ja ? Locale.JAPAN : Locale.ENGLISH;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                LocaleWrapper.setLocale(radioGroup.getResources().getConfiguration(), newLocale);
                runnable.run();
            }
            Toaster.tShort(radioGroup.getContext(), "New locale: " + newLocale.getLanguage());
        });
    }

}
