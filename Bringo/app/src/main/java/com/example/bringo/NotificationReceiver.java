package com.example.bringo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

/**
 * Created by xuyidi on 4/7/17.
 */

public class NotificationReceiver extends BroadcastReceiver {
    public static String NtfTitle = "";
    public static String NtfContent = "";

    public static int[] repeatDates = new int[0];
    @Override
    /*
     * onReceive() is calles when NotificationReceiver class gets triggered
     * set the notification in this method
     */
    public void onReceive(Context context, Intent intent) {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_WEEK);
        //modified

        //if repeatDate.length > 0, meaning repeatDates are set for repeating;
        if (repeatDates.length > 0) {
            for (int i = 0; i < repeatDates.length; i++) {
                if(day == repeatDates[i]) {
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                            Context.NOTIFICATION_SERVICE);
                    Intent notificationClick_intent = new Intent(context, HomeActivity.class);
                    notificationClick_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    // Notification service requires a pendingIntent
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 101,
                            notificationClick_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                    builder.setContentIntent(pendingIntent);
                    builder.setSmallIcon(R.drawable.notification);
                    builder.setContentTitle(NotificationReceiver.NtfTitle);
                    builder.setContentText(NotificationReceiver.NtfContent);

                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    builder.setSound(alarmSound);

                    // make notification disappear when user swipes the notification
                    builder.setAutoCancel(true);

                    notificationManager.notify(101, builder.build());
                }

            }
        }
        // repeatDates are not set, not repeat
        else {
            if (day != 0) {
                System.out.println("************************* i am working.");
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE);
                Intent notificationClick_intent = new Intent(context, HomeActivity.class);
                notificationClick_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Notification service requires a pendingIntent
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 101,
                        notificationClick_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setContentIntent(pendingIntent);
                builder.setSmallIcon(R.drawable.notification);
                builder.setContentTitle(NotificationReceiver.NtfTitle);
                builder.setContentText(NotificationReceiver.NtfContent);

                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setSound(alarmSound);

                // make notification disappear when user swipes the notification
                builder.setAutoCancel(true);

                notificationManager.notify(101, builder.build());
            }
        }
    }

    public static void updateNotification(String title, String content){
        NotificationReceiver.NtfTitle = title;
        NotificationReceiver.NtfContent = content;
    }

    public static void setRepeatDate(int[] dates){
        NotificationReceiver.repeatDates = dates;
    }
}
