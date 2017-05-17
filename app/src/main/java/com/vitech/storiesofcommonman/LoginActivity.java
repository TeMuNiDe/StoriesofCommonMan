package com.vitech.storiesofcommonman;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    View loginContainer;
    CallbackManager manager;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener listener;
    GoogleSignInOptions gso;
    SharedPreferences preferences;
    static String TAG = "Auth";
    GoogleApiClient googleApiClient;
    ProgressDialog builder;
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext())!= ConnectionResult.SUCCESS){


            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE, 2404, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });

            dialog.setCanceledOnTouchOutside(false);
            dialog.setTitle("Update Google Play Services");
            dialog.show();
        }else {
            builder.show();
            manager.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1258) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {

                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                } else {
                    Toast.makeText(this, "Sign in fail", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
            FacebookSdk.sdkInitialize(getApplicationContext());
            setContentView(R.layout.activity_login_activity);
            if(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext())!= ConnectionResult.SUCCESS){Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE, 2404, new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            finish();
        }
    });
    dialog.setCanceledOnTouchOutside(false);
    dialog.setTitle("Update Google Play Services");
    dialog.show();
}



            preferences = getSharedPreferences("userpreferences",MODE_PRIVATE);
            getSupportActionBar().hide();
            builder = new ProgressDialog(LoginActivity.this);
            builder.setCancelable(false);
            builder.setMessage("Please Wait...");
            builder.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            builder.setIndeterminate(false);

            manager = CallbackManager.Factory.create();
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            googleApiClient = new GoogleApiClient.Builder(this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
            auth = FirebaseAuth.getInstance();

            listener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        Log.d("Auth", "onAuthStateChanged:signed_in:" + user.getUid());
                        preferences.edit().putString("email",user.getEmail()).putString("name",user.getDisplayName()).commit();
                     Intent i =   new Intent(getApplicationContext(),DashBoardActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                      Log.d("Auth", "onAuthStateChanged:signed_out");
                    }
                }
            };


            loginContainer = findViewById(R.id.login_container);
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation a = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.logo_animation);
                    a.setInterpolator(new DecelerateInterpolator());
                    a.setDuration(1000);
                    a.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            loginContainer.setVisibility(View.VISIBLE);
                            Animation b = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.slide_up);
                            b.setInterpolator(new DecelerateInterpolator());
                            b.setDuration(1000);
                            loginContainer.startAnimation(b);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    findViewById(R.id.brand_logo).startAnimation(a);
                }
            }, 1000);

            LoginButton button = (LoginButton) findViewById(R.id.fb_login);
            List<String> perms = new ArrayList<>();
            perms.add("email");
            perms.add("public_profile");
            button.setReadPermissions(perms);
            button.registerCallback(manager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {

handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {
                    Log.d("Auth",error.toString());
                    error.printStackTrace();
                    FirebaseCrash.report(error);

                }
            });

            SignInButton buttonn =(SignInButton) findViewById(R.id.g_login);

            buttonn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext())!= ConnectionResult.SUCCESS){


                        Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LoginActivity.this, GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE, 2404, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                finish();
                            }
                        });

                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setTitle("Update Google Play Services");
                        dialog.show();
                    }else {

                        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                        startActivityForResult(signInIntent, 1258);

                    }
                }
            });


        }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(listener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (listener != null) {
            auth.removeAuthStateListener(listener);
        }
    }


        private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
            Log.d("Auth", "firebaseAuthWithGoogle:" + acct.getId());

            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
           auth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                            if (!task.isSuccessful()) {

                                if(task.getException()instanceof FirebaseAuthUserCollisionException){
                                    Toast.makeText(LoginActivity.this, "Email ID Already Exists",
                                            Toast.LENGTH_SHORT).show();
                                    LoginManager.getInstance().logOut();
                                }
                                Log.d(TAG, "signInWithCredential", task.getException());
                                builder.cancel();
                                FirebaseCrash.report(task.getException());
                                Toast.makeText(LoginActivity.this, "Google Authentication failed.",
                                        Toast.LENGTH_LONG).show();
                            }

                        }
                    });
        }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            if(task.getException()instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(LoginActivity.this, "Email ID Already Exists",
                                        Toast.LENGTH_SHORT).show();
                                LoginManager.getInstance().logOut();
                            }
                            else {
                                Log.d(TAG, "signInWithCredential", task.getException());
                                builder.cancel();
                                FirebaseCrash.report(task.getException());
                                Toast.makeText(LoginActivity.this, "FB Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
    }


}
