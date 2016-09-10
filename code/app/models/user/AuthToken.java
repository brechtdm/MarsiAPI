package models.user;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import util.ConfigurationHelper;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="authToken")
public class AuthToken extends Model {

    public static final Model.Finder<Long, AuthToken> FIND = new Model.Finder<>(AuthToken.class);

    public static final String CRYPTO_KEY_MARSI = "play.crypto.secret";
    public static final String JWT_ISSUER       = "MarsiAPI";
    public static final int AUTHENTICATION_TOKEN_EXPIRATION_HOURS = 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedTimestamp
    private Date creationTimestamp;

    private Date destroyTimestamp;

    @ManyToOne
    @Column(nullable = false)
    public UserAccount user;

    @Column(columnDefinition="text", unique = true, nullable = false)
    private String authToken;

    @Column(nullable = false)
    private Date authTokenDate;

    @Column(columnDefinition="text", nullable = false)
    private String authTokenUUID;

    private boolean destroyed = false;

    public String getAuthToken() {
        return authToken;
    }

    public String getAuthTokenUUID() {
        return authTokenUUID;
    }

    public Date getAuthTokenDate() {
        return authTokenDate;
    }

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public Date getDestroyTimestamp() {
        return destroyTimestamp;
    }

    public String createAuthToken() {
        if(user != null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, AUTHENTICATION_TOKEN_EXPIRATION_HOURS);

            authTokenDate = Calendar.getInstance().getTime();
            authTokenUUID = UUID.randomUUID().toString();

            // Authentication token
            JwtBuilder jwtb = Jwts.builder();
            jwtb.setSubject(user.getEmail());
            jwtb.setIssuer(JWT_ISSUER);
            jwtb.setExpiration(cal.getTime());
            jwtb.setIssuedAt(authTokenDate);
            jwtb.setId(authTokenUUID);

            String cryptoSecret = ConfigurationHelper.getConfigurationString(CRYPTO_KEY_MARSI);
            authToken = jwtb.signWith(SignatureAlgorithm.HS512, cryptoSecret).compact();

            this.save();

            return authToken;
        } else {
            return null;
        }
    }

    public void destroyAuthToken() {
        destroyed = true;
        destroyTimestamp = new Date();

        this.save();
    }

    public boolean isAuthTokenExpired() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, -AUTHENTICATION_TOKEN_EXPIRATION_HOURS);

        return authTokenDate.before(cal.getTime());
    }

    public static List<AuthToken> findByUser(UserAccount user) {
        return FIND.where().eq("user_id", user.getId()).findList();
    }

    public static List<AuthToken> findActiveByUser(UserAccount user) {
        return (FIND
                .where()
                .eq("user_id", user.getId())
                .eq("destroyed", false)
        ).findList();
    }

    public static AuthToken findByAuthToken(String authToken) {
        return FIND.where().eq("auth_token", authToken).findUnique();
    }
}

