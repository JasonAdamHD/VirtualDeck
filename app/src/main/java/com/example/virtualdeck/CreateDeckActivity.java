package com.example.virtualdeck;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.virtualdeck.helpers.CardListRecyclerViewAdapter;
import com.example.virtualdeck.helpers.DeckCardListRecyclerViewAdapter;
import com.example.virtualdeck.helpers.GlobalConstants;
import com.example.virtualdeck.helpers.SQLiteDatabaseHelper;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

public class CreateDeckActivity extends AppCompatActivity {

    private final String DECK_UUID = UUID.randomUUID().toString();
    private ArrayList<String> mCardUUIDs = new ArrayList<>();
    private ArrayList<String> mCardNames = new ArrayList<>();
    private HashMap<String, Boolean> mSelectedMap = new HashMap<>();

    private EditText deckName;
    private Button createDeckButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deck);
        createDeckButton = findViewById(R.id.create_deck_button);
        createDeckButton.setOnClickListener(this::OnClick);
        deckName = findViewById(R.id.deck_name_edit_text);

        initImagesLists();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void OnClick(View view) {
        // TODO: SAVE THE DECK TO LOCAL AND ONLINE DB
        SQLiteDatabaseHelper localDatabase = new SQLiteDatabaseHelper(this);
        ArrayList<String> cardUUIDArrayList = new ArrayList<>();
        mSelectedMap.forEach((key, value) -> {
            cardUUIDArrayList.add(key);
        });
        String json = new Gson().toJson(cardUUIDArrayList);

        if(localDatabase.insertDeck(GlobalConstants.USERUUID, DECK_UUID, json, deckName.getText().toString().trim())) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();

                FormBody formBody = new FormBody.Builder()
                        .add("DeckUUID", DECK_UUID)
                        .add("UserUUID", GlobalConstants.USERUUID)
                        .add("CardUUIDListJSON", json)
                        .add("DeckName", deckName.getText().toString().trim())
                        .add("token", GlobalConstants.TOKEN)
                        .build();

                Request request = new Request.Builder().url(GlobalConstants.UPLOAD_DECK_URL).post(formBody).build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if(response.isSuccessful()){
                        }
                    }
                });
                Toast.makeText(this, "Uploading Card", Toast.LENGTH_SHORT).show();

            } catch (Exception exception) {
                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Error creating deck!", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void initImagesLists() {
        SQLiteDatabaseHelper dbHelper = new SQLiteDatabaseHelper(this);
        dbHelper.getAllSQLiteCards(GlobalConstants.USERUUID, mCardUUIDs, mCardNames);

        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.create_deck_card_list_recycler_view);
        DeckCardListRecyclerViewAdapter adapter = new DeckCardListRecyclerViewAdapter(this, mCardNames, mCardUUIDs, mSelectedMap, true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}