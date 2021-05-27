package id.ac.umn.uasif633a.artgram.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.adapters.PostAdapter;
import id.ac.umn.uasif633a.artgram.models.Post;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    private Query userPost;

    private FirebaseFirestore firebaseDb;
    private static FirebaseUser firebaseUser;

    private List<String> followingList;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.post_item_rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        checkFollowing();

        userPost = FirebaseFirestore.getInstance().collection("posts");

        return view;
    }

    private void checkFollowing() {
        followingList = new ArrayList<>();

        firebaseDb = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        CollectionReference peopleRef = firebaseDb.collection("users").document(firebaseUser.getDisplayName())
                .collection("following");

        peopleRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                followingList.clear();
                for(DocumentSnapshot document : task.getResult()) {
                    followingList.add(document.getId());
                }

                readPost();
            }
        });
    }

    private void readPost() {
        userPost.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                postList.clear();
                if(task.isSuccessful()){
                    for(DocumentSnapshot document : task.getResult()){
                        Post post = new Post(
                                document.get("owner").toString(),
                                document.get("postId").toString(),
                                document.get("url").toString(),
                                document.get("caption").toString(),
                                Integer.parseInt(document.get("likes").toString(), 10)
                        );
                        for(String id : followingList) {
                            if(post.getOwner().equals(id)){
                                postList.add(post);
                            }
                        }
                    }
                    postAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}