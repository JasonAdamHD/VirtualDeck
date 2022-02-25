package com.example.virtualdeck;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.virtualdeck.helpers.GlobalConstants;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FindUsernameActivity extends AppCompatActivity {

    private TextView usernameTextView, displayNameTextView;
    private EditText usernameSearchEditText;
    private Button searchButton, followButton;
    private UserProfile userProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_username);
        usernameSearchEditText = findViewById(R.id.username_search_edit_text);
        usernameTextView = findViewById(R.id.username_text_view);
        displayNameTextView = findViewById(R.id.display_name_text_view);
        searchButton = findViewById(R.id.search_username_button);
        searchButton.setOnClickListener(this::onClick);
        followButton = findViewById(R.id.follow_user_button);
        followButton.setOnClickListener(this::followUser);
    }

    private void followUser(View view) {
        // FIXME: Replace the global constant with the db version once the backend is rewritten!!
        if (userProfile != null && !GlobalConstants.mFriendUserUUIDs.contains(userProfile.UserUUID))
            GlobalConstants.mFriendUserUUIDs.add(userProfile.UserUUID);
    }

    private void onClick(View view) {
        // FIXME: Figure out why this isn't 100% reliable and fix it
        // https://stackoverflow.com/a/60589306
        // Might be an emulator problem. Try on hardware soon
        OkHttpClient okHttpClient = new OkHttpClient();

        HttpUrl.Builder httpBuilder = HttpUrl.parse(GlobalConstants.GET_USERNAME_URL).newBuilder();
        httpBuilder.addQueryParameter("Username", usernameSearchEditText.getText().toString().trim());
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
                userProfile = g.fromJson(myResponse, UserProfile.class);

                FindUsernameActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        usernameTextView.setText(userProfile.Username);
                        displayNameTextView.setText(userProfile.DisplayName);
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