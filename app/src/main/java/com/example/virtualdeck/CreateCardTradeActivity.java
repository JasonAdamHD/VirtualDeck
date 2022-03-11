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

import com.example.virtualdeck.helpers.CardTradeCardListRecyclerViewAdapter;
import com.example.virtualdeck.helpers.GlobalConstants;
import com.example.virtualdeck.helpers.SQLiteDatabaseHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateCardTradeActivity extends AppCompatActivity {

    private ArrayList<String> mCardUUIDs = new ArrayList<>();
    private ArrayList<String> mCardNames = new ArrayList<>();
    
    private Button create_trade_button;
    private EditText create_trade_zip_code, create_trade_contact_info;
    private HashMap<String, Boolean> mSelectedMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card_trade);
        create_trade_zip_code = findViewById(R.id.create_trade_zip_code);
        create_trade_contact_info = findViewById(R.id.create_trade_contact_info);
        create_trade_button = findViewById(R.id.create_trade_button);
        create_trade_button.setOnClickListener(this::uploadCardTrade);
        initImagesLists();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void uploadCardTrade(View view) {
        AtomicReference<String> cardUUID = new AtomicReference<>();
        mSelectedMap.forEach((key, value) -> {
            cardUUID.set(key);
        });
        
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("UserUUID", GlobalConstants.USERUUID)
                .addFormDataPart("CardUUID", cardUUID.get())
                .addFormDataPart("ZIPCode", create_trade_zip_code.getText().toString().trim())
                .addFormDataPart("ContactInfo", create_trade_contact_info.getText().toString().trim())
                .addFormDataPart("token", GlobalConstants.TOKEN)
                .build();

        Request request = new Request.Builder().url(GlobalConstants.CREATE_CARD_TRADE).post(formBody).build();
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
        finish();
    }

    private void initImagesLists() {
        // TODO: Grab all of a USERUUID's cards from local db
        SQLiteDatabaseHelper dbHelper = new SQLiteDatabaseHelper(this);
        dbHelper.getAllSQLiteCards(GlobalConstants.USERUUID, mCardUUIDs, mCardNames);

        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.create_trade_recycler_view);
        CardTradeCardListRecyclerViewAdapter adapter = new CardTradeCardListRecyclerViewAdapter(this, mCardNames, mCardUUIDs, true, mSelectedMap);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}