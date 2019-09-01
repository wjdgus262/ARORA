package com.arora.arora.Audio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.arora.arora.MusicViewActivity;
import com.arora.arora.data.Audio_item;

import java.io.File;
import java.util.ArrayList;

public class AudioServiceInterface {
    private ServiceConnection mServiceConnection;
    private AudioService mService;
    private Context mContext;

    public AudioServiceInterface(Context context) {
        mContext = context;
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((AudioService.AudioServiceBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mServiceConnection = null;
                mService = null;
            }
        };
        context.bindService(new Intent(context, AudioService.class)
                .setPackage(context.getPackageName()), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void setPlayList(ArrayList<Long> audioIds) {
        if (mService != null) {
            mService.setPlayList(audioIds);
        }
    }

    public void play(int position) {
        if (mService != null) {
            mService.play(position);
        }
    }

    public void play() {
        if (mService != null) {
            mService.play();
        }
    }

    public void pause() {
        if (mService != null) {
            mService.play();
        }
        mService.pause();
    }

    public void forward() {
        if (mService != null) {
            mService.forward();
        }
    }

    public void rewind() {
        if (mService != null) {
            mService.rewind();
        }
    }

    public void change_speed(float speed){
        if(isPlaying()){
            mService.change_speed_playing(speed);
        }else{
            mService.change_speed_notplaying(speed);
        }
//        Log.i("change_speed",speed+"");
    }
    public void loop_action(){
        if(isPlaying()){
            mService.loop_playing();
        }else{
            mService.loop_notplaying();
        }
    }

    public void togglePlay(){
        if(isPlaying()){
            mService.pause();
        }else{
            mService.play();
        }
    }
    public boolean isPlaying(){
        if(mService != null){
            return mService.isPlaying();
        }
        return false;
    }

    public Audio_item getAudioItem() {
        if (mService != null) {
            return mService.getAudioItem();
        }
        return null;
    }


    public void go(String path){
        Intent intent = new Intent(mContext, MusicViewActivity.class);
        intent.putExtra("path",path);
        int id = getAudioSessionId();
        intent.putExtra("sessid",id);
        mContext.startActivity(intent);
    }
    public int getAudioSessionId(){
        if(mService.mMediaPlayer == null){
            return -1;
        }
        return mService.mMediaPlayer.getAudioSessionId();
    }
    public int getCurrentDuration(){
        return mService.getmCurrentDuration();
    }
    public int getMaxDuration(){
        return mService.getMaxDuration();
    }
    public void setDuration(int seek){
        mService.setDuration(seek);
    }


    public void share(String path){
        String a = Environment.getExternalStorageDirectory().getPath() + path;
        File fileToShare = new File(a);
        Log.i("filetoshare",fileToShare.getAbsolutePath());
        Uri uri = FileProvider.getUriForFile(mContext, "com.arora.ar.fileprovider",fileToShare);
        Log.i("customviewholder",uri+"");
        mContext.grantUriPermission(mContext.getPackageName(),uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent in = new Intent();
        in.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        in.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        in.setDataAndType(uri,"audio/*");
        in.setAction(Intent.ACTION_SEND);
        mContext.startActivity(in);


    }

//    public void img_change(){
//        ((Main2Activity)Main2Activity.mContext).img_change();
//    }
}
