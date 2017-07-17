package jp.toastkid.calendar.search.favorite;

import android.content.Context;
import android.support.annotation.NonNull;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.libs.Toaster;

/**
 * @author toastkidjp
 */
class Insertion {

    private Context context;

    private String category;

    private String query;

    Insertion(
            @NonNull final Context context,
            @NonNull final String  category,
            @NonNull final String  query
    ) {
        this.context = context;
        this.category = category;
        this.query = query;
    }

    void insert() {
        insertFavoriteSearch(makeFavoriteSearch(category, query));
    }

    private void insertFavoriteSearch(final FavoriteSearch favoriteSearch) {
        Completable.create(e -> {
            DbInitter.get(context).insertIntoFavoriteSearch(favoriteSearch);
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> Toaster.tShort(
                        context,
                        context.getString(R.string.format_message_done_adding_favorite_search, query)));
    }

    private FavoriteSearch makeFavoriteSearch(final String category, final String query) {
        final FavoriteSearch fs = new FavoriteSearch();
        fs.category = category;
        fs.query    = query;
        return fs;
    }

}