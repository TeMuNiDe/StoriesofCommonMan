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
import com.google.firebase.crash.FirebaseCrash;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;



public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.TeamHolder> {
    Context context;
    JSONArray teamList;
    public TeamListAdapter(Activity activity){
        this.context = activity;

    }
public  void setTeamList(JSONArray teamList) {
    this.teamList = teamList;
}
    @Override
    public TeamHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new TeamHolder(LayoutInflater.from(context).inflate(R.layout.team_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(final TeamHolder holder, int position) {
        try {
            holder.name.setText(teamList.getJSONObject(position).getString("title"));
holder.bio.setText(teamList.getJSONObject(position).getString("bio"));

            ImageLoader.getInstance().displayImage(teamList.getJSONObject(position).getString("avatar"), holder.avatar, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    holder.avatar.setImageResource(R.drawable.logo_brand);

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
        }catch (Exception e){
            e.printStackTrace();
            FirebaseCrash.report(e);
        }
    }

    @Override
    public int getItemCount() {
        return teamList.length();
    }

    class TeamHolder extends RecyclerView.ViewHolder{
ImageView avatar;
        TextView name,bio;
        public TeamHolder(View itemView) {
            super(itemView);
            avatar = (ImageView)itemView.findViewById(R.id.avatar);
            name = (TextView)itemView.findViewById(R.id.team_title);
            bio = (TextView)itemView.findViewById(R.id.team_bio);
        }
    }
}
