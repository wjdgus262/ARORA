package com.arora.arora.data;

public class CardItem {

    public long id;
    public String title;
    public String subTitle;
    public long duration;
    public long AlbumId;
    public String mDataPath;
    public int index;
    public String mAlbum;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getAlbumId() {
        return AlbumId;
    }

    public void setAlbumId(long albumId) {
        AlbumId = albumId;
    }

    public String getmDataPath() {
        return mDataPath;
    }

    public void setmDataPath(String mDataPath) {
        this.mDataPath = mDataPath;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getmAlbum() {
        return mAlbum;
    }

    public void setmAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    public CardItem(long id, String title, String subTitle, long duration, long albumId, int index,String mDataPath ) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.duration = duration;
        AlbumId = albumId;
        this.mDataPath = mDataPath;
        this.index = index;
    }
}
