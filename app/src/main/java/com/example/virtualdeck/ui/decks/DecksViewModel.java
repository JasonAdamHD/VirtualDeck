package com.example.virtualdeck.ui.decks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DecksViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DecksViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is decks fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}