package com.arora.arora;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arora.arora.Adapter.YoutubeAdapter;
import com.arora.arora.Audio.AudioApplication;
import com.arora.arora.Audio.BroadcastActions;
import com.arora.arora.Manager.DrawbleManager;
import com.arora.arora.data.Audio_item;
import com.arora.arora.data.YoutubeData;
import com.bumptech.glide.Glide;
import com.chibde.visualizer.CircleBarVisualizer;
import com.chibde.visualizer.CircleVisualizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import de.hdodenhof.circleimageview.CircleImageView;

public class MusicViewActivity extends AppCompatActivity implements View.OnClickListener {
    CircleVisualizer circleVisualizer;
    int id;
    MediaPlayer mediaPlayer;
    boolean prepared = false;
    boolean started = false;
    TextView title,subtitle;
    CircleImageView circleImageView;
    CircleBarVisualizer circleBarVisualizer;
    int color[] = {R.color.vi_color,R.color.vi_color1,R.color.vi_color2,R.color.vi_color3,R.color.vi_color4,R.color.vi_color5,R.color.vi_color6};
    WebView webView;
    ImageView foward_img,rewind_img,view_pause_play;
    SeekBar seekBar;
    Handler seekHandler = new Handler();
    int random = 0;
    int pro = 0;
    String keyward = "";

    static DrawbleManager DM =new DrawbleManager();
    AsyncTask<?,?,?> searchTask;
    ArrayList<YoutubeData> sdata = new ArrayList<YoutubeData>(20);

    final String serverKey = "AIzaSyAJTAHukInxx5ZSUSHw1qWn_B9HVG4VSmc";

    Spinner speed_spinner;
    TextView speed_text,total_duration,current_duration;
    ImageView loop_icon,rignthon_icon,share_icon,music_view_back;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_view);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.BLACK);
        id = getIntent().getExtras().getInt("sessid");
        circleBarVisualizer = findViewById(R.id.visualizer);
        seekBar = (SeekBar)findViewById(R.id.sound_seekbar);
        for(int i = 0; i < 7; i++){
            random = (int)(Math.random() * 7);
        }
        circleBarVisualizer.setColor(ContextCompat.getColor(this,color[random]));
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        title = (TextView)findViewById(R.id.view_title);
        subtitle = (TextView)findViewById(R.id.view_sub);
        circleBarVisualizer.setPlayer(id);
        circleBarVisualizer.setEnabled(false);
        title.setSelected(true);
        subtitle.setSelected(true);
        circleImageView = (CircleImageView)findViewById(R.id.view_circle_img);

        rewind_img = (ImageView)findViewById(R.id.view_rewind);
        foward_img = (ImageView)findViewById(R.id.view_forward);
        view_pause_play = (ImageView)findViewById(R.id.view_pause_play);
        music_view_back = (ImageView)findViewById(R.id.music_view_back);
        rewind_img.setOnClickListener(this);
        foward_img.setOnClickListener(this);
        view_pause_play.setOnClickListener(this);
        music_view_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   finish();
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pro = progress;
                String dateString = new SimpleDateFormat("mm:ss").format(new Date(progress));
                current_duration.setText(dateString);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                AudioApplication.getInstance().getServiceInterFace().setDuration(pro);
            }
        });

        speed_spinner = (Spinner)findViewById(R.id.speedOptions);
        final String[] speeds = {"0.25x","0.5x","0.75x","1.0x","1.25x","1.5x","1.75x","2.0x"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,speeds);
        speed_spinner.setAdapter(arrayAdapter);
        speed_spinner.setSelection(3);
        speed_text = (TextView)findViewById(R.id.speed_text);
        speed_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                speed_text.setText(speed_spinner.getItemAtPosition(position).toString());
                String str = speed_spinner.getItemAtPosition(position).toString().substring(0,speed_spinner.getItemAtPosition(position).toString().length()-1);
