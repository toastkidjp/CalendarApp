package jp.toastkid.calendar.search.favorite;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;

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

    private Adapter adapter;

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
        adapter = new Adapter(
                this,
                DbInitter.get(this).relationOfFavoriteSearch(),
                this::startSearch,
                messageId -> Toaster.snackShort(binding.favoriteSearchView, messageId, colorPair())
        );
        binding.favoriteSearchView.setAdapter(adapter);
        binding.favoriteSearchView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.RIGHT, ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(
                            final RecyclerView recyclerView,
                            final RecyclerView.ViewHolder viewHolder,
                            final RecyclerView.ViewHolder target
                    ) {
                        final int fromPos = viewHolder.getAdapterPosition();
                        final int toPos = target.getAdapterPosition();
                        adapter.notifyItemMoved(fromPos, toPos);
                        return true;
                    }

                    @Override
                    public void onSwiped(
                            final RecyclerView.ViewHolder viewHolder,
                            final int direction
                    ) {
                        if (direction != ItemTouchHelper.RIGHT) {
                            return;
                        }
                        adapter.removeAt(viewHolder.getAdapterPosition());
                    }
                }).attachToRecyclerView(binding.favoriteSearchView);
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
        new Addition(
                binding.additionArea,
                messageId -> Toaster.snackShort(binding.favoriteSearchView, messageId, colorPair())
        ).invoke();
    }

    @Override
    protected int getTitleId() {
        return R.string.title_favorite_search;
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