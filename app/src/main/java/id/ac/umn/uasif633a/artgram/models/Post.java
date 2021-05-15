package id.ac.umn.uasif633a.artgram.models;

public class Post {
    private String owner;
    private String postId;
    private String url;
    private String caption;
    private int likes;

    public Post(String owner, String postId, String url, String caption, int likes) {
        this.owner = owner;
        this.postId = postId;
        this.url = url;
        this.caption = caption;
        this.likes = likes;
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

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
