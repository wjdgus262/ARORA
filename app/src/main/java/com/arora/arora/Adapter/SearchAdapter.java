package com.arora.arora.Adapter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.arora.arora.Audio.AudioApplication;
import com.arora.arora.MainActivity;
import com.arora.arora.R;
import com.arora.arora.data.Audio_item;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.CustomViewHolder> implements Filterable {

    private ArrayList<String> datapath_array = new ArrayList<String>();
    private ArrayList<String> datapath_array_ringthon = new ArrayList<String>();


    //필터리스트
    private List<Audio_item> arrayList;

    //원본 리스트
    private List<Audio_item> arrayListFull;


    private ArrayList<Audio_item> audioIds;

//    Audio_item_1 audio_item_1 = new Audio_item_1();
    //context
    MainActivity mainActivity = new MainActivity();
    private Context mcontext;
    MediaPlayer mediaPlayer = new MediaPlayer();
    ImageView imageView;
    //viewholder create
    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(com.arora.arora.R.layout.listitem,viewGroup,false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }
    public ArrayList<Long> getAudioIds() {
    int count = getItemCount();
//    Log.i("COUNT",count+"");
    ArrayList<Long> audioIds = new ArrayList<>();
    for (int i = 0; i < count; i++) {
        audioIds.add(arrayList.get(i).getId());

    }
    return audioIds;
}

    public ArrayList<String> getPath(){
        int count = getItemCount();
        ArrayList<String> audioIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            audioIds.add(arrayList.get(i).getmDataPath());

        }
        return audioIds;
    }

    //bindviewholder // 객체생성, 클릭 이벤트
    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder customViewHolder, final int i) {
        final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(artworkUri,arrayList.get(i).getAlbumId());

            customViewHolder.title.setText(arrayList.get(i).getTitle());
            customViewHolder.title.setSelected(true);
            customViewHolder.subtitle.setText(arrayList.get(i).getSubTitle());
            customViewHolder.albumPath.setText(arrayList.get(i).getmDataPath());
            customViewHolder.dur.setText(android.text.format.DateFormat.format("mm:ss",arrayList.get(i).getDuration()));
            customViewHolder.artisid.setText(arrayList.get(i).getArtisId());
        Glide.with(customViewHolder.mView.getContext()).load(albumArtUri).error(com.arora.arora.R.drawable.aurora_empty_album_img).into(customViewHolder.imageView);
            customViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView pathView = (TextView)view.findViewById(com.arora.arora.R.id.list_path);
                    String path = pathView.getText().toString();

                    TextView artisid_view = (TextView)view.findViewById(com.arora.arora.R.id.item_artisid);
                    String artisid = artisid_view.getText().toString();
                    AudioApplication.getInstance().getServiceInterFace().setPlayList(getAudioIds()); // 재생목록등록
                    AudioApplication.getInstance().getServiceInterFace().play(i);
                    AudioApplication.getInstance().getServiceInterFace().go(path);

                }
            });

        customViewHolder.morebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(mcontext,customViewHolder.morebtn);
                popupMenu.inflate(com.arora.arora.R.menu.menu_main2);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_share:
                                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                StrictMode.setVmPolicy(builder.build());
                                datapath_array = getPath();
                                Uri uri = Uri.parse("file://" + datapath_array.get(i));
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("audio/*");
                                share.putExtra(Intent.EXTRA_STREAM, uri);
                                mcontext.startActivity(Intent.createChooser(share, "음악 파일 공유"));
                                break;
                            case R.id.menu_calling:
                                //벨소리설정
                                datapath_array_ringthon = getPath();
                                StrictMode.VmPolicy.Builder builder1 = new StrictMode.VmPolicy.Builder();
                                StrictMode.setVmPolicy(builder1.build());
                                File chosenFile = new File("file://" + datapath_array_ringthon.get(i));
                                Uri ringtoneUri = Uri.parse(chosenFile.getAbsolutePath());
                                ContentValues values = new ContentValues();
                                values.put(MediaStore.MediaColumns.DATA, chosenFile.getAbsolutePath());
                                values.put(MediaStore.MediaColumns.TITLE, chosenFile.getName());
                                values.put(MediaStore.MediaColumns.SIZE, chosenFile.length());
                                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
                                values.put(MediaStore.Audio.AudioColumns.ARTIST, mcontext.getString(R.string.app_name));
                                values.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, true);
                                values.put(MediaStore.Audio.AudioColumns.IS_NOTIFICATION, false);
                                values.put(MediaStore.Audio.AudioColumns.IS_ALARM, false);
                                values.put(MediaStore.Audio.AudioColumns.IS_MUSIC, false);
                                try {
                                    Uri uri1 = MediaStore.Audio.Media.getContentUriForPath(chosenFile.getAbsolutePath());
                                    mcontext.getContentResolver().delete(uri1, MediaStore.MediaColumns.DATA + "=\"" + chosenFile.getAbsolutePath() + "\"", null);
                                    Uri newUri = mcontext.getContentResolver().insert(uri1, values);
                                    RingtoneManager.setActualDefaultRingtoneUri(
                                            mcontext,
                                            RingtoneManager.TYPE_RINGTONE,
                                            newUri
                                    );
                                    Toast.makeText(mcontext,"벨소리 설정 성공",Toast.LENGTH_SHORT).show();
                                }catch (Exception e){
                                    StringWriter sw = new StringWriter();
                                    e.printStackTrace(new PrintWriter(sw));
                                    String exceptionAsStrting = sw.toString();
                                    Log.e("Stack", exceptionAsStrting);
                                    Toast.makeText(mcontext,"벨소리 설정 실패",Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }


    //생성자
    public SearchAdapter(Context context, List<Audio_item> list){
        this.arrayList = list;
        this.mcontext = context;
        arrayListFull = new ArrayList<>(list);
    }

    //count
    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }


    //filter(search)
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Audio_item> filterdList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filterdList.addAll(arrayListFull);

            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Audio_item item_1 : arrayListFull){
                    if(item_1.getTitle().toLowerCase().contains(filterPattern)){
                        filterdList.add(item_1);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterdList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList.clear();
            arrayList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        protected  TextView id;
        protected TextView title;
        protected TextView subtitle;
        protected TextView dur;
        protected  TextView index_1;
        protected TextView albumPath;
        protected ImageView imageView;
        protected ImageView btnpaueplay;
        protected TextView artisid;
        protected Context context;
        protected ImageView morebtn;
        public CustomViewHolder(View view) {
            super(view);
            mView =view;
            this.context = view.getContext();
//            this.id = (TextView)view.findViewById(R.id.albumid);
            this.title = (TextView) view.findViewById(com.arora.arora.R.id.item_title);
            this.subtitle = (TextView) view.findViewById(R.id.item_subtitle);
            this.dur = (TextView)view.findViewById(com.arora.arora.R.id.item_duration);
            this.imageView = (ImageView) view.findViewById(com.arora.arora.R.id.item_album_img);
            albumPath = (TextView)view.findViewById(com.arora.arora.R.id.list_path);
            artisid = (TextView)view.findViewById(com.arora.arora.R.id.item_artisid);
            morebtn = (ImageView)view.findViewById(com.arora.arora.R.id.item_more_btn);
//            this.circleImageView = (CircleImageView) view.findViewById(R.id.albumimg);
//            this.index_1 = (TextView)view.findViewById(R.id.albumindex);
//            this.albumPath = (TextView)view.findViewById(R.id.albumPath);
        }
    }
}
