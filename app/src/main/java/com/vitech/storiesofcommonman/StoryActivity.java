package com.vitech.storiesofcommonman;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
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
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.EXTRA_TEXT;

public class StoryActivity extends AppCompatActivity{
RecyclerView commentView;
    CommentViewAdapter adapter ;
    TextView postTitle;
    WebView postContent;
    ImageView featuredImage;
File imag;
    boolean comments = false;
    JSONObject current;
    String webviewTextColor;
    SharedPreferences preferences;
Bitmap loadeddImage;
    public static final MediaType JSON    = MediaType.parse("application/json; charset=utf-8");
    FirebaseAuth auth;
    EditText commentedit;

    FloatingActionButton fab;
    static int FAB_ACTION_SEND = 1;
    static int FAB_ACTION_SHARE = 0;
int fabAction = 0;
    View commentsView,storyView;
    String id = "";
    static String TAG = "StoryActivity";

View mDecorView;
Handler mHideHandler = new Handler();
    boolean mVisible = true;
    private static final int UI_ANIMATION_DELAY =300;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.story_menu,menu);
        menu.findItem(R.id.theme_story).setTitle(preferences.getBoolean("night",false)?"Day Mode":"Night Mode");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.go_full_screen:
                if(commentsView.getVisibility()==View.VISIBLE){
                    hideComments();
                }
               hide();break;
            case R.id.theme_story:preferences.edit().putBoolean("night",!preferences.getBoolean("night",false)).commit();recreate();invalidateOptionsMenu();break;
            case android.R.id.home:finish();
        }
        return true;
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("theme_preferences",MODE_PRIVATE);
        if(preferences.getBoolean("night",false)){
            setTheme(R.style.AppTheme_Dark);
            webviewTextColor = "#ffffff";
        }
        else {
            setTheme(R.style.AppTheme);
            webviewTextColor = "#000000";
        }

        setContentView(R.layout.activity_story);
        mDecorView = getWindow().getDecorView();
        getSupportActionBar().show();
        try {
            auth = FirebaseAuth.getInstance();
     current = new JSONObject(getIntent().getStringExtra("story"));
           this.id =current.getString("id");
            postTitle = (TextView) findViewById(R.id.post_full_title);
            postContent = (WebView) findViewById(R.id.post_content);
            featuredImage = (ImageView) findViewById(R.id.post_full_image);
            commentsView = findViewById(R.id.comments_view);
            storyView = findViewById(R.id.content_story);
            commentedit = (EditText)findViewById(R.id.comment_edit);
            postTitle.setText(Jsoup.parse(current.getJSONObject("title").getString("rendered")).text());
            postContent.setBackgroundColor(Color.TRANSPARENT);
           Document doc = Jsoup.parse(current.getJSONObject("content").getString("rendered"));
            doc.select("img").remove();
            doc.select("p").removeAttr("style");
            doc.outputSettings().charset("UTF-8");
            String docc ="<html><head><style>p{text-align:justify;color:"+webviewTextColor+"}</style></head><body>"+doc.select("p").outerHtml();


            Log.d("html",docc);

            postContent.loadData(docc,"text/html;charset=utf-8","UTF-8");

            ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(StoryActivity.this).build();
            ImageLoader.getInstance().init(configuration);
            fab = (FloatingActionButton) findViewById(R.id.fab);


            String featImage = "";
            try {
                featImage = current.getJSONObject("_embedded").getJSONArray("wp:featuredmedia").getJSONObject(0).getString("source_url");
            }catch (JSONException e){
                Document doct = Jsoup.parse(current.getJSONObject("content").getString("rendered"));

                featImage = doct.select("img").first().attr("src");
            }
            ImageLoader.getInstance().displayImage(featImage,featuredImage, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {

loadeddImage =loadedImage;
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });


            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        if(fabAction ==FAB_ACTION_SHARE) {

if(loadeddImage!=null) {
    shareImage(loadeddImage, postTitle.getText().toString() + "\n\n" + Jsoup.parse(current.getJSONObject("excerpt").getString("rendered")).select("p").text() + "\n\nRead More.. \n" + current.getString("link"));
}else {
    Toast.makeText(getApplicationContext(),"Pease wait till Image Loads",Toast.LENGTH_LONG).show();
}
                        }else {
                            if(commentedit.getText().toString().equals(""))

                            {                        Snackbar.make(findViewById(R.id.snackbar_container),"Empty Comment", Snackbar.LENGTH_SHORT).show();
                            }
                            else {
                                new NewComment().execute(new String[]{commentedit.getText().toString()});
                            }
                        }

                    }
                    catch (Exception e){
                        e.printStackTrace();
                        FirebaseCrash.report(e);
                    }}
            });



                     commentView = (RecyclerView) findViewById(R.id.comment_list);
            adapter = new CommentViewAdapter(this);

            commentView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        }catch (Exception e ){
            e.printStackTrace();
        }

        findViewById(R.id.show_comment_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!comments) {
                    new CommentLoader().execute(new String[]{id});
                }

                show();
