package com.example.virtualdeck;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.virtualdeck.helpers.CardListRecyclerViewAdapter;
import com.example.virtualdeck.helpers.GlobalConstants;
import com.example.virtualdeck.helpers.SQLiteDatabaseHelper;
import com.example.virtualdeck.objects.Card;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewDeckCardsActivity extends AppCompatActivity {

    private ArrayList<Card> mCardList = new ArrayList<>();
    private ArrayList<String> mCardUUIDs = new ArrayList<>();
    private ArrayList<String> mCardNames = new ArrayList<>();
    private String deckUUID;
    private Button editDeckButton, deleteDeckButton;
    private TextView deckName;
    private CardListRecyclerViewAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_deck_cards);
        deckUUID = getIntent().getStringExtra("DeckUUID");
        //Toast.makeText(this, getIntent().getStringExtra("DeckUUID"), Toast.LENGTH_SHORT).show();

        deckName = findViewById(R.id.deck_name_text_view);
        editDeckButton = findViewById(R.id.edit_deck_button);
        deleteDeckButton = findViewById(R.id.delete_deck_button);

        deckName.setText(getIntent().getStringExtra("DeckName"));
        editDeckButton.setOnClickListener(this::editDeck);
        deleteDeckButton.setOnClickListener(this::deleteDeck);

        initImagesLists();
    }

    private void editDeck(View view) {
        Intent intent = new Intent(this, EditDeckCardsActivity.class);
        intent.putExtra("DeckUUID", deckUUID);
        intent.putExtra("DeckName", deckName.getText().toString());
        startActivity(intent);
    }

    private void deleteDeck(View view) {
        SQLiteDatabaseHelper db = new SQLiteDatabaseHelper(this);
        db.deleteDeck(deckUUID);

        try {
            OkHttpClient okHttpClient = new OkHttpClient();

            FormBody formBody = new FormBody.Builder()
                    .add("DeckUUID", deckUUID)
                    .add("UserUUID", GlobalConstants.USERUUID)
                    .add("token", GlobalConstants.TOKEN)
                    .build();

            Request request = new Request.Builder().url(GlobalConstants.DELETE_DECK_URL).post(formBody).build();
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
        } catch (Exception exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initImagesLists() {
        // TODO: Grab all of a USERUUID's cards from local db
        SQLiteDatabaseHelper dbHelper = new SQLiteDatabaseHelper(this);
        mCardList = dbHelper.getDeckCards(deckUUID);

        mCardList.forEach(card -> {
            mCardNames.add(card.getCardName());
            mCardUUIDs.add(card.getCardUUID());
        });

        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.card_list_recycler_view);
        adapter = new CardListRecyclerViewAdapter(this, mCardNames, mCardUUIDs, true, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        SQLiteDatabaseHelper db = new SQLiteDatabaseHelper(this);
        deckName.setText(db.getDeckName(deckUUID));
        mCardList = new ArrayList<>();
        mCardUUIDs = new ArrayList<>();
        mCardNames = new ArrayList<>();
        initImagesLists();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu);
        return true;
    }
}