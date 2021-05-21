package id.ac.umn.uasif633a.artgram.adapters;

import android.content.Context;
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

import java.util.ArrayList;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.models.UserProperty;

public class PeopleListAdapter extends RecyclerView.Adapter<PeopleListAdapter.ViewHolder> {
    private ArrayList<UserProperty> users;
    private Context context;

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
        if (users.get(holder.getAdapterPosition()).getDpUrl() != null || users.get(holder.getAdapterPosition()).getDpUrl().length() != 0) {
            Glide.with(context)
                    .load(users.get(holder.getAdapterPosition()).getDpUrl())
                    .into(holder.getIvDisplayPicture());
        } else {
            Glide.with(context)
                    .load(R.drawable.display_picture_placeholder)
                    .into(holder.getIvDisplayPicture());
        }
        holder.getTvUsername().setText(users.get(holder.getAdapterPosition()).getUsername());
        holder.getTvFullName().setText(users.get(holder.getAdapterPosition()).getFullName());
        holder.getBtnFollow().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, users.get(holder.getAdapterPosition()).getUsername(), Toast.LENGTH_SHORT).show();
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
