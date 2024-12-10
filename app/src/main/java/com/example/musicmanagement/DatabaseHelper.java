package com.example.musicmanagement;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context) {
        super(context, "musicmanagement.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase myDatabase) {
        myDatabase.execSQL("create table users(email text, password text)");
        myDatabase.execSQL("create table songs(id integer primary key autoincrement, name text,artistName text, playlistId integer)");
        myDatabase.execSQL("create table playlists (id integer primary key autoincrement, name text)");
        myDatabase.execSQL("CREATE TABLE casi(macasi INTEGER PRIMARY KEY, tencasi TEXT, sinhnam INTEGER, image BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase myDatabase, int i, int i1) {
        myDatabase.execSQL("DROP TABLE IF EXISTS users");
        myDatabase.execSQL("DROP TABLE IF EXISTS songs");
        myDatabase.execSQL("DROP TABLE IF EXISTS playlists");
        myDatabase.execSQL("DROP TABLE IF EXISTS casi");
        onCreate(myDatabase);
    }

    // USER
    public boolean checkUserExist(String email) {
        SQLiteDatabase myDatabase = this.getWritableDatabase();
        Cursor cursor = myDatabase.rawQuery("select * from users where email = ?", new String[]{email});
        return cursor.getCount() > 0;
    }

    public boolean checkCorrectPassword(String email, String password) {
        SQLiteDatabase myDatabase = this.getWritableDatabase();
        Cursor cursor = myDatabase.rawQuery("select * from users where email = ? and password = ?", new String[]{email, password});
        return cursor.getCount() > 0;
    }

    public void insertUser(String email, String password) {
        SQLiteDatabase myDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        myDatabase.insert("users", null, contentValues);
    }

    // SONG

    public void insertSong(String name, String artistName) {
        SQLiteDatabase myDatabase = this.getWritableDatabase();
        myDatabase.execSQL("insert into songs(name, artistName) values(?, ?)", new String[]{name, artistName});
    }

    public Cursor getAllSongs() {
        SQLiteDatabase myDatabase = this.getWritableDatabase();
        return myDatabase.rawQuery("select * from songs", null);
    }

    public Cursor getSongsByPlaylistId(int playlistId) {
        SQLiteDatabase myDatabase = this.getWritableDatabase();
        return myDatabase.rawQuery("select * from songs where playlistId = " + playlistId, null);
    }

    public void editSong(int songId, String newTitle, String newArtistName) {
        SQLiteDatabase myDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", newTitle);
        values.put("artistName", newArtistName);
        myDatabase.update("songs", values, "id = ?", new String[]{String.valueOf(songId)});
    }

    public void deleteSong(int songId) {
        SQLiteDatabase myDatabase = this.getWritableDatabase();
        myDatabase.delete("songs", "id = ?", new String[]{String.valueOf(songId)});
    }

    // PLAYLIST
    public void addPlaylist(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.insert("playlists", null, values);
        db.close();
    }

    @SuppressLint("Range")
    public ArrayList<Playlist> getAllPlaylists() {
        ArrayList<Playlist> playlists = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM playlists", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                playlists.add(new Playlist(id, name));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return playlists;
    }

    public void updatePlaylist(int id, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", newName);
        db.update("playlists", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deletePlaylist(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("playlists", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void addSongToPlaylist(int songId, int playlistId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("update songs set playlistId = ? where id = ?", new String[]{String.valueOf(playlistId), String.valueOf(songId)});
        db.close();
    }

    public ArrayList<String> getAllArtistNames() {
        SQLiteDatabase myDatabase = this.getWritableDatabase();
        ArrayList<String> artistNames = new ArrayList<>();
        Cursor cursor =  myDatabase.rawQuery("select tencasi from casi", null);
        if (cursor.moveToFirst()) {
            do {
                int nameIndex = cursor.getColumnIndex("tencasi");
                artistNames.add(cursor.getString(nameIndex));
            } while (cursor.moveToNext());
        }
        return artistNames;
    }

}
