package jp.toastkid.calendar.libs;

import android.support.annotation.ColorInt;
import android.widget.EditText;

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
}
