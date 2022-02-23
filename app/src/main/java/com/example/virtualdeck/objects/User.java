package com.example.virtualdeck.objects;

public class User {
    private UserMetadata mUserMetadata = null;

    public User(UserMetadata m_UserMetadata) {
        this.mUserMetadata = m_UserMetadata;
    }

    public void setUserMetadata(UserMetadata m_UserMetadata) {
        // Make sure userMetadata can only be set once
        if(this.mUserMetadata == null) {
            this.mUserMetadata = m_UserMetadata;
        }
    }

    public String getUserUUID() {
        return mUserMetadata.getUserUUID();
    }
}
