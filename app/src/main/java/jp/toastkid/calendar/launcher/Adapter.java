package jp.toastkid.calendar.launcher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import jp.toastkid.calendar.R;
import jp.toastkid.calendar.databinding.AppLauncherItemBinding;
import jp.toastkid.calendar.libs.Toaster;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;

/**
 * @author toastkidjp
 */
class Adapter extends RecyclerView.Adapter<ViewHolder> {

    private final Context context;

    private final View parent;

    private final List<ApplicationInfo> master;

    private final List<ApplicationInfo> installedApps;

    private final PreferenceApplier preferenceApplier;

    private final PackageManager packageManager;

    Adapter(final Context context, final View parent) {
        this.packageManager = context.getPackageManager();
        this.context = context;
        this.parent = parent;
        this.master = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        this.installedApps = new ArrayList<>(master);
        this.preferenceApplier = new PreferenceApplier(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final AppLauncherItemBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.app_launcher_item,
                parent,
                false
        );
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ApplicationInfo info = installedApps.get(position);
        holder.setImage(info.loadIcon(packageManager));
        holder.setTitle(info.loadLabel(packageManager).toString());
        holder.setTargetSdk(info.targetSdkVersion);
        holder.setPackageName(info.packageName);
        try {
            final PackageInfo packageInfo
                    = packageManager.getPackageInfo(info.packageName, PackageManager.GET_META_DATA);
            holder.setVersionInformation(packageInfo.versionName + "(" + packageInfo.versionCode + ")");
            holder.setInstalled(packageInfo.firstInstallTime);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        final Intent intent = packageManager.getLaunchIntentForPackage(info.packageName);
        if (intent == null) {
            holder.itemView.setOnClickListener(v ->
                    Toaster.snackShort(
                            parent,
                            R.string.message_failed_launching,
                            preferenceApplier.colorPair()
                    )
            );
        } else {
            holder.itemView.setOnClickListener(v -> context.startActivity(intent));
        }
    }

    void filter(final String str) {
        installedApps.clear();
        if (TextUtils.isEmpty(str)) {
            installedApps.addAll(master);
            notifyDataSetChanged();
            return;
        }
        Observable.fromIterable(master)
                .filter(appInfo -> appInfo.packageName.contains(str))
                .subscribeOn(Schedulers.trampoline())
                .doOnComplete(this::notifyDataSetChanged)
                .subscribe(installedApps::add);
    }

    @Override
    public int getItemCount() {
        return installedApps.size();
    }
}
