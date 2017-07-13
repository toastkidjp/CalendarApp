package jp.toastkid.calendar.search.favorite;

import android.content.Context;

/**
 * @author toastkidjp
 */
class DbInitter {

    private static OrmaDatabase orma;

    public static OrmaDatabase get(final Context context) {
        if (orma == null) {
            orma = OrmaDatabase.builder(context).name("favorite_search.db").build();
        }
        return orma;
    }
}