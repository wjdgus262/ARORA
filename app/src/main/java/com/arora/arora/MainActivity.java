package com.arora.arora;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ImageView logo;
    TextView words,words_writer;

    private MyDatabaseOpenHelper helper;
    String dbName = "st_file.db";
    int dbVersion = 1;
    private SQLiteDatabase db;
    String tag = "SQLite";
    int flag = 0;
    Handler handler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
            startActivity(intent);
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.BLACK);
        ImageView aroraback = findViewById(R.id.intro_back_img);
        Glide.with(getApplicationContext()).load(R.raw.arora_back).into(aroraback);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_FINE_LOCATION},1000);
            }else{
                handler.postDelayed(r,2000);
            }
        }else{
            handler.postDelayed(r,2000);
        }

        if (Settings.System.canWrite(this)) {
//            Toast.makeText(this, "onCreate: Already Granted", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(this, "onCreate: Not Granted. Permission Requested", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + this.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        logo = (ImageView)findViewById(R.id.intro_logo);
        words = (TextView)findViewById(R.id.words);
        String[] arrayofString = getApplicationContext().getResources().getStringArray(R.array.array);
        words.setText(arrayofString[new Random().nextInt(arrayofString.length)]);
//        words_writer = (TextView)findViewById(R.id.words_writer);
        Animation fadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadein);
        Animation set = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadein_translate);
        logo.setAnimation(fadein);
        words.setAnimation(set);



//        words_writer.setAnimation(set);
//        select();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
//            getAudioListFromMediaDatabase(getApplicationContext());

            handler.postDelayed(r,2000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
//                Toast.makeText(this, "onResume: Granted", Toast.LENGTH_SHORT).show();
            }
        }
//            handler.postDelayed(r,5000);


    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(r);
    }

}
