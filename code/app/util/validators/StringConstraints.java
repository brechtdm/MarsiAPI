package util.validators;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import play.data.validation.Constraints;
import play.libs.F;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static play.libs.F.Tuple;

/**
 * Created by brecht on 24.05.16.
 */
public class StringConstraints {

    @Target({FIELD})
    @Retention(RUNTIME)
    @Constraint(validatedBy = URLValidator.class)
    public static @interface URL {
        String message() default URLValidator.message;

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    public static class URLValidator extends Constraints.Validator<String> implements ConstraintValidator<URL, String> {
        public static final String message = "error.invalid";

        // URL Validation
        private static final String[] schemes = { "http", "https" };
        private static final UrlValidator urlValidator = new UrlValidator(schemes);
        private static final int MAX_URL_SIZE = 1000;

        public URLValidator() {
        }

        public void initialize(URL constraintAnnotation) {
        }

        public boolean isValid(String object) {
            return (object != null
                    && object.length() < MAX_URL_SIZE
                    && urlValidator.isValid(StringEscapeUtils.escapeHtml4(object)));
        }

        public F.Tuple<String, Object[]> getErrorMessageKey() {
            return Tuple(message, new Object[] { });
        }
    }

    public static Constraints.Validator<String> url() {
        return new URLValidator();
    }

    @Target({FIELD})
    @Retention(RUNTIME)
    @Constraint(validatedBy = SanitizedTextValidator.class)
    public static @interface SanitizedText {
        String message() default SanitizedTextValidator.message;

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    public static class SanitizedTextValidator extends Constraints.Validator<String> implements ConstraintValidator<SanitizedText, String> {
        public static final String message = "error.invalid";

        public SanitizedTextValidator() {
        }

        public void initialize(SanitizedText constraintAnnotation) {
        }

        public boolean isValid(String object) {
            if(object != null) {
                // Check if value contains XSS or HTML tags
                String cleanedValue = Jsoup.parse(Jsoup.clean(object, Whitelist.basic())).text();
                if (!object.toString().equals(cleanedValue)) {
                    return false;
                }
            }

            return true;
        }

        public F.Tuple<String, Object[]> getErrorMessageKey() {
            return Tuple(message, new Object[] { });
        }
    }

    public static Constraints.Validator<String> sanitizedText() {
        return new SanitizedTextValidator();
    }
}
