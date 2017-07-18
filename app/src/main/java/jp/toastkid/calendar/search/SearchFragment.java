package jp.toastkid.calendar.search;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.ColorUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import jp.toastkid.calendar.BaseFragment;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.databinding.FragmentSearchBinding;
import jp.toastkid.calendar.libs.Colors;
import jp.toastkid.calendar.libs.Inputs;
import jp.toastkid.calendar.libs.network.NetworkChecker;
import jp.toastkid.calendar.libs.preference.ColorPair;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;
import jp.toastkid.calendar.search.suggest.SuggestAdapter;
import jp.toastkid.calendar.search.suggest.SuggestFetcher;

/**
 * Search activity.
 *
 * @author toastkidjp
 */
public class SearchFragment extends BaseFragment {

    /** Layout ID. */
    private static final int LAYOUT_ID = R.layout.fragment_search;

    /** Suggest cache capacity. */
    public static final int SUGGEST_CACHE_CAPACITY = 30;

    /** View binder. */
    private FragmentSearchBinding binding;

    /** Suggest Adapter. */
    private SuggestAdapter mSuggestAdapter;

    private PreferenceApplier preferenceApplier;

    private CompositeDisposable disposables;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposables = new CompositeDisposable();
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

        preferenceApplier = new PreferenceApplier(context);
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, LAYOUT_ID, container, false);
        binding.searchClear.setOnClickListener(v -> binding.searchInput.setText(""));
        SearchCategorySpinnerInitializer.initialize(binding.searchCategories);

        initSearchInput();

        applyColor();

        initSuggests(inflater);

        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);

        final MenuItem item = menu.findItem(R.id.suggest_check);
        item.setChecked(preferenceApplier.isEnableSuggest());
        item.setOnMenuItemClickListener(i -> {
            preferenceApplier.switchEnableSuggest();
            i.setChecked(preferenceApplier.isEnableSuggest());
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Inputs.toggle(getActivity());
    }

    private void initSuggests(final LayoutInflater inflater) {
        mSuggestAdapter = new SuggestAdapter(
                inflater,
                binding.searchInput,
                suggest -> search(binding.searchCategories.getSelectedItem().toString(), suggest)
                );
        binding.searchSuggests.setAdapter(mSuggestAdapter);
    }

    private void initSearchInput() {
        binding.searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId != EditorInfo.IME_ACTION_SEARCH) {
                return false;
            }
            search(binding.searchCategories.getSelectedItem().toString(), v.getText().toString());
            return true;
        });

        binding.searchInput.addTextChangedListener(new TextWatcher() {

            private final SuggestFetcher mFetcher = new SuggestFetcher(getActivity());

            private final Map<String, List<String>> mCache = new HashMap<>(SUGGEST_CACHE_CAPACITY);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // NOP.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (preferenceApplier.isDisableSuggest()) {
                    mSuggestAdapter.clear();
                    return;
                }

                final String key = s.toString();
                if (mCache.containsKey(key)) {
                    replaceSuggests(mCache.get(key));
                    return;
                }

                if (NetworkChecker.isNotAvailable(getActivity())) {
                    return;
                }

                mFetcher.fetchAsync(key, suggests -> {
                    if (suggests == null || suggests.isEmpty()) {
                        Completable.create(e -> {
                            binding.searchSuggests.setVisibility(View.GONE);
                            e.onComplete();
                        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                        return;
                    }
                    replaceSuggests(suggests);
                    mCache.put(key, suggests);
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
                // NOP.
            }
        });
    }

    /**
     * Replace suggests with specified items.
     * @param suggests
     */
    private void replaceSuggests(final List<String> suggests) {

        Observable.fromIterable(suggests)
                .doOnNext(mSuggestAdapter::add)
                .doOnSubscribe(d -> {
                    disposables.add(d);
                    mSuggestAdapter.clear();
                })
                .doOnTerminate(() -> {
                    binding.searchSuggests.setVisibility(View.VISIBLE);
                    if (mSuggestAdapter.isEmpty()) {
                        mSuggestAdapter.notifyDataSetInvalidated();
                    } else {
                        mSuggestAdapter.notifyDataSetChanged();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    /**
     * Apply color to views.
     */
    private void applyColor() {
        final ColorPair colorPair = preferenceApplier.colorPair();
        final int bgColor   = colorPair.bgColor();
        final int fontColor = colorPair.fontColor();
        Colors.setTextColor(binding.searchInput, bgColor);

        binding.searchActionBackground.setBackgroundColor(ColorUtils.setAlphaComponent(bgColor, 128));
        binding.searchAction.setTextColor(fontColor);
        binding.searchAction.setOnClickListener(view -> search(
                binding.searchCategories.getSelectedItem().toString(),
                binding.searchInput.getText().toString())
        );
        binding.searchIcon.setColorFilter(bgColor);
        binding.searchClear.setColorFilter(bgColor);
        binding.searchInputBorder.setBackgroundColor(bgColor);
    }

    /**
     * Open search result.
     *
     * @param category search category
     * @param query    search query
     */
    private void search(final String category, final String query) {
        new SearchAction(getActivity(), category, query).invoke();
    }

    @Override
    public void onPause() {
        super.onPause();
        Inputs.hideKeyboard(binding.searchInput);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        disposables.dispose();
    }

    @Override
    public int titleId() {
        return R.string.title_search;
    }
}
