package com.example.musicmanagement;

public class Song {
    private final int songId;
    private final String title;
    private final String artistName;


    public Song(int songId, String title, String artistName) {
        this.songId = songId;
        this.title = title;
        this.artistName = artistName;
    }

    public String getTitle() {
        return title;
    }

    public String getArtistName() {
        return artistName;
    }

    public int getSongId() {
        return songId;
    }

}