openComments();
            }
        });


    }
    public void shareImage(Bitmap image, final String text) {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},0);

            }

        }
        String directory = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/"+getPackageName()+"/";
        File folder = new File(directory);
        try {
            if (!folder.exists()) {
                folder.mkdirs();
            }
            imag = new File(directory+"sharedImage.jpeg");

                OutputStream stream = new FileOutputStream(imag);
                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                stream.flush();
                stream.close();

            Intent view = new Intent(Intent.ACTION_SEND);

            view.setType("*/*").putExtra(EXTRA_STREAM,Uri.fromFile(imag));
            view.putExtra(EXTRA_TEXT,text);
            startActivity(Intent.createChooser(view,"Share through..."));




        }catch (Exception e){


                e.printStackTrace();



        }        }
    class CommentLoader extends AsyncTask<String,String,ArrayList<CommentLoader.Comment>>{
        ProgressDialog dialog;
        class Comment{
            JSONObject object;
            String id;
            String parent;
            public Comment(JSONObject object){
                try {
                    this.id = object.getString("id");
                    this.parent = object.getString("parent");
                    this.object = object;
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            public Comment(String err){
                this.id=err;
            }
        }
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(StoryActivity.this);
            dialog.setMessage("Loading Comments");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Comment> doInBackground(String... params) {
            ArrayList<Comment> comments = new ArrayList<>();
            ArrayList<Comment> parents = new ArrayList<>();
            try {

                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS);
                builder.retryOnConnectionFailure(true);
                builder.followRedirects(true).followSslRedirects(true);
                OkHttpClient client = builder.build();

                Request request = new Request.Builder().url("http://storiesofcommonman.azurewebsites.net/wp-json/wp/v2/comments?post="+params[0]).get().build();
                Response response = client.newCall(request).execute();
                String resp = response.body().string();
                JSONArray root = new JSONArray(resp);
               for(int i=0;i<root.length();i++){
                   comments.add(new Comment(root.getJSONObject(i)));
               }
                for(Comment comment:comments){
                    if(comment.parent.equals("0")){
                        parents.add(comment);
                    }
                }
                for(int i=0;i<parents.size();i++){
                   for(Comment comment:comments){
                     if(parents.get(i).id.equals(comment.parent)){
                         parents.add(i+1,comment);
                     }
                   }
                }



                return parents;
            }catch (Exception e){
                e.printStackTrace();
                if(e instanceof UnknownHostException) {
                    ArrayList<Comment> err = new ArrayList();
                    err.add(new Comment("NO_INTERNET"));

                    return err;
                }
                else {
                    FirebaseCrash.report(e);
                    ArrayList<Comment> err = new ArrayList();
                    err.add(new Comment("UNKNOWN_ERROR"));
                    return err;
                }
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Comment> s) {
          dialog.cancel();
            if(s.size()!=0) {
                if (s.get(0).id.equals("NO_INTERNET")) {
Toast.makeText(StoryActivity.this,"No Internet",Toast.LENGTH_LONG).show();
                }
                if (s.get(0).id.equals("UNKNOWN_ERROR")) {
Toast.makeText(StoryActivity.this,"Unknown error",Toast.LENGTH_LONG).show();
                }
                else {
                     comments =true;
                    adapter.setArray(s);
                    commentView.setAdapter(adapter);
                }
            }


        }
    }


    @Override
    public void onBackPressed() {
        if(commentsView.getVisibility()==View.VISIBLE){

         hideComments();
        return;
        }
        if(!mVisible){
            show();
            return;
        }
        super.onBackPressed();
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {

            mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            fab.setVisibility(View.VISIBLE);
        }
    };

    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        fab.setVisibility(View.GONE);
        mVisible = false;
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }
    private void show() {
        mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
        mVisible = true;
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }
    void openComments(){




        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                storyView.setVisibility(View.GONE);
            }
        },400);
        commentsView.setVisibility(View.VISIBLE);
        fab.bringToFront();
        Animation slideUP = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_full_up);
        slideUP.setInterpolator(new DecelerateInterpolator());
        slideUP.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                storyView.setVisibility(View.GONE);
                fabAction = FAB_ACTION_SEND;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        slideUP.setDuration(500);
        commentsView.startAnimation(slideUP);
        Animation fabToSend = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_contract);
        if(!hasNavBar(getResources())) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) fab.getLayoutParams();

            int sizeInDP = 60;

            int marginInDp = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, sizeInDP, getResources()
                            .getDisplayMetrics());
            params.bottomMargin =  marginInDp;
            fab.setLayoutParams(params);
        }
        fabToSend.setInterpolator(new DecelerateInterpolator());
        fabToSend.setDuration(500);
        fabToSend.setFillEnabled(true);
        fabToSend.setFillAfter(true);
