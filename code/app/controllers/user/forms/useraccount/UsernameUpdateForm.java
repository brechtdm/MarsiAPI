package controllers.user.forms.useraccount;

import models.user.UserAccount;
import play.data.validation.Constraints;
import util.validators.UserConstraints;

/**
 * Created by brecht on 9/9/16.
 */
public class UsernameUpdateForm {
    @Constraints.Required
    private String password;

    @Constraints.Pattern(UserAccount.USERNAME_REGEX)
    @Constraints.MaxLength(UserAccount.MAX_USERNAME_SIZE)
    @UserConstraints.UniqueUsername
    private String newUsername;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }
}
