package jp.toastkid.calendar.search.favorite;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.gfx.android.orma.Relation;
import com.github.gfx.android.orma.widget.OrmaRecyclerViewAdapter;

import io.reactivex.schedulers.Schedulers;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.search.SearchCategory;

/**
 * @author toastkidjp
 */
class Adapter extends OrmaRecyclerViewAdapter<FavoriteSearch, FavoriteSearchHolder> {

    interface SearchCallback {
        void accept(final SearchCategory category, final String query);
    }

    interface ToasterCallback {
        void accept(@StringRes final int message);
    }

    private LayoutInflater inflater;

    private SearchCallback searchAction;

    private ToasterCallback toasterCallback;

    Adapter(
            @NonNull final Context context,
            @NonNull final Relation<FavoriteSearch, ?> relation,
            @NonNull final SearchCallback searchAction,
            @NonNull final ToasterCallback toasterCallback
    ) {
        super(context, relation);
        this.inflater = LayoutInflater.from(context);
        this.searchAction = searchAction;
        this.toasterCallback = toasterCallback;
    }

    @Override
    public FavoriteSearchHolder onCreateViewHolder(
            final ViewGroup parent,
            final int viewType
    ) {
        return new FavoriteSearchHolder(
                DataBindingUtil
                        .inflate(inflater, R.layout.favorite_search_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(final FavoriteSearchHolder holder, final int position) {
        bindViews(holder, getRelation().get(position));
    }

    @Override
    public int getItemCount() {
        return getRelation().count();
    }

    void removeAt(final int position) {
        removeItemAsMaybe(getRelation().get(position))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void bindViews(final FavoriteSearchHolder holder, final FavoriteSearch favoriteSearch) {
        final SearchCategory category = SearchCategory.findByCategory(favoriteSearch.category);
        holder.setImageId(category.getIconId());

        final String query = favoriteSearch.query;
        holder.setText(query);

        holder.setClickAction(v -> searchAction.accept(category, query));

        holder.setRemoveAction(v -> {
            removeItemAsMaybe(favoriteSearch).subscribeOn(Schedulers.io()).subscribe();
            toasterCallback.accept(R.string.settings_color_delete);
        });
    }
}