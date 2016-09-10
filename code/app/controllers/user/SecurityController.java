package controllers.user;

import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.user.forms.useraccount.IdentificationForm;
import models.user.AuthToken;
import models.user.UserAccount;
import models.user.UserRole;
import org.apache.commons.mail.EmailException;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import util.annotations.authentication.Authentication;
import util.annotations.authentication.Authenticator;
import util.exceptions.login.UninvalidatedRegistrationKeyException;

import javax.inject.Inject;

/**
 * Created by brecht on 27.03.16.
 */
public class SecurityController extends Controller {
    // Headers
    // - Authorization
    public static final String AUTH_TOKEN_HEADER = "Authentication";
    public static final String AUTH_TOKEN = "authToken";
    // - Impersonating

    // Errors


    @Inject FormFactory formFactory;

    /**
     *
     * @return The current logged in user
     */
    public static UserAccount getUser() /*throws ImpersonatingException*/ {
        UserAccount user = (UserAccount) Http.Context.current().args.get("user");
        if(user == null) {
            user = Authenticator.checkAuthentication(Http.Context.current());
        }

        return user;
    }

    /**
     *
     * @return
     */
    public Result login() {
        // Check form data
        Form<IdentificationForm> loginForm = formFactory.form(IdentificationForm.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(loginForm.errorsAsJson());
        }

        IdentificationForm login = loginForm.get();
        return loginUser(login);
    }

    private Result loginUser(IdentificationForm loginForm) {
        try {
            // Authenticate the user
            UserAccount user = getUser(loginForm.getUser(), loginForm.getPassword());

            if (user == null) {
                return unauthorized();
            }

            ObjectNode authTokenJson = Json.newObject();
            authTokenJson.put("status", "LOGIN_SUCCESS");
            authTokenJson.put(AUTH_TOKEN, getAuthToken(user));

            return ok(authTokenJson);
        } catch (EmailException e) {
            Logger.info("[User] Login failure", e);
            return internalServerError();
        } catch (UninvalidatedRegistrationKeyException e) {
            Logger.info("[User] Login failure", e);

            ObjectNode loginStatus = Json.newObject();
            loginStatus.put("status", "REGISTRATION_KEY_NOT_INVALIDATED");

            return unauthorized(loginStatus);
        }
    }

    private UserAccount getUser(String user, String password) throws EmailException, UninvalidatedRegistrationKeyException {
        UserAccount email = null;
        UserAccount nickname = null;

        if(user != null && password != null) {
            email = UserAccount.authenticateWithEmail(user, password);
            nickname = UserAccount.authenticateWithUsername(user, password);

            // Should not happen
            if(email != null && nickname != null) {
                return null;
            }
        }

        if(nickname != null) {
            return nickname;
        }
        if(email != null) {
            return email;
        }

        return null;
    }

    private String getAuthToken(UserAccount user) {
        AuthToken authToken = new AuthToken();
        authToken.setUser(user);
        authToken.createAuthToken();

        return authToken.getAuthToken();
    }

    @Authentication({UserRole.Role.USER})
    public Result logout() {
        response().discardCookie(AUTH_TOKEN);

        // Get authentication token
        String[] authTokenHeaderValues = Http.Context.current().request().headers().get(SecurityController.AUTH_TOKEN_HEADER);
        AuthToken authToken = AuthToken.findByAuthToken(authTokenHeaderValues[0]);

        authToken.destroyAuthToken();
        authToken.save();

        ObjectNode logoutStatus = Json.newObject();
        logoutStatus.put("status", "LOGOUT_SUCCESS");

        return ok(logoutStatus);
    }

    @Authentication({UserRole.Role.USER})
    public Result logoutAll() {

        UserAccount user = getUser();
        if(user != null) {
            AuthToken.findActiveByUser(user).stream().forEach(AuthToken::destroyAuthToken);
            return ok();
        } else {
            System.out.println("HEY");
            return unauthorized();
        }
    }
}
