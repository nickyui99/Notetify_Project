package com.xera.notetify.Model;

import java.util.Date;

public class ReminderModel {
    private int reminderID;
    private String dateReminder;
    private String uniqueID;

    public ReminderModel() {
        reminderID = -1;
        dateReminder = null;
        uniqueID = null;
    }

    public ReminderModel(int reminderID, String dateReminder, String uniqueID) {
        this.reminderID = reminderID;
        this.dateReminder = dateReminder;
        this.uniqueID = uniqueID;
    }

    public int getReminderID() {
        return reminderID;
    }

    public void setReminderID(int reminderID) {
        this.reminderID = reminderID;
    }

    public String getDateReminder() {
        return dateReminder;
    }

    public void setDateReminder(String dateReminder) {
        this.dateReminder = dateReminder;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }
}
