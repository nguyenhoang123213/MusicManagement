package com.example.musicmanagement;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SongManagement extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Song> songList;
    private DatabaseHelper databaseHelper;
    private SongAdapter adapter;
    private ArrayList<String> artistSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_song_management);
        // ===============================================
        databaseHelper = new DatabaseHelper(getApplicationContext());
        redirectHome();
        // ===============================================
        loadSongList();
        // ===============================================
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMusic();
            }
        });
    }

    void redirectHome() {
        Button btnRedirectHome = findViewById(R.id.btnRedirectHome);
        btnRedirectHome.setOnClickListener(v -> {
            finish();
        });
    }

    void loadSongList() {
        Cursor CursorAllSongs = databaseHelper.getAllSongs();
        songList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewSongList);

        if (CursorAllSongs.moveToFirst()) {
            do {
                int id = CursorAllSongs.getInt(0);
                String name = CursorAllSongs.getString(1);
                String artistName = CursorAllSongs.getString(2);
                songList.add(new Song(id, name, artistName));
            } while (CursorAllSongs.moveToNext());
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new SongAdapter(songList, SongManagement.this);
        recyclerView.setAdapter(adapter);
    }

    private void addMusic() {
        //Tạo 1 dialog để nhập thông tin về bài hát mới
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a new music");

        //Tạo layout chứa các trường nhập liệu
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Input cho tên bài hát
        EditText inputTitle = new EditText(this);
        inputTitle.setHint("Song Name");
        layout.addView(inputTitle);

        artistSpinner = new ArrayList<String>();
        artistSpinner = databaseHelper.getAllArtistNames();

        final Spinner spinner = new Spinner(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, artistSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        layout.addView(spinner);

        builder.setView(layout);
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            if (artistSpinner.size() == 0) {
                Toast.makeText(this, "No artist", Toast.LENGTH_SHORT).show();
                return;
            }
            String name = inputTitle.getText().toString();
            String artistName = spinner.getSelectedItem().toString();

            if (name.isEmpty() || artistName.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            databaseHelper.insertSong(name, artistName);
            loadSongList();
        });
        builder.setNegativeButton("Huỷ", (dialog, which) -> dialog.cancel());

        //Hiển thị dialog
        builder.show();
    }
}