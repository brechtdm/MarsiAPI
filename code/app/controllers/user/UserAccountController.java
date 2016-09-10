package controllers.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.user.forms.useraccount.CreateForm;
import models.user.UserAccount;
import org.apache.commons.mail.EmailException;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import play.mvc.BodyParser;
import play.mvc.Result;
import util.json.InvalidJsonException;
import util.json.JsonHelper;
import util.json.ResultCode;
import util.json.results.DataJsonResult;
import util.json.results.ErrorJsonResult;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
            JsonNode strippedBody = JsonHelper.fetchFormData(body, UserAccount.class);
            Form<CreateForm> filledUserForm = formFactory.form(CreateForm.class).bind(strippedBody);

            // Return bad request if validation failed
            if (filledUserForm.hasErrors()) {
                ObjectNode errors = (ObjectNode) filledUserForm.errorsAsJson();
                ErrorJsonResult result = new ErrorJsonResult(ResultCode.BAD_REQUEST, "INVALID_JSON");

                // TODO to controller helper
                ObjectMapper mapper = new ObjectMapper();
                Iterator<String> errorIterator = errors.fieldNames();
                while(errorIterator.hasNext()) {
                    String error = errorIterator.next();
                    // Create sub-error list
                    List<String> subErrorList = new ArrayList<>();
                    errors.get(error).forEach(e -> subErrorList.add(e.asText()));
                    // Add sub-error list to result
                    result.addSubError(subErrorList.toArray(new String[subErrorList.size()]), error);
                }

                return badRequest(result.toJson());
            }

            // Form does not contain errors, so we can create the user
            UserAccount createdUserAccount = filledUserForm.get().getUserAccount();
            //createdUserAccount.save();

            try {
                sendRegistrationKey(createdUserAccount);
            } catch (EmailException e) {
                ErrorJsonResult result = new ErrorJsonResult(ResultCode.INTERNAL_SERVER_ERROR, "MAIL_FAILED");
                return internalServerError(result.toJson());
            }

            // Return the saved user
            DataJsonResult result = new DataJsonResult(ResultCode.CREATED, "userAccount", (ObjectNode) Json.toJson(createdUserAccount));
            return created(result.toJson());
        } catch(InvalidJsonException ex) {
            ErrorJsonResult result = new ErrorJsonResult(ResultCode.BAD_REQUEST, "INVALID_JSON");
            return badRequest(result.toJson());
        }
    }

    public Result validateRegistrationKey(Long id, String registrationKey) {
        UserAccount user = UserAccount.findById(id);

        // Check if user exists
        if(user == null) {
            ErrorJsonResult result = new ErrorJsonResult(ResultCode.NOT_FOUND, "USER_NOT_FOUND");
            return notFound(result.toJson());
        }

        // Check if registration key is used
        if(user.getRegistrationKey() == null) {
            ErrorJsonResult result = new ErrorJsonResult(ResultCode.BAD_REQUEST, "REGISTRATION_KEY_USED");
            return badRequest(result.toJson());
        }

        // Check if registration key is valid
        if(!user.getRegistrationKey().equals(registrationKey)) {
            ErrorJsonResult result = new ErrorJsonResult(ResultCode.BAD_REQUEST, "REGISTRATION_KEY_INVALID");
            return badRequest(result.toJson());
        }

        // Check if registration key is expired
        if(user.isRegistrationKeyExpired()) {
            user.createRegistrationKey();
            user.save();

            try {
                sendRegistrationKey(user);
            } catch (EmailException e) {
                ErrorJsonResult result = new ErrorJsonResult(ResultCode.INTERNAL_SERVER_ERROR, "MAIL_FAILED");
                return internalServerError(result.toJson());
            }

            ErrorJsonResult result = new ErrorJsonResult(ResultCode.BAD_REQUEST, "REGISTRATION_KEY_EXPIRED");
            return badRequest(result.toJson());
        }

        user.destroyRegistrationKey();
        user.save();

        // TODO set new role (MEMBER role)

        DataJsonResult result = new DataJsonResult(ResultCode.OK, "REGISTRATION_KEY_VALIDATED");
        return ok(result.toJson());
    }

    // TODO change id to something else?
    public Result resendRegistrationKey(Long id) {
        try {
            UserAccount user = UserAccount.findById(id);

            if(user == null) {
                ErrorJsonResult result = new ErrorJsonResult(ResultCode.NOT_FOUND, "USER_NOT_FOUND");
                return notFound(result.toJson());
            }

            sendRegistrationKey(user);

            DataJsonResult result = new DataJsonResult(ResultCode.OK, "REGISTRATION_KEY_MAIL_RESEND");
            return ok(result.toJson());
        } catch (EmailException e) {
            ErrorJsonResult result = new ErrorJsonResult(ResultCode.INTERNAL_SERVER_ERROR, "MAIL_FAILED");
            return internalServerError(result.toJson());
        }
    }

    private void sendRegistrationKey(UserAccount userAccount) throws EmailException {
        String url = new StringBuilder()
                .append("http://localhost:9000/users/")
                .append(userAccount.getId())
                .append("/register?regKey=")
                .append(userAccount.getRegistrationKey())
                .toString();

        String bodyText = url;
        String bodyHtml = url;

        String subject = "Registration to Marsi";
        String to = userAccount.getEmail();
        String from = "Marsi <noreply@marsi.com>";

        Email email = new Email()
                .setSubject(subject)
                .setFrom(from)
                .addTo(to)
                .setBodyText(bodyText)
                .setBodyHtml(bodyHtml);
        String id = mailerClient.send(email);

        System.out.println("REGISTRATION KEY URL: " + url + "\n"
                + "REGISTRATION KEY: " + userAccount.getRegistrationKey());
    }

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
