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
import android.support.v4.app.FragmentManager;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.arora.arora.Adapter.SearchAdapter;
import com.arora.arora.data.Audio_item;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    private final static int LOADER_ID = 0x001;
    private List<Audio_item> mArrayList;
    private List<Long> longarraylist;
    private RecyclerView recyclerView;
    private SearchAdapter mAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_search, container, false);
        getAudioListFromMediaDatabase(((FragmentActivity)getActivity()).getApplicationContext());

        final EditText search_edit = (EditText)rootview.findViewById(R.id.search_edit);
        search_edit.setInputType(0);
        search_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_edit.setInputType(1);
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                mgr.showSoftInput(search_edit, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        search_edit.addTextChangedListener(watcher);

        ImageView back_icon = (ImageView)rootview.findViewById(R.id.back_icon);
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(SearchFragment.this).commit();
                fragmentManager.popBackStack();
                getActivity().finish();
            }
        });

        return rootview;
    }
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

    int index = 0;
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
                        Log.i("Bookmark",title);
                        mArrayList.add(new Audio_item(id,title,artist,dur,albumid,index,path,artisid,album));
                        index++;
                    }
                    recyclerView = (RecyclerView)getView().findViewById(R.id.search_recy);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);
                    mAdapter = new SearchAdapter(context,mArrayList);
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

}
