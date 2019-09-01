package com.arora.arora.Audio;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.arora.arora.data.Audio_item;

import java.util.ArrayList;

public class AudioService extends Service {

    private final IBinder mBinder = new AudioServiceBinder();
    private ArrayList<Long> mAudioIds = new ArrayList<>();
    public MediaPlayer mMediaPlayer;
    private boolean isPrepared;
    private int mCurrentPosition;
    private Audio_item mAudioItem;
    private NotificationPlayer mNotificationPlayer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class AudioServiceBinder extends Binder{
        AudioService getService(){
            return AudioService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                isPrepared = true;
                mediaPlayer.start();
                sendBroadcast(new Intent(BroadcastActions.PREPARED));
//                forward();
                updateNotificationPlayer();
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                isPrepared = false;
                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));

                updateNotificationPlayer();
                forward();
//                test_log();
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                isPrepared = false;
                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
                updateNotificationPlayer();
                return false;

            }
        });
        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {
//                Log.i("media_end","end");
//                forward();
            }
        });
        mNotificationPlayer = new NotificationPlayer(this,getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
    public void test_log(){
        Log.i("media_end","end");
//        play(mCurrentPosition++);
    }
    private void queryAudioItem(int position) {
        mCurrentPosition = position;
        long audioId = mAudioIds.get(position);
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST_ID,
        };
        String selection = MediaStore.Audio.Media._ID + " = ?";
        String[] selectionArgs = {String.valueOf(audioId)};
        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                mAudioItem = Audio_item.bindCursor(cursor);
            }
            cursor.close();
        }
    }
    private void prepare() {
        try {

            mMediaPlayer.setDataSource(mAudioItem.mDataPath);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepareAsync();
//            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
    }

    public void setPlayList(ArrayList<Long> audioIds) {
        if (!mAudioIds.equals(audioIds)) {
            mAudioIds.clear();
            mAudioIds.addAll(audioIds);
        }
    }

    public void play(int position) {
        queryAudioItem(position);
        mMediaPlayer.setLooping(false);
        stop();
        prepare();
        sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
        updateNotificationPlayer();
    }

    public void play() {
        if (isPrepared) {
            mMediaPlayer.start();
            sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED));
//            Toast.makeText(getApplicationContext(),"true",Toast.LENGTH_SHORT).show();
//            updateNotificationPlayer();
        }
    }

    public void change_speed_playing(float speed){
        mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setSpeed(speed));
    }

    public void change_speed_notplaying(float speed){
        mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setSpeed(speed));
        mMediaPlayer.pause();
    }

    public void pause() {
        if (isPrepared) {
            mMediaPlayer.pause();
            updateNotificationPlayer();
        }
    }

    public void forward() {
        if (mAudioIds.size() - 1 > mCurrentPosition) {
            mCurrentPosition++; // 다음 포지션으로 이동.
        } else {
            mCurrentPosition = 0; // 처음 포지션으로 이동.
        }

        play(mCurrentPosition);
    }

    public void rewind() {
        if (mCurrentPosition > 0) {
            mCurrentPosition--; // 이전 포지션으로 이동.
        } else {
            mCurrentPosition = mAudioIds.size() - 1; // 마지막 포지션으로 이동.
        }

        play(mCurrentPosition);
    }

    public int getmCurrentDuration(){
        return  mMediaPlayer.getCurrentPosition();
    }
    public int getMaxDuration(){
        return mMediaPlayer.getDuration();
    }
    public void setDuration(int seek){
        mMediaPlayer.pause();
        mMediaPlayer.seekTo(seek);
        mMediaPlayer.start();
    }


    public void loop_playing(){
        mMediaPlayer.setLooping(true);
    }


    public void loop_notplaying(){
        mMediaPlayer.pause();
        mMediaPlayer.setLooping(true);
    }
//    public void loop_media(){
//        if(isPlaying()){
//
//        }else{
//
//        }
//            mMediaPlayer.pause();
//        mMediaPlayer.setLooping(true);
//    }

    public Audio_item getAudioItem() {
        return mAudioItem;
    }
    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }

    private void updateNotificationPlayer(){
        if(mNotificationPlayer != null){
            mNotificationPlayer.updateNotificationPlayer();
        }
    }
    private void removeNotificationPlayer(){
        if(mNotificationPlayer != null){
            mNotificationPlayer.removeNotificationPlayer();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            String action = intent.getAction();
            if(CommandActions.TOGGLE_PLAY.equals(action)){
                if(isPlaying()){
                    pause();
                }else{
                    play();
                }
            }else if(CommandActions.REWIND.equals(action)){
                rewind();
            }else if(CommandActions.FORWARD.equals(action)){
                forward();
            }else if(CommandActions.CLOSE.equals(action)){
                pause();
                removeNotificationPlayer();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }




}
