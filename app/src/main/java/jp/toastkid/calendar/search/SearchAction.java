package jp.toastkid.calendar.search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import jp.toastkid.calendar.analytics.LogSender;
import jp.toastkid.calendar.libs.preference.ColorPair;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;

/**
 * @author toastkidjp
 */
public class SearchAction {

    private final Context activityContext;

    private final LogSender logSender;

    private final PreferenceApplier preferenceApplier;

    private final String category;

    private final String query;

    public SearchAction(
            @NonNull final Context activityContext,
            @NonNull final String category,
            @NonNull final String query
    ) {
        this.activityContext = activityContext;
        this.logSender = new LogSender(activityContext);
        this.preferenceApplier = new PreferenceApplier(activityContext);
        this.category = category;
        this.query = query;
    }

    public void invoke() {
        final Bundle bundle = new Bundle();
        bundle.putString("category", category);
        bundle.putString("query", query);
        logSender.send("search", bundle);

        final ColorPair colorPair = preferenceApplier.colorPair();
        new SearchIntentLauncher(activityContext)
                .setBackgroundColor(colorPair.bgColor())
                .setFontColor(colorPair.fontColor())
                .setCategory(category)
                .setQuery(query)
                .invoke();
    }
}
