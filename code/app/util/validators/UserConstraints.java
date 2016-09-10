package util.validators;

import models.user.UserAccount;
import org.passay.*;
import play.data.validation.Constraints;
import play.libs.F;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static play.libs.F.Tuple;

/**
 * Created by brecht on 23.05.16.
 */
public class UserConstraints {

    @Target({FIELD})
    @Retention(RUNTIME)
    @Constraint(validatedBy = UniqueEmailValidator.class)
    public static @interface UniqueEmail {
        String message() default UniqueEmailValidator.message;

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    /**
     * Validator for <code>@Email</code> fields.
     */
    public static class UniqueEmailValidator extends Constraints.Validator<String> implements ConstraintValidator<UniqueEmail, String> {
        public static final String message = "error.unique";

        public UniqueEmailValidator() {
        }

        public void initialize(UniqueEmail constraintAnnotation) {
        }

        public boolean isValid(String object) {
            return (object != null && UserAccount.findByEmail(object) == null);
        }

        public F.Tuple<String, Object[]> getErrorMessageKey() {
            return Tuple(message, new Object[] {  });
        }
    }

    public static Constraints.Validator<String> uniqueEmail() {
        return new UniqueEmailValidator();
    }

    @Target({FIELD})
    @Retention(RUNTIME)
    @Constraint(validatedBy = EmailPatternValidator.class)
    public static @interface EmailPattern {
        String message() default EmailPatternValidator.message;

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    public static class EmailPatternValidator extends Constraints.Validator<String> implements ConstraintValidator<EmailPattern, String> {
        public static final String message = "error.invalid";

        public EmailPatternValidator() {
        }

        public void initialize(EmailPattern constraintAnnotation) {
        }

        public boolean isValid(String object) {
            return (object != null && org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(object));
        }

        public F.Tuple<String, Object[]> getErrorMessageKey() {
            return Tuple(message, new Object[] {  });
        }
    }

    public static Constraints.Validator<String> EmailPattern() {
        return new EmailPatternValidator();
    }

    @Target({FIELD})
    @Retention(RUNTIME)
    @Constraint(validatedBy = UniqueUsernameValidator.class)
    public static @interface UniqueUsername {
        String message() default UniqueUsernameValidator.message;

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    public static class UniqueUsernameValidator extends Constraints.Validator<String> implements ConstraintValidator<UniqueUsername, String> {
        public static final String message = "error.unique";

        public UniqueUsernameValidator() {
        }

        public void initialize(UniqueUsername constraintAnnotation) {
        }

        public boolean isValid(String object) {
            return (object != null && UserAccount.findByUsername(object) == null);
        }

        public F.Tuple<String, Object[]> getErrorMessageKey() {
            return Tuple(message, new Object[] {  });
        }
    }

    public static Constraints.Validator<String> uniqueUsername() {
        return new UniqueUsernameValidator();
    }

    @Target({FIELD})
    @Retention(RUNTIME)
    @Constraint(validatedBy = PasswordPatternValidator.class)
    @play.data.Form.Display(name = "constraint.password", attributes = {})
    public static @interface PasswordPattern {
        String message() default PasswordPatternValidator.message;

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    public static class PasswordPatternValidator extends Constraints.Validator<String> implements ConstraintValidator<PasswordPattern, String> {
        public static final String message = "error.invalid";

        // - Password validator
        public static final org.passay.PasswordValidator validator;
        // - Characteristics password
        private static final int MIN_PASSWORD_LENGTH            = 8;
        private static final int MAX_PASSWORD_LENGTH            = 256;
        private static final int NM_DIGIT_CHARACTERS            = 1;
        private static final int NM_NON_ALPHANUMERIC_CHARACTERS = 1;
        private static final int NM_UPPER_CASE_CHARACTERS       = 1;
        private static final int NM_LOWER_CASE_CHARACTERS       = 1;
        private static final int MAX_NM_REPEATED_CHARS          = 4;
        // - Usage password rules
        private static final boolean USE_BASIC_CHAR_RULES               = false;
        private static final boolean USE_ALPHABETICAL_SEQUENCE_RULE     = false;
        private static final boolean USE_NUMERICAL_SEQUENCE_RULE        = false;
        private static final boolean USE_USQWERTY_SEQUENCE_RULE         = false;
        private static final boolean USE_REPEAT_CHARACTER_SEQUENCE_RULE = false;
        static {
            // group all rules together in a List
            List<Rule> ruleList = new ArrayList<>();

            // Password must be at least 8 symbols long
            LengthRule lengthRule = new LengthRule(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH);

            // Don't allow whitespace
            WhitespaceRule whitespaceRule = new WhitespaceRule();

            if(USE_BASIC_CHAR_RULES) {
                // Control allowed characters
                CharacterCharacteristicsRule charRule = new CharacterCharacteristicsRule();
                // Require at least 1 digit in passwords
                charRule.getRules().add(new CharacterRule(EnglishCharacterData.Digit, NM_DIGIT_CHARACTERS));
                // Require at least 1 non-alphanumeric char
                charRule.getRules().add(new CharacterRule(EnglishCharacterData.Special, NM_NON_ALPHANUMERIC_CHARACTERS));
                // Require at least 1 upper case char
                charRule.getRules().add(new CharacterRule(EnglishCharacterData.UpperCase, NM_UPPER_CASE_CHARACTERS));
                // Require at least 1 lower case char
                charRule.getRules().add(new CharacterRule(EnglishCharacterData.LowerCase, NM_LOWER_CASE_CHARACTERS));

                ruleList.add(charRule);
            }

            if(USE_ALPHABETICAL_SEQUENCE_RULE) {
                // Don't allow alphabetical sequences
                IllegalSequenceRule alphaSeqRule = new IllegalSequenceRule(EnglishSequenceData.Alphabetical);
                ruleList.add(alphaSeqRule);
            }

            if(USE_NUMERICAL_SEQUENCE_RULE) {
                // Don't allow numerical sequences
                IllegalSequenceRule numSeqRule = new IllegalSequenceRule(EnglishSequenceData.Numerical);
                ruleList.add(numSeqRule);
            }

            if(USE_USQWERTY_SEQUENCE_RULE) {
                // Don't allow qwerty sequences
                IllegalSequenceRule qwertySeqRule = new IllegalSequenceRule(EnglishSequenceData.USQwerty);
                ruleList.add(qwertySeqRule);
            }

            if(USE_REPEAT_CHARACTER_SEQUENCE_RULE) {
                // Don't allow 4 repeat characters
                RepeatCharacterRegexRule repeatRule = new RepeatCharacterRegexRule(MAX_NM_REPEATED_CHARS);
                ruleList.add(repeatRule);
            }

            ruleList.add(lengthRule);
            ruleList.add(whitespaceRule);

            validator = new org.passay.PasswordValidator(ruleList);
        }

        public PasswordPatternValidator() {
        }

        public void initialize(PasswordPattern constraintAnnotation) {
        }

        public boolean isValid(String object) {
            if(object != null) {
                PasswordData passwordData = PasswordData.newInstance(object.toString(), null, null);

                return validator.validate(passwordData).isValid();
            }

            return false;
        }

        public F.Tuple<String, Object[]> getErrorMessageKey() {
            return Tuple(message, new Object[] {  });
        }
    }

    public static Constraints.Validator<String> passwordPattern() {
        return new PasswordPatternValidator();
    }
}
