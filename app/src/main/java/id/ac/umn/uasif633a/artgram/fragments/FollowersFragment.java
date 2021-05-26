package id.ac.umn.uasif633a.artgram.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.adapters.PeopleListAdapter;
import id.ac.umn.uasif633a.artgram.interfaces.ProfileDataReceiver;
import id.ac.umn.uasif633a.artgram.models.UserProperty;

public class FollowersFragment extends Fragment {
    private static final String TAG = "FollowersFragment";
    private FirebaseFirestore firebaseDb;
    ArrayList<UserProperty> listOfUsers = new ArrayList<>();
    private static FirebaseUser firebaseUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inisialisasi instance Firebase
        firebaseDb = FirebaseFirestore.getInstance();
    }

    public FollowersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_followers, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference peopleRef = firebaseDb.collection("users").document(firebaseUser.getDisplayName())
                .collection("followers");
        peopleRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        UserProperty user = new UserProperty(
                                document.get("username").toString(),
                                document.get("fullname").toString(),
                                document.get("display_picture").toString()
                        );
                        listOfUsers.add(user);
                    }
                    RecyclerView peopleListRv = getActivity().findViewById(R.id.fragment_followers_list_rv);
                    PeopleListAdapter adapter = new PeopleListAdapter(listOfUsers, getContext());
                    peopleListRv.setAdapter(adapter);
                    peopleListRv.setLayoutManager(new LinearLayoutManager(getContext()));
                } else {
                    Log.d(TAG, "onComplete: error..." + task.getException());
                }
            }
        });
        return view;
    }
}