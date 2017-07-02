package jp.toastkid.calendar.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import jp.toastkid.calendar.BaseActivity;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.databinding.ActivitySearchBinding;
import jp.toastkid.calendar.libs.ImageLoader;
import jp.toastkid.calendar.libs.Inputs;
import jp.toastkid.calendar.libs.network.NetworkChecker;
import jp.toastkid.calendar.libs.preference.ColorPair;
import jp.toastkid.calendar.search.suggest.SuggestAdapter;
import jp.toastkid.calendar.search.suggest.SuggestFetcher;

/**
 * Search activity.
 *
 * @author toastkidjp
 */
public class SearchActivity extends BaseActivity {

    /** Key of extra. */
    private static final String EXTRA_KEY_FINISH_SOON = "finish_soon";

    /** Suggest cache capacity. */
    public static final int SUGGEST_CACHE_CAPACITY = 30;

    /** View binder. */
    private ActivitySearchBinding binding;

    /** Suggest Adapter. */
    private SuggestAdapter mSuggestAdapter;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        SearchCategorySpinnerInitializer.initialize(binding.searchCategories);
        initSuggests();
        initSearchInput();
        initToolbar(binding.searchToolbar);

        binding.searchToolbar.inflateMenu(R.menu.search_menu);
        binding.searchToolbar.getMenu().findItem(R.id.suggest_check)
                .setChecked(getPreferenceApplier().isEnableSuggest());

        final Intent intent = getIntent();
        if (intent != null && intent.hasExtra(SearchManager.QUERY)) {
            final String category = SearchCategory.WEB.name();
            search(category, intent.getStringExtra(SearchManager.QUERY));
            if (intent.getBooleanExtra(EXTRA_KEY_FINISH_SOON, false)) {
                finish();
            }
        }

        binding.searchClear.setOnClickListener(v -> binding.searchInput.setText(""));
    }

    private void initSuggests() {
        mSuggestAdapter = new SuggestAdapter(
                LayoutInflater.from(this),
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

            private final SuggestFetcher mFetcher = new SuggestFetcher(SearchActivity.this);

            private final Map<String, List<String>> mCache = new HashMap<>(SUGGEST_CACHE_CAPACITY);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // NOP.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (getPreferenceApplier().isDisableSuggest()) {
                    mSuggestAdapter.clear();
                    return;
                }

                final String key = s.toString();
                if (mCache.containsKey(key)) {
                    replaceSuggests(mCache.get(key));
                    return;
                }

                if (NetworkChecker.isNotAvailable(SearchActivity.this)) {
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
        runOnUiThread(() -> {
            binding.searchSuggests.setVisibility(View.VISIBLE);
            mSuggestAdapter.replace(suggests);
            mSuggestAdapter.notifyDataSetChanged();
            mSuggestAdapter.notifyDataSetInvalidated();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Inputs.showKeyboard(this, binding.searchInput);
        applyColor();

        ImageLoader.setImageToImageView(binding.searchBackground, getBackgroundImagePath());
    }

    /**
     * Apply color to views.
     */
    private void applyColor() {
        final ColorPair colorPair = colorPair();
        final int bgColor   = colorPair.bgColor();
        final int fontColor = colorPair.fontColor();
        applyColorToToolbar(binding.searchToolbar);
        binding.searchInput.setTextColor(fontColor);
        binding.searchInput.setHintTextColor(fontColor);
        binding.searchInput.setHighlightColor(fontColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ColorUtils.setAlphaComponent(bgColor, 255));
        }

        binding.searchActionBackground.setBackgroundColor(ColorUtils.setAlphaComponent(bgColor, 128));
        binding.searchAction.setTextColor(fontColor);
        binding.searchAction.setOnClickListener(view -> search(
                binding.searchCategories.getSelectedItem().toString(),
                binding.searchInput.getText().toString())
        );
        binding.searchClear.setColorFilter(fontColor);
        binding.searchInputBorder.setBackgroundColor(fontColor);
    }

    /**
     * Close this activity.
     */
    private void close() {
        finish();
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
        sendLog("search", bundle);

        final ColorPair colorPair = colorPair();
        new SearchIntentLauncher(this)
                .setBackgroundColor(colorPair.bgColor())
                .setFontColor(colorPair.fontColor())
                .setCategory(category)
                .setQuery(query)
                .invoke();
    }

    @Override
    protected int getTitleId() {
        return R.string.title_search_action;
    }

    @Override
    protected boolean clickMenu(final MenuItem item) {
        if (item.getItemId() == R.id.suggest_check) {
            getPreferenceApplier().switchEnableSuggest();
            item.setChecked(getPreferenceApplier().isEnableSuggest());
            return true;
        }
        return super.clickMenu(item);
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
        final Intent intent = new Intent(context, SearchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (query.length() != 0) {
            intent.putExtra(SearchManager.QUERY, query);
        }
        return intent;
    }

}
