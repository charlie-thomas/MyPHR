package com.csbgroup.myphr;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.csbgroup.myphr.Login.LoginActivity;

import static com.csbgroup.myphr.R.color.colorAccent;

public class AlarmReceiver extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

//        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
         //Re-set any alarms after reboot here
            //Intent alarmIntent = new Intent(context, AlarmReceiver.class);
//        }

        // Sets action that notification should perform when clicked on
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context.getApplicationContext(), "notify_001");
        Intent notificationIntent = new Intent(context.getApplicationContext(), LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        // Sets notification text
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();

        // Find out if medicine or appointment reminder
        String notificationType = intent.getStringExtra("type");

        if (notificationType.equals("medicine")) {
            // Get name of medicine from medicine details section
            String medicine = intent.getStringExtra("medicine");
            int meddescriptive = intent.getIntExtra("descriptive", 0);

            if (meddescriptive == 0) {
                bigText.bigText("You have a new reminder.");
                bigText.setBigContentTitle("New reminder");
                bigText.setSummaryText("Reminder");
            } else {
                bigText.bigText("Take " + medicine + ".");
                bigText.setBigContentTitle("Medicine reminder");
                bigText.setSummaryText("Medicine");
            }
        }

        if (notificationType.equals("appointment")) {
            // Get name of appointment from appointment details section
            String appointment = intent.getStringExtra("appointment");
            String location = intent.getStringExtra("location");
            String date = intent.getStringExtra("date");
            String time = intent.getStringExtra("time");
            int apptdescriptive = intent.getIntExtra("descriptive", 0);

            if (apptdescriptive == 0) {
                bigText.bigText("You have a new reminder.");
                bigText.setBigContentTitle("New reminder");
                bigText.setSummaryText("Reminder");
            } else {
                bigText.bigText(location + ".");
                bigText.setBigContentTitle(appointment + " - " + date + " " + time);
                bigText.setSummaryText("Appointment");
            }

        }

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_notification);
        mBuilder.setColor(context.getResources().getColor(colorAccent));
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        int id = intent.getIntExtra(NOTIFICATION_ID, 0);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // For newer android version, uses new notification type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(false);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
        }

        // Starts notification
        if (mNotificationManager != null) {
            mNotificationManager.notify(id, mBuilder.build());
        }


        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }
}