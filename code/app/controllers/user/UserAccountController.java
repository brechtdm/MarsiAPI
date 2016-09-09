package controllers.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.user.forms.useraccount.CreateForm;
import models.user.UserAccount;
import org.h2.engine.User;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.mailer.MailerClient;
import play.mvc.BodyParser;
import play.mvc.Result;
import util.json.InvalidJsonException;
import util.json.JsonHelper;
import util.json.ResultCode;
import util.json.results.DataJsonResult;
import util.json.results.ErrorJsonResult;

import javax.inject.Inject;

import static play.mvc.Controller.request;
import static play.mvc.Results.*;

public class UserAccountController {

    @Inject
    MailerClient mailerClient;
    @Inject
    FormFactory formFactory;

    //================================================================================
    //region GET

    public Result get(Long id) {
        return getUser(UserAccount.findById(id));
    }

    public Result getCurrentUser() {
        return null;
        //return getUser(SecurityController.getUser());
    }

    private Result getUser(UserAccount user) {
        if(user == null) {
            ErrorJsonResult result = new ErrorJsonResult(ResultCode.NOT_FOUND, "USER_NOT_FOUND");
            return notFound(result.toJson());
        }

        DataJsonResult result = new DataJsonResult(ResultCode.OK, "userAccount", (ObjectNode) Json.toJson(user));
        return ok(result.toJson());
    }

    public Result getAll() {
        return null;
    }

    //endregion
    //================================================================================

    //================================================================================
    //region CREATE

    @BodyParser.Of(BodyParser.Json.class)
    public Result create() {
        JsonNode body = request().body().asJson();


        try {
            JsonNode strippedBody;
            strippedBody = JsonHelper.fetchFormData(body, UserAccount.class);
            Form<CreateForm> filledUserForm = formFactory.form(CreateForm.class).bind(strippedBody);

            // Return bad request if validation failed
            if (filledUserForm.hasErrors()) {
                return badRequest(filledUserForm.errorsAsJson());
            }

            // Form does not contain errors, so we can create the user
            UserAccount createdUser = filledUserForm.get().getUserAccount();
            createdUser.save();

            try {
                sendRegistrationKey(createdUser);
            } catch (EmailException e) {
                return internalServerError();
            }

            // Return the saved user
            return created(JsonHelper.createJsonNode(createdUser, User.class));
        } catch(InvalidJsonException ex) {
            return badRequest();
        }
        return null;
    }

    // Validate registrationKey
    // Resend registrationKey

    //endregion
    //================================================================================

    //================================================================================
    //region DELETE

    public Result deleteCurrentUser() {
        /*User userToDelete = SecurityController.getUser();
        if(userToDelete == null) {
            return notFound();
        }

        userToDelete.delete();*/
        return ok();
    }

    public Result delete(Long id) {
        /*User userToDelete = User.findById(id);
        if(userToDelete == null) {
            return notFound();
        }

        boolean isSuperAdmin = UserRole.findByUser(SecurityController.getUser()).contains(UserRole.Role.SUPER_ADMIN);
        boolean userToDeleteIsAdmin = UserRole.isAdmin(userToDelete);

        if(userToDeleteIsAdmin) {
            if(isSuperAdmin) {
                userToDelete.delete();
                return ok();
            }

            return forbidden();
        } else {
            userToDelete.delete();
            return ok();
        }
        */
        return null;
    }

    //endregion
    //================================================================================

    //================================================================================
    //region UPDATE

    // Update password
    // Reset password
    // Update username
    // Update email

    //endregion
    //================================================================================
}
