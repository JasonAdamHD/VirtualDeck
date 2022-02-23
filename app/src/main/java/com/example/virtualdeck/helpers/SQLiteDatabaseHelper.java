package com.example.virtualdeck.helpers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.virtualdeck.objects.Card;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "parity.db";
    private static final String CARD_TABLE = "card_table";
    private static final String DECK_TABLE = "deck_table";
    private static final String CARD_TABLE_COL1 = "USERUUID";
    private static final String CARD_TABLE_COL2 = "CARDUUID";
    private static final String CARD_TABLE_COL3 = "CARDJSON";
    private static final String CARD_TABLE_COL4 = "CARDNAME";
    private static final String CARD_TABLE_COL5 = "CARDBITMAPBLOB";
    private static final String DECK_TABLE_COL1 = "USERUUID";
    private static final String DECK_TABLE_COL2 = "DECKUUID";
    private static final String DECK_TABLE_COL3 = "CARDUUIDLISTJSON";
    private static final String DECK_TABLE_COL4 = "DECKNAME";


    public SQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 7);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    public boolean insertCard(String userUUID, String cardUUID, String cardJSON, String cardName, byte[] cardImageBLOB){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CARD_TABLE_COL1, userUUID);
        contentValues.put(CARD_TABLE_COL2, cardUUID);
        contentValues.put(CARD_TABLE_COL3, cardJSON);
        contentValues.put(CARD_TABLE_COL4, cardName);
        contentValues.put(CARD_TABLE_COL5, cardImageBLOB);
        long result = db.insert(CARD_TABLE, null, contentValues);
        return result >= 0;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + CARD_TABLE +
                " (USERUUID TEXT, CARDUUID TEXT PRIMARY KEY, CARDJSON TEXT, CARDNAME TEXT, CARDBITMAPBLOB BLOB)");
        sqLiteDatabase.execSQL("create table " + DECK_TABLE +
                " (USERUUID TEXT, DECKUUID TEXT PRIMARY KEY, CARDUUIDLISTJSON TEXT, DECKNAME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CARD_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void getAllSQLiteCards(String UserUUID, ArrayList<String> cardUUIDs, ArrayList<String> cardNames){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + CARD_TABLE + " WHERE USERUUID = ?;", new String[] { UserUUID });
        try {
            cursor.moveToFirst();
            if (cursor.getCount() == 0)
                return;
            do{
                String cardName = cursor.getString(3);
                String cardUUID = cursor.getString(1);
                cardUUIDs.add(cardUUID);
                cardNames.add(cardName);
            } while (cursor.moveToNext());
        } finally {
            cursor.close();
        }
    }

    public Bitmap getCardBitmap(String cardUUID) throws Exception{
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + CARD_TABLE + " WHERE CARDUUID = ?;", new String[] { cardUUID });
        cursor.moveToFirst();
        byte[] bitmapBlob = cursor.getBlob(4);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBlob, 0, bitmapBlob.length);
        return bitmap;
    }

    public Card getCardByCardUUID(String cardUUID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + CARD_TABLE + " WHERE CARDUUID = ?;", new String[] { cardUUID });
        cursor.moveToFirst();
        String cardJson = cursor.getString(2);
        Card card = new Gson().fromJson(cardJson, Card.class);

        return card;
    }

    public void updateCard(Card card, String cardName, Bitmap cardBitmap) {

        //Update Local DB
        ContentValues values = new ContentValues();
        String cardJSON = new Gson().toJson(card);
        values.put(CARD_TABLE_COL3, cardJSON);
        values.put(CARD_TABLE_COL4, cardName);
        ByteArrayOutputStream stream = null;
        stream = new ByteArrayOutputStream();
        cardBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        values.put(CARD_TABLE_COL5, stream.toByteArray());

        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "CARDUUID = ?";
        String[] whereArgs = { card.getCardUUID() };
        int result = db.update(CARD_TABLE, values, whereClause, whereArgs);
    }

    public void deleteCard(String cardUUID) {
        String whereClause = "CARDUUID = ?";
        String[] whereArgs = { cardUUID };
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CARD_TABLE, whereClause, whereArgs);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Card> getDeckCards(String DeckUUID){
        ArrayList<Card> deck = new ArrayList<>();
        ArrayList<String> cardUUIDs;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DECK_TABLE + " WHERE DECKUUID = ?;", new String[] { DeckUUID });
        cursor.moveToFirst();
        String cardListJSON = cursor.getString(2);
        cardUUIDs = new Gson().fromJson(cardListJSON, ArrayList.class);
        
        cardUUIDs.forEach(cardUUID -> {
            deck.add(getCardByCardUUID(cardUUID));
        });
        
        return deck;
    }

    public ArrayList<String> getDeckCardsUUIDs(String DeckUUID) {
        ArrayList<String> cardUUIDs;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DECK_TABLE + " WHERE DECKUUID = ?;", new String[] { DeckUUID });
        cursor.moveToFirst();
        String cardListJSON = cursor.getString(2);
        cardUUIDs = new Gson().fromJson(cardListJSON, ArrayList.class);

        return cardUUIDs;
    }

    public boolean insertDeck(String userUUID, String deckUUID, String cardUUIDArrayListJSON, String deckName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DECK_TABLE_COL1, userUUID);
        contentValues.put(DECK_TABLE_COL2, deckUUID);
        contentValues.put(DECK_TABLE_COL3, cardUUIDArrayListJSON);
        contentValues.put(DECK_TABLE_COL4, deckName);
        long result = db.insert(DECK_TABLE, null, contentValues);
        return result >= 0;
    }

    public void getAllSQLiteDecks(String UserUUID, ArrayList<String> DeckNames, ArrayList<String> DeckUUID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DECK_TABLE + " WHERE USERUUID = ?;", new String[] { UserUUID });
        try {
            cursor.moveToFirst();
            if (cursor.getCount() == 0)
                return;
            do{
                String deckUUID = cursor.getString(1);
                String deckName = cursor.getString(3);
                DeckUUID.add(deckUUID);
                DeckNames.add(deckName);
            } while (cursor.moveToNext());
        } finally {
            cursor.close();
        }
    }

    public void deleteDeck(String deckUUID) {
        String whereClause = "DECKUUID = ?";
        String[] whereArgs = { deckUUID };
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DECK_TABLE, whereClause, whereArgs);
    }

    public void updateDeck(String cardUUIDArrayListJSON, String deckName, String deckUUID) {

        //Update Local DB
        ContentValues values = new ContentValues();
        values.put(DECK_TABLE_COL3, cardUUIDArrayListJSON);
        values.put(DECK_TABLE_COL4, deckName);

        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "DECKUUID = ?";
        String[] whereArgs = { deckUUID };
        int result = db.update(DECK_TABLE, values, whereClause, whereArgs);
    }

    public String getDeckName(String deckUUID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DECK_TABLE + " WHERE DECKUUID = ?;", new String[] { deckUUID });
        cursor.moveToFirst();
        String deckName = cursor.getString(3);
        return deckName;
    }
}
