package util.annotations.authentication;

import controllers.user.SecurityController;
import models.user.AuthToken;
import play.Logger;
import util.ConfigurationHelper;
import io.jsonwebtoken.*;
import models.user.UserAccount;
import models.user.UserRole;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class Authenticator extends Action<Authentication> {

    public CompletionStage<Result> call(Http.Context context) {
        // Get the user based on authentication token (and/or impersonating values)
        UserAccount user = checkAuthentication(context);

        // Check if a user is logged in
        if (user != null) {
            // Get all the roles of a user
            List<UserRole.Role> userRoles = UserRole.toRoleList(UserRole.findByUser(user));

            // Check if user has an allowed role
            UserRole.Role[] allowedRoles = configuration.value();
            for(UserRole.Role role : allowedRoles)  {
                // User is authorized to perform action
                if(userRoles.contains(role)) {
                    return delegate.call(context);
                }
            }
        }

        // User is unauthorized
        return CompletableFuture.completedFuture(unauthorized());
    }

    public static UserAccount checkAuthentication(Http.Context context) {
        // Get the token value
        String[] authTokenHeaderValues = context.request().headers().get(SecurityController.AUTH_TOKEN_HEADER);
        if(!checkValidityAuthHeader(authTokenHeaderValues)) {
            return null;
        }

        // Get the token
        AuthToken authToken = AuthToken.findByAuthToken(authTokenHeaderValues[0]);

        if(authToken == null) {
            return null;
        }

        // Check the tokens if it is valid
        if(!checkJWT(authToken)) {
            return null;
        }

        return authToken.getUser();
    }

    private static boolean checkValidityAuthHeader(String[] authTokenHeaderValues) {
        if(authTokenHeaderValues == null) {
            return false;
        }

        if(authTokenHeaderValues.length != 1 || authTokenHeaderValues[0] == null) {
            return false;
        }

        return true;
    }

    private static boolean checkJWT(AuthToken authToken) {
        if(authToken.isAuthTokenExpired()) {
            return false;
        }

        Jws<Claims> claims = parseJWT(authToken.getAuthToken());

        // Double check subject
        if(!claims.getBody().getSubject().equals(authToken.getUser().getEmail())) {
            return false;
        }
        // Double check id
        if(!claims.getBody().getId().equals(authToken.getAuthTokenUUID())) {
            return false;
        }
        // Double check issuer
        if(!claims.getBody().getIssuer().equals(AuthToken.JWT_ISSUER)) {
            return false;
        }

        return true;
    }

    private static Jws<Claims> parseJWT(String authToken) {
        String cryptoSecret = ConfigurationHelper.getConfigurationString(AuthToken.CRYPTO_KEY_MARSI);
        Jws<Claims> claims;

        // Parse JWT
        try {
            claims = Jwts.parser().setSigningKey(cryptoSecret).parseClaimsJws(authToken);
        } catch(InvalidClaimException e) {
            Logger.info("Evaluation JWT failed", e);
            return null;
        } catch(ExpiredJwtException e) {
            Logger.info("JWT expired", e);
            return null;
        }

        return claims;
    }
}

