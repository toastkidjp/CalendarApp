package jp.toastkid.calendar.settings.background;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import jp.toastkid.calendar.R;
import jp.toastkid.calendar.databinding.SavedImageBinding;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;

/**
 * RecyclerView's adapter.
 *
 * @author toastkidjp
 */
class Adapter extends RecyclerView.Adapter<ViewHolder> {

    /** Preferences wrapper. */
    private PreferenceApplier preferenceApplier;

    /** FilesDir wrapper. */
    private Storeroom storeroom;

    /**
     *
     * @param preferenceApplier
     * @param storeroom
     */
    Adapter(final PreferenceApplier preferenceApplier, final Storeroom storeroom) {
        this.preferenceApplier = preferenceApplier;
        this.storeroom = storeroom;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final SavedImageBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.saved_image,
                parent,
                false
        );
        return new ViewHolder(itemBinding, preferenceApplier, this::notifyDataSetChanged);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.applyContent(storeroom.get(position));
    }

    @Override
    public int getItemCount() {
        return storeroom.getCount();
    }
}