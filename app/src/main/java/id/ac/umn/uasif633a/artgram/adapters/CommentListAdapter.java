package id.ac.umn.uasif633a.artgram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.models.Comment;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {
    private ArrayList<Comment> comments;
    private Context context;

    public CommentListAdapter(ArrayList<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_comment_container, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTvUsername().setText(comments.get(holder.getAdapterPosition()).getUser());
        holder.getTvCommentText().setText(comments.get(holder.getAdapterPosition()).getCommentText());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private TextView tvCommentText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.layout_comment_container_tv_username);
            tvCommentText = itemView.findViewById(R.id.layout_comment_container_tv_commentText);
        }

        public TextView getTvUsername() {
            return tvUsername;
        }

        public void setTvUsername(TextView tvUsername) {
            this.tvUsername = tvUsername;
        }

        public TextView getTvCommentText() {
            return tvCommentText;
        }

        public void setTvCommentText(TextView tvCommentText) {
            this.tvCommentText = tvCommentText;
        }
    }
}
