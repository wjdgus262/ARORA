package com.arora.arora;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.arora.arora.Adapter.RecomAudioAdapter;
import com.arora.arora.data.Audio_item;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Tab_music_recom extends Fragment {
    private final static int LOADER_ID = 0x001;
    private View rootView;
    private Context context;
    LocationManager lm;
    Location location;
    private List<Audio_item> mArrayList;
    private ProgressDialog pd;
    TextView arora_text_view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.music_recom, container, false);
        context = getContext();
        arora_text_view = (TextView)rootView.findViewById(R.id.arora_text);
  return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getActivity().getApplicationContext(),"권한 체크 안됨",Toast.LENGTH_SHORT).show();
                return;
            }else{
//                handler.postDelayed(r,5000);
                location  = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location == null){
                    location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
//                getAudioListFormMediaDatabase();
            }
        }else{
//            handler.postDelayed(r,5000);

            location  = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null){
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            //            getAudioListFormMediaDatabase();
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                10000,
                5,
                getLocationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                10000,
                5,
                getLocationListener);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        try {
            JSONObject asnyjson = new weatherTask().execute(longitude,latitude).get();
            String msg[] = jsonparser(asnyjson);
            String arora = arora_text(msg[0]);
            arora_text_view.setText(arora);
            getAudioListFormMediaDatabase(msg[0]);
        }catch (Exception e){
            e.printStackTrace();
        }



    }

    @Override
    public void onResume() {
        super.onResume();
        lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getActivity().getApplicationContext(),"권한 체크 안됨",Toast.LENGTH_SHORT).show();
                return;
            }else{
                location  = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location == null){
                    location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
        }else{
            location  = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null){
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }


        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                10000,
                5,
                getLocationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                10000,
                5,
                getLocationListener);
                double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        try {
            JSONObject asnyjson = new weatherTask().execute(longitude,latitude).get();
            String msg[] = jsonparser(asnyjson);
            String arora = arora_text(msg[0]);
            arora_text_view.setText(arora);
            getAudioListFormMediaDatabase_resume(msg[0]);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //위치정보 가지고 오기
    final LocationListener getLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
//            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
//            double altitude = location.getAltitude();
//                Toast.makeText(getActivity().getApplicationContext(),longitude+"",Toast.LENGTH_SHORT).show();
            try {
                JSONObject asnyjson = new weatherTask().execute(longitude,latitude).get();
                String msg[] = jsonparser(asnyjson);
                String arora = arora_text(msg[0]);
                arora_text_view.setText(arora);
//                Toast.makeText(getActivity().getApplicationContext(),"날씨"+msg[0],Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


    //오픈날씨api
    private class weatherTask extends AsyncTask<Double,Void,JSONObject>{

        @Override
        protected JSONObject doInBackground(Double... doubles) {
            try {
                HttpURLConnection conn = (HttpURLConnection)new URL("http://api.openweathermap.org/data/2.5/weather?lat="+doubles[1].doubleValue()+"&lon="+doubles[0].doubleValue()+"&APPID=3b9fa03df7b91262d69d64e072d43a60&units=metric").openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.connect();
                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                    InputStream is = conn.getInputStream();
                    InputStreamReader reader = new InputStreamReader(is);
                    BufferedReader in = new BufferedReader(reader);

                    String readed;
                    while((readed = in.readLine()) != null){
                        JSONObject jsonObject = new JSONObject(readed);
                        return jsonObject;
                    }
                }else{
                    return null;
                }
                return null;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    //받아온 json 파싱
    private String[] jsonparser(JSONObject jsonObject){
        if(jsonObject != null){
                String nowTemp = "";
                String description = "";
                try {
                    nowTemp = jsonObject.getJSONObject("main").getString("temp");
                    description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                }catch (JSONException e){
                    e.printStackTrace();
                }
            description = transferWeather(description);
            final String meg[] = {description,nowTemp};
            return meg;
            }else{
            return null;
        }

    }

    private String transferWeather (String weather){
        weather = weather.toLowerCase();
        if(weather.equals("haze")){
            return "Ballad";
        }else if(weather.equals("fog")){
            return "Ballad";
        }else if(weather.equals("clouds")){
            return "Rap / Hip-hop";
        }else if(weather.equals("few clouds")){
            return "Rap / Hip-hop";
        }else if(weather.equals("broken clouds")){
            return "Dance";
        }else if(weather.equals("overcast clouds")){
            return "Ballad";
        }else if(weather.equals("clear sky")){
            return "Dance";
        }
        return "";
    }
    private String arora_text(String text){
        if(text.equals("Ballad")){
            return "구름많은날 듣는 감성";
        }else if(text.equals("Rap / Hip-hop")){
            return "흥을 붙돝아주는 감성";
        }else{
            return "맑은 날 듣는 댄스 감성";
        }
    }
    int index = 0;
    private void getAudioListFormMediaDatabase(String weather_1){
        mArrayList = new ArrayList<Audio_item>();
        final String aa = weather_1;
        final ArrayList id_array = new ArrayList<Integer>();
        getLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
                String selection = MediaStore.Audio.Media.IS_MUSIC + " =1";
                String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
                return new CursorLoader(((FragmentActivity)getActivity()).getApplicationContext(),uri,projection,selection,null,sortOrder);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                String[] genresProjection = {
                        MediaStore.Audio.Genres.NAME,
                        MediaStore.Audio.Genres._ID
                };
                Cursor genresCursor;
                    if(data != null && data.getCount() > 0){
                        while(data.moveToNext()){
                            long id = data.getLong(data.getColumnIndex(MediaStore.Audio.Media._ID));
                            Uri m_uri = MediaStore.Audio.Genres.getContentUriForAudioId("external",(int)id);
                            genresCursor = context.getContentResolver().query(m_uri,genresProjection,null,null,null);
                            if(genresCursor.moveToNext()){
                                String genername = genresCursor.getString(genresCursor.getColumnIndex(MediaStore.Audio.Genres.NAME));
                                Log.i("Log genres name",genername);
                                if(genername.contains(aa)){
                                    String title = data.getString(data.getColumnIndex(MediaStore.Audio.Media.TITLE));
                                    String artist = data.getString(data.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                                    long dur = data.getLong(data.getColumnIndex(MediaStore.Audio.Media.DURATION));
                                    long albumid = data.getLong(data.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                                    String path = data.getString(data.getColumnIndex(MediaStore.Audio.Media.DATA));
                                    String artisId = data.getString(data.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                                    String album = data.getString(data.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                                    mArrayList.add(new Audio_item(id,title,artist,dur,albumid,index,path,artisId,album));
                                    index++;
                                }
                            }
                        }
                        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recom_recycler);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,3);
                        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(gridLayoutManager);
                        RecomAudioAdapter adapter = new RecomAudioAdapter(mArrayList,context);
                        recyclerView.setAdapter(adapter);
                    }
            }
            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                    loader.reset();
            }
        });
    }

    private void getAudioListFormMediaDatabase_resume(String weather_1){
        mArrayList = new ArrayList<Audio_item>();
        final String aa = weather_1;
        getLoaderManager().restartLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
                String selection = MediaStore.Audio.Media.IS_MUSIC + " =1";
                String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
                return new CursorLoader(((FragmentActivity)getActivity()).getApplicationContext(),uri,projection,selection,null,sortOrder);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                String[] genresProjection = {
                        MediaStore.Audio.Genres.NAME,
                        MediaStore.Audio.Media._ID
                };
                Cursor genresCursor;
                if(data != null && data.getCount() > 0){
                    while(data.moveToNext()){
                        long id = data.getLong(data.getColumnIndex(MediaStore.Audio.Media._ID));
                        Uri m_uri = MediaStore.Audio.Genres.getContentUriForAudioId("external",(int)id);
                        genresCursor = context.getContentResolver().query(m_uri,genresProjection,null,null,null);
                        if(genresCursor.moveToNext()){
                            String genername = genresCursor.getString(genresCursor.getColumnIndex(MediaStore.Audio.Genres.NAME));
                            Log.i("Log genres name",genername);
                            if(genername.contains(aa)){
                                String title = data.getString(data.getColumnIndex(MediaStore.Audio.Media.TITLE));
                                String artist = data.getString(data.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                                long dur = data.getLong(data.getColumnIndex(MediaStore.Audio.Media.DURATION));
                                long albumid = data.getLong(data.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                                String path = data.getString(data.getColumnIndex(MediaStore.Audio.Media.DATA));
                                String artisId = data.getString(data.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                                String album = data.getString(data.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                                mArrayList.add(new Audio_item(id,title,artist,dur,albumid,index,path,artisId,album));
                                index++;
                            }
                        }
                    }
                    RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recom_recycler);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(context,3);
                    gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    RecomAudioAdapter adapter = new RecomAudioAdapter(mArrayList,context);
                    recyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        });
    }
}
