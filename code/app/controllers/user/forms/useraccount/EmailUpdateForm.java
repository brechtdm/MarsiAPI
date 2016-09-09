package controllers.user.forms.useraccount;

import models.user.UserAccount;
import play.data.validation.Constraints;
import util.validators.UserConstraints;

/**
 * Created by brecht on 9/9/16.
 */
public class EmailUpdateForm {
    @Constraints.Required
    private String password;

    @Constraints.Required
    @Constraints.MaxLength(UserAccount.MAX_EMAIL_SIZE)
    @UserConstraints.EmailPattern
    @UserConstraints.UniqueEmail
    private String newEmail;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}
