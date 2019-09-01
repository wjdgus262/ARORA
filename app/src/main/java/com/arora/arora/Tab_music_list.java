package com.arora.arora;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arora.arora.Adapter.AudioAdapter;
import com.arora.arora.Audio.AudioApplication;
import com.arora.arora.data.Audio_item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Tab_music_list extends Fragment {
    private final static int LOADER_ID = 0x001;
    TextView item_title;
    EditText search_edit;
    int index = 0;
    private List<Audio_item> mArrayList;
    private List<Long> longarraylist;
    private RecyclerView recyclerView;
    private AudioAdapter mAdapter;
    TextWatcher watcher = new TextWatcher()
    {
        @Override
        public void afterTextChanged(Editable s) {
            //텍스트 변경 후 발생할 이벤트를 작성.
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
            //텍스트의 길이가 변경되었을 경우 발생할 이벤트를 작성.
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
//            //텍스트가 변경될때마다 발생할 이벤트를 작성.
//            Toast.makeText(getActivity().getApplicationContext(),s+"",Toast.LENGTH_SHORT).show();
            mAdapter.getFilter().filter(s);
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.music_list, container, false);
//        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//        textView.setText(getString(R.string, getArguments().getInt(ARG_SECTION_NUMBER)));

        getAudioListFromMediaDatabase(((FragmentActivity)getActivity()).getApplicationContext());

        LinearLayout shuffle_layout = (LinearLayout)rootView.findViewById(R.id.shuffle_layout);
        shuffle_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText()
//
                long seed = System.nanoTime();
                ArrayList<Long> shuffle_list = new ArrayList<Long>(longarraylist);
                Collections.shuffle(shuffle_list,new Random(seed));
                AudioApplication.getInstance().getServiceInterFace().setPlayList(shuffle_list); // 재생목록등록
                int num = new Random().nextInt(shuffle_list.size());
                AudioApplication.getInstance().getServiceInterFace().play(num);
//                Log.i("Linear Layout","Click"+longarraylist.get(0));
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getAudioListFromMediaDatabase(((FragmentActivity)getActivity()).getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        getAudioListFromMediaDatabase_resume(((FragmentActivity)getActivity()).getApplicationContext());
//        getLoaderManager().restartLoader(LOADER_ID,null,getActivity().getApplicationContext());
    }

    public void getAudioListFromMediaDatabase(final Context context) {
        mArrayList = new ArrayList<Audio_item>();
        longarraylist = new ArrayList<Long>();
        getLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @NonNull
            @Override
            public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
                Log.i("loader_create","loader_create");
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] projection = new String[]{
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ARTIST_ID,
                };
                String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";
                String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
                return new CursorLoader(((FragmentActivity)getActivity()).getApplicationContext(),uri,projection,selection,null,sortOrder);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
                Log.i("loader_finsished","loader_finsished");
                 String[] genresProjection = {
                         MediaStore.Audio.Genres.NAME,
                         MediaStore.Audio.Media._ID
                };
                Cursor genresCursor ;
                if(cursor != null && cursor.getCount() > 0){
                    while(cursor.moveToNext()){
                        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                        longarraylist.add(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                        //---------------장르-----------------------
                        //-----------------장르-------------------------
                        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        long dur = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        long albumid = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        String artisid = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                        String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                        mArrayList.add(new Audio_item(id,title,artist,dur,albumid,index,path,artisid,album));
                        index++;
                    }
                    recyclerView = (RecyclerView)getView().findViewById(R.id.recyclerview);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);
                    mAdapter = new AudioAdapter(context,mArrayList);
                    mAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(mAdapter);
                }
            }
            @Override
            public void onLoaderReset(@NonNull Loader<Cursor> loader) {
                Log.i("loader_reset","reset");
            }
        });
    }


    public void getAudioListFromMediaDatabase_resume(final Context context) {
        mArrayList = new ArrayList<Audio_item>();
        longarraylist = new ArrayList<Long>();
        getLoaderManager().restartLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @NonNull
            @Override
            public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
                Log.i("loader_create","loader_create");
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] projection = new String[]{
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ARTIST_ID,
                        MediaStore.Audio.Media.DISPLAY_NAME
                };
                String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";
                String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
                return new CursorLoader(((FragmentActivity)getActivity()).getApplicationContext(),uri,projection,selection,null,sortOrder);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
                Log.i("loader_finsished","loader_finsished");
                String[] genresProjection = {
                        MediaStore.Audio.Genres.NAME,
                        MediaStore.Audio.Media._ID
                };
                Cursor genresCursor ;
                if(cursor != null && cursor.getCount() > 0){
                    while(cursor.moveToNext()){
                        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                        longarraylist.add(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                        //---------------장르-----------------------
                        //-----------------장르-------------------------
                        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        long dur = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        long albumid = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        String artisid = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                        String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                        String dispaly = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        Log.i("mDataPath",path+"");
                        Log.i("displayname",dispaly);
                        mArrayList.add(new Audio_item(id,title,artist,dur,albumid,index,path,artisid,album));
                        index++;
                    }
                    recyclerView = (RecyclerView)getView().findViewById(R.id.recyclerview);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);
                    mAdapter = new AudioAdapter(context,mArrayList);
                    recyclerView.setAdapter(mAdapter);
                }
            }
            @Override
            public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            }
        });
    }
}
