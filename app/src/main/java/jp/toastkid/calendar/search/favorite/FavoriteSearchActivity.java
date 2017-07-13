package jp.toastkid.calendar.search.favorite;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.gfx.android.orma.Relation;
import com.github.gfx.android.orma.widget.OrmaRecyclerViewAdapter;

import io.reactivex.schedulers.Schedulers;
import jp.toastkid.calendar.BaseActivity;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.databinding.ActivityFavoriteSearchBinding;
import jp.toastkid.calendar.libs.ImageLoader;
import jp.toastkid.calendar.libs.Toaster;
import jp.toastkid.calendar.search.SearchActivity;
import jp.toastkid.calendar.search.SearchCategory;

/**
 * @author toastkidjp
 */
public class FavoriteSearchActivity extends BaseActivity {

    private static final int LAYOUT_ID = R.layout.activity_favorite_search;

    private OrmaRecyclerViewAdapter<FavoriteSearch, FavoriteSearchHolder> adapter;

    private ActivityFavoriteSearchBinding binding;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT_ID);
        binding = DataBindingUtil.setContentView(this, LAYOUT_ID);
        binding.setActivity(this);

        initToolbar(binding.toolbar);
        binding.toolbar.inflateMenu(R.menu.favorite_toolbar_menu);
        initFavSearchsView();
    }

    private void initFavSearchsView() {
        adapter = new Adapter(this, DbInitter.get(this).relationOfFavoriteSearch());
        binding.favoriteSearchView.setAdapter(adapter);
        binding.favoriteSearchView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void bindViews(final FavoriteSearchHolder holder, final FavoriteSearch favoriteSearch) {
        final SearchCategory category = SearchCategory.findByCategory(favoriteSearch.category);
        holder.setImageId(category.getIconId());

        final String query = favoriteSearch.query;
        holder.setText(query);

        holder.setClickAction(v -> startSearch(category, query));

        holder.setRemoveAction(v -> {
            adapter.removeItemAsMaybe(favoriteSearch).subscribeOn(Schedulers.io()).subscribe();
            Toaster.snackShort(binding.favoriteSearchView, R.string.settings_color_delete, colorPair());
        });
    }

    /**
     * Start search action.
     * @param category Search category
     * @param query    Search query
     */
    private void startSearch(final SearchCategory category, final String query) {
        startActivity(SearchActivity.makeShortcutIntent(this, category, query, true));
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyColorToToolbar(binding.toolbar);
        ImageLoader.setImageToImageView(binding.background, getBackgroundImagePath());
    }

    @Override
    protected boolean clickMenu(final MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.favorite_toolbar_menu_clear) {
            new Clear(binding.toolbar, adapter.getRelation().deleter()).invoke();
        }
        if (itemId == R.id.favorite_toolbar_menu_add) {
            invokeAddition();
        }
        return super.clickMenu(item);
    }

    public void add(final View v) {
        invokeAddition();
    }

    private void invokeAddition() {
        new Addition(binding.toolbar).invoke();
    }

    @Override
    protected int getTitleId() {
        return R.string.title_favorite_search;
    }

    private class Adapter extends OrmaRecyclerViewAdapter<FavoriteSearch, FavoriteSearchHolder> {

        private LayoutInflater inflater;

        Adapter(
                @NonNull final Context context,
                @NonNull final Relation<FavoriteSearch, ?> relation
        ) {
            super(context, relation);
            inflater = LayoutInflater.from(context);
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
            bindViews(holder, adapter.getRelation().get(position));
        }

        @Override
        public int getItemCount() {
            return adapter.getRelation().count();
        }
    }

    /**
     * Make launcher intent.
     * @param context
     * @return {@link FavoriteSearchActivity} launcher intent
     */
    public static Intent makeIntent(final Context context) {
        final Intent intent = new Intent(context, FavoriteSearchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }
}