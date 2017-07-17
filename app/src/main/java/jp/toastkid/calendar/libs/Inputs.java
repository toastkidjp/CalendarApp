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
        if (!inputMethodManager.isActive()) {

            Logger.i("Active.");
            return;
        }
        inputMethodManager.showSoftInput(editText, 0);
    }

    /**
     * For Fragment.
     * @param activity
     */
    public static void toggle(final Activity activity) {
        final InputMethodManager inputMethodManager
                = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(
                InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * Hide software keyboard.
     * @param v
     */
    public static void hideKeyboard(final View v) {
        final InputMethodManager manager = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!manager.isActive()) {
            return;
        }
        manager.hideSoftInputFromWindow(
                v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
