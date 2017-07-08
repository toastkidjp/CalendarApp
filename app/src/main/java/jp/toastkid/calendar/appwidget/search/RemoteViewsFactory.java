package jp.toastkid.calendar.appwidget.search;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import jp.toastkid.calendar.R;
import jp.toastkid.calendar.libs.intent.PendingIntentFactory;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;

/**
 * App Widget's RemoteViews factory.
 *
 * @author toastkidjp
 */
class RemoteViewsFactory {

    /** Method name. */
    private static final String METHOD_NAME_SET_COLOR_FILTER     = "setColorFilter";

    /** Method name. */
    private static final String METHOD_NAME_SET_BACKGROUND_COLOR = "setBackgroundColor";

    /** Layout ID. */
    private static final int APPWIDGET_LAYOUT_ID = R.layout.appwidget_layout;

    /**
     * Make RemoteViews.
     *
     * @param context
     * @return RemoteViews
     */
    @NonNull
    static RemoteViews make(final Context context) {
        final RemoteViews remoteViews
                = new RemoteViews(context.getPackageName(), APPWIDGET_LAYOUT_ID);
        setTapActions(context, remoteViews);

        final PreferenceApplier preferenceApplier = new PreferenceApplier(context);

        setBackgroundColor(remoteViews, preferenceApplier.getColor());

        setFontColor(remoteViews, preferenceApplier.getFontColor());

        return remoteViews;
    }

    /**
     * Set background color to remote views.
     * @param remoteViews
     * @param backgroundColor
     */
    private static void setBackgroundColor(
            final RemoteViews remoteViews,
            @ColorInt final int backgroundColor
    ) {
        remoteViews.setInt(
                R.id.widget_background, METHOD_NAME_SET_BACKGROUND_COLOR, backgroundColor);
    }

    /**
     * Set font color to remote views.
     * @param remoteViews
     * @param fontColor
     */
    private static void setFontColor(
            final RemoteViews remoteViews,
            @ColorInt final int fontColor
    ) {
        remoteViews.setInt(R.id.widget_search_border, METHOD_NAME_SET_BACKGROUND_COLOR, fontColor);
        remoteViews.setInt(R.id.widget_search_image,  METHOD_NAME_SET_COLOR_FILTER,     fontColor);
        remoteViews.setInt(R.id.widget_calendar,      METHOD_NAME_SET_COLOR_FILTER,     fontColor);

        remoteViews.setTextColor(R.id.widget_search_text, fontColor);
    }

    /**
     * Set pending intents.
     * @param context
     * @param remoteViews
     */
    private static void setTapActions(final Context context, final RemoteViews remoteViews) {
        remoteViews.setOnClickPendingIntent(
                R.id.widget_search,   PendingIntentFactory.makeSearchIntent(context));
        remoteViews.setOnClickPendingIntent(
                R.id.widget_calendar, PendingIntentFactory.main(context));
    }

}