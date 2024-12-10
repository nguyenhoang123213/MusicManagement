package com.example.musicmanagement;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PlaylistManagement extends AppCompatActivity {
    private RecyclerView recyclerViewPlaylistList;
    private List<Playlist> playlistList;
    private DatabaseHelper db;
    private PlaylistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playlist_management);

        // Xử lý sự kiện khi nhấn nút Home
        Button btnRedirectHome = findViewById(R.id.btnRedirectHome);
        btnRedirectHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // ===============================================
        db = new DatabaseHelper(getApplicationContext());
        // set adapter
        recyclerViewPlaylistList = findViewById(R.id.recyclerViewPlaylistList);
        recyclerViewPlaylistList.setLayoutManager(new LinearLayoutManager(this));
        loadPlaylist();
        // add playlist
        handleAddPlaylist();
    }

    void loadPlaylist() {
        playlistList = db.getAllPlaylists();
        adapter = new PlaylistAdapter(playlistList, this);
        recyclerViewPlaylistList.setAdapter(adapter);
    }

    void handleAddPlaylist() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add Playlist");
            final EditText input = new EditText(this);
            builder.setView(input);

            builder.setPositiveButton("Add", (dialog, which) -> {
                String name = input.getText().toString();
                if(name.isEmpty()) {
                    Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
                    return;
                }
                db.addPlaylist(name);
                loadPlaylist();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });
    }
}