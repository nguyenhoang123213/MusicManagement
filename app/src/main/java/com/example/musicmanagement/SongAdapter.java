package com.example.musicmanagement;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private final List<Song> songList;
    private final Context context;
    private DatabaseHelper databaseHelper;
    private SongAdapter adapter;

    public SongAdapter(List<Song> songList, Context context) {
        this.songList = songList;
        this.context = context;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtistName());
        holder.moreOptions.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.itemView);
            popupMenu.inflate(R.menu.menu_song);

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.edit) {
                    handleClickEdit(song, position);
                    return true;
                }
                if (item.getItemId() == R.id.delete) {
                    deleteSong(position, song.getSongId());
                    loadSongs();
                    return true;
                }
                if (item.getItemId() == R.id.addToPlaylist) {
                    handleClickAddPlaylist(song);
                    return true;
                }
                return false;
            });

            popupMenu.setGravity(android.view.Gravity.END);
            popupMenu.show();
        });
    }

    void handleClickAddPlaylist(Song song) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add to playlist");
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        LinearLayout playlistLayout = new LinearLayout(context);
        playlistLayout.setOrientation(LinearLayout.VERTICAL);
        playlistLayout.setPadding(20, 20, 20, 20); // Thêm padding cho khoảng cách xung quanh

        ArrayList<Playlist> playlists = databaseHelper.getAllPlaylists();

        for (int i = 0; i < playlists.size(); i++) {
            // Tạo TextView cho mỗi playlist
            TextView playlistName = new TextView(context);
            playlistName.setText(playlists.get(i).getName());

            // Tùy chỉnh TextView cho giao diện đẹp hơn
            playlistName.setTextSize(18); // Cỡ chữ lớn hơn
            playlistName.setPadding(20, 20, 20, 20); // Padding bên trong

            int finalI = i;
            playlistName.setOnClickListener(view1 -> {
                databaseHelper.addSongToPlaylist(song.getSongId(), playlists.get(finalI).getId());
                Toast.makeText(context, "Added to playlist", Toast.LENGTH_SHORT).show();
            });

            playlistLayout.addView(playlistName);
        }

        builder.setView(playlistLayout);
        builder.show();
    }

    void handleClickEdit(Song song, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit the song");
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20,20,20,20);

        EditText edtTitle = new EditText(context);
        edtTitle.setText(song.getTitle());
        EditText edtArtist = new EditText(context);
        edtArtist.setText(song.getArtistName());

        linearLayout.addView(edtTitle);
        linearLayout.addView(edtArtist);

        builder.setPositiveButton("Update", (dialog, which) -> {
            databaseHelper.editSong(song.getSongId(), edtTitle.getText().toString(), edtArtist.getText().toString());
            Toast.makeText(context, "Updated song successfully.", Toast.LENGTH_SHORT).show();
            notifyItemChanged(position);
            loadSongs();
        });
        builder.setView(linearLayout);
        builder.show();
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView title, artist;
        ImageView moreOptions;

        public SongViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_title);
            artist = itemView.findViewById(R.id.text_artist);
            moreOptions = itemView.findViewById(R.id.btnMoreOptions);
        }
    }

    public void loadSongs() {
        songList.clear();  // Xóa danh sách cũ
        Cursor cursor = databaseHelper.getAllSongs();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String artistName = cursor.getString(2);
                songList.add(new Song(id, name, artistName));
            } while (cursor.moveToNext());
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();  // Cập nhật RecyclerView
        } else {
            Log.e("PlaylistAdapter", "Adapter is null in loadPlaylists!");
        }
    }

    public void deleteSong(int position, int songId) {
        databaseHelper.deleteSong(songId);
        notifyItemRemoved(position);
    }

}
