package jp.toastkid.calendar.search;

import android.content.Context;
import android.net.Uri;

/**
 * @author toastkidjp
 */
class UrlFactory {

    Uri make(final Context context, final String category, final String query) {
        return Uri.parse(SearchCategory.findByCategory(category).make(context, query));
    }

}
