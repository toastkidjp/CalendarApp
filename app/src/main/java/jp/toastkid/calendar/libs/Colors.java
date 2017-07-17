package jp.toastkid.calendar.libs;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.TextView;

import jp.toastkid.calendar.libs.preference.ColorPair;

/**
 * Color utilities.
 *
 * @author toastkidjp
 */
public class Colors {

    /**
     * Set specified color to passed EditText.
     * @param editText
     * @param fontColor
     */
    public static void setTextColor(final EditText editText, @ColorInt final int fontColor) {
        editText.setTextColor(fontColor);
        editText.setHintTextColor(fontColor);
        editText.setHighlightColor(fontColor);
    }

    public static void setBgAndText(
            final TextView tv,
            @NonNull final ColorPair pair
            ) {
        tv.setBackgroundColor(pair.bgColor());
        tv.setTextColor(pair.fontColor());
    }
}
