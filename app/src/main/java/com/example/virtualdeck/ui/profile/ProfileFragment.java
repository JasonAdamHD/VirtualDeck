package com.example.virtualdeck.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.virtualdeck.FindUsernameActivity;
import com.example.virtualdeck.MainActivity;
import com.example.virtualdeck.R;
import com.example.virtualdeck.databinding.FragmentProfileBinding;
import com.example.virtualdeck.helpers.GlobalConstants;
import com.google.gson.Gson;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileFragment extends Fragment {

    private TextView profileUsername;
    private TextView profileRealName;
    private TextView profileBio;
    private CircleImageView profileImage;
    private Button editProfileButton;
    private ProfileFragment.UserProfile userProfile;

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // TODO: LOAD USERS PROFILE!!
        profileUsername = binding.profileUsername;
        profileRealName = binding.profileRealName;
        profileBio = binding.profileBio;
        profileImage = binding.profileImage;
        editProfileButton = binding.profileEditProfileButton;


        getProfile();

        editProfileButton.setOnClickListener(this::onClick);

        return root;
    }

    private void getProfile()
    {
        OkHttpClient okHttpClient = new OkHttpClient();

        HttpUrl.Builder httpBuilder = HttpUrl.parse(GlobalConstants.GET_USERUUID_URL).newBuilder();
        httpBuilder.addQueryParameter("UserUUID", GlobalConstants.USERUUID);
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
                userProfile = g.fromJson(myResponse, ProfileFragment.UserProfile.class);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        profileUsername.setText("@" + userProfile.Username);
                        profileRealName.setText(userProfile.DisplayName);
                        Glide.with(getActivity()).load(userProfile.PhotoURL).into(profileImage);
                    }
                });
            }
        });
    }
    private void onClick(View view) {
        // TODO: CREATE EDIT PROFILE PAGE
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class UserProfile {
        String DisplayName;
        String PhotoURL;
        String UserUUID;
        String Username;
    }
}