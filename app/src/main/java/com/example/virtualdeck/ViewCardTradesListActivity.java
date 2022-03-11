package com.example.virtualdeck;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
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
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewCardTradesListActivity extends AppCompatActivity {

    private EditText searchText;
    private TextView testing_text_view;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_card_trades_list);
        searchButton = findViewById(R.id.search_zip_code_button);
        searchButton.setOnClickListener(this::onClick);
        searchText = findViewById(R.id.edit_text_search_zipcode);
        testing_text_view = findViewById(R.id.testing_text_view);
    }

    private void onClick(View view) {
        OkHttpClient okHttpClient = new OkHttpClient();

        HttpUrl.Builder httpBuilder = HttpUrl.parse(GlobalConstants.GET_ALL_LOCATION_TRADES_URL).newBuilder();
        httpBuilder.addQueryParameter("ZIPCode", searchText.getText().toString().trim());
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

                ViewCardTradesListActivity.this.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {

                        testing_text_view.setText(myResponse);
                        //initRecyclerView();
                    }
                });
            }
        });
    }
}