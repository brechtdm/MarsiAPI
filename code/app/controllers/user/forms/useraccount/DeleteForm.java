package controllers.user.forms.useraccount;

import play.data.validation.Constraints;

/**
 * Created by brecht on 9/9/16.
 */
public class DeleteForm {
    @Constraints.Required
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
