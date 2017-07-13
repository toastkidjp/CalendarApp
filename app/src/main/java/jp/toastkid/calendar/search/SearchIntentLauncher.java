package jp.toastkid.calendar.search;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;

import jp.toastkid.calendar.R;
import jp.toastkid.calendar.libs.intent.CustomTabsFactory;
import jp.toastkid.calendar.libs.intent.PendingIntentFactory;

/**
 * Search intent launcher.
 *
 * @author toastkidjp
 */
class SearchIntentLauncher {

    private @NonNull Context context;

    private @ColorInt int backgroundColor;

    private @ColorInt int fontColor;

    private @NonNull String category;

    private @NonNull String query;

    SearchIntentLauncher(@NonNull final Context context) {
        this.context   = context;
    }

    SearchIntentLauncher setBackgroundColor(@ColorInt final int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    SearchIntentLauncher setFontColor(@ColorInt final int fontColor) {
        this.fontColor = fontColor;
        return this;
    }

    SearchIntentLauncher setCategory(@NonNull final String category) {
        this.category = category;
        return this;
    }

    SearchIntentLauncher setQuery(@NonNull final String query) {
        this.query = query;
        return this;
    }

    void invoke() {
        final CustomTabsIntent intent = CustomTabsFactory.make(
                context, backgroundColor, fontColor, R.drawable.ic_back)
                .addMenuItem(
                        context.getString(R.string.title_search),
                        PendingIntentFactory.makeSearchIntent(context)
                )
                .addMenuItem(
                        context.getString(R.string.title_settings_color),
                        PendingIntentFactory.makeColorSettingsIntent(context)
                )
                .addMenuItem(
                        context.getString(R.string.title_adding_favorite_search),
                        PendingIntentFactory.favoriteSearchAdding(context, category, query)
                )
                .build();
        intent.launchUrl(context, new UrlFactory().make(context, category, query));
    }

}
