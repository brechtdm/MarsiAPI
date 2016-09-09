package controllers.user.forms.useraccount;

import play.data.validation.Constraints;
import util.validators.UserConstraints;

/**
 * Created by brecht on 9/9/16.
 */
public class PasswordResetForm {

    @Constraints.Required
    private String token;

    @Constraints.Required
    @UserConstraints.PasswordPattern
    private String newPassword;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
