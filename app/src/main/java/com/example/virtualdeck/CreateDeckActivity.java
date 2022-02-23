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
import android.widget.Toast;

import com.example.virtualdeck.helpers.CardListRecyclerViewAdapter;
import com.example.virtualdeck.helpers.DeckCardListRecyclerViewAdapter;
import com.example.virtualdeck.helpers.GlobalConstants;
import com.example.virtualdeck.helpers.SQLiteDatabaseHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

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

        if(localDatabase.insertDeck(GlobalConstants.USER.getUserUUID(), DECK_UUID, json, deckName.getText().toString().trim())) {
            try {
                // TODO: SAVE THE DECK TO THE ONLINE DB
            }
            catch (Exception exception) {
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
        dbHelper.getAllSQLiteCards(GlobalConstants.USER.getUserUUID(), mCardUUIDs, mCardNames);

        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.create_deck_card_list_recycler_view);
        DeckCardListRecyclerViewAdapter adapter = new DeckCardListRecyclerViewAdapter(this, mCardNames, mCardUUIDs, mSelectedMap, true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}