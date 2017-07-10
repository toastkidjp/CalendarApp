package jp.toastkid.calendar.launcher;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;

import jp.toastkid.calendar.BaseActivity;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.databinding.ActivityLauncherBinding;

/**
 * App Launcher.
 *
 * @author toastkidjp
 */
public class LauncherActivity extends BaseActivity {

    /** Layout ID. */
    private static final int LAYOUT_ID = R.layout.activity_launcher;

    /** Binding object. */
    private ActivityLauncherBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT_ID);
        binding = DataBindingUtil.setContentView(this, LAYOUT_ID);

        initToolbar(binding.toolbar);

        binding.appItemsView.setLayoutManager(
                new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false));

        final Adapter adapter = new Adapter(this, binding.toolbar);
        binding.appItemsView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyColorToToolbar(binding.toolbar);
    }

    @Override
    protected int getTitleId() {
        return R.string.title_apps_launcher;
    }

    /**
     * Make launcher intent.
     * @param context
     * @return
     */
    public static Intent makeIntent(final Context context) {
        final Intent intent = new Intent(context, LauncherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }
}
