package controllers.user.forms.UserAccount;

import models.user.UserAccount;
import play.data.validation.Constraints;
import util.validators.UserConstraints;

public class CreateForm {

    @Constraints.Required
    @Constraints.MaxLength(UserAccount.MAX_EMAIL_SIZE)
    @UserConstraints.EmailPattern
    @UserConstraints.UniqueEmail
    private String email;

    @Constraints.Required
    @Constraints.Pattern(UserAccount.USERNAME_REGEX)
    @Constraints.MaxLength(UserAccount.MAX_USERNAME_SIZE)
    @UserConstraints.UniqueUsername
    private String username;

    @Constraints.Required
    @UserConstraints.PasswordPattern
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
