package com.example.virtualdeck.helpers;

import android.content.SharedPreferences;

import com.example.virtualdeck.objects.User;
import com.example.virtualdeck.objects.UserMetadata;
import com.google.gson.Gson;

import java.util.ArrayList;

public class GlobalConstants {
    // TODO: CREATE REAL URL LATER. PURCHASE DOMAIN OR SOMETHING
    //public static final String DB_SERVER = "http://192.168.1.9:5000";
    public static final String DB_SERVER = "http://96.41.172.252:5000";
    public static final String UPLOAD_CARD_URL = DB_SERVER + "/uploadCard";
    public static final String UPDATE_CARD_URL = DB_SERVER + "/updateCard";
    public static final String DELETE_CARD_URL = DB_SERVER + "/deleteCard";
    public static final String GET_CARD_URL = DB_SERVER + "/getCard";
    public static final String UPLOAD_DECK_URL = DB_SERVER + "/uploadDeck";
    public static final String UPDATE_DECK_URL = DB_SERVER + "/updateDeck";
    public static final String DELETE_DECK_URL = DB_SERVER + "/deleteDeck";
    public static final String GET_DECK_URL = DB_SERVER + "/getDeck";
    public static final String GET_USERNAME_URL = DB_SERVER + "/getUsername";
    public static final String GET_USERUUID_URL = DB_SERVER + "/getUserUUID";
    public static final String GET_USERNAME_FROM_UUID_URL = DB_SERVER + "/getUsernameFromUUID";
    public static final String GET_ALL_USER_CARDS_URL = DB_SERVER + "/getAllUserCards";
    public static final String GET_ALL_LOCATION_TRADES_URL = DB_SERVER + "/getTradeLocalTrades";
    public static final String CREATE_CARD_TRADE = DB_SERVER + "/createTrade";
    public static final String AUTH_TOKEN = DB_SERVER + "/authToken";
    public static final String CHECK_TOKEN = DB_SERVER + "/checkToken";
    public static ArrayList<String> mFriendUserUUIDs;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static String TOKEN = "";
    public static String USERUUID = "";

    
    //public static final User USER = new User(new UserMetadata("test-UUID-1"));
}
