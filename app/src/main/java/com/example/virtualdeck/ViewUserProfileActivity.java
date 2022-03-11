package com.example.virtualdeck;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.virtualdeck.helpers.GlobalConstants;
import com.example.virtualdeck.objects.Card;
import com.example.virtualdeck.ui.profile.ProfileFragment;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewUserProfileActivity extends AppCompatActivity {

    private TextView profileUsername;
    private TextView profileRealName;
    private TextView profileBio;
    private CircleImageView profileImage;
    private Button view_users_cards, unfollow_user_button;
    private String userUUID;
    private ViewUserProfileActivity.UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);
        profileUsername = findViewById(R.id.profile_username);
        profileRealName = findViewById(R.id.profile_real_name);
        profileBio = findViewById(R.id.profile_bio);
        profileImage = findViewById(R.id.profile_image);
        view_users_cards = findViewById(R.id.view_users_cards);
        unfollow_user_button = findViewById(R.id.unfollow_user_button);

        userUUID = getIntent().getStringExtra("UserUUID");
        getProfile(userUUID);

        //TODO: THIS IS WRONG CHANGE THIS SO THAT THE ONCLICK POPULATES A LIST OF CARDS!!
        view_users_cards.setOnClickListener(this::onClick);
        unfollow_user_button.setOnClickListener(this::unfollowUser);
    }

    private void unfollowUser(View view) {
        GlobalConstants.mFriendUserUUIDs.remove(userUUID);
        String json = new Gson().toJson(GlobalConstants.mFriendUserUUIDs);
        SharedPreferences sharedPreferences = getSharedPreferences(GlobalConstants.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("FRIENDSLISTJSON", json);
        editor.apply();

        finish();
    }

    private void onClick(View view) {
        Intent intent = new Intent(this, ViewUserCardsActivity.class);
        intent.putExtra("UserUUID", userUUID);
        startActivity(intent);
    }


    private void getProfile(String UserUUID)
    {
        OkHttpClient okHttpClient = new OkHttpClient();

        HttpUrl.Builder httpBuilder = HttpUrl.parse(GlobalConstants.GET_USERUUID_URL).newBuilder();
        httpBuilder.addQueryParameter("UserUUID", UserUUID);
        Request request = new Request.Builder().url(httpBuilder.build()).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();

                Gson g = new Gson();
                userProfile = g.fromJson(myResponse, ViewUserProfileActivity.UserProfile.class);

                ViewUserProfileActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        profileUsername.setText("@" + userProfile.Username);
                        profileRealName.setText(userProfile.DisplayName);
                        Glide.with(getApplicationContext()).load(userProfile.PhotoURL).into(profileImage);
                    }
                });
            }
        });
    }

    private class UserProfile {
        String DisplayName;
        String PhotoURL;
        String UserUUID;
        String Username;
    }
}