package core.minecraft.common.utils;

import java.util.concurrent.TimeUnit;

/**
 * System utilities.
 *
 * @author Preston Brown
 */
public class SystemUtil {

    public static String getWrittenTimeRemaining(long time)
    {
        long remainingTime = time;
        int days = (int)TimeUnit.MILLISECONDS.toDays(remainingTime);
        remainingTime -= (int)TimeUnit.DAYS.toMillis(days);
        int hours = (int)TimeUnit.MILLISECONDS.toHours(remainingTime);
        remainingTime -= (int)TimeUnit.HOURS.toMillis(hours);
        int minutes = (int)TimeUnit.MILLISECONDS.toMinutes(remainingTime);
        remainingTime -= (int)TimeUnit.MINUTES.toMillis(minutes);
        int seconds = (int)TimeUnit.MILLISECONDS.toSeconds(remainingTime);

        StringBuilder stringBuilder = new StringBuilder();
        if (days > 1)
        {
            stringBuilder.append(days + "days ");
        }
        else if (days == 1)
        {
            stringBuilder.append(days + "day ");
        }
        if (hours > 1)
        {
            stringBuilder.append(hours + "hours ");
        }
        else if (hours == 1)
        {
            stringBuilder.append(hours + "hour ");
        }
        if (minutes > 1)
        {
            stringBuilder.append(minutes + "mins ");
        }
        else if (minutes == 1)
        {
            stringBuilder.append(minutes + "min ");
        }
        if (seconds > 1)
        {
            stringBuilder.append(seconds + "secs ");
        }
        else if (seconds == 1)
        {
            stringBuilder.append(seconds + "sec ");
        }
        return stringBuilder.toString().trim();
    }
}
