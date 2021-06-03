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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.activities.EditProfileActivity;
import id.ac.umn.uasif633a.artgram.activities.FollowActivity;
import id.ac.umn.uasif633a.artgram.activities.LoginActivity;
import id.ac.umn.uasif633a.artgram.activities.MainActivity;
import id.ac.umn.uasif633a.artgram.adapters.ProfileGridAdapter;
import id.ac.umn.uasif633a.artgram.interfaces.ProfileDataReceiver;
import id.ac.umn.uasif633a.artgram.models.Post;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private TextView tvFullName;
    private TextView tvUsername;
    private Button btnEditProfile;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Query myPosts;
    private ProfileDataReceiver profile;
    private ArrayList<Post> userPosts = new ArrayList<>();
    private String username;
    private String fullName;
    private String userEmail;
    private String userBio;
    private CircleImageView profileImageView;
    private TextView tvFollowing;
    private TextView tvFollowers;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inisialisasi instance Firebase
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        myPosts = db.collection("posts").whereEqualTo("owner", user.getDisplayName());
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
        getUserPosts();

        tvFullName = (TextView) view.findViewById(R.id.fragment_profile_tv_display_name);
        tvUsername = (TextView) view.findViewById(R.id.fragment_profile_tv_username);
        btnEditProfile = (Button) view.findViewById(R.id.fragment_profile_btn_edit_profile);
        profileImageView = (CircleImageView) view.findViewById(R.id.fragment_profile_iv_display_picture);
        tvFollowing = (TextView) view.findViewById(R.id.fragment_profile_tv_following);
        tvFollowers = (TextView) view.findViewById(R.id.fragment_profile_tv_followers);

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

        tvFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowActivity.class);
                startActivity(intent);
            }
        });

        tvFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void getUserPosts() {
        myPosts.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Post post = new Post(
                                document.get("owner").toString(),
                                document.get("postId").toString(),
                                document.get("url").toString(),
                                document.get("caption").toString(),
                                Integer.parseInt(document.get("likes").toString(), 10)
                        );
                        userPosts.add(post);
                    }
                    RecyclerView profileFeedsRv = getActivity().findViewById(R.id.fragment_profile_rv_feeds);
                    ProfileGridAdapter adapter = new ProfileGridAdapter(userPosts, getContext());
                    profileFeedsRv.setAdapter(adapter);
                    profileFeedsRv.setLayoutManager(new GridLayoutManager(getContext(), 2));
                } else {
                    Log.d(TAG, "onComplete: error getting data");
                }
            }
        });
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