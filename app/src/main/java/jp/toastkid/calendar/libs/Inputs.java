package jp.toastkid.calendar.libs;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @author toastkidjp
 */
public class Inputs {

    /**
     * Show software keyboard.
     * @param activity
     * @param editText
     */
    public static void showKeyboard(final Activity activity, final EditText editText) {
        final InputMethodManager inputMethodManager
                = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, 0);
    }

    /**
     * Hide software keyboard.
     * @param v
     */
    public static void hideKeyboard(final View v) {
        final InputMethodManager manager = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(
                v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
