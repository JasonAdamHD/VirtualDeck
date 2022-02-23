package com.example.virtualdeck.ui.decks;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualdeck.CreateCardActivity;
import com.example.virtualdeck.CreateDeckActivity;
import com.example.virtualdeck.R;
import com.example.virtualdeck.databinding.FragmentDecksBinding;
import com.example.virtualdeck.helpers.CardListRecyclerViewAdapter;
import com.example.virtualdeck.helpers.DeckListRecyclerViewAdapter;
import com.example.virtualdeck.helpers.GlobalConstants;
import com.example.virtualdeck.helpers.SQLiteDatabaseHelper;

import java.util.ArrayList;

public class DecksFragment extends Fragment {

    private DecksViewModel decksViewModel;
    private FragmentDecksBinding binding;

    private ArrayList<String> mDeckUUIDs = new ArrayList<>();
    private ArrayList<String> mDeckNames = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        decksViewModel =
                new ViewModelProvider(this).get(DecksViewModel.class);

        binding = FragmentDecksBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // TODO: CHANGE THIS TO BE CREATE DECK PAGE
        binding.createDeckPageButton.setOnClickListener(this::onClick);

        initRecyclerView();

        return root;
    }

    private void onClick(View view) {
        //Toast.makeText(getContext(), "Create Deck Page", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), CreateDeckActivity.class);
        startActivity(intent);
    }

    private void initRecyclerView() {
        SQLiteDatabaseHelper dbHelper = new SQLiteDatabaseHelper(getContext());
        dbHelper.getAllSQLiteDecks(GlobalConstants.USER.getUserUUID(), mDeckNames, mDeckUUIDs);

        RecyclerView recyclerView = binding.getRoot().findViewById(R.id.deck_list_recycler_view);
        DeckListRecyclerViewAdapter adapter = new DeckListRecyclerViewAdapter(getContext(), mDeckNames, mDeckUUIDs, true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onResume() {
        super.onResume();
        mDeckUUIDs = new ArrayList<>();
        mDeckNames = new ArrayList<>();
        initRecyclerView();
    }
}