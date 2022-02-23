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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import com.example.virtualdeck.helpers.GlobalConstants;
import com.example.virtualdeck.helpers.SQLiteDatabaseHelper;
import com.example.virtualdeck.objects.Card;
import com.google.gson.Gson;

import java.io.IOException;

public class CreateCardActivity extends AppCompatActivity {

    private ImageView card_image_view;
    private EditText name, game, collection, desc, series, info;
    private Uri filePath;
    private SQLiteDatabaseHelper localDatabase;
    // TODO: REMOVE THESE TWO
    private final String USER_UUID = GlobalConstants.USER.getUserUUID();

    private final String CARD_UUID = UUID.randomUUID().toString();
    private static final int STORAGE_PERMISSION_CODE = 100;
    //private static final int REQUEST_CODE_IMAGE_PICKER = 101;
    private static final int PICK_IMAGE_REQUEST = 102;


    // TODO: NOT ALLOW CARD TO BE UPLOADED UNTIL CARD IMAGE HAS BEEN CHOSEN!!!
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card);

        requestStoragePermission();

        Button create_card_button = findViewById(R.id.create_card_button);
        card_image_view = findViewById(R.id.card_image_view);
        name = findViewById(R.id.card_name_edit_text);
        game = findViewById(R.id.card_game_name_edit_text);
        collection = findViewById(R.id.card_game_collection_edit_text);
        desc = findViewById(R.id.card_description_edit_text);
        series = findViewById(R.id.card_print_series_edit_text);
        info = findViewById(R.id.card_extra_info_edit_text);

        card_image_view.setOnClickListener(this::imageOnClick);

        create_card_button.setOnClickListener(this::onClick);
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

    private void uploadImage(){
        if(filePath == null)
        {
            Toast.makeText(this, "Please select and image for the card.", Toast.LENGTH_LONG);
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

        if (localDatabase.insertCard(USER_UUID, CARD_UUID, json, card.getCardName(), stream.toByteArray())) {
            // Upload to server db
            try {
                new MultipartUploadRequest(this, CARD_UUID, GlobalConstants.UPLOAD_URL)
                        .addFileToUpload(path, "image")
                        .addParameter("UserUUID", USER_UUID)
                        .addParameter("CardUUID", CARD_UUID)
                        .addParameter("CardJSON", json)
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(1)
                        .startUpload(); // Starting the upload

            } catch (Exception exception) {
                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Failed to insert into card_table", Toast.LENGTH_LONG).show();
        }
    }

    private void imageOnClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void onClick(View view) {
        //Toast.makeText(this, "Card is being added...", Toast.LENGTH_SHORT).show();
        uploadImage();
        // Return to the main activity
        finish();
    }
}