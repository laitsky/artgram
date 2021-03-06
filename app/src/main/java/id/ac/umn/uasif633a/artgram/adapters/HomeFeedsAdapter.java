package id.ac.umn.uasif633a.artgram.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.activities.CommentDetailActivity;
import id.ac.umn.uasif633a.artgram.activities.MainActivity;
import id.ac.umn.uasif633a.artgram.models.Post;
import id.ac.umn.uasif633a.artgram.models.UserProperty;

public class HomeFeedsAdapter extends RecyclerView.Adapter<HomeFeedsAdapter.ViewHolder> {
    private static final String TAG = "HomeFeedsAdapter";

    private ArrayList<Post> posts;
    private Context context;


    public HomeFeedsAdapter(ArrayList<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_feeds_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);

        holder.getTvUsername().setText(posts.get(holder.getAdapterPosition()).getOwner());

        if (posts.get(holder.getAdapterPosition()).getUrl() != null) {
            Glide.with(context)
                    .load(posts.get(holder.getAdapterPosition()).getUrl())
                    .into(holder.getIvPostImage());
        }

        holder.getTvCaption().setText(posts.get(holder.getAdapterPosition()).getCaption());

        ownerInfo(holder.ivDisplayPicture, post.getOwner());

        holder.getIvComments().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentDetailActivity.class);
                intent.putExtra("postId", posts.get(holder.getAdapterPosition()).getPostId());
                context.startActivity(intent);
            }
        });
    }

    private  void ownerInfo(final ImageView ivDisplayPicture, final String tvUsername) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection("users").whereEqualTo("username", tvUsername);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        UserProperty user = new UserProperty(
                                document.get("username").toString(),
                                document.get("full_name").toString(),
                                document.get("display_picture").toString()
                        );
                        Glide.with(context).load(user.getDpUrl()).into(ivDisplayPicture);
                    }
                }
            }
        });
    }
    @Override
    public int getItemCount() { return posts.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivDisplayPicture;
        private TextView tvUsername;
        private ImageView ivPostImage;
        private TextView tvCountLikes;
        private TextView tvCaption;
        private ImageView ivComments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDisplayPicture = itemView.findViewById(R.id.layout_feeds_post_iv_display_picture);
            tvUsername = itemView.findViewById(R.id.layout_feeds_post_tv_username);
            ivPostImage = itemView.findViewById(R.id.layout_feeds_post_iv_image);
            tvCountLikes = itemView.findViewById(R.id.layout_feeds_post_tv_count_likes);
            tvCaption = itemView.findViewById(R.id.layout_feeds_post_tv_caption);
            ivComments = itemView.findViewById(R.id.layout_feeds_post_iv_comments);
        }

        public ImageView getIvDisplayPicture() {
            return ivDisplayPicture;
        }

        public TextView getTvUsername() {
            return tvUsername;
        }

        public ImageView getIvPostImage() {
            return ivPostImage;
        }

        public TextView getTvCountLikes() {
            return tvCountLikes;
        }

        public TextView getTvCaption() {
            return tvCaption;
        }

        public ImageView getIvComments() {
            return ivComments;
        }
    }
}
