package com.example.musicmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ArtistAdapter extends ArrayAdapter<Artist> {
    private final Context context;
    private final ArrayList<Artist> items;

    public ArtistAdapter(Context context, ArrayList<Artist> items) {
        super(context, R.layout.artist_item, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.artist_item, parent, false);

        TextView txtMacasi = rowView.findViewById(R.id.edtmacasi);
        TextView txtTencasi = rowView.findViewById(R.id.edttencasi);
        TextView txtSinhnam = rowView.findViewById(R.id.edtsinhnam);
        ImageView imageView = rowView.findViewById(R.id.image_view);

        Artist currentItem = items.get(position);

        txtMacasi.setText(currentItem.getMacasi());
        txtTencasi.setText(currentItem.getTencasi());
        txtSinhnam.setText(String.valueOf(currentItem.getSinhnam()));

        // Hiển thị ảnh nếu có
        if (currentItem.getImage() != null) {
            imageView.setImageBitmap(currentItem.getImage());
        } else {
            imageView.setImageResource(R.drawable.placeholder); // Hình ảnh mặc định nếu không có ảnh
        }

        return rowView;
    }
}
