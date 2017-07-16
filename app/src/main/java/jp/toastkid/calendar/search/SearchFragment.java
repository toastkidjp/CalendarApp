package jp.toastkid.calendar.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.ColorUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.analytics.LogSender;
import jp.toastkid.calendar.databinding.FragmentSearchBinding;
import jp.toastkid.calendar.libs.Colors;
import jp.toastkid.calendar.libs.Inputs;
import jp.toastkid.calendar.libs.Logger;
import jp.toastkid.calendar.libs.network.NetworkChecker;
import jp.toastkid.calendar.libs.preference.ColorPair;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;
import jp.toastkid.calendar.main.MainActivity;
import jp.toastkid.calendar.search.favorite.AddingFavoriteSearchService;
import jp.toastkid.calendar.search.suggest.SuggestAdapter;
import jp.toastkid.calendar.search.suggest.SuggestFetcher;

/**
 * Search activity.
 *
 * @author toastkidjp
 */
public class SearchFragment extends Fragment {

    /** Key of extra. */
    private static final String EXTRA_KEY_FINISH_SOON = "finish_soon";

    /** Layout ID. */
    private static final int LAYOUT_ID = R.layout.fragment_search;

    /** Suggest cache capacity. */
    public static final int SUGGEST_CACHE_CAPACITY = 30;

    /** View binder. */
    private FragmentSearchBinding binding;

    /** Suggest Adapter. */
    private SuggestAdapter mSuggestAdapter;

    private PreferenceApplier preferenceApplier;

    private LogSender logSender;

    private CompositeDisposable disposables;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposables = new CompositeDisposable();

        /*binding.searchBar.inflateMenu(R.menu.search_menu);
        binding.searchBar.getMenu().findItem(R.id.suggest_check)
                .setChecked(getPreferenceApplier().isEnableSuggest());
*/
        /*final Intent intent = getIntent();
        if (intent != null && intent.hasExtra(SearchManager.QUERY)) {
            final String category = intent.hasExtra(AddingFavoriteSearchService.EXTRA_KEY_CATEGORY)
                    ? intent.getStringExtra(AddingFavoriteSearchService.EXTRA_KEY_CATEGORY)
                    : SearchCategory.WEB.name();
            search(category, intent.getStringExtra(SearchManager.QUERY));
            if (intent.getBooleanExtra(EXTRA_KEY_FINISH_SOON, false)) {
                finish();
            }
        }*/
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

        preferenceApplier = new PreferenceApplier(context);
        logSender = new LogSender(getActivity());
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
        return binding.getRoot();
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

        final Bundle bundle = new Bundle();
        bundle.putString("category", category);
        bundle.putString("query", query);
        logSender.send("search", bundle);

        final ColorPair colorPair = preferenceApplier.colorPair();
        new SearchIntentLauncher(getActivity())
                .setBackgroundColor(colorPair.bgColor())
                .setFontColor(colorPair.fontColor())
                .setCategory(category)
                .setQuery(query)
                .invoke();
    }

    /**
     * Make launcher intent.
     * @param context
     * @return launcher intent
     */
    public static Intent makeIntent(@NonNull final Context context) {
        return makeIntent(context, "");
    }

    /**
     * Make launcher intent with search query.
     * @param context
     * @param query
     * @return launcher intent
     */
    public static Intent makeIntent(
            @NonNull final Context context,
            @NonNull final String  query
            ) {
        final Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (query.length() != 0) {
            intent.putExtra(SearchManager.QUERY, query);
        }
        return intent;
    }

    /**
     * Make launcher intent.
     * @param context
     * @param category
     * @param query
     * @param finishSoon
     * @return launcher intent
     */
    public static Intent makeShortcutIntent(
            @NonNull final Context context,
            @NonNull final SearchCategory category,
            @NonNull final String query,
            final boolean finishSoon
    ) {
        final Intent intent = makeIntent(context);
        intent.putExtra(AddingFavoriteSearchService.EXTRA_KEY_CATEGORY, category.name());
        intent.putExtra(SearchManager.QUERY,   query);
        intent.putExtra(EXTRA_KEY_FINISH_SOON, finishSoon);
        return intent;
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
}
