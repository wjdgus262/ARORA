package com.arora.arora.Adapter;

import android.content.ContentUris;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.arora.arora.Audio.AudioApplication;
import com.arora.arora.R;
import com.arora.arora.data.Audio_item;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class RecomAudioAdapter extends RecyclerView.Adapter<RecomAudioAdapter.CustomViewHolder> implements Filterable {
    //필터 리스트
    private List<Audio_item> arrayList;

    private List<Audio_item>arrayListFull;

    private Context mcontext;
    MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public  RecomAudioAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card,parent,false);
        RecomAudioAdapter.CustomViewHolder viewHolder = new  RecomAudioAdapter.CustomViewHolder(view);
        return viewHolder;
    }


    public ArrayList<Long> getAudioIds(){
        int count = getItemCount();
        ArrayList<Long> audioIds = new ArrayList<>();
        for(int i = 0; i < count; i++){
            audioIds.add(arrayList.get(i).getId());
        }
        return audioIds;
    }


    @Override
    public void onBindViewHolder(RecomAudioAdapter.CustomViewHolder customViewHolder, final int position) {
//        CardItem item = mDataList.get(position);
//        holder.contents.setText(item.getContents());
        final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(artworkUri,arrayList.get(position).getAlbumId());
        customViewHolder.title.setText(arrayList.get(position).getTitle());
        customViewHolder.albumPath.setText(arrayList.get(position).getmDataPath());
//        RequestBuilder<Drawable> builder = Glide.with(mcontext)
//                .load(R.drawable.aurora_empty_album_img);
//        Glide.with(mcontext).load(albumArtUri).error(builder).into(customViewHolder.ImageView);
//        Picasso.with(mcontext).load(albumArtUri).error(R.drawable.aurora_empty_album_img).into(customViewHolder.ImageView);
        Glide.with(mcontext).load(albumArtUri).error(R.drawable.album_empty).into(customViewHolder.ImageView);
        customViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioApplication.getInstance().getServiceInterFace().setPlayList(getAudioIds()); // 재생목록등록
                TextView pathView = (TextView)view.findViewById(R.id.recom_path);
                String path = pathView.getText().toString();
                AudioApplication.getInstance().getServiceInterFace().play(position);
                AudioApplication.getInstance().getServiceInterFace().go(path);
            }
        });
    }


    public RecomAudioAdapter(List<Audio_item> arrayList, Context mcontext) {
        this.arrayList = arrayList;
        this.arrayListFull = new ArrayList<>(arrayList);
        this.mcontext = mcontext;
    }


    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    @Override
    public Filter getFilter() {
        return null;
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder{
        public View mView;
        protected  TextView id;
        protected TextView title;
        protected TextView subtitle;
        protected TextView dur;
        protected  TextView index_1;
        protected TextView albumPath;
        protected android.widget.ImageView ImageView;
        protected ImageView btnpaueplay;

        public CustomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            this.title = (TextView)itemView.findViewById(R.id.recom_title);
            this.ImageView = (android.widget.ImageView)itemView.findViewById(R.id.recom_album_img);
            this.albumPath = (TextView)itemView.findViewById(R.id.recom_path);
//            itemView.findViewById(R.id.recomp)
        }
    }
}
