package jp.toastkid.calendar.calendar;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import jp.toastkid.calendar.R;
import jp.toastkid.calendar.libs.intent.CustomTabsFactory;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;

/**
 * @author toastkidjp
 */
public class CalendarArticleLinker {

    private Context context;

    private final PreferenceApplier applier;

    private int month;

    private int dayOfMonth;

    /**
     *
     * @param context
     * @param month
     * @param dayOfMonth
     */
    public CalendarArticleLinker(
            @NonNull final Context context,
            final int month,
            final int dayOfMonth
    ) {
        this.context    = context;
        applier         = new PreferenceApplier(context);
        this.month      = month;
        this.dayOfMonth = dayOfMonth;
    }

    public void invoke() {
        openCalendarArticle();
    }

    /**
     * Open calendar wikipedia article.
     */
    private void openCalendarArticle() {
        final String url = DateArticleUrlFactory.make(context, month, dayOfMonth);
        if (url.length() == 0) {
            return;
        }
        CustomTabsFactory
                .make(context, applier.colorPair(), R.drawable.ic_back)
                .build()
                .launchUrl(context, Uri.parse(url));
    }

}
