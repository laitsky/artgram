package id.ac.umn.uasif633a.artgram.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Comment {
    private String user;
    private String commentText;
    private String postId;
    @ServerTimestamp
    private Date timestamp;

    public Comment(String user, String commentText) {
        this.user = user;
        this.commentText = commentText;
    }

    public Comment(String user, String commentText, String documentRef) {
        this.user = user;
        this.commentText = commentText;
        this.postId = documentRef;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
