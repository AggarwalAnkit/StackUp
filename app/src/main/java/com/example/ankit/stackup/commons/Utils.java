package com.example.ankit.stackup.commons;

import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.ankit.stackup.R;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static String formatTimeStamp(long timeToFormat) {

        Long currentSystemTime = System.currentTimeMillis();
        Long timeDiff = currentSystemTime - timeToFormat * 1000;

        if (timeDiff < (60 * 1000)) {
            return "few seconds back";
        } else if (timeDiff < (60 * 60 * 1000)) {
            int minuteDifference = (int) (timeDiff / (60 * 1000));
            return minuteDifference == 1 ? minuteDifference + " minute back" : minuteDifference + " minutes back";
        } else if (timeDiff < (24 * 60 * 60 * 1000)) {
            int hourDifference = (int) (timeDiff / (60 * 60 * 1000));
            return hourDifference == 1 ? hourDifference + " hour back" : hourDifference + " hours back";
        } else if (timeDiff < (48 * 60 * 60 * 1000)) {
            return "Yesterday";
        }

        return getDateFromTimestamp(timeToFormat, "MMM d, yyyy");
    }

    public static String getDateFromTimestamp(long timestamp, String dateFormat) {
        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.getDefault());
        dateFormatSymbols.setAmPmStrings(new String[]{"am", "pm"});
        simpleDateFormat.setDateFormatSymbols(dateFormatSymbols);
        return simpleDateFormat.format(date);
    }

    public static void openLink(Context context, String link) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));

        Bundle startBundle = null;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Bundle extras = new Bundle();

            //chrome custom tab
            extras.putBinder(Constants.EXTRA_CUSTOM_TABS_SESSION, null);
            intent.putExtras(extras);

            //tab color
            intent.putExtra(Constants.EXTRA_CUSTOM_TABS_TOOLBAR_COLOR, context.getResources().getColor(R.color.colorPrimary));

            //finish animation
            Bundle finishBundle = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_right).toBundle();
            intent.putExtra(Constants.EXTRA_CUSTOM_TABS_EXIT_ANIMATION_BUNDLE, finishBundle);

            //start animation
            startBundle = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
        }

        try {
            context.startActivity(intent, startBundle);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, Constants.MESSAGE_NO_APP_TO_HANDLE, Toast.LENGTH_SHORT).show();
        }

    }
}
