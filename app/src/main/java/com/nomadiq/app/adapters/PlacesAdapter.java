package com.nomadiq.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.nomadiq.app.R;
import com.nomadiq.app.models.Place;
import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {
    // Изменил на protected и убрал final, чтобы наследники могли менять список
    protected List<Place> places;
    private final OnPlaceClickListener listener;

    public interface OnPlaceClickListener {
        void onPlaceClick(Place place);
    }

    public PlacesAdapter(List<Place> places, OnPlaceClickListener listener) {
        this.places = places;
        this.listener = listener;
    }

    public void setPlaces(List<Place> newPlaces) {
        this.places = newPlaces;
        notifyDataSetChanged();
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

        holder.placeCard.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (listener != null && adapterPosition != RecyclerView.NO_POSITION) {
                listener.onPlaceClick(places.get(adapterPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return (places != null) ? places.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public MaterialCardView placeCard;
        public TextView name, rating, tag;

        public ViewHolder(View itemView) {
            super(itemView);
            placeCard = itemView.findViewById(R.id.placeCard);
            name = itemView.findViewById(R.id.placeName);
            rating = itemView.findViewById(R.id.placeRating);
            tag = itemView.findViewById(R.id.placeTag);
        }
    }
}