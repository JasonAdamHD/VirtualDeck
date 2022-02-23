package com.example.virtualdeck.ui.profile;

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

import com.example.virtualdeck.R;
import com.example.virtualdeck.databinding.FragmentProfileBinding;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private TextView profileUsername;
    private TextView profileRealName;
    private TextView profileBio;
    private CircleImageView profileImage;
    private Button editProfileButton;

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

        profileUsername.setText("@" + "ExampleUsername");
        profileRealName.setText("FName" + " " + "LName");
        profileBio.setText("No Bio Set");
        editProfileButton.setOnClickListener(this::onClick);

        return root;
    }

    private void onClick(View view) {
        // TODO: CREATE EDIT PROFILE PAGE
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}