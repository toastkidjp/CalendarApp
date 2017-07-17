package jp.toastkid.calendar.search.favorite;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.toastkid.calendar.R;
import jp.toastkid.calendar.databinding.FragmentFavoriteSearchBinding;
import jp.toastkid.calendar.libs.Toaster;
import jp.toastkid.calendar.libs.preference.ColorPair;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;
import jp.toastkid.calendar.search.SearchAction;
import jp.toastkid.calendar.search.SearchCategory;

/**
 * Favorite search fragment.
 *
 * @author toastkidjp
 */
public class FavoriteSearchFragment extends Fragment {

    /** Layout ID. */
    private static final int LAYOUT_ID = R.layout.fragment_favorite_search;

    /** RecyclerView's adapter */
    private Adapter adapter;

    /** Data Binding object. */
    private FragmentFavoriteSearchBinding binding;

    /** Preferences wrapper. */
    private PreferenceApplier preferenceApplier;

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, LAYOUT_ID, container, false);
        binding.setActivity(this);

        initFavSearchsView();

        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    private void initFavSearchsView() {
        adapter = new Adapter(
                getActivity(),
                DbInitter.get(getActivity()).relationOfFavoriteSearch(),
                this::startSearch,
                messageId -> Toaster.snackShort(binding.favoriteSearchView, messageId, colorPair())
        );
        binding.favoriteSearchView.setAdapter(adapter);
        binding.favoriteSearchView.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
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
        new SearchAction(getActivity(), category.name(), query).invoke();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.favorite_toolbar_menu, menu);

        menu.findItem(R.id.favorite_toolbar_menu_clear).setOnMenuItemClickListener(v -> {
            new Clear(binding.favoriteSearchView, adapter.getRelation().deleter()).invoke();
            return true;
        });

        menu.findItem(R.id.favorite_toolbar_menu_add).setOnMenuItemClickListener(v -> {
            invokeAddition();
            return true;
        });
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

    private ColorPair colorPair() {
        if (preferenceApplier == null) {
            preferenceApplier = new PreferenceApplier(getContext());
        }
        return preferenceApplier.colorPair();
    }
}