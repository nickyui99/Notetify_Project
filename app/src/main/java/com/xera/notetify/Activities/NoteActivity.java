package com.xera.notetify.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.feature.service.AuthService;
import com.xera.notetify.Controller.NotesAdapter;
import com.xera.notetify.Database.DatabaseFinancialPlanner;
import com.xera.notetify.Database.DatabaseNote;
import com.xera.notetify.Database.DatabaseReminder;
import com.xera.notetify.Database.DatabaseUser;
import com.xera.notetify.Model.NoteModel;
import com.xera.notetify.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class NoteActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "NoteActivity";

    private AuthService mAuthService;
    private AccountAuthParams mAuthParam;
    private ImageView imgViewSwipe;
    private Animation rotateOpenFAB, rotateCloseFAB, fromBottomFAB, toBottomFAB;
    private FloatingActionButton fab_more, fab_add, fab_delete, fab_doneDeleting;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    public static RecyclerView rv_noteList;
    private static ArrayList<NoteModel> noteModelArrayList;
    private NotesAdapter notesAdapter;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseNote databaseNote;
    private ItemTouchHelper itemTouchHelperCallback;
    private ConstraintLayout constraintLayout1;
    private boolean fab_moreClicked = false;
    private boolean imgViewSwipeStatus = false; //false is invisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        initDatabase();
        initViews();
        initDrawer();
        initAnimation();
        initFAB();
        initNoteList();
    }

    private void initDatabase() {
        databaseNote = new DatabaseNote(NoteActivity.this, "notetify_note.db");
    }

    private void initViews() {
        rv_noteList = findViewById(R.id.rv_noteList);
        imgViewSwipe =findViewById(R.id.imgViewSwipe);
        rv_noteList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(imgViewSwipeStatus){
                    imgViewSwipe.setVisibility(View.GONE);
                    imgViewSwipeStatus = false;
                }
                return false;
            }
        });
    }

    private void initDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        this.toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initAnimation() {
        rotateOpenFAB = AnimationUtils.loadAnimation(this, R.anim.rotate_open_fab);
        rotateCloseFAB = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottomFAB = AnimationUtils.loadAnimation(this, R.anim.from_bottom_fab);
        toBottomFAB = AnimationUtils.loadAnimation(this, R.anim.to_bottom_fab);
    }

    private void initFAB() {

        fab_more = findViewById(R.id.fab_more);
        fab_add = findViewById(R.id.fab_add);
        fab_delete = findViewById(R.id.fab_delete);
        fab_doneDeleting = findViewById(R.id.fab_doneDeleting);

        fab_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMoreButtonClicked();
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newNoteIntent = new Intent(NoteActivity.this, NoteViewActivity.class);
                startActivity(newNoteIntent);
                onMoreButtonClicked();
            }
        });

        //When user clicked on delete button fab_more, fab_add, and fab_delete will be invisible and
        // the card note list will start shake animation, however fab_doneDeleting will be visible
        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMoreButtonClicked();
                deleteNote();
                fabDeleteSettings();
                imgViewSwipe.setVisibility(View.VISIBLE);
                imgViewSwipeStatus = true; // imgViewSwipe is visible
                disableNoteListClickListener(); //to disable on click listener on the note list
            }
        });

        //When user clicked on fab_doneDeleting it will stop shake animation, fab_more, fab_add, and fab_delete
        // will be visible. However, fab_doneDeleting will become invisible
        fab_doneDeleting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemTouchHelperCallback.attachToRecyclerView(null); //disable swipe deletion
                fabDoneDeletingSettings();
                fab_more.setVisibility(View.VISIBLE);
                setNoteListClickListener();
                imgViewSwipe.setVisibility(View.GONE);
                imgViewSwipeStatus = false;
            }
        });
    }

    private void deleteNote() {
        Toast.makeText(NoteActivity.this, "Swipe right to delete note", Toast.LENGTH_SHORT).show();
        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                NoteModel selectedNote = (NoteModel) noteModelArrayList.get(viewHolder.getAdapterPosition());
                String title = selectedNote.getNoteTitle();
                boolean deleteStatus = databaseNote.deleteNote(selectedNote);
                if(deleteStatus){
                    Toast.makeText(NoteActivity.this, "Delete " + title, Toast.LENGTH_SHORT).show();
                    initNoteList();
                }else{
                    Toast.makeText(NoteActivity.this, "Delete " + title + " fail", Toast.LENGTH_SHORT).show();
                }
            }
        };
        itemTouchHelperCallback = new ItemTouchHelper(callback);
        itemTouchHelperCallback.attachToRecyclerView(rv_noteList); //for swipe deletion
    }

    private void fabDeleteSettings(){
        fab_moreClicked = false;
        fab_add.setVisibility(View.GONE);
        fab_delete.setVisibility(View.GONE);
        fab_more.setVisibility(View.GONE);
        fab_doneDeleting.setVisibility(View.VISIBLE);

        fab_add.setClickable(false);
        fab_delete.setClickable(false);
        fab_more.setClickable(false);
        fab_doneDeleting.setClickable(true);
    }

    private void fabDoneDeletingSettings(){
        fab_moreClicked = false;
        fab_add.setVisibility(View.GONE);
        fab_delete.setVisibility(View.GONE);
        fab_more.setVisibility(View.VISIBLE);
        fab_doneDeleting.setVisibility(View.GONE);

        fab_add.setClickable(false);
        fab_delete.setClickable(false);
        fab_more.setClickable(true);
        fab_doneDeleting.setClickable(true);
    }

    private void onMoreButtonClicked() {
        setFABVisibility(fab_moreClicked);
        setAnimation(fab_moreClicked);
        fab_moreClicked = !fab_moreClicked;
    }

    private void setFABVisibility(Boolean clicked){
        if(!clicked){
            fab_add.setVisibility(View.VISIBLE);
            fab_delete.setVisibility(View.VISIBLE);
            fab_add.setClickable(true);
            fab_delete.setClickable(true);
        }else{
            fab_add.setVisibility(View.GONE);
            fab_delete.setVisibility(View.GONE);
            fab_add.setClickable(false);
            fab_delete.setClickable(false);
        }
    }

    private void setAnimation(Boolean clicked){
        if(!clicked){
            fab_add.startAnimation(fromBottomFAB);
            fab_delete.startAnimation(fromBottomFAB);
        }else{
            fab_add.startAnimation(toBottomFAB);
            fab_delete.startAnimation(toBottomFAB);
        }
    }

    private void initNoteList() {
        noteModelArrayList = databaseNote.getAllData();
        Collections.sort(noteModelArrayList, new SortByDate());
        Collections.reverse(noteModelArrayList);
        notesAdapter = new NotesAdapter(this, noteModelArrayList);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rv_noteList.setLayoutManager(linearLayoutManager);
        rv_noteList.setAdapter(notesAdapter);
        setNoteListClickListener();
    }

    private void setNoteListClickListener(){
        notesAdapter.setOnItemClickListener(new NotesAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Collections.sort(noteModelArrayList, new SortByDate());
                Collections.reverse(noteModelArrayList);
                NoteModel getNoteModel = noteModelArrayList.get(position);
                if(getNoteModel.getPrivacyLockStatus()){
                    Intent privacyLockIntent = new Intent(NoteActivity.this, PrivacyLockActivity.class);
                    privacyLockIntent.putExtra("noteID", getNoteModel.getNoteId());
                    startActivity(privacyLockIntent);
                }else{
                    Intent viewNoteIntent = new Intent(NoteActivity.this, NoteViewActivity.class);
                    viewNoteIntent.putExtra("noteID", getNoteModel.getNoteId());
                    startActivity(viewNoteIntent);
                }

            }
        });
    }

    private void disableNoteListClickListener(){
        notesAdapter.setOnItemClickListener(new NotesAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                //do nothing when click
            }
        });
    }

    protected static void refreshNoteList(Context context){
        DatabaseNote databaseNote = new DatabaseNote(context, "notetify_note.db");
        noteModelArrayList = databaseNote.getAllData();
        Collections.sort(noteModelArrayList, new SortByDate());
        Collections.reverse(noteModelArrayList);
        NotesAdapter notesAdapter = new NotesAdapter(context, noteModelArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rv_noteList.setLayoutManager(linearLayoutManager);
        rv_noteList.setAdapter(notesAdapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.nav_home){
            finish();
        }
        if(itemId == R.id.nav_notes){
            //do nothing because currently at note page
        }
        if(itemId == R.id.nav_financial){
            Intent financialPlannerIntent = new Intent(NoteActivity.this, FinancialPlannerActivity.class);
            startActivity(financialPlannerIntent);
            finish();
        }
        if (itemId == R.id.nav_settings){
            Intent settingsIntent = new Intent(NoteActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        if (itemId == R.id.nav_logout){
            signOut();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        // 1. Use AccountAuthParams to specify the user information to be obtained, including the user ID (OpenID and UnionID), email address, and profile (nickname and picture).
        // 2. By default, DEFAULT_AUTH_REQUEST_PARAM specifies two items to be obtained, that is, the user ID and profile.
        // 3. If your app needs to obtain the user's email address, call setEmail().
        mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .createParams();

        // Use AccountAuthParams to build AccountAuthService.
        mAuthService = AccountAuthManager.getService(this, mAuthParam);
        Task<Void> signOutTask = mAuthService.signOut();
        signOutTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "signOut Success");
                cleanDatabase();

                //proceed to login page when user logout
                Intent logoutIntent = new Intent(NoteActivity.this, LoginHuaweiActivity.class);
                startActivity(logoutIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.i(TAG, "signOut fail");
            }
        });
    }

    private void cleanDatabase() {
        DatabaseUser databaseUser = new DatabaseUser(this);
        databaseUser.deleteData();
        DatabaseNote databaseNote = new DatabaseNote(this, "notetify_note.db");
        databaseNote.deleteAllNote();
        DatabaseReminder databaseReminder = new DatabaseReminder(this);
        databaseReminder.deleteAllReminder();
        DatabaseFinancialPlanner databaseFinancialPlanner = new DatabaseFinancialPlanner(this);
        databaseFinancialPlanner.deleteAllList();
    }

    private static class SortByDate implements Comparator<NoteModel> {
        @Override
        public int compare(NoteModel a, NoteModel b) {
            if (a.getDateCreated() == null || b.getDateCreated() == null){
                return -1;
            }else{
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy hh:mm aa");
                try{
                    Date date1 = sdf.parse(a.getDateCreated());
                    Date date2 = sdf.parse(b.getDateCreated());
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return -1;
                }
            }
        }
    }
}