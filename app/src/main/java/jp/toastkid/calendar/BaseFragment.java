package jp.toastkid.calendar;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

/**
 * @author toastkidjp
 */
public abstract class BaseFragment extends Fragment {

    /**
     * Return this fragment's title ID.
     *
     * @return
     */
    public abstract @StringRes int titleId();
}