//                Toast.makeText(getApplicationContext(),str+"",Toast.LENGTH_SHORT).show();
                float selectedSpeed = Float.parseFloat(str);
                AudioApplication.getInstance().getServiceInterFace().change_speed(selectedSpeed);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//



        loop_icon = (ImageView)findViewById(R.id.loop_icon);
        loop_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"현재 곡을 반복합니다.",Toast.LENGTH_SHORT).show();
                AudioApplication.getInstance().getServiceInterFace().loop_action();
            }
        });

        rignthon_icon = (ImageView)findViewById(R.id.ringtone_icon);
        share_icon = (ImageView)findViewById(R.id.share_icon);

        path = getIntent().getExtras().getString("path");
        rignthon_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrictMode.VmPolicy.Builder builder1 = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder1.build());
                File chosenFile = new File("file://" + path);
                Uri ringtoneUri = Uri.parse(chosenFile.getAbsolutePath());
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DATA, chosenFile.getAbsolutePath());
                values.put(MediaStore.MediaColumns.TITLE, chosenFile.getName());
                values.put(MediaStore.MediaColumns.SIZE, chosenFile.length());
                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
                values.put(MediaStore.Audio.AudioColumns.ARTIST, getString(R.string.app_name));
                values.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, true);
                values.put(MediaStore.Audio.AudioColumns.IS_NOTIFICATION, false);
                values.put(MediaStore.Audio.AudioColumns.IS_ALARM, false);
                values.put(MediaStore.Audio.AudioColumns.IS_MUSIC, false);
                try {
                    Uri uri1 = MediaStore.Audio.Media.getContentUriForPath(chosenFile.getAbsolutePath());
                    getContentResolver().delete(uri1, MediaStore.MediaColumns.DATA + "=\"" + chosenFile.getAbsolutePath() + "\"", null);
                    Uri newUri = getContentResolver().insert(uri1, values);
                    RingtoneManager.setActualDefaultRingtoneUri(
                            getApplicationContext(),
                            RingtoneManager.TYPE_RINGTONE,
                            newUri
                    );
                    Toast.makeText(getApplicationContext(),"벨소리 설정 성공",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    String exceptionAsStrting = sw.toString();
                    Log.e("Stack", exceptionAsStrting);
                    Toast.makeText(getApplicationContext(),"벨소리 설정 실패",Toast.LENGTH_SHORT).show();
                }
            }
        });
        share_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Uri uri = Uri.parse("file://" + path);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("audio/*");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, "음악 파일 공유"));
            }
        });
        current_duration = (TextView)findViewById(R.id.current_duration);
        total_duration = (TextView)findViewById(R.id.total_duration);
        registerBroadcast();
        updateUI();
        searchTask = new searchTask().execute();
    }
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if( keyCode == KeyEvent.KEYCODE_BACK )
        {
            this.finish();
            Log.i("resetset","rest");
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (started) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(prepared){
            mediaPlayer.release();
        }
        if(circleBarVisualizer != null){
            circleBarVisualizer.release();
        }
        unregisterBroadcast();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (started) {
            //mediaPlayer.start();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.prepareAsync();
        }
    }
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };
    public void registerBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastActions.PLAY_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
    }

    public void unregisterBroadcast(){
        unregisterReceiver(mBroadcastReceiver);
    }

    private void updateUI() {
//        if (AudioApplication.getInstance().getServiceInterface().isPlaying()) {
////            btn_play_pause.setImageResource(R.drawable.perpase);
//            playpause_btn.setText("멈춤");
//        } else {
////            btn_play_pause.setImageResource(R.drawable.play);
//            playpause_btn.setText("시작");
//        }
//        Toast.makeText(getApplicationContext(),AudioApplication.getInstance().getServiceInterFace().isPlaying()+"",Toast.LENGTH_SHORT).show();
        if(AudioApplication.getInstance().getServiceInterFace().isPlaying() == true){
            view_pause_play.setImageResource(R.drawable.ic_pause_black_24dp);
        }else{
            view_pause_play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
        Audio_item audioItem = AudioApplication.getInstance().getServiceInterFace().getAudioItem();
        Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), audioItem.AlbumId);
//        Picasso.with(getApplicationContext()).load(albumArtUri).error(R.drawable.aurora_empty_album_img).into(circleImageView);
        Glide.with(getApplicationContext()).load(albumArtUri).error(R.drawable.aurora_empty_album_img).into(circleImageView);
        title.setText(audioItem.title);
        subtitle.setText(audioItem.subTitle);
