package jp.toastkid.calendar.search;

import android.net.Uri;

/**
 * @author toastkidjp
 */
class UrlFactory {

    Uri make(final String category, final String query) {
        return Uri.parse(SearchCategory.findByCategory(category).make(query));
    }

}
