package com.example.virtualdeck;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.example.virtualdeck.helpers.CardListRecyclerViewAdapter;
import com.example.virtualdeck.helpers.GlobalConstants;
import com.example.virtualdeck.objects.Card;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewUserCardsActivity extends AppCompatActivity {

    private String userUUID;
    private ArrayList<String> mCardNames = new ArrayList<>();
    private ArrayList<String> mCardUUIDs = new ArrayList<>();
    private TextView test_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_cards);

        userUUID = getIntent().getStringExtra("UserUUID");
        test_text = findViewById(R.id.textView);
        InitCardList();
    }


    private void InitCardList(){
        OkHttpClient okHttpClient = new OkHttpClient();

        HttpUrl.Builder httpBuilder = HttpUrl.parse(GlobalConstants.GET_ALL_USER_CARDS_URL).newBuilder();
        httpBuilder.addQueryParameter("UserUUID", userUUID);
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
                //ArrayList<Card> cards = g.fromJson(myResponse, ArrayList.class);

                ViewUserCardsActivity.this.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {

//                        cards.forEach((card) -> {
//                            mCardNames.add(card.getCardName());
//                            mCardUUIDs.add(card.getCardUUID());
//                        });

                        test_text.setText(myResponse);
                        //initRecyclerView();
                    }
                });
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.card_list_recycler_view);
        CardListRecyclerViewAdapter adapter = new CardListRecyclerViewAdapter(this, mCardNames, mCardUUIDs, true, true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}