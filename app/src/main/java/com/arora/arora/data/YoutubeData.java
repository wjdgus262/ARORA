package com.arora.arora.data;

public class YoutubeData {
    private String imgurl;
    private String title;
    private String channel;
    private String videoid;
    private String channelid;



    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public String getChannelid() {
        return channelid;
    }

    public void setChannelid(String channelid) {
        this.channelid = channelid;
    }


    public YoutubeData(String imgurl, String title, String channel, String videoid, String channelid) {
        this.imgurl = imgurl;
        this.title = title;
        this.channel = channel;
        this.videoid = videoid;
        this.channelid = channelid;
    }
}
