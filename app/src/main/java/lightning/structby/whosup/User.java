package lightning.structby.whosup;

/**
 * Created by azwreith on 4/5/17.
 */

public class User {

    private String name;
    private String email;
    private String shortBio;
    private String profileImage;

    public User(String name, String email, String shortBio, String profileImage) {
        this.name = name;
        this.email = email;
        this.shortBio = shortBio;
        this.profileImage = profileImage;
    }

    public User() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getShortBio() {
        return shortBio;
    }

    public void setShortBio(String shortBio) {
        this.shortBio = shortBio;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

}