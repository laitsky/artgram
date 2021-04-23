package id.ac.umn.uasif633a.artgram.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;
import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.activities.EditProfileActivity;
import id.ac.umn.uasif633a.artgram.activities.LoginActivity;
import id.ac.umn.uasif633a.artgram.activities.MainActivity;
import id.ac.umn.uasif633a.artgram.interfaces.ProfileDataReceiver;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private TextView tvFullName;
    private TextView tvUsername;
    private Button btnEditProfile;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private ProfileDataReceiver profile;
    private String username;
    private String fullName;
    private String userEmail;
    private String userBio;
    private CircleImageView profileImageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inisialisasi instance Firebase
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        profile = (ProfileDataReceiver) context;
        username = profile.getUsername();
        fullName = profile.getFullName();
        userEmail = profile.getUserEmail();
        userBio = profile.getUserBio();

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(username);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inisialisasi View
        tvFullName = (TextView) view.findViewById(R.id.fragment_profile_tv_display_name);
        tvUsername = (TextView) view.findViewById(R.id.fragment_profile_tv_username);
        btnEditProfile = (Button) view.findViewById(R.id.fragment_profile_btn_edit_profile);
        profileImageView = (CircleImageView) view.findViewById(R.id.fragment_profile_iv_display_picture);

        tvFullName.setText(fullName);
        tvUsername.setText(username);
        if (user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(profileImageView);
        }
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra("FULL_NAME", fullName);
                intent.putExtra("USERNAME", username);
                intent.putExtra("USER_EMAIL", userEmail);
                intent.putExtra("USER_BIO", userBio);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.playlist_menu_logout:
                logout();
                return true;
        }
        return true;
    }

    private void logout() {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear().commit();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

}