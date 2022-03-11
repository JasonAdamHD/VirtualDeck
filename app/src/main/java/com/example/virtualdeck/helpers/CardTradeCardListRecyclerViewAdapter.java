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

public class CardTradeCardListRecyclerViewAdapter extends RecyclerView.Adapter<CardTradeCardListRecyclerViewAdapter.ViewHolder> {

    private static final int ACTION_OPEN_DOCUMENT_CODE = 103;
    private HashMap<String, Boolean> mSelectedMap;
    private ArrayList<String> mCardNames;
    private ArrayList<String> mCardUUIDs;
    private Context mContext;
    private boolean mIsLocal;

    public CardTradeCardListRecyclerViewAdapter(Context context, ArrayList<String> mCardNames, ArrayList<String> mCardUUIDs, boolean mIsLocal, HashMap<String, Boolean> mSelectedMap) {
        this.mContext = context;
        this.mCardNames = mCardNames;
        this.mCardUUIDs = mCardUUIDs;
        this.mIsLocal = mIsLocal;
        this.mSelectedMap = mSelectedMap;
    }

    @NonNull
    @Override
    public CardTradeCardListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_list_item, parent, false);
        CardTradeCardListRecyclerViewAdapter.ViewHolder holder = new CardTradeCardListRecyclerViewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardTradeCardListRecyclerViewAdapter.ViewHolder holder, int position) {
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
        }
        else {
            //TODO: MAKE CALL TO DB FOR OTHER USERS CARDS
            // Since not local get UserUUID for person to lookup

        }

        holder.parentLayout.setOnClickListener(this::onClick);
    }

    private void onClick(View view) {
        TextView cardUUID = view.findViewById(R.id.card_uuid_list_item);
        String sCardUUID = cardUUID.getText().toString();

        if (mSelectedMap.get(sCardUUID) == null && mSelectedMap.size() == 0) {
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
        return mCardNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

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
