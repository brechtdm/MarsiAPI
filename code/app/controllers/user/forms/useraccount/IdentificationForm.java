package controllers.user.forms.useraccount;

import play.data.validation.Constraints;

/**
 * Created by brecht on 9/10/16.
 */
public class IdentificationForm {

    @Constraints.Required
    private String user;

    @Constraints.Required
    private String password;

    public IdentificationForm() {

    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}