package models;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by brecht on 9/9/16.
 */
public class ExpirationTimeHelper {
    public static boolean isExpired(Date expirationDate, int expirationTime) {
        return isExpired(expirationDate, expirationTime, Calendar.HOUR_OF_DAY);
    }

    public static boolean isExpired(Date expirationDate, int expirationTime, int timeUnit) {
        return expirationDate != null && expirationDate.before(expirationTime(expirationTime, timeUnit));
    }

    public static Date expirationTime(int expirationTime, int timeUnit) {
        Calendar cal = Calendar.getInstance();

        cal.add(timeUnit, -expirationTime);
        return cal.getTime();
    }
}
