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

import com.example.virtualdeck.helpers.DeckCardListRecyclerViewAdapter;
import com.example.virtualdeck.helpers.GlobalConstants;
import com.example.virtualdeck.helpers.SQLiteDatabaseHelper;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditDeckCardsActivity extends AppCompatActivity {

    private ArrayList<String> mCardUUIDs = new ArrayList<>();
    private ArrayList<String> mCardNames = new ArrayList<>();
    private HashMap<String, Boolean> mSelectedMap = new HashMap<>();
    private EditText deckName;
    private Button saveDeckButton;
    private String deckUUID;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deck_cards);
        deckName = findViewById(R.id.edit_deck_name_edit_text);
        saveDeckButton = findViewById(R.id.save_edited_deck_button);
        deckName.setText(getIntent().getStringExtra("DeckName"));
        deckUUID = getIntent().getStringExtra("DeckUUID");
        saveDeckButton.setOnClickListener(this::onClick);


        initImagesLists();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onClick(View view) {
        ArrayList<String> cardUUIDArrayList = new ArrayList<>();
        mSelectedMap.forEach((key, value) -> {
            cardUUIDArrayList.add(key);
        });
        String json = new Gson().toJson(cardUUIDArrayList);
        SQLiteDatabaseHelper db = new SQLiteDatabaseHelper(this);
        db.updateDeck(json, deckName.getText().toString(), deckUUID);


        try {
            OkHttpClient okHttpClient = new OkHttpClient();

            FormBody formBody = new FormBody.Builder()
                    .add("DeckUUID", deckUUID)
                    .add("UserUUID", GlobalConstants.USERUUID)
                    .add("CardUUIDListJSON", json)
                    .add("DeckName", deckName.getText().toString().trim())
                    .add("token", GlobalConstants.TOKEN)
                    .build();

            Request request = new Request.Builder().url(GlobalConstants.UPDATE_DECK_URL).post(formBody).build();
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

        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initImagesLists() {
        SQLiteDatabaseHelper dbHelper = new SQLiteDatabaseHelper(this);
        dbHelper.getAllSQLiteCards(GlobalConstants.USERUUID, mCardUUIDs, mCardNames);

        initRecyclerView();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.edit_deck_card_list_recycler_view);
        SQLiteDatabaseHelper db = new SQLiteDatabaseHelper(this);
        ArrayList<String> cardUUIDS = db.getDeckCardsUUIDs(getIntent().getStringExtra("DeckUUID"));
        cardUUIDS.forEach(cardUUID -> {
           mSelectedMap.put(cardUUID, true);
        });


        DeckCardListRecyclerViewAdapter adapter = new DeckCardListRecyclerViewAdapter(this, mCardNames, mCardUUIDs, mSelectedMap, true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}