package jp.toastkid.calendar.libs;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;

/**
 * Common {@link android.content.Intent} factory.
 *
 * @author toastkidjp
 */
public class IntentFactory {

    /**
     * Make sharing message intent.
     * @param message
     * @return Intent
     */
    @NonNull
    public static Intent makeShare(@NonNull final String message) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        return intent;
    }

    /**
     * Make intent of sharing with twitter.
     * @return CustomTabsIntent
     */
    @NonNull
    public static CustomTabsIntent makeTwitter(
            @NonNull final Context context,
            @ColorInt final int bgColor,
            @ColorInt final int fontColor,
            @DrawableRes final int iconId
    ) {
        return CustomTabsFactory.make(context, bgColor, fontColor, iconId).build();
    }

    /**
     * Make pick image intent.
     * @return Intent
     */
    @NonNull
    public static Intent makePickImage() {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        return intent;
    }

    /**
     * Make launching calendar intent.
     * @param eventStartMs
     * @return Intent
     */
    @NonNull
    public static Intent makeCalendar(final long eventStartMs) {
        final Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", eventStartMs);
        return intent;
    }

}
