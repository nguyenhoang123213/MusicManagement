package com.example.musicmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SongListPlayerClass extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_song_list_player_class);

        ListView songListView = findViewById(R.id.song_list_view);

        String[] songTitles = {
                "Em của ngày hôm qua - Sơn Tùng MTP",
                "Anh thôi nhân nhượng Remix - Kiều Chi",
                "Lao tâm khổ tứ Remix - Thanh Hưng",
                "Vì anh đâu có biết Remix - Vũ",
                "Lạ lùng - Vũ"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songTitles);
        songListView.setAdapter(adapter);

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mainIntent = new Intent(SongListPlayerClass.this, Player.class);
                mainIntent.putExtra("songIndex", position);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Xóa Activity hiện tại để quay lại MainActivity
                startActivity(mainIntent);
            }
        });
    }
}