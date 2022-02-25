package com.example.virtualdeck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.virtualdeck.helpers.GlobalConstants;
import com.example.virtualdeck.helpers.SQLiteDatabaseHelper;
import com.example.virtualdeck.objects.Card;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ViewCardActivity extends AppCompatActivity {

    private ImageView card_image_view;
    private TextView name, game, collection, desc, series, info;
    private Button edit_card_button, delete_card_button;
    String cardUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_card);

        card_image_view = findViewById(R.id.card_image_view);
        name = findViewById(R.id.card_name_text_view);
        game = findViewById(R.id.card_game_name_text_view);
        collection = findViewById(R.id.card_game_collection_text_view);
        desc = findViewById(R.id.card_description_text_view);
        series = findViewById(R.id.card_print_series_text_view);
        info = findViewById(R.id.card_extra_info_text_view);
        edit_card_button = findViewById(R.id.edit_card_button);
        delete_card_button = findViewById(R.id.delete_card_button);

        Card card = getIntent().getParcelableExtra("Card");
        cardUUID = card.getCardUUID();
        card.setCardMetadata(getIntent().getParcelableExtra("CardMetadata"));

        String filePath = getIntent().getStringExtra("CardBitmapPath");
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        card_image_view.setImageBitmap(bitmap);

        if (getIntent().getBooleanExtra("CanEdit", false)){
            edit_card_button.setOnClickListener(this::onClick);
            delete_card_button.setOnClickListener(this::onClickDeleteCard);

        }
        else{
            edit_card_button.setVisibility(View.GONE);
            delete_card_button.setVisibility(View.GONE);
        }


        // If there is no value for the given field then just hide it from the view.
        if(card.getCardName().compareTo("")  != 0)
            name.setText(card.getCardName());
        else
            name.setVisibility(View.GONE);
        if(card.getMetadata().getGame().compareTo("") != 0)
            game.setText(card.getMetadata().getGame());
        else
            game.setVisibility(View.GONE);
        if(card.getMetadata().getGameCollection().compareTo("")  != 0)
            collection.setText(card.getMetadata().getGameCollection());
        else
            collection.setVisibility(View.GONE);
        if(card.getMetadata().getCardDescription().compareTo("")  != 0)
            desc.setText(card.getMetadata().getCardDescription());
        else
            desc.setVisibility(View.GONE);
        if(card.getMetadata().getPrintSeries().compareTo("")  != 0)
            series.setText(card.getMetadata().getPrintSeries());
        else
            series.setVisibility(View.GONE);
        if(card.getMetadata().getExtraInfo().compareTo("")  != 0)
            info.setText(card.getMetadata().getExtraInfo());
        else
            info.setVisibility(View.GONE);

    }

    private void onClickDeleteCard(View view) {
        //TODO: MAKE POPUP FOR VERIFICATION TO DELETE

        // Delete Card Locally
        SQLiteDatabaseHelper db = new SQLiteDatabaseHelper(this);
        db.deleteCard(cardUUID);
        // Do PHP API call to delete card on the server
        try {
            OkHttpClient okHttpClient = new OkHttpClient();

            FormBody formBody = new FormBody.Builder()
                    .add("CardUUID", cardUUID)
                    .add("UserUUID", GlobalConstants.USERUUID)
                    .add("token", GlobalConstants.TOKEN)
                    .build();

            Request request = new Request.Builder().url(GlobalConstants.DELETE_CARD_URL).post(formBody).build();
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

    private void onClick(View view) {
        Intent intent = new Intent(this, EditCardActivity.class);
        String s_name = name.getText().toString().trim();
        String s_game = game.getText().toString().trim();
        String s_collection = collection.getText().toString().trim();
        String s_desc = desc.getText().toString().trim();
        String s_series = series.getText().toString().trim();
        String s_info = info.getText().toString().trim();
        intent.putExtra("name", s_name);
        intent.putExtra("game", s_game);
        intent.putExtra("collection", s_collection);
        intent.putExtra("desc", s_desc);
        intent.putExtra("series", s_series);
        intent.putExtra("info", s_info);
        card_image_view.buildDrawingCache();
        String filePath = tempFileImage(this, card_image_view.getDrawingCache(),"name");
        intent.putExtra("bitmap", filePath);
        intent.putExtra("uuid", cardUUID);
        startActivity(intent);
    }

    public static String tempFileImage(Context context, Bitmap bitmap, String name) {

        File outputDir = context.getCacheDir();
        File imageFile = new File(outputDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Error writing file", e);
        }

        return imageFile.getAbsolutePath();
    }

    @Override
    protected void onResume() {
        super.onResume();

        name.setVisibility(View.VISIBLE);
        game.setVisibility(View.VISIBLE);
        collection.setVisibility(View.VISIBLE);
        desc.setVisibility(View.VISIBLE);
        series.setVisibility(View.VISIBLE);
        info.setVisibility(View.VISIBLE);

        SQLiteDatabaseHelper db = new SQLiteDatabaseHelper(this);
        Card card = db.getCardByCardUUID(cardUUID);

        // TODO: THIS CAN BE MADE MORE EFFICIENT IF YOU CHECK IF THE BITMAP CHANGES
        //  PROBLEM FOR FUTURE ME!!!
        Bitmap bitmap = null;
        try {
            bitmap = db.getCardBitmap(cardUUID);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        card_image_view.setImageBitmap(bitmap);

        if(card.getCardName().compareTo("")  != 0)
            name.setText(card.getCardName());
        else
            name.setVisibility(View.GONE);
        if(card.getMetadata().getGame().compareTo("") != 0)
            game.setText(card.getMetadata().getGame());
        else
            game.setVisibility(View.GONE);
        if(card.getMetadata().getGameCollection().compareTo("")  != 0)
            collection.setText(card.getMetadata().getGameCollection());
        else
            collection.setVisibility(View.GONE);
        if(card.getMetadata().getCardDescription().compareTo("")  != 0)
            desc.setText(card.getMetadata().getCardDescription());
        else
            desc.setVisibility(View.GONE);
        if(card.getMetadata().getPrintSeries().compareTo("")  != 0)
            series.setText(card.getMetadata().getPrintSeries());
        else
            series.setVisibility(View.GONE);
        if(card.getMetadata().getExtraInfo().compareTo("")  != 0)
            info.setText(card.getMetadata().getExtraInfo());
        else
            info.setVisibility(View.GONE);
    }
}