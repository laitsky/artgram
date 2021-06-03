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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.activities.MainActivity;
import id.ac.umn.uasif633a.artgram.adapters.ExploreGridAdapter;
import id.ac.umn.uasif633a.artgram.models.Post;

public class ExploreFragment extends Fragment {
    private static final String TAG = "ExploreFragment";
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Query posts;
    private ArrayList<Post> listOfPosts = new ArrayList<>();
    public ExploreFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inisialisasi instance Firebase
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        posts = db.collection("posts").whereNotEqualTo("owner", user.getDisplayName());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Explore");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        getExploreFeeds();
        return view;
    }

    private void getExploreFeeds() {
        posts.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                        listOfPosts.add(post);
                    };
                    RecyclerView exploreRv = getActivity().findViewById(R.id.fragment_explore_rv);
                    ExploreGridAdapter adapter = new ExploreGridAdapter(listOfPosts, getContext());
                    exploreRv.setAdapter(adapter);
                    exploreRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
                } else {
                    Log.d(TAG, "onComplete: error getting data");
                }
            }
        });
    }


}