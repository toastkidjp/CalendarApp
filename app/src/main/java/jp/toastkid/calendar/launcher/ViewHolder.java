package jp.toastkid.calendar.launcher;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;

import jp.toastkid.calendar.R;
import jp.toastkid.calendar.databinding.AppLauncherItemBinding;

/**
 * @author toastkidjp
 */
class ViewHolder extends RecyclerView.ViewHolder{

    /** Binding object. */
    private AppLauncherItemBinding binding;

    /** Date and Time format. */
    private String format;

    /**
     *
     * @param binding
     */
    ViewHolder(
            final AppLauncherItemBinding binding
    ) {
        super(binding.getRoot());
        this.binding = binding;
        format = binding.appInstalled.getContext().getString(R.string.date_format);
    }

    void setImage(final Drawable drawable) {
        binding.appIcon.setImageDrawable(drawable);
    }

    void setTitle(final String text) {
        binding.appTitle.setText(text);
    }

    void setTargetSdk(final int targetSdkVersion) {
        binding.appTargetSdk.setText("Target SDK: " + targetSdkVersion);
    }

    void setPackageName(final String packageName) {
        binding.appPackageName.setText("Package Name: " + packageName);
    }

    void setInstalled(final long firstInstallTime) {
        binding.appInstalled.setText("Installed: " + DateFormat.format(format, firstInstallTime));
    }

    void setVersionInformation(final String versionText) {
        binding.appVersion.setText("Version: " + versionText);
    }
}
