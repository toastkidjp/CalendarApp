package jp.toastkid.calendar.libs;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import jp.toastkid.calendar.R;
import jp.toastkid.calendar.advertisement.activity.InterstitialAdActivity;
import jp.toastkid.calendar.libs.preference.ColorPair;

/**
 * Simple toasting utilities.
 *
 * @author toastkidjp
 */
public class Toaster {

    /**
     * Short toasting.
     * @param context
     * @param messageId
     */
    public static void tShort(
            @NonNull final Context context,
            @StringRes final int messageId
    ) {
        Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
    }

    /**
     * Short toasting.
     * @param context
     * @param message
     */
    public static void tShort(
            @NonNull final Context context,
            @NonNull final String message
    ) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

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
        snackShort(view, view.getContext().getString(messageId), new ColorPair(color, Color.WHITE));
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
        snackShort(view, message, new ColorPair(color, Color.WHITE));
    }

    /**
     * Show simple snackbar on short time.
     *
     * @param view
     * @param messageId
     * @param pair
     */
    public static void snackShort(
            @NonNull final View view,
            @StringRes final int messageId,
            @NonNull final ColorPair pair
    ) {
        snackShort(view, view.getContext().getString(messageId), pair);
    }

    /**
     * Show simple snackbar on short time.
     *
     * @param view
     * @param message
     * @param pair
     */
    public static void snackShort(
            @NonNull final View view,
            @NonNull final String message,
            @NonNull final ColorPair pair
            ) {
        final Snackbar snackbar
                = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        final View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(pair.bgColor());
        ((TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text))
                .setTextColor(pair.fontColor());
        snackbar.show();
    }

    /**
     * Show snackbar on long time.
     * @param view
     * @param messageId
     * @param actionTextId
     * @param action
     * @param pair
     */
    public static void snackLong(
            @NonNull final View view,
            @StringRes final int messageId,
            @StringRes final int actionTextId,
            @NonNull final View.OnClickListener action,
            @NonNull final ColorPair pair
    ) {
        final Snackbar snackbar
                = Snackbar.make(view, messageId, Snackbar.LENGTH_LONG);
        snackbar.setAction(actionTextId, action);
        final View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(pair.bgColor());
        ((TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text))
                .setTextColor(pair.fontColor());
        ((TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_action))
                .setTextColor(pair.fontColor());
        snackbar.show();
    }
}
