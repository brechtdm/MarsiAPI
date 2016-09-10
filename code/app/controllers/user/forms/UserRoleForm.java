package controllers.user.forms;

import models.user.UserAccount;
import models.user.UserRole;
import play.data.validation.Constraints;

public class UserRoleForm {

    @Constraints.Required
    private Long userId;

    @Constraints.Required
    private UserRole.Role userRole;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserRole.Role getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole.Role userRole) {
        this.userRole = userRole;
    }

    public UserRole toUserRole() {
        UserAccount user = UserAccount.findById(userId);

        UserRole createdUserRole = new UserRole();
        createdUserRole.setRole(this.userRole);
        createdUserRole.setUser(user);

        return createdUserRole;
    }
}
