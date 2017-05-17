package com.vitech.storiesofcommonman;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TeamActivity extends AppCompatActivity {
RecyclerView teamListView;
TeamListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        teamListView = (RecyclerView)findViewById(R.id.team_list);
        adapter = new TeamListAdapter(this);
        teamListView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
new TeamLoader().execute();
    }

    class TeamLoader extends AsyncTask<String,String,String>{
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(TeamActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setMessage("Loading");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String resp;
            try {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS);
                builder.retryOnConnectionFailure(true);
                builder.followRedirects(true).followSslRedirects(true);
                OkHttpClient client = builder.build();
                Request request = new Request.Builder().url("http://storiesofcommonman.azurewebsites.net/wp-json/wp/v2/pages/81?_embed").get().build();
                Response response = client.newCall(request).execute();
             resp = response.body().string();
            }catch(Exception e){
                e.printStackTrace();
                FirebaseCrash.report(e);
                resp = "0";
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if (s.equals("0")) {
                    dialog.cancel();
                    new AlertDialog.Builder(TeamActivity.this).setMessage("!Oops an error occurred").show();

                } else {
                    dialog.cancel();
                    JSONObject data = new JSONObject(s);

                    Document document = Jsoup.parse(data.getJSONObject("content").getString("rendered"));
                    Elements avatars = document.getElementsByAttributeValueContaining("data-type", "avatar");
                    Elements names = document.getElementsByAttributeValueContaining("data-type", "title");
                    Elements bios = document.getElementsByAttributeValueContaining("data-type", "bio");
                    JSONArray teamList = new JSONArray();
                    for (int i = 0; i < avatars.size(); i++) {
                        JSONObject object = new JSONObject();
                        object.put("avatar", avatars.get(i).attr("src"));
                        object.put("title", names.get(i).text());
                        object.put("bio", bios.get(i).text());
                        teamList.put(object);
                    }
                    adapter.setTeamList(teamList);
                    teamListView.setAdapter(adapter);

                }
            } catch (Exception e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
                new AlertDialog.Builder(TeamActivity.this).setCancelable(false).setMessage("!Oops an error occurred").show();
            }
        }
    }
}
