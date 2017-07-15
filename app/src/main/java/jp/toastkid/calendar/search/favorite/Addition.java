package jp.toastkid.calendar.search.favorite;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.MessageFormat;

import jp.toastkid.calendar.R;
import jp.toastkid.calendar.databinding.FavoriteSearchAdditionDialogContentBinding;
import jp.toastkid.calendar.libs.Colors;
import jp.toastkid.calendar.libs.Inputs;
import jp.toastkid.calendar.libs.functions.SingleValueCallback;
import jp.toastkid.calendar.libs.preference.ColorPair;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;
import jp.toastkid.calendar.search.SearchCategorySpinnerInitializer;

/**
 * Show input dialog and call inserting action.
 *
 * @author toastkidjp
 */
public class Addition {

    @LayoutRes
    private static final int LAYOUT_ID = R.layout.favorite_search_addition_dialog_content;

    /** Context. */
    private final Context context;

    private final FavoriteSearchAdditionDialogContentBinding binding;

    /** For using extract background color. */
    private final ViewGroup parent;

    private final SingleValueCallback<String> toasterCallback;

    private final Spinner categorySelector;

    private final EditText input;

    /**
     *
     * @param view
     */
    Addition(@NonNull final ViewGroup view, @NonNull final SingleValueCallback<String> callback) {
        this.context = view.getContext();
        this.parent = view;

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                LAYOUT_ID,
                view,
                false
        );
        this.toasterCallback = callback;
        binding.setAction(this);
        final View content = binding.getRoot();

        categorySelector = initSpinner(content);

        input = initInput(content);

        if (view.getChildCount() == 0) {
            view.addView(content);
        }
    }

    /**
     * Show input dialog.
     */
    void invoke() {
        final ColorPair colorPair = new PreferenceApplier(context).colorPair();
        Colors.setBgAndText(binding.close, colorPair);
        Colors.setBgAndText(binding.add,   colorPair);
        parent.setVisibility(View.VISIBLE);
    }

    /**
     * Initialize spinner.
     * @param content
     * @return
     */
    @NonNull
    private Spinner initSpinner(final View content) {
        final Spinner categorySelector
                = (Spinner) content.findViewById(R.id.favorite_search_addition_categories);
        SearchCategorySpinnerInitializer.initialize(categorySelector);
        return categorySelector;
    }

    /**
     * Initialize input field.
     * @param content
     * @return
     */
    @NonNull
    private EditText initInput(final View content) {
        final TextInputLayout inputLayout
                = (TextInputLayout) content.findViewById(R.id.favorite_search_addition_query);

        final EditText input = inputLayout.getEditText();
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (s.toString().length() == 0) {
                    inputLayout.setError(
                            context.getString(R.string.favorite_search_addition_dialog_empty_message));
                    return;
                }
                inputLayout.setErrorEnabled(false);
            }
        });
        return input;
    }

    public void cancel(final View v) {
        parent.setVisibility(View.GONE);
        Inputs.hideKeyboard(input);
    }

    public void ok(final View v) {
        final String query = input.getText().toString();

        if (TextUtils.isEmpty(query)) {
            toasterCallback.accept(context.getString(R.string.favorite_search_addition_dialog_empty_message));
            return;
        }

        final String category = categorySelector.getSelectedItem().toString();

        new Insertion(context, category, query).insert();

        final String message = MessageFormat.format(
                context.getString(R.string.favorite_search_addition_successful_format),
                query
        );
        toasterCallback.accept(message);
    }
}