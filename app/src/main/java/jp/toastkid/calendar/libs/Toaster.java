package jp.toastkid.calendar.libs;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Simple toasting utilities.
 *
 * @author toastkidjp
 */
public class Toaster {

    /**
     * Show simple snackbar on short time.
     *
     * @param view
     * @param messageId
     * @param color
     */
    public static void snackShort(
            @NonNull final View view,
            @StringRes final int messageId,
            @ColorInt final int color
    ) {
        snackShort(view, view.getContext().getString(messageId), color, Color.WHITE);
    }

    /**
     * Show simple snackbar on short time.
     *
     * @param view
     * @param message
     * @param color
     */
    public static void snackShort(
            @NonNull final View view,
            @NonNull final String message,
            @ColorInt final int color
    ) {
        snackShort(view, message, color, Color.WHITE);
    }

    /**
     * Show simple snackbar on short time.
     *
     * @param view
     * @param message
     * @param color
     */
    public static void snackShort(
            @NonNull final View view,
            @NonNull final String message,
            @ColorInt final int color,
            @ColorInt final int textColor
            ) {
        final Snackbar snackbar
                = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        final View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(color);
        ((TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text))
                .setTextColor(textColor);
        snackbar.show();
    }
}
