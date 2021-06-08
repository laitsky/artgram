package id.ac.umn.uasif633a.artgram.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.activities.CommentDetailActivity;
import id.ac.umn.uasif633a.artgram.models.Post;

public class PostFragment extends Fragment {
    private static final String TAG = "PostFragment";
    private FirebaseUser user;
    private FirebaseFirestore db;
    private CollectionReference postRef;
    private Query commentsRef;
    private Bundle bundle;
    private TextView tvUsername;
    private TextView tvCaption;
    private TextView tvLikesCount;
    private ImageView ivDisplayPicture;
    private ImageView ivImage;
    private Button btnViewComments;
    private Button btnDeletePost;
    private Post post;
    private String dpUrl;

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        postRef = db.collection("posts");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        bundle = this.getArguments();
        if (bundle != null) {
            post = bundle.getParcelable("data");
            if (bundle.getString("owner_dp") != null) {
                dpUrl = bundle.getString("owner_dp");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        tvUsername = (TextView) view.findViewById(R.id.fragment_post_tv_username);
        tvCaption = (TextView) view.findViewById(R.id.fragment_post_tv_caption);
        tvLikesCount = (TextView) view.findViewById(R.id.fragment_post_tv_count_likes);
        ivDisplayPicture = (ImageView) view.findViewById(R.id.fragment_post_iv_display_picture);
        ivImage = (ImageView) view.findViewById(R.id.fragment_post_iv_image);
        btnViewComments = (Button) view.findViewById(R.id.fragment_post_btn_comments);
        btnDeletePost = (Button) view.findViewById(R.id.fragment_post_btn_delete_post);

        // setting delete post button visibility
        // if it is not the same with the user logged in
        // then do not show the delete post button
        if (user.getDisplayName().equals(post.getOwner())) {
            btnDeletePost.setVisibility(View.VISIBLE);
        }

        tvUsername.setText(post.getOwner());
        tvCaption.setText(post.getCaption());
        tvLikesCount.setText(String.valueOf(post.getLikes()));
        if (dpUrl != null) {
            Glide.with(this)
                    .load(dpUrl)
                    .into(ivDisplayPicture);
        }
        if (post.getUrl() != null) {
            Glide.with(this)
                    .load(post.getUrl())
                    .into(ivImage);
        }

        btnViewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CommentDetailActivity.class);
                intent.putExtra("postId", post.getPostId());
                startActivity(intent);
            }
        });

        btnDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePost();
            }
        });
        return view;
    }

    private void deletePost() {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Post")
                .setMessage("Do you really want to delete this post?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgressDialog progressDialog = new ProgressDialog(getContext());
                        progressDialog.setMessage("Please wait");
                        progressDialog.show();
                        postRef.whereEqualTo("postId", post.getPostId())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        String docId = "";
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                                docId = doc.getId();
                                                break;
                                            }
                                            postRef.document(docId)
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(getContext(), "Post successfully deleted", Toast.LENGTH_SHORT).show();
                                                            goToProfileFragment();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.d(TAG, "onFailure: Error deleting document", e);
                                                        }
                                                    });
                                        }
                                    }
                                });

                    }
                })
                .setNegativeButton("No", null).show();
    }

    private void goToProfileFragment() {
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                )
                .replace(R.id.fragment_container, new ProfileFragment())
                .commit();
    }
}