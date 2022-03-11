package com.example.virtualdeck.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.virtualdeck.R;
import com.example.virtualdeck.ViewDeckCardsActivity;
import com.example.virtualdeck.ViewUserProfileActivity;
import com.example.virtualdeck.ui.profile.ProfileFragment;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FriendListRecyclerViewAdapter extends RecyclerView.Adapter<FriendListRecyclerViewAdapter.ViewHolder> {
    private ArrayList<String> mFriendUserUUIDs;
    private Username username;
    private Context mContext;
    private int mPos;
    private FriendListRecyclerViewAdapter.ViewHolder mHolder;

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
        // TODO: Make it so that the username is actually the username, not the UUID
        getUsername(mFriendUserUUIDs.get(position));
        mPos = position;
        mHolder = holder;
        holder.parentLayout.setOnClickListener(this::onClick);
    }

    private void getUsername(String UserUUID){
        OkHttpClient okHttpClient = new OkHttpClient();

        HttpUrl.Builder httpBuilder = HttpUrl.parse(GlobalConstants.GET_USERNAME_FROM_UUID_URL).newBuilder();
        httpBuilder.addQueryParameter("UserUUID", UserUUID);
        Request request = new Request.Builder().url(httpBuilder.build()).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();

                Gson g = new Gson();
                username = g.fromJson(myResponse, FriendListRecyclerViewAdapter.Username.class);

                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mHolder.usernameTextView.setText(username.Username);
                    }
                });
            }
        });
    }

    private void onClick(View view) {
        // TODO: Reimplement the profile view to work with the new Python Flask API instead of the PHP one
        //Toast.makeText(mContext, "To be replaced later with profile view page." , Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(mContext, ViewUserProfileActivity.class);
        TextView username = view.findViewById(R.id.username_text_view_list_item);
        Toast.makeText(mContext, username.getText().toString(), Toast.LENGTH_SHORT).show();

        intent.putExtra("UserUUID", mFriendUserUUIDs.get(mPos));
        mContext.startActivity(intent);

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

    private class Username{
        String Username;
    }
}
