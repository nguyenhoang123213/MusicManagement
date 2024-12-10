package com.example.musicmanagement;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private List<Playlist> playlists;
    private Context context;
    private DatabaseHelper dbHelper;
    private PlaylistAdapter adapter;

    public PlaylistAdapter(List<Playlist> playlists, Context context) {
        this.playlists = playlists;
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.playlistName.setText(playlist.getName());

        holder.moreOptions.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.itemView);
            popupMenu.inflate(R.menu.menu_playlist);
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.edit) {
                    showEditDialog(playlist, position);
                    loadPlaylists();
                    return true;
                }
                if (item.getItemId() == R.id.delete) {
                    deletePlaylist(playlist.getId(), position);
                    loadPlaylists();
                    return true;
                }
                if (item.getItemId() == R.id.viewSong) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Song list");
                    builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                    LinearLayout playlistLayout = new LinearLayout(context);
                    playlistLayout.setOrientation(LinearLayout.VERTICAL);
                    playlistLayout.setPadding(20, 20, 20, 20); // Thêm padding cho khoảng cách xung quanh
                    builder.setView(playlistLayout);

                    Cursor cursor = dbHelper.getSongsByPlaylistId(playlist.getId());
                    if (cursor.moveToFirst()) {
                        do {
                            int songNameIndex = cursor.getColumnIndex("name");
                            String songName = cursor.getString(songNameIndex);
                            TextView songTextView = new TextView(context);
                            songTextView.setText(songName);
                            playlistLayout.addView(songTextView);
                        } while (cursor.moveToNext());
                    }

                    builder.show();
                    return true;
                }
                return false;
            });
            popupMenu.setGravity(android.view.Gravity.END);
            popupMenu.show();
        });

    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView playlistName;
        ImageView moreOptions;

        ViewHolder(View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.tvPlaylistName);
            moreOptions = itemView.findViewById(R.id.btnMoreOptions);
        }
    }

    void showEditDialog(Playlist playlist, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Playlist");

        final EditText input = new EditText(context);
        input.setText(playlist.getName());
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(context, "Playlist name cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            playlist.setName(newName);
            dbHelper.updatePlaylist(playlist.getId(), newName); // Cập nhật trong SQLite
            notifyItemChanged(position);// Cập nhật RecyclerView
            loadPlaylists();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void deletePlaylist(int playlistId, int position) {
        dbHelper.deletePlaylist(playlistId);
        notifyItemRemoved(position);
    }

    public void loadPlaylists() {
        playlists.clear();  // Xóa danh sách cũ
        playlists.addAll(dbHelper.getAllPlaylists());  // Lấy danh sách mới từ cơ sở dữ liệu

        if (adapter != null) {
            adapter.notifyDataSetChanged();  // Cập nhật RecyclerView
        } else {
            Log.e("PlaylistAdapter", "Adapter is null in loadPlaylists!");
        }
    }
}
