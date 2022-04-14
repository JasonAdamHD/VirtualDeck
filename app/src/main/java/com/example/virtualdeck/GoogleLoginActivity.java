package com.example.virtualdeck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.virtualdeck.helpers.GlobalConstants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class GoogleLoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;
    private static final String REQUEST_ID_TOKEN = "1067103079036-ck4drvi56akfibt4n32rl2b314k037vg.apps.googleusercontent.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this::onClick);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(REQUEST_ID_TOKEN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void onClick(View view) {
        signIn();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            finish();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account == null)
                return;

            SharedPreferences sharedPreferences = getSharedPreferences(GlobalConstants.SHARED_PREFS, MODE_PRIVATE);
            Boolean isRegistered = sharedPreferences.getBoolean("REGISTERED", false);

            if (!isRegistered) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("REGISTERED", true);
                editor.putString("USERUUID", account.getId());
                GlobalConstants.USERUUID = getSharedPreferences(GlobalConstants.SHARED_PREFS, MODE_PRIVATE).getString("USERUUID", "");
                editor.apply();
            }
            OkHttpClient okHttpClient = new OkHttpClient();
            FormBody formBody = new FormBody.Builder()
                    .add("token", account.getIdToken())
                    .build();
            Request request = new Request.Builder().url(GlobalConstants.AUTH_TOKEN).post(formBody).build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if(response.isSuccessful()){
                        String resp = response.body().string();
                        String token = resp.substring(10, resp.length()-3);
                        // If the token is invalid, make the user log in again and get a new token
                        SharedPreferences sharedPreferences = getSharedPreferences(GlobalConstants.SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("TOKEN", token);
                        editor.apply();
                        GlobalConstants.TOKEN = getSharedPreferences(GlobalConstants.SHARED_PREFS, MODE_PRIVATE).getString("TOKEN", "");
                        Intent intent = new Intent(getApplicationContext(), SelectUsernameActivity.class);
                        startActivity(intent);
                    }
                }
            });
            // Signed in successfully, show authenticated UI.
            Toast.makeText(this, "Sign-In Successful", Toast.LENGTH_SHORT).show();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
        }
    }
}