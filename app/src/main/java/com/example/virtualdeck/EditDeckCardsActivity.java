package com.example.virtualdeck;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.virtualdeck.helpers.DeckCardListRecyclerViewAdapter;
import com.example.virtualdeck.helpers.GlobalConstants;
import com.example.virtualdeck.helpers.SQLiteDatabaseHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

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
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initImagesLists() {
        SQLiteDatabaseHelper dbHelper = new SQLiteDatabaseHelper(this);
        dbHelper.getAllSQLiteCards(GlobalConstants.USER.getUserUUID(), mCardUUIDs, mCardNames);

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