//        Toast.makeText(getApplicationContext(),audioItem.duration+"",Toast.LENGTH_SHORT).show();

        //seekbar
        int cu_post = AudioApplication.getInstance().getServiceInterFace().getCurrentDuration();
        int max = AudioApplication.getInstance().getServiceInterFace().getMaxDuration();
        seekBar.setMax(max);
        seekBar.setProgress(cu_post);
        seekHandler.removeCallbacks(moveSeekBarThread);
        seekHandler.postDelayed(moveSeekBarThread,100);

        keyward = audioItem.subTitle;
        String dateString = new SimpleDateFormat("mm:ss").format(new Date(audioItem.duration));
        total_duration.setText(dateString);
    }
    private Runnable moveSeekBarThread = new Runnable() {
        public void run() {
            if (AudioApplication.getInstance().getServiceInterFace().isPlaying() == true) {
                int cu_post = AudioApplication.getInstance().getServiceInterFace().getCurrentDuration();
                int max = AudioApplication.getInstance().getServiceInterFace().getMaxDuration();
                float mid_max = max / 7;
                seekBar.setMax(max);
                seekBar.setProgress(cu_post);

                for(int i = 0; i < 7; i++){
                    random = (int)(Math.random() * 7);
                    circleBarVisualizer.setColor(ContextCompat.getColor(getApplicationContext(),color[random]));
                }


                seekHandler.postDelayed(this, 100); //Looping the thread after 0.1 second
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.view_rewind:
                AudioApplication.getInstance().getServiceInterFace().rewind();
                int sessionId = AudioApplication.getInstance().getServiceInterFace().getAudioSessionId();
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                if(sessionId != -1){
                    for(int i = 0; i < 7; i++){
                        random = (int)(Math.random() * 7);
                    }
                    circleBarVisualizer.release();
                    circleBarVisualizer.setColor(ContextCompat.getColor(getApplicationContext(),color[random]));
                    circleBarVisualizer.setColor(ContextCompat.getColor(getApplicationContext(),color[random]));
                    circleBarVisualizer.setPlayer(id);
                    circleBarVisualizer.setEnabled(false);
                    view_pause_play.setImageResource(R.drawable.ic_pause_black_24dp);
                    updateUI();
                }
                break;
            case R.id.view_forward:
//                AudioApplication.getInstance().getServiceInterFace().pause();
//                AudioApplication.getInstance().getServiceInterFace().play();
                AudioApplication.getInstance().getServiceInterFace().forward();
                int sessionId1 = AudioApplication.getInstance().getServiceInterFace().getAudioSessionId();
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                if(sessionId1 != -1){
                    for(int i = 0; i < 7; i++){
                        random = (int)(Math.random() * 7);
                    }
                    circleBarVisualizer.release();
                    circleBarVisualizer.setColor(ContextCompat.getColor(getApplicationContext(),color[random]));
                    circleBarVisualizer.setColor(ContextCompat.getColor(getApplicationContext(),color[random]));
                    circleBarVisualizer.setPlayer(id);
                    circleBarVisualizer.setEnabled(false);
                    updateUI();
                }
                break;
            case R.id.view_pause_play:
                AudioApplication.getInstance().getServiceInterFace().togglePlay();
                updateUI();
                break;
        }
    }




    //youtube search api
    private class searchTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
//            Toast.makeText(getApplicationContext(),"aaa",Toast.LENGTH_SHORT).show();
            try {

                paringJsonData(getUtube());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            ListView searchlist = (ListView) findViewById(R.id.searchlist);
            RecyclerView recyclerView = (RecyclerView)findViewById(R.id.youtube_recy);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);

            YoutubeAdapter adapter = new YoutubeAdapter(getApplicationContext(),sdata);

            recyclerView.setAdapter(adapter);
            Log.i("onpostexecute",""+sdata.size());
            super.onPostExecute(aVoid);
        }
    }

    public JSONObject getUtube(){
//        HttpGet a = new HttpGet("a")
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        HttpGet httpget = new HttpGet("https://www.googleapis.com/youtube/v3/search?"
        + "part=snippet&maxResults=50&q="+keyward.replaceAll(" ", "").replaceAll(match,"")+ "&key="+ serverKey);
        Log.i("getutube","https://www.googleapis.com/youtube/v3/search?"
                + "part=snippet&maxResults=20&q="+keyward.replaceAll(" ", "")+ "&key="+ serverKey);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpget);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while((b = stream.read()) != -1){
                stringBuilder.append((char) b);
            }
        }catch (ClientProtocolException e){

        }catch (IOException e){

        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }


    private void paringJsonData(JSONObject jsonObject) throws JSONException {
        sdata.clear();

        JSONArray contacts = jsonObject.getJSONArray("items");
        Log.i("doingbackground",contacts.length()+"");
        for (int i = 0; i < contacts.length(); i++) {
            JSONObject c = contacts.getJSONObject(i);
            String vodid = c.getJSONObject("id").optString("videoId","1");
            String channelid = c.getJSONObject("snippet").getString("channelId");
            String title = c.getJSONObject("snippet").getString("title");
            String channel = c.getJSONObject("snippet").getString("channelTitle");
            String changString = "";
            String chanelString = "";
            try {
                changString = new String(title.getBytes("8859_1"), "utf-8");
                chanelString = new String(channel.getBytes("8859_1"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();

            }

            String date = c.getJSONObject("snippet").getString("publishedAt")
                    .substring(0, 10);
            String imgUrl = c.getJSONObject("snippet").getJSONObject("thumbnails")
                    .getJSONObject("high").getString("url");
//
            Log.i("doingbackgroundfor",i+"");
            sdata.add(new YoutubeData(imgUrl,changString,chanelString,vodid,channelid));
        }

    }

    String vodid = "";
}
