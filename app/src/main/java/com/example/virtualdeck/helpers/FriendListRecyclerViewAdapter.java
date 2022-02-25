package com.example.virtualdeck.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualdeck.R;

import java.util.ArrayList;

public class FriendListRecyclerViewAdapter extends RecyclerView.Adapter<FriendListRecyclerViewAdapter.ViewHolder> {
    private ArrayList<String> mFriendUserUUIDs;
    private Context mContext;

    public FriendListRecyclerViewAdapter(Context context, ArrayList<String> mFriendUserUUIDs) {
        this.mContext = context;
        this.mFriendUserUUIDs = mFriendUserUUIDs;
    }

    @NonNull
    @Override
    public FriendListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friend_list_item, parent, false);
        FriendListRecyclerViewAdapter.ViewHolder holder = new FriendListRecyclerViewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.usernameTextView.setText(mFriendUserUUIDs.get(position));
        holder.parentLayout.setOnClickListener(this::onClick);
    }

    private void onClick(View view) {
        // TODO: Reimplement the profile view to work with the new Python Flask API instead of the PHP one
        Toast.makeText(mContext, "To be replaced later with profile view page." , Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return mFriendUserUUIDs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username_text_view_list_item);
            parentLayout = itemView.findViewById(R.id.layout_friend_list_item);
        }
    }

}
