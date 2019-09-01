package com.arora.arora;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arora.arora.Audio.AudioApplication;
import com.arora.arora.Audio.BroadcastActions;
import com.arora.arora.data.Audio_item;
import com.bumptech.glide.Glide;


public class Main2Activity extends AppCompatActivity implements View.OnClickListener {


    //플레이어 ui 셋팅 변수
    ImageView player_img;
    TextView player_title;
    ImageButton btn_player_pause;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    public static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mContext = getApplicationContext();
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.BLACK);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(null);
//        toolbar.setTitleTextColor(Color.WHITE);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#D6D322"));
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


        //검색이동
        ImageView search_icon = (ImageView)findViewById(R.id.search_icon);
        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this,SearchActivity.class);
                startActivity(intent);
            }
        });


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        //플레이어 ui 셋팅
        player_img = (ImageView)findViewById(R.id.player_ui_img);
        player_title = (TextView)findViewById(R.id.player_ui_title);
        btn_player_pause = (ImageButton)findViewById(R.id.player_ui_btn_pause);
        findViewById(R.id.lin_minplayer).setOnClickListener(this);
        findViewById(R.id.player_ui_rewind).setOnClickListener(this);
        btn_player_pause.setOnClickListener(this);
        findViewById(R.id.player_ui_forward).setOnClickListener(this);
        player_title.setSelected(true);
        registerBroadcast();
        updateUI();
//        Intent intent = new Intent(Main2Activity.this,MusicViewActivity.class);
//        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }
    

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lin_minplayer:
//                Toast.makeText(getApplicationContext(),player_title.getText()+"",Toast.LENGTH_SHORT).show();
//
                String flag_title = (String)player_title.getText();
                if(flag_title.equals("재생중인 음악이 없습니다.")){
                    Toast.makeText(getApplicationContext(),"재생중인 음악이 없습니다.",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(Main2Activity.this,MusicViewActivity.class);
//                int id = AudioApplication.getInstance().getServiceInterFace().getAudioSessionId();
//                intent.putExtra("sessid",id);
//                startActivity(intent);
                }

                break;
            case R.id.player_ui_rewind:
                AudioApplication.getInstance().getServiceInterFace().rewind();
                break;
            case R.id.player_ui_btn_pause:
                AudioApplication.getInstance().getServiceInterFace().togglePlay();
                updateUI();
                break;
            case R.id.player_ui_forward:
                AudioApplication.getInstance().getServiceInterFace().forward();
                break;
        }
    }
    public void img_change(){
        btn_player_pause.setImageResource(R.drawable.ic_pause_black_24dp);
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
        registerReceiver(mBroadcastReceiver,filter);
    }
    public void unregisterBroadcast(){
        unregisterReceiver(mBroadcastReceiver);
    }
    public void updateUI(){
        if(AudioApplication.getInstance().getServiceInterFace().isPlaying() == true){
            btn_player_pause.setImageResource(R.drawable.ic_pause_black_24dp);
        }else{
            btn_player_pause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
        Audio_item audio_item = AudioApplication.getInstance().getServiceInterFace().getAudioItem();
        if(audio_item != null){
            Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),audio_item.AlbumId);
//            RequestBuilder<Drawable> builder = Glide.with(getApplicationContext())
//                    .load(R.drawable.aurora_empty_album_img);
//            Glide.with(getApplicationContext()).load(albumArtUri).error(builder).into(player_img);
//            Picasso.with(getApplicationContext()).load(albumArtUri).error(R.drawable.aurora_empty_album_img).into(player_img);
            Glide.with(getApplicationContext()).load(albumArtUri).error(R.drawable.aurora_empty_album_img).into(player_img);
            player_title.setText(audio_item.title);
        }else{
            player_img.setImageResource(R.drawable.aurora_empty_album_img);
            player_title.setText("재생중인 음악이 없습니다.");
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    Tab_music_list tab1 = new Tab_music_list();
                    return tab1;
                case 1:
                    Tab_music_recom tab3 = new Tab_music_recom();
                    return tab3;
                default :
                    return null;
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
//            return super.getPageTitle(position);
                switch (position){
                    case 0:
                        return "List";
                    case 1:
                        return "recom";
                }
                return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }
    }
}
