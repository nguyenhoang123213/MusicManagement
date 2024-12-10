package com.example.musicmanagement;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // ===============================================
        // ===============================================
        Button btnManageMusic = findViewById(R.id.btnManageSong);
        Button btnManageAlbum = findViewById(R.id.btnManageAlbum);
        Button btnManageArtist = findViewById(R.id.btnManageArtist);
        Button btnPlayer = findViewById(R.id.btnPlayer);

        // Sự kiện khi nhấn nút Quản lý nhạc
        btnManageMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SongManagement.class);
                startActivity(intent);
            }
        });

        btnManageArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ArtistManagement.class);
                startActivity(intent);
            }
        });

        btnManageAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlaylistManagement.class);
                startActivity(intent);
            }
        });
        btnPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SongListPlayerClass.class);
                startActivity(intent);
            }
        });
    }
}