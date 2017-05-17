package com.vitech.storiesofcommonman;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.iamtheib.infiniterecyclerview.InfiniteAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;


public class DashBoardAdapter extends InfiniteAdapter<DashBoardAdapter.DashBoardItemHolder> {
    Context context;
    OnClickListener listener;
    JSONArray dashBoardObjects;

    public DashBoardAdapter( Activity activity){
        this.context = activity;
        this.listener = (OnClickListener)activity;

    }
    public void setDashBoardObjects(JSONArray array){
        this.dashBoardObjects = array;
    }


    @Override
    public int getVisibleThreshold() {
        return 2;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder hholder, int position) {

if(hholder instanceof DashBoardItemHolder) {


    final DashBoardAdapter.DashBoardItemHolder holder = (DashBoardItemHolder) hholder;
    try {
        String featImage = "";
        final JSONObject current = dashBoardObjects.getJSONObject(position);
        try {
          featImage = current.getJSONObject("_embedded").getJSONArray("wp:featuredmedia").getJSONObject(0).getString("source_url");
        }catch (JSONException e){
            Document doct = Jsoup.parse(current.getJSONObject("content").getString("rendered"));

          featImage = doct.select("img").first().attr("src");
        }
        ImageLoader.getInstance().displayImage(featImage, holder.header_Image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                holder.header_Image.setImageResource(R.drawable.logo_brand);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
        holder.postTitle.setText(Jsoup.parse(current.getJSONObject("title").getString("rendered")).text());
        Document doc = Jsoup.parse(current.getJSONObject("excerpt").getString("rendered"));

        holder.post_excerpt.setText(doc.select("p").text());
        holder.postAuthor.setText("Author : " + current.getJSONObject("_embedded").getJSONArray("author").getJSONObject(0).getString("name"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(current);
            }
        });

    } catch (Exception e) {
        e.printStackTrace();
        FirebaseCrash.report(e);
    }
    super.onBindViewHolder(hholder,position);
}

    }


    @Override
    public RecyclerView.ViewHolder getLoadingViewHolder(ViewGroup parent) {
      return new LoadingViewHolder(LayoutInflater.from(context).inflate(R.layout.dash_board_loading,parent,false));

    }

    @Override
    public int getCount() {
        return dashBoardObjects.length();
    }

    @Override
    public int getViewType(int position) {
        return 1;
    }

    @Override
    public DashBoardItemHolder onCreateView(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.dash_board_item,parent,false);
        return new DashBoardItemHolder(itemView);
    }



    class DashBoardItemHolder extends RecyclerView.ViewHolder{
ImageView header_Image;
        View itemView;
        TextView postTitle,post_excerpt,postAuthor;
        public DashBoardItemHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            header_Image = (ImageView)itemView.findViewById(R.id.header_image);
            postTitle = (TextView)itemView.findViewById(R.id.postTitle);
            post_excerpt = (TextView)itemView.findViewById(R.id.post_excerpt);
            postAuthor  = (TextView)itemView.findViewById(R.id.post_author);

        }

    }
    class LoadingViewHolder extends RecyclerView.ViewHolder{
        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }




    interface OnClickListener{
void onItemClick(JSONObject object);
    }

}
