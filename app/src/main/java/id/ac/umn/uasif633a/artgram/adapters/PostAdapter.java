package id.ac.umn.uasif633a.artgram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.models.Post;
import id.ac.umn.uasif633a.artgram.models.UserProperty;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private static final String TAG = "PostAdapter";
    public Context mContext;
    public List<Post> mPost;


    private FirebaseUser firebaseUser;


    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(position);

        Glide.with(mContext).load(post.getUrl()).into(holder.post_image);

        if(post.getCaption().equals("")){
            holder.description.setVisibility(View.GONE);
        } else{
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getCaption());
        }

        ownerInfo(holder.image_profile, holder.username, post.getOwner());
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile, post_image, like;
        public TextView username, likes, description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.fragment_post_iv_display_picture);
            post_image = itemView.findViewById(R.id.fragment_post_iv_image);
            like = itemView.findViewById(R.id.fragment_post_tv_likes);
            description = itemView.findViewById(R.id.fragment_post_tv_caption);
            username = itemView.findViewById(R.id.fragment_post_tv_username);
            likes = itemView.findViewById(R.id.fragment_post_tv_count_likes);

        }
    }

    
    private  void ownerInfo(final ImageView image_profile, final TextView fullname, final String username){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection("users").whereEqualTo("username", username);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(DocumentSnapshot document : task.getResult()) {
                        UserProperty user = new UserProperty(
                                document.get("username").toString(),
                                document.get("full_name").toString(),
                                document.get("display_picture").toString()
                        );
                        Glide.with(mContext).load(user.getDpUrl()).into(image_profile);
                        fullname.setText(user.getUsername());
                    }
                }
            }
        });

    }
}
