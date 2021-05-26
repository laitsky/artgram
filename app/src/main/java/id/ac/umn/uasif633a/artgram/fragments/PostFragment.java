package id.ac.umn.uasif633a.artgram.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.activities.CommentDetailActivity;
import id.ac.umn.uasif633a.artgram.models.Post;

public class PostFragment extends Fragment {
    private static final String TAG = "PostFragment";
    Bundle bundle;
    private TextView tvUsername;
    private TextView tvCaption;
    private TextView tvLikesCount;
    private ImageView ivDisplayPicture;
    private ImageView ivImage;
    private Button btnViewComments;
    private Post post;
    private String dpUrl;

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        return view;
    }
}