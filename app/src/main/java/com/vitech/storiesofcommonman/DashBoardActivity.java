package com.vitech.storiesofcommonman;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;

import com.iamtheib.infiniterecyclerview.InfiniteAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DashBoardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,DashBoardAdapter.OnClickListener {
    RecyclerView dashBoardList;
    DashBoardAdapter adapter;
    FloatingActionButton fab;
    EditText newPostTitle,newPostContent;
    String file_1,file_2,file_3;
    SharedPreferences preferences;
    TextView im_1,im_2,im_3;
    Activity activity;
    JSONArray dashboardArray;
    int currFile = 0;
    int fabstate = 0;
    String newTitle,newContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("theme_preferences",MODE_PRIVATE);
        setTheme(preferences.getBoolean("night",false)?R.style.AppTheme_Dark_NoActionBar:R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

newPostTitle = (EditText)findViewById(R.id.new_post_title);
        newPostContent = (EditText)findViewById(R.id.new_post_content);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageView userPic = (ImageView)navigationView.getHeaderView(0).findViewById(R.id.user_picture);
        TextView userName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.user_name);
im_1 = (TextView)findViewById(R.id.im_1);
        im_3 = (TextView)findViewById(R.id.im_3);
        im_2 = (TextView)findViewById(R.id.im_2);
        im_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currFile = 1;
                Intent intent = new Intent(getApplicationContext(), AlbumSelectActivity.class);
                intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 1);
                startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);

        }});
        im_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currFile = 2;
                Intent intent = new Intent(getApplicationContext(), AlbumSelectActivity.class);
                intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 1);
                startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
            }
        });
        im_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currFile = 3;
                Intent intent = new Intent(getApplicationContext(), AlbumSelectActivity.class);
                intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 1);
                startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);

            }
        });
        TextView userEmail = (TextView)navigationView.getHeaderView(0).findViewById(R.id.user_email);
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
        ImageLoader.getInstance().init(configuration);

        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(true).build();
        ImageLoader.getInstance().displayImage(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString(),userPic,options);
        userName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        userEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());



 fab = (FloatingActionButton) findViewById(R.id.fab);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.KITKAT){

fab.setVisibility(View.GONE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
if(fabstate==0){
    setTitle("Submit Story");
    View newPost = findViewById(R.id.newPostParent);
    Animation slideUP = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_full_up);
    slideUP.setInterpolator(new DecelerateInterpolator());
    slideUP.setDuration(500);
    slideUP.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
fab.setImageResource(R.drawable.ic_check_circle_black_24dp);
            fabstate=1;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    });
    newPost.setVisibility(View.VISIBLE);dashBoardList.setVisibility(View.GONE);
    newPost.startAnimation(slideUP);
}
                else{
    sendPost();


                }
            }
        });
dashBoardList=(RecyclerView)findViewById(R.id.dash_board_list);
        adapter=new DashBoardAdapter(this);

