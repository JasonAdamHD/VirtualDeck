package com.example.virtualdeck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.virtualdeck.helpers.GlobalConstants;
import com.example.virtualdeck.helpers.SQLiteDatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.virtualdeck.databinding.ActivityMainBinding;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SQLiteDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = new SQLiteDatabaseHelper(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_profile, R.id.navigation_decks, R.id.navigation_cards)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        GlobalConstants.TOKEN = getSharedPreferences(GlobalConstants.SHARED_PREFS, MODE_PRIVATE).getString("TOKEN", "");
        GlobalConstants.USERUUID = getSharedPreferences(GlobalConstants.SHARED_PREFS, MODE_PRIVATE).getString("USERUUID", "");

        String tempListStr = getSharedPreferences(GlobalConstants.SHARED_PREFS, MODE_PRIVATE).getString("FRIENDSLISTJSON", "");
        Gson g = new Gson();
        ArrayList<String> result = g.fromJson(tempListStr, ArrayList.class);
        if (result == null) {
            result = new ArrayList<>();
        }
        GlobalConstants.mFriendUserUUIDs = result;

        Toast.makeText(this, GlobalConstants.TOKEN, Toast.LENGTH_SHORT).show();

        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("token", GlobalConstants.TOKEN)
                .build();
        Request request = new Request.Builder().url(GlobalConstants.CHECK_TOKEN).post(formBody).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                }
                // If the token is invalid, make the user log in again and get a new token
                else if(response.code() == 406){
                    Intent intent = new Intent(getBaseContext(), GoogleLoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}