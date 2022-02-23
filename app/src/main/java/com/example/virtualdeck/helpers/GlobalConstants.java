package com.example.virtualdeck.helpers;

import com.example.virtualdeck.objects.User;
import com.example.virtualdeck.objects.UserMetadata;

public class GlobalConstants {
    // TODO: CREATE REAL URL LATER. PURCHASE DOMAIN OR SOMETHING
    public static final String UPLOAD_URL = "http://192.168.1.9/virtual_deck/Api.php?apicall=upload";
    public static final User USER = new User(new UserMetadata("test-UUID-1"));
}
