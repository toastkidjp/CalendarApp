package jp.toastkid.calendar.settings.background;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import jp.toastkid.calendar.R;
import jp.toastkid.calendar.databinding.SavedImageBinding;
import jp.toastkid.calendar.libs.preference.PreferenceApplier;

/**
 * @author toastkidjp
 */
class ImagesViewAdapter extends RecyclerView.Adapter<ImagesViewHolder> {

    private PreferenceApplier preferenceApplier;

    private Storeroom storeroom;

    ImagesViewAdapter(
            final PreferenceApplier preferenceApplier,
            final Storeroom storeroom
            ) {
        this.preferenceApplier = preferenceApplier;
        this.storeroom = storeroom;
    }

    @Override
    public ImagesViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final SavedImageBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.saved_image,
                parent,
                false
        );
        return new ImagesViewHolder(itemBinding, preferenceApplier, this::notifyDataSetChanged);
    }

    @Override
    public void onBindViewHolder(final ImagesViewHolder holder, final int position) {
        holder.applyContent(storeroom.get(position));
    }

    @Override
    public int getItemCount() {
        return storeroom.getCount();
    }
}