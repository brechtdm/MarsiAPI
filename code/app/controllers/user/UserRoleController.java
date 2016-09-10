package controllers.user;


import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.user.forms.UserRoleForm;
import models.user.UserAccount;
import models.user.UserRole;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import util.json.InvalidJsonException;
import util.json.JsonHelper;
import util.QueryHelper;
import util.annotations.authentication.Authentication;

import javax.inject.Inject;

public class UserRoleController extends Controller {

    @Inject
    FormFactory formFactory;

    @Authentication({UserRole.Role.USER_ADMIN, UserRole.Role.SUPER_ADMIN})
    public Result create() {
        JsonNode body = request().body().asJson();
        JsonNode strippedBody;

        try {
            strippedBody = JsonHelper.fetchFormData(body, UserRole.class);
            Form<UserRoleForm> filledUserRoleForm = formFactory.form(UserRoleForm.class).bind(strippedBody);

            if(filledUserRoleForm.hasErrors()) {
                return badRequest(filledUserRoleForm.errorsAsJson());
            }

            UserRole createdUserRole = filledUserRoleForm.get().toUserRole();
            createdUserRole.save();

            return created();
        } catch(InvalidJsonException ex) {
            play.Logger.debug(ex.getMessage(), ex);
            return badRequest(ex.getMessage());
        }
    }

    @Authentication({UserRole.Role.USER_ADMIN, UserRole.Role.SUPER_ADMIN})
    public Result delete(Long id) {
        UserRole userRoleToDelete = UserRole.FIND.byId(id);
        if(userRoleToDelete == null) {
            return notFound();
        }

        userRoleToDelete.delete();

        return ok();
    }

    @Authentication({UserRole.Role.USER})
    public Result getAllCurrentUser() {
        UserAccount user = SecurityController.getUser();
        if(user == null) {
            return notFound();
        }

        ExpressionList<UserRole> exp = QueryHelper.buildQuery(UserRole.class, UserRole.FIND.where().eq("user_id", user.getId()));
        return getUserRoles(exp);
    }

    @Authentication({UserRole.Role.MEMBER})
    public Result getAllUser(Long userId) {
        UserAccount user = UserAccount.findById(userId);
        if(user == null) {
            return notFound();
        }

        ExpressionList<UserRole> exp = QueryHelper.buildQuery(UserRole.class, UserRole.FIND.where().eq("user_id", user.getId()));
        return getUserRoles(exp);
    }

    @Authentication({UserRole.Role.MEMBER})
    public Result getAll() {
        ExpressionList<UserRole> exp = QueryHelper.buildQuery(UserRole.class, UserRole.FIND.where());
        return getUserRoles(exp);
    }

    private Result getUserRoles(ExpressionList<UserRole> exp) {
        /*try {
            JsonNode result = JsonHelper.createJsonNodeList(exp.findList(), UserRole.class);

            String[] totalQuery = request().queryString().get("total");
            if (totalQuery != null && totalQuery.length == 1 && totalQuery[0].equals("true")) {
                ExpressionList<UserRole> countExpression = QueryHelper.buildQuery(UserRole.class, UserRole.FIND.where(), true);
                String root = UserRole.class.getAnnotation(JsonRootName.class).value();
                ((ObjectNode) result.get(root)).put("total",countExpression.findRowCount());
            }

            return ok(result);
        } catch(Exception ex) {
            play.Logger.error(ex.getMessage(), ex);
            return internalServerError();
        }*/
        return ok();
    }
}
