package com.example.virtualdeck.helpers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualdeck.CreateCardActivity;
import com.example.virtualdeck.R;
import com.example.virtualdeck.ViewCardActivity;
import com.example.virtualdeck.objects.Card;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import retrofit2.HttpException;

public class CardListRecyclerViewAdapter extends RecyclerView.Adapter<CardListRecyclerViewAdapter.ViewHolder> implements Filterable {

    private static final int ACTION_OPEN_DOCUMENT_CODE = 103;
    private ArrayList<String> mCardNames;
    private ArrayList<String> mCardUUIDs;
    private ArrayList<String> mCardNamesAll;
    private ArrayList<String> mCardUUIDsAll;
    private Context mContext;
    private boolean mIsLocal;
    private boolean mCanEditCards;

    public CardListRecyclerViewAdapter(Context context, ArrayList<String> mCardNames, ArrayList<String> mCardUUIDs, boolean mIsLocal, boolean mCanEditCards) {
        this.mContext = context;
        this.mCardNames = mCardNames;
        this.mCardUUIDs = mCardUUIDs;
        this.mIsLocal = mIsLocal;
        this.mCanEditCards = mCanEditCards;
        this.mCardNamesAll = new ArrayList<>(mCardNames);
        this.mCardUUIDsAll = new ArrayList<>(mCardUUIDs);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
        Intent intent = new Intent(mContext, ViewCardActivity.class);
        TextView cardUUID = view.findViewById(R.id.card_uuid_list_item);
        Toast.makeText(mContext, cardUUID.getText().toString(), Toast.LENGTH_LONG).show();
        SQLiteDatabaseHelper dbHelper = new SQLiteDatabaseHelper(mContext);
        Card card = null;
        Bitmap bitmap = null;
        if(mIsLocal) {
            card = dbHelper.getCardByCardUUID(cardUUID.getText().toString());
            try{
                bitmap = dbHelper.getCardBitmap(card.getCardUUID());
                String filePath = tempFileImage(mContext, bitmap,"name");
                intent.putExtra("CardBitmapPath", filePath);
            } catch (Exception exception) {
                // TODO: Add some kind of message here
                exception.printStackTrace();
            }
        }
        else{
            // TODO: Do the online db call here to create the card the user is about to view.
        }

        intent.putExtra("CanEdit", mCanEditCards);
        intent.putExtra("Card", card);
        intent.putExtra("CardMetadata", card.getMetadata());
        mContext.startActivity(intent);
    }
    public static String tempFileImage(Context context, Bitmap bitmap, String name) {

        File outputDir = context.getCacheDir();
        File imageFile = new File(outputDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Error writing file", e);
        }

        return imageFile.getAbsolutePath();
    }


    @Override
    public int getItemCount() {
        return mCardNames.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ArrayList<String>> filteredList = new ArrayList<>();
            ArrayList<String> names = new ArrayList<>();
            ArrayList<String> cards = new ArrayList<>();
            filteredList.add(names);
            filteredList.add(cards);

            if (charSequence.toString().isEmpty()){
                filteredList.get(0).addAll(mCardNamesAll);
                filteredList.get(1).addAll(mCardUUIDsAll);

            }
            else {
                for (int i = 0; i < mCardNamesAll.size(); i++){
                    if(mCardNamesAll.get(i).toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filteredList.get(0).add(mCardNamesAll.get(i));
                        filteredList.get(1).add(mCardUUIDsAll.get(i));
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mCardNames.clear();
            mCardUUIDs.clear();
            ArrayList<ArrayList<String>> tempList = (ArrayList<ArrayList<String>>) filterResults.values;
            mCardNames.addAll(tempList.get(0));
            mCardUUIDs.addAll(tempList.get(1));
            notifyDataSetChanged();
        }
    };

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
