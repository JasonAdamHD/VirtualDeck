package com.example.virtualdeck.objects;

// TODO: PUT THIS CLASS INTO THE USER CLASS
public class UserMetadata {
    private String mUserUUID = null;

    public UserMetadata(String m_UserUUID) {
        this.mUserUUID = m_UserUUID;
    }

    public void setUserUUID(String m_UserUUID){
        // Make sure user_uuid can only be set once.
        if(this.mUserUUID == null) {
            this.mUserUUID = m_UserUUID;
        }
    }

    protected String getUserUUID() {
        return mUserUUID;
    }
}
