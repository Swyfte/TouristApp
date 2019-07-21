package com.example.iamatourist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView topImg;
        ImageView midImg;
        ImageView botImg;
        TextView tripTitle;
        ImageButton menuBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            topImg = itemView.findViewById(R.id.TopImage);
            midImg = itemView.findViewById(R.id.MidImage);
            botImg = itemView.findViewById(R.id.BottomImage);
            tripTitle = itemView.findViewById(R.id.tripName);
            menuBtn = itemView.findViewById(R.id.trip_menu_button);
        }
    }

    private List<Trip> trips;

    @NonNull
    @Override
    public TripAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tripView = inflater.inflate(R.layout.trip_layout, parent, false);
        return new ViewHolder(tripView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Trip trip = trips.get(position);

        ImageView topPhoto = holder.topImg;
        if (trip.getGallerySize() > 0) {
            topPhoto.setImageBitmap(trip.getImageAtPos(0).getPhoto());
        }
        ImageView midPhoto = holder.midImg;
        if (trip.getGallerySize() > 1) {
            midPhoto.setImageBitmap(trip.getImageAtPos(1).getPhoto());
        }
        ImageView botPhoto = holder.botImg;
        if (trip.getGallerySize() > 2) {
            botPhoto.setImageBitmap(trip.getImageAtPos(2).getPhoto());
        }

        TextView title = holder.tripTitle;
        title.setText(trip.getTitle());

        ImageButton menuButton = holder.menuBtn;
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu optionMenu = new PopupMenu(context, holder.menuBtn);
                optionMenu.inflate(R.menu.trip_options_menu);
                optionMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.trip_details:
                                return true;
                            case R.id.trip_delete:
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                optionMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return trips.size();
    }


}
