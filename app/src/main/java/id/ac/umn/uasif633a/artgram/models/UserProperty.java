package id.ac.umn.uasif633a.artgram.models;

public class UserProperty {
    private String email;
    private String password;
    private String username;
    private String fullName;
    private String userBio;


    public UserProperty(String email, String username, String fullName, String userBio) {
        this.email = email;
        this.username = username;
        this.fullName = fullName;
        this.userBio = userBio;
    }

    public UserProperty(String email, String password, String username, String fullName, String userBio) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.fullName = fullName;
        this.userBio = userBio;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }
}
