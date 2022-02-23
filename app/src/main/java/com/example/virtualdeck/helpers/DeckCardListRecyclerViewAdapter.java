package com.example.virtualdeck.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
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
import com.example.virtualdeck.objects.Card;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class DeckCardListRecyclerViewAdapter extends RecyclerView.Adapter<DeckCardListRecyclerViewAdapter.ViewHolder>{

    private static final int ACTION_OPEN_DOCUMENT_CODE = 103;
    private HashMap<String, Boolean> mSelectedMap;
    private ArrayList<String> mCardNames;
    private ArrayList<String> mCardUUIDs;
    private Context mContext;
    private boolean mIsLocal;

    public DeckCardListRecyclerViewAdapter(Context context, ArrayList<String> mCardNames, ArrayList<String> mCardUUIDs, HashMap<String, Boolean> mSelectedMap, boolean mIsLocal) {
        this.mContext = context;
        this.mCardNames = mCardNames;
        this.mCardUUIDs = mCardUUIDs;
        this.mIsLocal = mIsLocal;
        this.mSelectedMap = mSelectedMap;
    }

    @NonNull
    @Override
    public DeckCardListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_list_item, parent, false);
        DeckCardListRecyclerViewAdapter.ViewHolder holder = new DeckCardListRecyclerViewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeckCardListRecyclerViewAdapter.ViewHolder holder, int position) {
        if (mIsLocal) {
            SQLiteDatabaseHelper dbHelper = new SQLiteDatabaseHelper(mContext);

            try {
                Bitmap bitmap = dbHelper.getCardBitmap(mCardUUIDs.get(position));
                holder.cardImage.setImageBitmap(bitmap);
            } catch (Exception exception) {
                Toast.makeText(mContext, exception.toString(), Toast.LENGTH_LONG).show();
                //"Error with getting card image " + mCardNames.get(position)
            }

            holder.cardName.setText(mCardNames.get(position));
            holder.cardUUID.setText(mCardUUIDs.get(position));

            if(mSelectedMap.get(mCardUUIDs.get(position)) != null){
                holder.itemView.setBackgroundColor(Color.LTGRAY);
            }
        }
        else {
            //TODO: MAKE CALL TO DB FOR OTHER USERS CARDS
        }

        //holder.itemView.setBackgroundColor(selected_position == position ? Color.GREEN : Color.TRANSPARENT);
        holder.parentLayout.setOnClickListener(this::onClick);
    }

    private void onClick(View view) {
        // TODO: MAKE IT SO THAT THIS HIGHLIGHTS THE SELECTED ITEM AND ADDS IT TO THE DECK CARD LIST
        TextView cardUUID = view.findViewById(R.id.card_uuid_list_item);
        String sCardUUID = cardUUID.getText().toString();

        if (mSelectedMap.get(sCardUUID) == null) {
            mSelectedMap.put(sCardUUID, true);
            view.setBackgroundColor(Color.LTGRAY);
        }
        else{
            mSelectedMap.remove(sCardUUID);
            view.setBackgroundColor(Color.TRANSPARENT);
        }
    }


    @Override
    public int getItemCount() {
        return mCardNames == null ? 0 : mCardNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView cardImage;
        TextView cardName;
        TextView cardUUID;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardImage = itemView.findViewById(R.id.card_image_list_item);
            cardName = itemView.findViewById(R.id.card_name_list_item);
            cardUUID = itemView.findViewById(R.id.card_uuid_list_item);
            parentLayout = itemView.findViewById(R.id.layout_card_list_item);
        }
    }
}
