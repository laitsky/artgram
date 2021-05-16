package id.ac.umn.uasif633a.artgram.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.fragments.PostFragment;
import id.ac.umn.uasif633a.artgram.models.Post;

public class ProfileFeedsAdapter extends RecyclerView.Adapter<ProfileFeedsAdapter.ViewHolder> {
    private static final String TAG = "ProfileFeedsAdapter";

    private ArrayList<Post> posts;
    private Context context;

    public ProfileFeedsAdapter(ArrayList<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_grid_profile_feeds, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (posts.get(position).getUrl() != null) {
            Glide.with(context)
                    .load(posts.get(position).getUrl())
                    .into(holder.getImage());
        }

        holder.getImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                String username = posts.get(position).getOwner();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference dpRef = db.collection("users").document(username);
                dpRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String dpUrl;
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                dpUrl = doc.get("display_picture").toString();
                                if (dpUrl != null || dpUrl.length() != 0) {
                                    bundle.putString("owner_dp", dpUrl);
                                }
                            }
                        }
                        bundle.putParcelable("data", posts.get(position));
                        PostFragment postFragment = new PostFragment();
                        postFragment.setArguments(bundle);
                        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, postFragment).commit();
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.layout_grid_profile_feeds_iv_image);
        }

        public ImageView getImage() {
            return image;
        }

    }
}
