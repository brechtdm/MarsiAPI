package controllers.user.forms.useraccount;

import play.data.validation.Constraints;
import util.validators.UserConstraints;

/**
 * Created by brecht on 9/9/16.
 */
public class PasswordUpdateForm {
    @Constraints.Required
    private String password;

    @Constraints.Required
    @UserConstraints.PasswordPattern
    private String newPassword;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
