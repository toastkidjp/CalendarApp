package jp.toastkid.calendar.launcher;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;

import jp.toastkid.calendar.BaseActivity;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.databinding.ActivityLauncherBinding;
import jp.toastkid.calendar.libs.ImageLoader;

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
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        final Adapter adapter = new Adapter(this, binding.toolbar);
        binding.appItemsView.setAdapter(adapter);
        binding.filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // NOP.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // NOP.
            }
        });
        binding.toolbar.inflateMenu(R.menu.launcher);
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

        final int itemCount = binding.appItemsView.getAdapter().getItemCount();

        if (itemId == R.id.to_top) {
            if (itemCount > 30) {
                binding.appItemsView.scrollToPosition(0);
                return true;
            }
            binding.appItemsView.smoothScrollToPosition(0);
            return true;
        }

        if (itemId == R.id.to_bottom) {
            if (itemCount > 30) {
                binding.appItemsView.scrollToPosition(itemCount - 1);
                return true;
            }
            binding.appItemsView.smoothScrollToPosition(itemCount);
            return true;
        }

        return super.clickMenu(item);
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
