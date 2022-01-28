package com.xera.notetify.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.feature.service.AuthService;
import com.xera.notetify.Controller.NotetifySecurityManager;
import com.xera.notetify.Database.DatabaseNote;
import com.xera.notetify.Database.DatabaseReminder;
import com.xera.notetify.Model.NoteModel;
import com.xera.notetify.Model.ReminderModel;
import com.xera.notetify.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


public class NoteViewActivity extends AppCompatActivity {

    private final String TAG = "NoteViewActivity";
    private TextView txtViewReminder, txtViewDate;
    private DatabaseNote databaseNote;
    private DatabaseReminder databaseReminder;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private MaterialToolbar toolbarNoteView;
    private EditText edtTextTitle, edtTextNoteContent;
    private Boolean isPrivacyLockSet = false;
    private Boolean isReminderSet = false;
    private Boolean isNoteSaved = false;
    private int noteID;
    private String uniqueID = "";
    NoteModel currentNoteModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);

        //retrieve data from NoteActivity
        Intent intent = getIntent();
        noteID = intent.getIntExtra("noteID", -1);

        initDatabase();
        initViews();
        initBottomNav();
        initData();
    }

    private void initDatabase() {
        databaseNote = new DatabaseNote(NoteViewActivity.this, "notetify_note.db");
        databaseReminder = new DatabaseReminder(NoteViewActivity.this);
    }

    private void initViews(){
        toolbarNoteView = findViewById(R.id.toolbarNoteView);
        setSupportActionBar(toolbarNoteView);
        toolbarNoteView.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close activity when user click back button
                finish();
            }
        });
        edtTextTitle = findViewById(R.id.edtTextNoteTitle);
        edtTextNoteContent = findViewById(R.id.edtTextNoteContent);
        txtViewDate = findViewById(R.id.txtViewDate);
        txtViewReminder = findViewById(R.id.txtViewReminder);
    }

    private void initBottomNav(){
        bottomNavigationView =findViewById(R.id.bottom_navigation);
        Menu botMenu = bottomNavigationView.getMenu();

        if(isPrivacyLockSet){
            botMenu.findItem(R.id.function_privacy_lock).setIcon(R.drawable.lock);
            botMenu.findItem(R.id.function_privacy_lock).setTitle("Lock");
        }else{
            botMenu.findItem(R.id.function_privacy_lock).setIcon(R.drawable.unlock);
            botMenu.findItem(R.id.function_privacy_lock).setTitle("Unlock");
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.function_done:
                        if (edtTextTitle.getText().toString().equals("") || edtTextNoteContent.getText().toString().equals("")){
                            Toast.makeText(NoteViewActivity.this, "Empty field in title or notes", Toast.LENGTH_SHORT).show();
                        }
                        else if (noteID == -1){
                            addNewNote(); //create a new note into the database if the position is -1
                        }else{
                            updateNote(); //update the existing note if the position is not -1
                        }
                        return true;
                    case R.id.function_reminder:
                        setReminder();
                        return true;
                    case R.id.function_delete:
                        deleteNote();
                        return true;
                    case R.id.function_privacy_lock:
                        setPrivacyLock(item);
                        return true;
                }
                return false;
            }
        });
    }

    private void updateNote() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy   hh:mm aa", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        NoteModel noteModel = new NoteModel(noteID, uniqueID, currentDateTime, edtTextTitle.getText().toString(), edtTextNoteContent.getText().toString(), isPrivacyLockSet);
        boolean updateStatus = databaseNote.updateNote(noteModel);

        if (updateStatus){
            NoteActivity.refreshNoteList(this);
            finish(); //Kill this activity and back to note list activity
        }else{
            Toast.makeText(NoteViewActivity.this,TAG + ": Error updating into database", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNewNote() {
        if(isNoteSaved){
            //note is saved in database
            finish();
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy   hh:mm aa", Locale.getDefault());
            String currentDateTime = sdf.format(new Date());
            NoteModel noteModel = new NoteModel(-1, uniqueID, currentDateTime, edtTextTitle.getText().toString(), edtTextNoteContent.getText().toString(), isPrivacyLockSet);

            boolean insertStatus = databaseNote.addNote(noteModel);

            if (insertStatus){
                NoteActivity.refreshNoteList(this);
                finish(); //Kill this activity and back to note list activity
            }else{
                Toast.makeText(NoteViewActivity.this,TAG + ": Error inserting into database", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //display a alert dialog with edit text to input date time. when user click on date time it will
    //direct user to click date time picker
    private void setReminder() {
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View promptView = layoutInflater.inflate(R.layout.date_time_dialog, null);
        final EditText edtTextDate = promptView.findViewById(R.id.date_input);
        final EditText edtTextTime = promptView.findViewById(R.id.time_input);
        edtTextDate.setOnClickListener(view -> {
            if (edtTextTime.getText().toString().equals("")){
                showTimeDialog(edtTextTime);
            }
            showDateDialog(edtTextDate);
        });

        edtTextTime.setOnClickListener(view -> showTimeDialog(edtTextTime));

        AlertDialog reminderDialog = new AlertDialog.Builder(this)
                .setMessage("Enter your date and time")
                .setTitle("Set reminder")
                .setCancelable(false)
                .setPositiveButton("SET", null)
                .setNegativeButton("CANCEL", null)
                .setView(promptView)
                .create();

        reminderDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btn_set = ((AlertDialog) reminderDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                btn_set.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //If note id is -1 save note into database then proceed set reminder
                        if(noteID == -1){
                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy   hh:mm aa", Locale.getDefault());
                            String currentDateTime = sdf.format(new Date());
                            NoteModel noteModel = new NoteModel(-1, uniqueID, currentDateTime, edtTextTitle.getText().toString(), edtTextNoteContent.getText().toString(), isPrivacyLockSet);

                            databaseNote.addNote(noteModel);
                            ArrayList<NoteModel> noteModelArrayList = databaseNote.getAllData();
                            noteModel = noteModelArrayList.get(noteModelArrayList.size()-1);
                            uniqueID = noteModel.getUniqueID();
                            isNoteSaved = true;
                        }

                        //if there is no date input and time input, then show a error toast message
                        //else proceed set reminder
                        if(edtTextDate.getText().toString().equals("")||edtTextTime.getText().toString().equals("")){
                            Toast.makeText(NoteViewActivity.this, "Empty date time input", Toast.LENGTH_SHORT).show();
                        }else{
                            String dateTimeString =  edtTextDate.getText().toString()+ " " + edtTextTime.getText().toString();

                            //for checking date time validity
                            long dateTimeInMillis = dateTimeToMillis(dateTimeString);
                            if (dateTimeInMillis == -1){
                                Toast.makeText(NoteViewActivity.this, "Invalid date time input", Toast.LENGTH_SHORT).show();
                            }else {

                                //insert data into reminder database
                                DatabaseReminder databaseReminder = new DatabaseReminder(NoteViewActivity.this);
                                ReminderModel reminderModel = new ReminderModel(-1, dateTimeString, uniqueID);
                                if(isReminderSet){
                                    databaseReminder.updateReminder(uniqueID, reminderModel);
                                }else{
                                    //create new reminder in database
                                    boolean insertReminderStatus = databaseReminder.addReminder(reminderModel);
                                }
                                //Dismiss once everything is OK.
                                reminderDialog.dismiss();
                                checkIsReminderSet();
                            }
                        }
                    }
                });

                Button btn_cancel = ((AlertDialog) reminderDialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                btn_cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                            //Dismiss once everything is OK.
                            reminderDialog.dismiss();

                        }
                    });
                }
            });
            reminderDialog.show();
            reminderDialog.getWindow().setGravity(Gravity.BOTTOM);
        }

    /**
     * This method is used to convert date and time to millisecond.
     * If this method receive an error input it will return a -1 value.
     * @param dateTimeString
     * @return
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


    private void showDateDialog(final EditText date_in) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd MMMM yyyy");
                date_in.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };

        new DatePickerDialog(NoteViewActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimeDialog(final EditText time_in) {
        final Calendar calendar=Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar.set(Calendar.MINUTE,minute);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm aa");
                time_in.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };

        new TimePickerDialog(NoteViewActivity.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
    }

    private void deleteNote(){
        if(noteID != -1){
            boolean deleteStatus = databaseNote.deleteNote(currentNoteModel);
            if(deleteStatus){
                Toast.makeText(NoteViewActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                NoteActivity.refreshNoteList(this);
                finish(); //Kill this activity and back to note list activity
            }else{
                Toast.makeText(NoteViewActivity.this, "Delete note fail", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setPrivacyLock(MenuItem item){
        NotetifySecurityManager notetifySecurityManager = new NotetifySecurityManager();
        
        //To Check If The Security is Set Up
        Boolean isPrivacySecuritySet = notetifySecurityManager.checkPrivacySecurityDB(NoteViewActivity.this);
        if(isPrivacySecuritySet) {
            isPrivacyLockSet = !isPrivacyLockSet;
            if(isPrivacyLockSet){
                item.setIcon(R.drawable.lock);
                item.setTitle("Locked");
            }else{
                item.setIcon(R.drawable.unlock);
                item.setTitle("Unlocked");
            }
        }else{
            Intent setPrivacyLockIntent = new Intent(NoteViewActivity.this, SetPrivacyLockActivity.class);
            startActivity(setPrivacyLockIntent);
        }

    }

    private void initData(){
        //when user clicked on the notelist the note activity will pass the position data of the note
        //to noteView activity
        if (noteID != -1){
            ArrayList<NoteModel> noteModelArrayList = databaseNote.getAllData();

            for(NoteModel nm: noteModelArrayList){
                if(nm.getNoteId() == noteID){
                    currentNoteModel = nm;
                    uniqueID = nm.getUniqueID();
                }
            }
            edtTextTitle.setText(currentNoteModel.getNoteTitle());
            edtTextNoteContent.setText(currentNoteModel.getNoteContent());
            txtViewDate.setText(String.format("Date:  %s", currentNoteModel.getDateCreated()));
            isPrivacyLockSet = currentNoteModel.getPrivacyLockStatus();
            //Check reminder database
            checkIsReminderSet();
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy   hh:mm aa", Locale.getDefault());
            String currentDateTime = sdf.format(new Date());
            txtViewDate.setText(String.format("Date:  %s", currentDateTime));
            txtViewReminder.setVisibility(View.GONE);
            generateUniqueID();
        }
    }

    private void generateUniqueID() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyhhmm", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        uniqueID = currentDateTime + "-" + UUID.randomUUID().toString();
    }

    private void checkIsReminderSet() {
        ArrayList<ReminderModel> reminderModelArrayList = databaseReminder.getAllData();

        for(ReminderModel reminderModel: reminderModelArrayList){
            if(reminderModel.getUniqueID().equals(uniqueID)){
                txtViewReminder.setVisibility(View.VISIBLE);
                txtViewReminder.setText(String.format("Reminder: %s", reminderModel.getDateReminder()));
                isReminderSet = true;
            }
        }
        if(!isReminderSet){
            txtViewReminder.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }

}