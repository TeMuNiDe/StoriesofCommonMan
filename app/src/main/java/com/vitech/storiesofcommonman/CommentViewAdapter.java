package com.vitech.storiesofcommonman;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import com.google.firebase.crash.FirebaseCrash;
import com.vitech.storiesofcommonman.StoryActivity.CommentLoader.Comment;

import java.util.ArrayList;


public class CommentViewAdapter extends RecyclerView.Adapter<CommentViewAdapter.CommentHolder> {
    Context context;
ArrayList<Comment> array;
    public CommentViewAdapter(Activity activity){
        this.context = activity;
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_layout,parent,false);

        return new CommentHolder(view);
    }
public void setArray(ArrayList<Comment> array){
    this.array = array;
}
    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        try {
            holder.commentAuthor.setText(array.get(position).object.getString("author_name"));
            Document doc = Jsoup.parse(array.get(position).object.getJSONObject("content").getString("rendered"));

            holder.commentContent.setText(doc.select("p").text());
        }catch ( Exception e){
            FirebaseCrash.report(e);
        }
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    class CommentHolder extends RecyclerView.ViewHolder{
        TextView commentAuthor;
        TextView commentContent;
        public CommentHolder(View itemView) {
            super(itemView);
            commentAuthor = (TextView)itemView.findViewById(R.id.comment_author);
            commentContent = (TextView)itemView.findViewById(R.id.comment_content);
        }
    }
}
