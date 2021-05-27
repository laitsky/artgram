package id.ac.umn.uasif633a.artgram.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.adapters.HomeFeedsAdapter;
import id.ac.umn.uasif633a.artgram.models.Post;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private FirebaseFirestore db;
    private FirebaseUser user;
    private CollectionReference followingRef;
    private ArrayList<String> listOfFollowing = new ArrayList<>();
    private ArrayList<Post> listOfPosts = new ArrayList<>();

    public HomeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inisialisasi instance Firebase
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        followingRef = db.collection("users")
                .document(user.getDisplayName())
                .collection("following");
        getUserFeeds();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void getUserFeeds() {
        followingRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        listOfFollowing.add(document.getString("username"));
                    }

                    CollectionReference postsRef = db.collection("posts");
                    postsRef.whereIn("owner", listOfFollowing)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Post post = new Post(
                                                    document.getString("owner"),
                                                    document.getString("postId"),
                                                    document.getString("url"),
                                                    document.getString("caption"),
                                                    Integer.parseInt(document.get("likes").toString(), 10)
                                            );
                                            listOfPosts.add(post);
                                        }
                                        RecyclerView homeFeedsRv = getActivity().findViewById(R.id.fragment_home_rv_feeds);
                                        HomeFeedsAdapter adapter = new HomeFeedsAdapter(listOfPosts, getContext());
                                        homeFeedsRv.setAdapter(adapter);
                                        homeFeedsRv.setLayoutManager(new LinearLayoutManager(getContext()));
                                    }
                                }
                            });
                }
            }
        });
    }
}