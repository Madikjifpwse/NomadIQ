package com.nomadiq.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nomadiq.app.R;
import com.nomadiq.app.models.Place;
import java.util.List;

public class VisitedPlacesAdapter extends RecyclerView.Adapter<VisitedPlacesAdapter.ViewHolder> {
    private List<Place> places;
    private OnPlaceClickListener listener;

    public interface OnPlaceClickListener {
        void onPlaceClick(Place place);
    }

    public VisitedPlacesAdapter(List<Place> places, OnPlaceClickListener listener) {
        this.places = places;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = places.get(position);
        holder.name.setText(place.getName());
        holder.rating.setText(String.valueOf(place.getRating()));
        holder.tag.setText(place.getCategory());

        // Клик по всей карточке
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPlaceClick(place);
        });
    }

    @Override
    public int getItemCount() { return places != null ? places.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, rating, tag;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.placeName);
            rating = itemView.findViewById(R.id.placeRating);
            tag = itemView.findViewById(R.id.placeTag);
        }
    }
}