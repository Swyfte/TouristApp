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

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    Context context;

    public ImageAdapter(List<Image> images) {
        this.images = images;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView photoView;
        public ImageButton fav_button;
        public ImageButton menu_button;
        public TextView titleView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.photoView);
            fav_button = itemView.findViewById(R.id.fav_button_img);
            menu_button = itemView.findViewById(R.id.options_button_img);
            titleView = itemView.findViewById(R.id.img_title);
        }
    }

    private List<Image> images;

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View imageView = inflater.inflate(R.layout.image_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(imageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Image image = images.get(position);

        ImageView photoView = holder.photoView;
        photoView.setImageBitmap(image.getPhoto());

        ImageButton fav_button = holder.fav_button;
        if(image.isFav()) {
            fav_button.setImageResource(R.drawable.ic_favorite_white_75percent);
        } else {
            fav_button.setImageResource(R.drawable.ic_favorite_border_white_50percent);
        }

        ImageButton menu_button = holder.menu_button;
        holder.menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu optionMenu = new PopupMenu(context, holder.menu_button);
                optionMenu.inflate(R.menu.image_options_menu);
                optionMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.edit_item:
                                return true;
                            case R.id.move_item:
                                return true;
                            case R.id.delete_item:
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                optionMenu.show();
            }
        });

        TextView titleView = holder.titleView;
        titleView.setText(image.getTitle());
    }

    @Override
    public int getItemCount() {
        return images.size();
    }


}
