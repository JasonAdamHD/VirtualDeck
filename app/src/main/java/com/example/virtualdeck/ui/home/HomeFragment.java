package com.example.virtualdeck.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualdeck.FindUsernameActivity;
import com.example.virtualdeck.FriendListActivity;
import com.example.virtualdeck.R;
import com.example.virtualdeck.databinding.FragmentHomeBinding;
import com.example.virtualdeck.helpers.DeckListRecyclerViewAdapter;
import com.example.virtualdeck.helpers.FriendListRecyclerViewAdapter;
import com.example.virtualdeck.helpers.GlobalConstants;
import com.example.virtualdeck.helpers.SQLiteDatabaseHelper;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private Button find_user_activity_button, view_followed_users_button;
    private ArrayList<String> mFriendUsernames = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        find_user_activity_button = binding.findUserActivityButton;
        find_user_activity_button.setOnClickListener(this::onClick);

        view_followed_users_button = binding.viewFollowedUsersButton;
        view_followed_users_button.setOnClickListener(this::viewFollowedUsers);

        return root;
    }

    private void viewFollowedUsers(View view) {
        Intent intent = new Intent(getContext(), FriendListActivity.class);
        startActivity(intent);
    }

    private void onClick(View view) {
        Intent intent = new Intent(getContext(), FindUsernameActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}