adapter.setOnLoadMoreListener(new InfiniteAdapter.OnLoadMoreListener() {
    @Override
    public void onLoadMore() {
new MoreLoader().execute();
Log.d("moreload","called");
    }
});
if(preferences.getBoolean("data_saved",false)){
    try {
        this.dashboardArray = new JSONArray(preferences.getString("data","null"));
    }catch (JSONException e){
        e.printStackTrace();
    }
}
        clearData();
        dashBoardList.setLayoutManager(new LinearLayoutManager(DashBoardActivity.this,LinearLayoutManager.VERTICAL,false));
        if(this.dashboardArray!=null){
         adapter.setDashBoardObjects(this.dashboardArray);
            dashBoardList.setAdapter(adapter);
        }else {
            new PrimeLoader().execute();
        }
        activity = this;
    }

    @Override
    public void onBackPressed() {
        if(fabstate==1){
            revertBack();
            return;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.theme_main).setTitle(preferences.getBoolean("night",false)?"Day Mode":"Night Mode");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.theme_main) {
            preferences.edit().putBoolean("night",!preferences.getBoolean("night",false)).commit();saveData();recreate();invalidateOptionsMenu();
        }if(id==R.id.log_out){
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_bob) {
            Intent link = new Intent(Intent.ACTION_VIEW);
            link.setData(Uri.parse("https://www.amazon.com/storiesofcommonman-First-Copy-Book-1-ebook/dp/B01M7YYHOK"));
            startActivity(Intent.createChooser(link,"Open with..."));

        } else if (id == R.id.nav_about) {
            startActivity(new Intent(getApplicationContext(),AboutActivity.class));

        } else if (id == R.id.nav_team) {
            startActivity(new Intent(getApplicationContext(),TeamActivity.class));

        }else if (id == R.id.nav_share) {
            Intent link = new Intent(Intent.ACTION_SEND);
            link.setType("text/plain");
            link.putExtra(Intent.EXTRA_TEXT,"Stories of Common Man\n\nhttp://play.google.com/store/apps/details?id=com.vitech.storiesofcommonman");
            startActivity(Intent.createChooser(link,"Share with..."));


        } else if (id == R.id.nav_visit) {
            Intent link = new Intent(Intent.ACTION_VIEW);
            link.setData(Uri.parse("http://storiesofcommonman.in"));
            startActivity(Intent.createChooser(link,"Open with..."));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onItemClick(JSONObject i){

Intent story = new Intent(this,StoryActivity.class).putExtra("story",i.toString());
      startActivity(story);

    }
    void revertBack(){
        setTitle("Story Feed");
       final View newPost = findViewById(R.id.newPostParent);
        Animation slideUP = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_full_down);
        slideUP.setInterpolator(new AccelerateInterpolator());
        slideUP.setDuration(500);
        slideUP.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                newPost.setVisibility(View.GONE);
                fab.setImageResource(R.drawable.ic_mode_edit_black_24dp);
                fabstate=0;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        dashBoardList.setVisibility(View.VISIBLE);
        newPost.startAnimation(slideUP);
    }

    class PrimeLoader extends AsyncTask<String,String,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS);
                builder.retryOnConnectionFailure(true);
                builder.followRedirects(true).followSslRedirects(true);
                OkHttpClient client = builder.build();

                Request request = new Request.Builder().url("http://storiesofcommonman.azurewebsites.net/wp-json/wp/v2/posts?_embed").get().build();
                Response response = client.newCall(request).execute();
                String resp = response.body().string();
                return resp;
            }catch (Exception e){
                if(e instanceof UnknownHostException)
                return "0";
                if ( e instanceof SocketTimeoutException)
                    return "2";
                else {
                    e.printStackTrace();
                    FirebaseCrash.report(e);
                    return "1";
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if (s.equals("0")) {
                    Toast.makeText(DashBoardActivity.this, "Please Check Your Network Connection", Toast.LENGTH_LONG).show();
                    finish();


                } else if (s.equals("1")) {
                    Toast.makeText(DashBoardActivity.this, "Un Known Error Occurred", Toast.LENGTH_LONG).show();

                } else if(s.equals("2") ){
                    Toast.makeText(DashBoardActivity.this, "Connection Timed out", Toast.LENGTH_LONG).show();
                }
                else
                 {
                    setDashBoardList(new JSONArray(s));
                }


            }catch(Exception e){
                e.printStackTrace();
                FirebaseCrash.report(e);
            }
        }
    }

    void setDashBoardList(JSONArray dashBoardArray){
if(this.dashboardArray==null) {
    this.dashboardArray = dashBoardArray;
    adapter.setDashBoardObjects(dashBoardArray);
    dashBoardList.setAdapter(adapter);
}else {
    if(dashBoardArray.length()<10){
        adapter.setShouldLoadMore(false);


    }
    this.dashboardArray = addItems(this.dashboardArray, dashBoardArray);
    adapter.setDashBoardObjects(this.dashboardArray);
    adapter.moreDataLoaded(this.dashboardArray.length()-dashBoardArray.length(), dashBoardArray.length());



}

    }


    public static String getPath(Context context, Uri uri){
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
    void sendPost(){
        Snackbar snackbar = Snackbar.make(findViewById(R.id.snac_container),"Text",Snackbar.LENGTH_SHORT);

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if(newPostTitle.getText().toString().equals("")){
           snackbar.setText("Title Cannot be Empty").show();
            return;
        }
        if(newPostContent.getText().toString().equals("")){
            snackbar.setText("Content Cannot be Empty").show();
            return;
        }
        if(file_1==null||file_2==null||file_3==null){
            snackbar.setText("Select 3 Images").show();
            return;
        }
        if(file_1.equals(file_2)||file_1.equals(file_3)||file_2.equals(file_3)){
            snackbar.setText("Select 3 Different Images").show();
            return;
        }
        newTitle = newPostTitle.getText().toString();
        newContent = newPostContent.getText().toString();
new Submit().execute(new File(file_1),new File(file_2),new File(file_3));
    }
    void saveData(){
        if(dashboardArray!=null)
        preferences.edit().putBoolean("data_saved",true).putString("data",dashboardArray.toString()).commit();
    }
    void clearData(){
        preferences.edit().remove("data_saved").remove("data").commit();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantsCustomGallery.REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);

            for (int i = 0; i < images.size(); i++) {
                Uri uri = Uri.fromFile(new File(images.get(i).path));

switch (currFile){
    case 1:file_1 = getPath(getApplicationContext(),uri);im_1.setText(getPath(getApplicationContext(),uri));break;
    case 2:file_2 = getPath(getApplicationContext(),uri);im_2.setText(getPath(getApplicationContext(),uri));break;
    case 3:file_3 = getPath(getApplicationContext(),uri);im_3.setText(getPath(getApplicationContext(),uri));break;
}
            }
        }
    }

    class MoreLoader extends AsyncTask<String,String,String>{
        int page = 1;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
page = (dashboardArray.length()/10)+1;

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS);
                builder.retryOnConnectionFailure(true);
                builder.followRedirects(true).followSslRedirects(true);
                OkHttpClient client = builder.build();

                Request request = new Request.Builder().url("http://storiesofcommonman.azurewebsites.net/wp-json/wp/v2/posts/?_embed&&page="+page).get().build();
                Log.d("more",request.toString());
                Response response = client.newCall(request).execute();
                String resp = response.body().string();
                return resp;
                }catch (Exception e){
                if(e instanceof UnknownHostException)
                    return "0";
                if ( e instanceof SocketTimeoutException)
                    return "2";
                else {
                    e.printStackTrace();
                    FirebaseCrash.report(e);
                    return "1";
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if (s.equals("0")) {
                    Toast.makeText(DashBoardActivity.this, "Please Check Your Network Connection", Toast.LENGTH_LONG).show();
                    finish();


                } else if (s.equals("1")) {
                    Toast.makeText(DashBoardActivity.this, "Unknown Error Occurred", Toast.LENGTH_LONG).show();

                } else if(s.equals("2") ){
                    Toast.makeText(DashBoardActivity.this, "Connection Timed out", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Log.d("More Loaded","true");
                    setDashBoardList(new JSONArray(s));
                }


            }catch(Exception e){
                e.printStackTrace();
                FirebaseCrash.report(e);
            }
        }
    }
    private JSONArray addItems(JSONArray arr1, JSONArray arr2){
        JSONArray result = new JSONArray();
        try {

            for (int i = 0; i < arr1.length(); i++) {
                result.put(arr1.get(i));
            }
            for (int i = 0; i < arr2.length(); i++) {
                result.put(arr2.get(i));
            }
        }
            catch(Exception e){

              e.printStackTrace();
                FirebaseCrash.report(e);
            }
        return result;
    }
    class Submit extends AsyncTask<File,String,String> {
ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(DashBoardActivity.this);
            dialog.setIndeterminate(false);

            dialog.setMessage("Uploading Image 1/3...");
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(File... params) {
            try {

                String file1 = params[0].getName();
                String file2 = params[1].getName();
                String file3 = params[2].getName();
                Log.d("Files",file1+file2+file3);
                String[] ext1 = file1.split("\\.");
                Log.d("length",Integer.toString(ext1.length));
                String[] ext2 = file2.split("\\.");
                String[] ext3 = file3.split("\\.");

                OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
                clientBuilder.followRedirects(true).followSslRedirects(true).retryOnConnectionFailure(true).connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS);


                RequestBody Image1body = RequestBody.create(MediaType.parse("image/"+ext1[ext1.length-1]), params[0]);
                RequestBody Image2body = RequestBody.create(MediaType.parse("image/"+ext2[ext2.length-1]), params[1]);
                RequestBody Image3body = RequestBody.create(MediaType.parse("image/"+ext3[ext3.length-1]), params[2]);

                Request upload1 = new Request.Builder().addHeader("Content-type",ext1[ext1.length-1]).url("http://storiesofcommonman.azurewebsites.net/wp-json/wp/v2/media").addHeader("Content-Disposition","attachment;filename=\""+file1+"\"").addHeader("Authorization", " Basic c3Rvcmllc29mY29tbW9ubWFuOndldHQgY1RpeCBDQkpaIFVrTUkgRGRVNiBINkNG").post(Image1body).build();
                Request upload2 = new Request.Builder().addHeader("Content-type", ext2[ext2.length-1]).url("http://storiesofcommonman.azurewebsites.net/wp-json/wp/v2/media").addHeader("Content-Disposition","attachment;filename=\""+file2+"\"").addHeader("Authorization", " Basic c3Rvcmllc29mY29tbW9ubWFuOndldHQgY1RpeCBDQkpaIFVrTUkgRGRVNiBINkNG").post(Image2body).build();
                Request upload3 = new Request.Builder().addHeader("Content-type", ext3[ext3.length-1]).url("http://storiesofcommonman.azurewebsites.net/wp-json/wp/v2/media").addHeader("Content-Disposition","attachment;filename=\""+file3+"\"").addHeader("Authorization", " Basic c3Rvcmllc29mY29tbW9ubWFuOndldHQgY1RpeCBDQkpaIFVrTUkgRGRVNiBINkNG").post(Image3body).build();

                Response uploadResponse1 = clientBuilder.build().newCall(upload1).execute();
                if(uploadResponse1.code()!=201){
                    FirebaseCrash.report(new Throwable(uploadResponse1.body().string()));
                    return "2";
                }
                publishProgress("1");
                Response uploadResponse2 = clientBuilder.build().newCall(upload2).execute();
                publishProgress("2");
                if(uploadResponse2.code()!=201){
                    FirebaseCrash.report(new Throwable(uploadResponse1.body().string()));
                    return "2";
                }
                Response uploadResponse3 = clientBuilder.build().newCall(upload3).execute();
                if(uploadResponse3.code()!=201){
                    FirebaseCrash.report(new Throwable(uploadResponse1.body().string()));
                    return "2";
                }
                publishProgress("3");
                FirebaseUser user  =FirebaseAuth.getInstance().getCurrentUser();
                RequestBody body = RequestBody.create(StoryActivity.JSON,buildPost(newTitle,newContent,user.getDisplayName()+","+user.getEmail(),new String[]{trimHtml(uploadResponse1.body().string()),trimHtml(uploadResponse2.body().string()),trimHtml(uploadResponse3.body().string())}));
                Request post = new Request.Builder().addHeader("Authorization", " Basic c3Rvcmllc29mY29tbW9ubWFuOndldHQgY1RpeCBDQkpaIFVrTUkgRGRVNiBINkNG").addHeader("Content-Type","application/json").url("http://storiesofcommonman.azurewebsites.net/wp-json/wp/v2/posts").post(body).build();


                Response postResponse  = clientBuilder.build().newCall(post).execute();
                if(postResponse.code()==201){
                    return "success";
                }
                }
                catch(Exception e){

                e.printStackTrace();

              if(e instanceof SocketTimeoutException){
                  return "-1";

              }
              if(e instanceof UnknownHostException){
                  return "1";
              }
              else {
                  FirebaseCrash.report(e);
              }
            }
            return "0";
        }


        @Override
        protected void onPostExecute(String s) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.snac_container),"Text",Snackbar.LENGTH_LONG);

            View snac = snackbar.getView();

            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            dialog.cancel();
            if(s.equals("-1")){
                snac.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                snackbar.setText("Connection Timed out");
                snackbar.show();
            }
           else if(s.equals("1")){
                snac.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                snackbar.setText("Please Check your Network Connection");
                snackbar.show();
            }
                            else    if(s.equals("success")){
                                snac.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                                snackbar.setText("Success..!");
                                snackbar.show();
                            }
                            else if(s.equals("2")){
                snac.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                snackbar.setText("Internal Server Error");
                snackbar.show();

            }
                            else {
                                snac.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                                snackbar.setText("Unknown Error");
                                snackbar.show();
                            }
                            }

        @Override
        protected void onProgressUpdate(String... values) {
            if(values[0].equals("1")){
                dialog.setMessage("Uploading Image 2/3...");
            }
            if(values[0].equals("2")){
                dialog.setMessage("Uploading Image 3/3...");
            }
            if(values[0].equals("3")){
                dialog.setMessage("Submitting post...");
            }
            super.onProgressUpdate(values);
        }
        }
    public String trimHtml(String s) throws Exception{
String[] parts  = s.split("</body>");
        String js =  parts[parts.length-1];
   JSONObject object  = new JSONObject(js);
    return object.getJSONObject("guid").getString("raw");
    }
    public String buildPost(String title,String data,String author,String[] images){
        JSONObject object = new JSONObject();
        String content  = data +"\n\n\nBy : \t"+author+"\n\n\n"+"Images :"+images[0]+"\n\n"+images[1]+"\n\n"+images[2];
        try {
            object.put("author","1").put("title",title).put("content",content);
            }catch (Exception e){
            e.printStackTrace();
            }
        return object.toString();
    }
}
