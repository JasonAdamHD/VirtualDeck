package com.example.virtualdeck.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualdeck.R;
import com.example.virtualdeck.ViewCardActivity;
import com.example.virtualdeck.ViewDeckCardsActivity;
import com.example.virtualdeck.objects.Card;
import com.example.virtualdeck.objects.Deck;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DeckListRecyclerViewAdapter extends RecyclerView.Adapter<DeckListRecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> mDeckNames;
    private ArrayList<String> mDeckUUIDs;
    private Context mContext;
    private boolean mIsLocal;

    public DeckListRecyclerViewAdapter(Context context, ArrayList<String> mDeckNames, ArrayList<String> mDeckUUIDs, boolean mIsLocal) {
        this.mContext = context;
        this.mDeckNames = mDeckNames;
        this.mDeckUUIDs = mDeckUUIDs;
        this.mIsLocal = mIsLocal;
    }

    @NonNull
    @Override
    public DeckListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_deck_list_item, parent, false);
        DeckListRecyclerViewAdapter.ViewHolder holder = new DeckListRecyclerViewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeckListRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.deckName.setText(mDeckNames.get(position));
        holder.deckUUID.setText(mDeckUUIDs.get(position));

        holder.parentLayout.setOnClickListener(this::onClick);
    }

    // TODO: MAKE IT SO THAT THIS LOADS THE CARDS FROM THE DECK
    private void onClick(View view) {
        Intent intent = new Intent(mContext, ViewDeckCardsActivity.class);
        TextView deckName = view.findViewById(R.id.deck_name_list_item);
        TextView deckUUID = view.findViewById(R.id.deck_uuid_list_item);
        Toast.makeText(mContext, deckUUID.getText().toString(), Toast.LENGTH_SHORT).show();

        intent.putExtra("DeckUUID", deckUUID.getText().toString());
        intent.putExtra("DeckName", deckName.getText().toString());
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mDeckNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView deckName;
        TextView deckUUID;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deckName = itemView.findViewById(R.id.deck_name_list_item);
            deckUUID = itemView.findViewById(R.id.deck_uuid_list_item);
            parentLayout = itemView.findViewById(R.id.layout_deck_list_item);
        }
    }
}
