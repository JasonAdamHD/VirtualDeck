package com.example.virtualdeck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.virtualdeck.helpers.GlobalConstants;
import com.example.virtualdeck.helpers.SQLiteDatabaseHelper;
import com.example.virtualdeck.objects.Card;
import com.google.gson.Gson;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditCardActivity extends AppCompatActivity {

    private ImageView card_image_view;
    private TextView name, game, collection, desc, series, info;
    private Button updateButton;
    private Uri filePath;
    private SQLiteDatabaseHelper localDatabase;
    private String CARD_UUID;
    private static final int STORAGE_PERMISSION_CODE = 100;
    //private static final int REQUEST_CODE_IMAGE_PICKER = 101;
    private static final int PICK_IMAGE_REQUEST = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);

        requestStoragePermission();

        card_image_view = findViewById(R.id.card_image_view);
        name = findViewById(R.id.card_name_edit_text);
        game = findViewById(R.id.card_game_name_edit_text);
        collection = findViewById(R.id.card_game_collection_edit_text);
        desc = findViewById(R.id.card_description_edit_text);
        series = findViewById(R.id.card_print_series_edit_text);
        info = findViewById(R.id.card_extra_info_edit_text);
        updateButton = findViewById(R.id.save_edited_card_button);

        String filePath = getIntent().getStringExtra("bitmap");
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        CARD_UUID = getIntent().getStringExtra("uuid");

        card_image_view.setImageBitmap(bitmap);
        getIntent().getStringExtra("name");
        name.setText(getIntent().getStringExtra("name"));
        game.setText(getIntent().getStringExtra("game"));
        collection.setText(getIntent().getStringExtra("collection"));
        desc.setText(getIntent().getStringExtra("desc"));
        series.setText(getIntent().getStringExtra("series"));
        info.setText(getIntent().getStringExtra("info"));

        card_image_view.setOnClickListener(this::imageOnClick);
        updateButton.setOnClickListener(this::onClick);

    }

    private void onClick(View view) {
        uploadCard();
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            try {
                Bitmap card_image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                card_image_view.setImageBitmap(card_image_bitmap);
            }
            catch (IOException exception)
            {
                Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getPath(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);

        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        // Suppress the range warning because we know for a fac that the Image will be there.
        @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private void uploadCard(){
        // TODO: Workaround that needs to be fixed. Need a way to not have to select the same card to save it.
        if(filePath == null)
        {
            Toast.makeText(this, "Please select and image for the card.", Toast.LENGTH_LONG).show();
            return;
        }

        String s_name = name.getText().toString().trim();
        String s_game = game.getText().toString().trim();
        String s_collection = collection.getText().toString().trim();
        String s_desc = desc.getText().toString().trim();
        String s_series = series.getText().toString().trim();
        String s_info = info.getText().toString().trim();

        Card card = new Card(new Card.CardMetadata(s_game, s_collection, s_desc, s_series, s_info), s_name, null, CARD_UUID);

        String json = new Gson().toJson(card);

        String path = getPath(filePath);
        String extension = path.substring(path.lastIndexOf("."));

        // Store to local db
        localDatabase = new SQLiteDatabaseHelper(this);
        ByteArrayOutputStream stream = null;
        try {
            Bitmap card_image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            stream = new ByteArrayOutputStream();
            card_image_bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);

        }
        catch (IOException exception)
        {
            Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show();
        }

        card_image_view.buildDrawingCache();
        if (localDatabase.updateCard(json, card.getCardUUID(), card.getCardName(), card_image_view.getDrawingCache())) {
            // Upload to server db
            try {
                OkHttpClient okHttpClient = new OkHttpClient();

                RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("image", CARD_UUID, RequestBody.create(new File(path), MediaType.parse("image")))
                        .addFormDataPart("extension", extension)
                        .addFormDataPart("UserUUID", GlobalConstants.USERUUID)
                        .addFormDataPart("CardUUID", CARD_UUID)
                        .addFormDataPart("CardJSON", json)
                        .addFormDataPart("token", GlobalConstants.TOKEN)
                        .build();

                Request request = new Request.Builder().url(GlobalConstants.UPDATE_CARD_URL).post(formBody).build();
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
        } else{
            Toast.makeText(this, "Failed to insert into card_table", Toast.LENGTH_LONG).show();
        }
    }

    private void imageOnClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void requestStoragePermission() {
        if((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) == PackageManager.PERMISSION_GRANTED)
            return;

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show();
            }
        }
    }
}