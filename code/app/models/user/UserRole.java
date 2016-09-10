package models.user;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name="UserRole")
@JsonRootName("userRole")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRole extends Model {

    // UserRole finder
    public static final Finder<Long, UserRole> FIND = new Finder<>(UserRole.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role role;

    @CreatedTimestamp
    private Date creationDate;

    @ManyToOne
    public UserAccount user;

    public UserRole(Role role, UserAccount user) {
        setRole(role);
        setUser(user);
    }

    public UserRole() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    public UserAccount getUser() {
        return user;
    }

    public enum Role {
        USER(false),
        MEMBER(false),
        USER_ADMIN(true),
        DEVELOPER(true),
        SUPER_ADMIN(true);

        private boolean admin;
        private Role(boolean admin) {
            this.admin = admin;
        }

        public boolean isAdminRole() {
            return admin;
        }
    }

    public static List<UserRole> findByUser(UserAccount user) {
        return FIND.where().eq("user_id", user.getId()).findList();
    }

    public static UserRole findById(Long id) {
        return FIND.where().eq("id", id).findUnique();
    }

    public static boolean isAdmin(UserAccount user) {
        List<UserRole> userRoles = findByUser(user);
        for(UserRole userRole: userRoles) {
            if(userRole.getRole().isAdminRole()) {
                return true;
            }
        }

        return false;
    }

    public static boolean isRole(UserAccount user, Role role) {
        List<UserRole> userRoles = findByUser(user);
        for(UserRole userRole: userRoles) {
            if(userRole.getRole().equals(role)) {
                return true;
            }
        }

        return false;
    }

    public static List<Role> toRoleList(List<UserRole> userRoleList) {
        return userRoleList.stream().map(UserRole::getRole).collect(Collectors.toList());
    }
}
