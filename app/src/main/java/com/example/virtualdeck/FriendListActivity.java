package com.example.virtualdeck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.virtualdeck.helpers.FriendListRecyclerViewAdapter;
import com.example.virtualdeck.helpers.GlobalConstants;

import java.util.ArrayList;

public class FriendListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.friend_list_recycler_view);
        FriendListRecyclerViewAdapter adapter = new FriendListRecyclerViewAdapter(this, GlobalConstants.mFriendUserUUIDs);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}