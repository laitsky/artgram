package id.ac.umn.uasif633a.artgram.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.interfaces.ProfileDataReceiver;
import id.ac.umn.uasif633a.artgram.models.UserProperty;

public class PeopleListFragment extends Fragment {
    private static final String TAG = "PeopleListFragment";
    ArrayList<UserProperty> listOfUsers = new ArrayList<>();
    private FirebaseFirestore firebaseDb;
    private ProfileDataReceiver profile;
    private String username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inisialisasi instance Firebase
        firebaseDb = FirebaseFirestore.getInstance();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        profile = (ProfileDataReceiver) context;
        username = profile.getUsername();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people_list, container, false);
        // Mendapatkan random user
        CollectionReference peopleRef = firebaseDb.collection("users");
        Query query = peopleRef.whereNotEqualTo("username", username);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        UserProperty user = new UserProperty(
                                document.get("email").toString(),
                                document.get("username").toString(),
                                document.get("full_name").toString()
                        );
                        listOfUsers.add(user);
                    }
                    Log.d(TAG, "onComplete: array populating done...");
                    for (UserProperty user : listOfUsers) {
                        Log.d(TAG, "getting fullname..." + user.getFullName());
                    }
                } else {
                    Log.d(TAG, "onComplete: error..." + task.getException());
                }
            }
        });
        return view;
    }

    public PeopleListFragment() {

    }
}