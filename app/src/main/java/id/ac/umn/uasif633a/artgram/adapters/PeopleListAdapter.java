package id.ac.umn.uasif633a.artgram.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.models.UserProperty;

public class PeopleListAdapter extends RecyclerView.Adapter<PeopleListAdapter.ViewHolder> {
    private static final String TAG = "PeopleListAdapter";
    private ArrayList<UserProperty> users;
    private Context context;
    private String dpUrl, fullname;

    private static FirebaseUser firebaseUser;
    private static FirebaseFirestore db;

    public PeopleListAdapter(ArrayList<UserProperty> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_people_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        DocumentReference loggedInUserRef = db.collection("users").document(firebaseUser.getDisplayName());
        loggedInUserRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                fullname = document.get("full_name").toString();
                                dpUrl = document.get("display_picture").toString();
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        final UserProperty user = users.get(position);

        if (users.get(holder.getAdapterPosition()).getDpUrl().length() != 0) {
            Glide.with(context)
                    .load(users.get(holder.getAdapterPosition()).getDpUrl())
                    .into(holder.getIvDisplayPicture());
        } else {
            Glide.with(context)
                    .load(R.drawable.display_picture_placeholder)
                    .into(holder.getIvDisplayPicture());
        }
        isFollowing(holder);
        holder.getTvUsername().setText(users.get(holder.getAdapterPosition()).getUsername());
        holder.getTvFullName().setText(users.get(holder.getAdapterPosition()).getFullName());
        holder.getBtnFollow().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserFollowing(user, holder);
            }
        });
    }

    private void updateUserFollowing(UserProperty user, ViewHolder holder){
        Map<String, Object> newFollowing = new HashMap<>();
        newFollowing.put("username", user.getUsername());
        newFollowing.put("display_picture", user.getDpUrl());
        newFollowing.put("fullname", user.getFullName());


        Map<String, Object> newFollowers = new HashMap<>();
        newFollowers.put("username", firebaseUser.getDisplayName());
        newFollowers.put("display_picture", dpUrl);
        newFollowers.put("fullname", fullname);

        if(holder.getBtnFollow().getText().toString().equalsIgnoreCase("follow")) {
            db.collection("users").document(firebaseUser.getDisplayName())
                    .collection("following")
                    .document(user.getUsername())
                    .set(newFollowing)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            holder.getBtnFollow().setText("Following");
                            Toast.makeText(context, "Follow " + user.getUsername(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Gagal follow" + user.getUsername(), Toast.LENGTH_SHORT).show();
                        }
                    });
            db.collection("users").document(user.getUsername())
                    .collection("followers")
                    .document(firebaseUser.getDisplayName())
                    .set(newFollowers)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.w(TAG, "followers " +user.getUsername()+": "+firebaseUser.getDisplayName());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        } else {
            db.collection("users").document(firebaseUser.getDisplayName())
                    .collection("following")
                    .document(user.getUsername())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            holder.getBtnFollow().setText("Follow");
                            Toast.makeText(context, "Unfollow " + user.getUsername(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting document", e);
                        }
                    });
            db.collection("users").document(user.getUsername())
                    .collection("followers")
                    .document(firebaseUser.getDisplayName())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            holder.getBtnFollow().setText("Follow");
                            Toast.makeText(context, "Unfollow " + user.getUsername(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting document", e);
                        }
                    });

        }
    }

    private void isFollowing(ViewHolder holder){
        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("users").document(firebaseUser.getDisplayName())
                .collection("following")
                .document(users.get(holder.getAdapterPosition()).getUsername());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        holder.getBtnFollow().setText("Following");
                    } else {
                        Log.d(TAG, "No such document");
                        holder.getBtnFollow().setText("Follow");
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private TextView tvFullName;
        private ImageView ivDisplayPicture;
        private Button btnFollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.layout_people_list_tv_username);
            tvFullName = itemView.findViewById(R.id.layout_people_list_tv_full_name);
            ivDisplayPicture = itemView.findViewById(R.id.layout_people_list_iv_display_picture);
            btnFollow = itemView.findViewById(R.id.layout_people_list_btn_follow);
        }

        public TextView getTvUsername() {
            return tvUsername;
        }

        public void setTvUsername(TextView tvUsername) {
            this.tvUsername = tvUsername;
        }

        public TextView getTvFullName() {
            return tvFullName;
        }

        public void setTvFullName(TextView tvFullName) {
            this.tvFullName = tvFullName;
        }

        public ImageView getIvDisplayPicture() {
            return ivDisplayPicture;
        }

        public void setIvDisplayPicture(ImageView ivDisplayPicture) {
            this.ivDisplayPicture = ivDisplayPicture;
        }

        public Button getBtnFollow() {
            return btnFollow;
        }

        public void setBtnFollow(Button btnFollow) {
            this.btnFollow = btnFollow;
        }
    }
}
