package models.user;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

import models.ExpirationTimeHelper;
import org.jsoup.Jsoup;
import org.mindrot.jbcrypt.BCrypt;

@Entity
@Table(name="userAccount")
@JsonRootName("user_account")
public class UserAccount extends Model {

    public static final Finder<Long, UserAccount> FIND = new Finder<>(UserAccount.class);
    // Field constraints
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_-]*[a-zA-Z]+[a-zA-Z0-9_-]*$";
    public static final int MAX_USERNAME_SIZE = 25;
    public static final int MAX_EMAIL_SIZE = 254;
    public static final int REGISTRATION_KEY_EXPIRATION_HOURS = 24 * 5; // Valid for 5 days

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedTimestamp
    private Date creationDate;

    @UpdatedTimestamp
    private Date lastUpdatedDate;

    @Column(length = 256, unique = true, nullable = false)
    private String email;

    @Column(length = 256, unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @Column(columnDefinition = "text")
    private String bcryptPassword;

    @JsonIgnore
    @Column(columnDefinition = "text")
    private String registrationKey;

    @JsonIgnore
    private Date registrationKeyDate;

    public UserAccount() {
    }

    public UserAccount(String email, String username, String password) {
        this();

        // Set the fields
        setEmail(email);
        setPassword(password);
        setUsername(username);

        // Create a registration key
        createRegistrationKey();
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    public void setUsername(String username) {
        this.username = Jsoup.parse(username).text();
    }

    public String getUsername() {
        return username;
    }

    public String getRegistrationKey() {
        return registrationKey;
    }

    public void createRegistrationKey() {
        registrationKey = UUID.randomUUID().toString();
        registrationKeyDate = new Date();
    }

    public void destroyRegistrationKey() {
        this.registrationKey = null;
        this.registrationKeyDate = null;
    }

    @JsonIgnore
    public boolean isRegistrationKeyExpired() {
        return ExpirationTimeHelper.isExpired(registrationKeyDate, REGISTRATION_KEY_EXPIRATION_HOURS);
    }

    public void setPassword(String password) {
        bcryptPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public void setBCryptPassword(String bcryptPassword) {
        this.bcryptPassword = bcryptPassword;
    }

    @JsonIgnore
    public String getBCryptPassword() {
        return bcryptPassword;
    }

    public boolean checkPassword(String password) {
        return BCrypt.checkpw(password, bcryptPassword);
    }

    public static UserAccount findByEmail(String emailAddress) {
        try {
            return FIND.where().eq("email", emailAddress).findUnique();
        } catch (Exception e) {
            play.Logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static UserAccount findByUsername(String username) {
        try {
            return FIND.where().eq("username", username).findUnique();
        } catch (Exception e) {
            play.Logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static UserAccount findById(Long id) {
        try  {
            return FIND.where().eq("id", id).findUnique();
        } catch (Exception e) {
            play.Logger.error(e.getMessage(), e);
            return null;
        }
    }
}