fabToSend.setAnimationListener(new Animation.AnimationListener() {
    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

        fab.setImageResource(R.drawable.ic_menu_send);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
});
       fab.startAnimation(fabToSend);

    }
    void hideComments(){


        commentsView.setVisibility(View.GONE);
        Handler h = new Handler();
        h.postDelayed(new Runnable(){
            @Override
            public void run() {
                storyView.setVisibility(View.VISIBLE);
            }
        },200);
        Animation fabToShare = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_expand);
        fabToShare.setInterpolator(new AccelerateInterpolator());
        fabToShare.setDuration(500);
        fabToShare.setFillEnabled(true);
        fabToShare.setFillAfter(true);

        Animation slideUP = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_full_down);
        slideUP.setInterpolator(new AccelerateInterpolator());
        slideUP.setDuration(500);commentsView.startAnimation(slideUP);
        fabAction = FAB_ACTION_SHARE;
        fabToShare.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setImageResource(R.drawable.ic_menu_share);
            }

            @Override

            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.startAnimation(fabToShare);
    }
    class NewComment extends AsyncTask<String,String,String>{
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(StoryActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setMessage("Sending Comment...");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
try {

    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
    clientBuilder.followRedirects(true).followSslRedirects(true).retryOnConnectionFailure(true).connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS);
    RequestBody body = RequestBody.create(JSON, requestBuilder(auth.getCurrentUser().getDisplayName(), auth.getCurrentUser().getEmail(), params[0]));
    Request req = new Request.Builder().addHeader("Content-type", "application/json").url("http://storiesofcommonman.azurewebsites.net/wp-json/wp/v2/comments?post="+id).addHeader("Authorization"," Basic c3Rvcmllc29mY29tbW9ubWFuOndldHQgY1RpeCBDQkpaIFVrTUkgRGRVNiBINkNG").post(body).build();
    Response response = clientBuilder.build().newCall(req).execute();
    if(response.code()==201){
        return "1";
    }
}catch (Exception e){
    e.printStackTrace();
}
            return "0";
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("1")){
                dialog.cancel();
                new CommentLoader().execute(new String[]{id});
            }
            super.onPostExecute(s);
        }
    }
    static String requestBuilder(String... args) {
        try {

                JSONObject object = new JSONObject();
                object.put("author","1").put("author_name",args[0]).put("author_email",args[1]).put("content",args[2]);
                return object.toString();

        }catch (Exception e){
            e.printStackTrace();

        }
return "null";
    }
    public boolean hasNavBar (Resources resources)
    {
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }
}


