package com.pdf.and.image.cropper;

// ImageAdapter.java
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context context;
    private List<ImageItem> imageItems;

    public ImageAdapter(Context context, List<ImageItem> imageItems) {
        this.context = context;
        this.imageItems = imageItems;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageItem imageItem = imageItems.get(position);
        Uri uri = imageItem.getUri();
        holder.imageView.setImageURI(uri);
    }

    @Override
    public int getItemCount() {
        return imageItems.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
