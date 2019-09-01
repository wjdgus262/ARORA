package com.arora.arora.data;

import android.database.Cursor;
import android.provider.MediaStore;

public class Audio_item {
    public long id;
    public String title;
    public String subTitle;
    public long duration;
    public long AlbumId;
    public String mDataPath;
    public int index;
    public String mAlbum;
    public String artisId;

    public String getmAlbum() {
        return mAlbum;
    }

    public void setmAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    public String getArtisId() {
        return artisId;
    }

    public void setArtisId(String artisId) {
        this.artisId = artisId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAlbumId() {
        return AlbumId;
    }

    public void setAlbumId(long albumId) {
        AlbumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
    public Audio_item(){

    }

    public String getmDataPath() {
        return mDataPath;
    }

    public void setmDataPath(String mDataPath) {
        this.mDataPath = mDataPath;
    }

    public Audio_item(long id, String title, String subTitle, long duration, long albumId, int index, String path,String artisId,String album) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.duration = duration;
        this.AlbumId = albumId;
        this.index =  index;
        this.mDataPath = path;
        this.artisId = artisId;
        this.mAlbum = album;
    }
    public static Audio_item bindCursor(Cursor cursor) {
        Audio_item audioItem = new Audio_item();
        audioItem.id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID));
        audioItem.title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
        audioItem.AlbumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
        audioItem.subTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
        audioItem.duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
        audioItem.mAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
        audioItem.mDataPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
        audioItem.artisId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
//        audioItem.mAlbumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
//        audioItem.mTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
//        audioItem.mArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
//        audioItem.mAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
//        audioItem.mDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
//        audioItem.mDataPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
        return audioItem;
    }
}
