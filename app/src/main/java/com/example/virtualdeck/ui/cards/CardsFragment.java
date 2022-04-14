package com.example.virtualdeck.ui.cards;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualdeck.CreateCardActivity;
import com.example.virtualdeck.MainActivity;
import com.example.virtualdeck.R;
import com.example.virtualdeck.databinding.FragmentCardsBinding;
import com.example.virtualdeck.helpers.CardListRecyclerViewAdapter;
import com.example.virtualdeck.helpers.GlobalConstants;
import com.example.virtualdeck.helpers.SQLiteDatabaseHelper;
import com.example.virtualdeck.objects.Card;

import java.util.ArrayList;

public class CardsFragment extends Fragment {

    private CardsViewModel CardsViewModel;
    private FragmentCardsBinding binding;
    private CardListRecyclerViewAdapter adapter;
    private ArrayList<String> mCardUUIDs = new ArrayList<>();
    private ArrayList<String> mCardNames = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CardsViewModel =
                new ViewModelProvider(this).get(CardsViewModel.class);

        binding = FragmentCardsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.createCardPageButton.setOnClickListener(this::onClick);

        initImagesLists();

        return root;
    }

    // Use the init's below to init a new recycler view but swap out the localdb for the online db
    // gotta make a new fragment or maybe activity for viewing a users profile.
    // The init's for viewing the profile will be in a different file than this so come back later
    private void initImagesLists() {
        // TODO: Grab all of a USERUUID's cards from local db
        SQLiteDatabaseHelper dbHelper = new SQLiteDatabaseHelper(getContext());
        dbHelper.getAllSQLiteCards(GlobalConstants.USERUUID, mCardUUIDs, mCardNames);

        initRecyclerView();
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = binding.getRoot().findViewById(R.id.card_list_recycler_view);
        adapter = new CardListRecyclerViewAdapter(getContext(), mCardNames, mCardUUIDs, true, true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onClick(View v) {
        Intent intent = new Intent(getActivity(), CreateCardActivity.class);

        startActivity(intent);
    }

    // TODO: ON ACTIVITY RESULT REFRESH THE RECYCLER VIEW!!!

    @Override
    public void onResume() {
        super.onResume();
        mCardUUIDs = new ArrayList<>();
        mCardNames = new ArrayList<>();
        initImagesLists();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}