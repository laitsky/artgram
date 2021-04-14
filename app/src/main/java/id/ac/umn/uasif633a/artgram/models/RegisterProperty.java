package id.ac.umn.uasif633a.artgram.models;

public class RegisterProperty {
    private String email;
    private String password;
    private String username;
    private String fullName;

    public RegisterProperty(String email, String password, String username, String fullName) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.fullName = fullName;
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
}
