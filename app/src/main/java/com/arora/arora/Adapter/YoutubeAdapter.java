package com.arora.arora.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arora.arora.R;
import com.arora.arora.data.YoutubeData;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class YoutubeAdapter extends RecyclerView.Adapter<YoutubeAdapter.CustomViewHolder> {
    Handler handler = new Handler();
    private ArrayList<YoutubeData> mList;
    private Context mcontext;
    public YoutubeAdapter(Context context,ArrayList<YoutubeData> mList) {
        this.mList = mList;
        mcontext = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_start,parent,false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                    try {
                        URL url = new URL(mList.get(position).getImgurl());
                        InputStream is = url.openStream();
                        final Bitmap bm = BitmapFactory.decodeStream(is);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                holder.youtube_img.setImageBitmap(bm);
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
            }
        });
        t.start();
        holder.youtube_title.setText(mList.get(position).getTitle());
        holder.youtube_title.setSelected(true);
        holder.youtube_chang.setText(mList.get(position).getChannel());
        holder.youtube_id.setText(mList.get(position).getVideoid());
        holder.youtube_channel.setText(mList.get(position).getChannelid());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView idView = (TextView)v.findViewById(R.id.youtube_id);
                String id = idView.getText().toString();

                TextView channelView = (TextView)v.findViewById(R.id.youtube_channel);
                String channel = channelView.getText().toString();
//                Log.i("clickid",id);
                String intentstring;
                if(id.equals("1")){
                    intentstring ="https://www.youtube.com/channel/"+channel;
//                    Toast.makeText(mcontext,""+)
                }else{
                    intentstring = "http://youtu.be/"+id;
                }
                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(intentstring ));
                mcontext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView youtube_img;
        protected TextView youtube_title;
        protected TextView youtube_chang;
        protected TextView youtube_id;
        protected TextView youtube_channel;
        public View mView;
        public CustomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            this.youtube_img = (ImageView)itemView.findViewById(R.id.youtube_img);
            this.youtube_title = (TextView) itemView.findViewById(R.id.youtube_title);
            this.youtube_chang = (TextView)itemView.findViewById(R.id.youtube_chan);
            this.youtube_id = (TextView)itemView.findViewById(R.id.youtube_id);
            this.youtube_channel = (TextView)itemView.findViewById(R.id.youtube_channel);
        }
    }
}
