package id.ac.umn.uasif633a.artgram.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {
    private String owner;
    private String postId;
    private String url;
    private String caption;
    private String dpUrl;
    private int likes;

    public Post(String owner, String postId, String url, String caption, int likes) {
        this.owner = owner;
        this.postId = postId;
        this.url = url;
        this.caption = caption;
        this.likes = likes;
    }

    protected Post(Parcel in) {
        owner = in.readString();
        postId = in.readString();
        url = in.readString();
        caption = in.readString();
        likes = in.readInt();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(owner);
        dest.writeString(postId);
        dest.writeString(url);
        dest.writeString(caption);
        dest.writeInt(likes);
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDpUrl() {
        return dpUrl;
    }

    public void setDpUrl(String dpUrl) {
        this.dpUrl = dpUrl;
    }

    public static Creator<Post> getCREATOR() {
        return CREATOR;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

}
