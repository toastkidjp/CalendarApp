package jp.toastkid.calendar.search.favorite;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import jp.toastkid.calendar.databinding.FavoriteSearchItemBinding;

/**
 * Favorite Search item views holder.
 * @author toastkidjp
 */
class FavoriteSearchHolder extends RecyclerView.ViewHolder {

    private FavoriteSearchItemBinding binding;

    FavoriteSearchHolder(final FavoriteSearchItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    void setImageId(@DrawableRes final int iconId) {
        binding.favoriteSearchImage.setImageDrawable(
                AppCompatDrawableManager.get()
                        .getDrawable(binding.favoriteSearchImage.getContext(), iconId));
    }

    void setText(@NonNull final String query) {
        binding.favoriteSearchText.setText(query);
    }

    void setRemoveAction(final View.OnClickListener listener) {
        binding.favoriteSearchDelete.setOnClickListener(listener);
    }

    void setClickAction(final View.OnClickListener listener) {
        itemView.setOnClickListener(listener);
    }
}