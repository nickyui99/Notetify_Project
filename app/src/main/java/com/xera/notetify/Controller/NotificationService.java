package com.xera.notetify.Controller;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.xera.notetify.Activities.DashboardActivity;
import com.xera.notetify.Database.DatabaseNote;
import com.xera.notetify.Database.DatabaseReminder;
import com.xera.notetify.Model.NoteModel;
import com.xera.notetify.Model.ReminderModel;
import com.xera.notetify.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {

    String TAG = "Notification Service";
    private Timer timer;
    private TimerTask timerTask;
    private int counter = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        startTimer();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        CharSequence name = "Reminder Channel";
        String description = "Channel for Reminder";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("notifyNote", name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * This method is used to convert date and time to millisecond.
     * If this method receive an error input it will return a -1 value.
     * @param dateTimeString
     * @return date time in millisecond
     */
    private long dateTimeToMillis(String dateTimeString) {
        long timeInMilliseconds = -1;

        try{
            SimpleDateFormat simpledateformat = new SimpleDateFormat("dd MMMM yyyy hh:mm aa");
            Date dateTime = simpledateformat.parse(dateTimeString);
            timeInMilliseconds = dateTime.getTime();
        }catch (ParseException e){
            e.printStackTrace();
        }

        return timeInMilliseconds;
    }


    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                Log.i(TAG, "=========  "+ (counter++) +" seconds");
                DatabaseReminder databaseReminder = new DatabaseReminder(NotificationService.this);
                ArrayList<ReminderModel> reminderModelArrayList = databaseReminder.getAllData();
                for(ReminderModel reminderModel: reminderModelArrayList){
                    String dateTimeString = reminderModel.getDateReminder();
                    long dateTimeInMillis = dateTimeToMillis(dateTimeString);
                    if(dateTimeInMillis<System.currentTimeMillis()){
                        databaseReminder.deleteReminder(reminderModel);
                    }else{
                        //the time is less than 5 second pop the notification to notify user
                        if(dateTimeInMillis-System.currentTimeMillis()<5000){
                            DatabaseNote databaseNote = new DatabaseNote(NotificationService.this, "notetify_note.db");
                            ArrayList<NoteModel> noteModelArrayList = databaseNote.getAllData();
                            String reminderTitle = "";
                            String reminderContent = "";
                            boolean isNoteFound = false;
                            for(NoteModel noteModel: noteModelArrayList){
                                if(noteModel.getUniqueID().equals(reminderModel.getUniqueID())){
                                    reminderTitle = noteModel.getNoteTitle();
                                    reminderContent = noteModel.getNoteContent();
                                    isNoteFound = true;
                                    break;
                                }
                            }
                            if(isNoteFound){
                                popNotificationReminder(reminderModel.getReminderID(), reminderTitle, reminderContent);
                            }else{
                                databaseReminder.deleteReminder(reminderModel);
                            }

                        }
                    }
                }
            }
        };
        timer.schedule(timerTask, 0, 2500); //Loop for every 5 second
    }

    private void popNotificationReminder(int reminderID, String reminderTitle, String reminderContent) {
        Log.i(TAG, "popNotificationReminder called");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
            Intent notificationServiceIntent  = new Intent(NotificationService.this, ReminderBroadcast.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(NotificationService.this, 0, notificationServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Uri defaultSoundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/raw/mysound");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notifyNote")
                    .setSmallIcon(R.drawable.notetify_logo)
                    .setContentTitle("Reminder: " + reminderTitle)
                    .setContentText(reminderContent)
                    .setSound(defaultSoundUri)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            // Create pending intent, mention the Activity which needs to be
            //triggered when user clicks on notification(StopScript.class in this case)

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, DashboardActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(contentIntent);

            Notification notificationReminder = builder.build();
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(reminderID, notificationReminder);

    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();

        }
        Intent notificationServiceIntent  = new Intent(NotificationService.this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(NotificationService.this, 0, notificationServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notifyNote")
                .setSmallIcon(R.drawable.notetify_logo)
                .setContentTitle("Notetify")
                .setContentText("Notetify running in background to keep you notify")
                .setPriority(NotificationCompat.PRIORITY_LOW);

        Notification notificationReminder = builder.build();
        startForeground(1, notificationReminder);
    }
}