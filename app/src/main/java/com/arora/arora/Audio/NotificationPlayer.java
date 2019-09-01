package com.arora.arora.Audio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.arora.arora.Main2Activity;
import com.arora.arora.R;
import com.squareup.picasso.Picasso;

public class NotificationPlayer {
    private final static int NOTIFICATION_PLAYER_ID = 0x342;
    private AudioService audioService;
    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationMangerBuilder mNotificationMangerBuilder;
    private boolean isForeground;

    public NotificationPlayer(AudioService audioService,Context context) {
        this.audioService = audioService;
        mNotificationManager = (NotificationManager) audioService.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, AudioService.class);
        context.startService(intent);
        mContext = context;
    }
    public void updateNotificationPlayer(){
        cancel();
        mNotificationMangerBuilder = new NotificationMangerBuilder();
        mNotificationMangerBuilder.execute();
    }

    public void removeNotificationPlayer(){
        cancel();
        audioService.stopForeground(true);
        isForeground = false;
    }
    private void cancel(){
        if(mNotificationMangerBuilder != null){
            mNotificationMangerBuilder.cancel(true);
            mNotificationMangerBuilder = null;
        }
    }

    private class NotificationMangerBuilder extends AsyncTask<Void,Void,Notification>{
        private RemoteViews remoteViews;
        private android.support.v4.app.NotificationCompat.Builder mNotificationBuilder;
        private PendingIntent mMainPendignInter;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Intent mainActivity = new Intent(audioService, Main2Activity.class);
            mMainPendignInter = PendingIntent.getActivity(audioService,0,mainActivity,0);
            remoteViews = createRemoteView(R.layout.notification_player);
            mNotificationBuilder = new NotificationCompat.Builder(audioService);

            if(Build.VERSION.SDK_INT >= 26){
                NotificationChannel mChannel = new NotificationChannel("com.arora.aroraplayer","arora",NotificationManager.IMPORTANCE_DEFAULT);
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                mChannel.setShowBadge(false);
                mChannel.enableLights(true);
                mChannel.enableVibration(true);
                mNotificationManager.createNotificationChannel(mChannel);
                mNotificationBuilder = new NotificationCompat.Builder(mContext,"com.arora.aroraplayer");
            }else{
                mNotificationBuilder = new NotificationCompat.Builder(mContext);
            }
            mNotificationBuilder.setSmallIcon(R.drawable.noti_logo)
                    .setOngoing(true)
                    .setContentIntent(mMainPendignInter)
                    .setContent(remoteViews)
                    .setAutoCancel(false);
            Notification notification = mNotificationBuilder.build();
            notification.priority = Notification.PRIORITY_MAX;
            notification.contentIntent = mMainPendignInter;
            if(!isForeground){
                isForeground = true;
                audioService.startForeground(NOTIFICATION_PLAYER_ID,notification);
            }
        }

        @Override
        protected void onPostExecute(Notification notification) {
            super.onPostExecute(notification);
            try {
                mNotificationManager.notify(NOTIFICATION_PLAYER_ID,notification);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected Notification doInBackground(Void... voids) {
            mNotificationBuilder.setContent(remoteViews);
            mNotificationBuilder.setContentIntent(mMainPendignInter);
            mNotificationBuilder.setPriority(Notification.PRIORITY_MAX);
            Notification notification = mNotificationBuilder.build();
            updateRemoteView(remoteViews,notification);
            return notification;
        }

        private RemoteViews createRemoteView(int layoutId){
            RemoteViews remoteView = new RemoteViews(audioService.getPackageName(),layoutId);
            Intent acctionTogglePlay = new Intent(CommandActions.TOGGLE_PLAY);
            Intent actionForward = new Intent(CommandActions.FORWARD);
            Intent actionRewind = new Intent(CommandActions.REWIND);
            Intent actionClose = new Intent(CommandActions.CLOSE);
            PendingIntent togglePlay = PendingIntent.getService(audioService,0,acctionTogglePlay,0);
            PendingIntent forward = PendingIntent.getService(audioService,0,actionForward,0);
            PendingIntent rewind = PendingIntent.getService(audioService,0,actionRewind,0);
            PendingIntent close = PendingIntent.getService(audioService,0,actionClose,0);

            remoteView.setOnClickPendingIntent(R.id.noti_play,togglePlay);
            remoteView.setOnClickPendingIntent(R.id.noti_next,forward);
            remoteView.setOnClickPendingIntent(R.id.noti_prev,rewind);
            remoteView.setOnClickPendingIntent(R.id.noti_close,close);
            return remoteView;
        }
        private void updateRemoteView(final RemoteViews remoteViews, final Notification notification){
            if(audioService.isPlaying() == true){
                remoteViews.setImageViewResource(R.id.noti_play,R.drawable.noti_pause);
            }else{
                remoteViews.setImageViewResource(R.id.noti_play,R.drawable.noti_play);
            }
            String title = audioService.getAudioItem().title;
            String subtitle = audioService.getAudioItem().subTitle;
            remoteViews.setTextViewText(R.id.noti_title,title);
            final Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), audioService.getAudioItem().AlbumId);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {

                    Picasso.get().load(albumArtUri).error(R.drawable.aurora_empty_album_img).into(remoteViews,R.id.noti_img,NOTIFICATION_PLAYER_ID,notification);
                }
            });
        }
    }

}
