package jp.toastkid.calendar.libs.preference;

import android.support.annotation.ColorInt;

/**
 * @author toastkidjp
 */
public class ColorPair {

    @ColorInt private final int bgColor;

    @ColorInt private final int fontColor;

    public ColorPair(@ColorInt final int bgColor, @ColorInt final int fontColor) {
        this.bgColor = bgColor;
        this.fontColor = fontColor;
    }

    public int bgColor() {
        return bgColor;
    }

    public int fontColor() {
        return fontColor;
    }
}
