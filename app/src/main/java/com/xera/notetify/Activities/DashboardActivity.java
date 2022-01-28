package com.xera.notetify.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.banner.BannerView;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.feature.service.AuthService;
import com.xera.notetify.Controller.NotificationService;
import com.xera.notetify.Database.DatabaseFinancialPlanner;
import com.xera.notetify.Database.DatabaseNote;
import com.xera.notetify.Database.DatabasePrivacySecurity;
import com.xera.notetify.Database.DatabaseReminder;
import com.xera.notetify.Database.DatabaseUser;
import com.xera.notetify.Model.UserModel;
import com.xera.notetify.R;

// Import the weather capture-related classes.

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "DashboardActivity";
    private AuthService mAuthService;
    private AccountAuthParams mAuthParam;
    private TextView txtViewUserGreeting;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private UserModel userModel;
    private CardView cardNotes, cardFinancialPlanner, cardSettings;
    private Intent notificationServiceIntent;
    private NotificationService notificationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initServices();
        initDatabase();
        initDrawer();
        initViews();
        initAds();
    }


    private void initServices() {
        //Start Notification Service
        notificationService = new NotificationService();
        notificationServiceIntent = new Intent(getBaseContext(), NotificationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "Notification foreground service started");
            startForegroundService(notificationServiceIntent);
        }else{
            Log.d(TAG, "Notification service started");
            startService(notificationServiceIntent);
        }
    }

    private void initDatabase(){
        DatabaseUser databaseUser = new DatabaseUser(DashboardActivity.this);
        userModel = databaseUser.getAllData();
    }




    private void initDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        this.toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void initViews(){
        txtViewUserGreeting = findViewById(R.id.txtViewUserGreeting);
        txtViewUserGreeting.setText("Hello " + userModel.getName());
        cardNotes = findViewById(R.id.cardViewNote1);
        cardNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent noteIntent = new Intent(DashboardActivity.this, NoteActivity.class);
                startActivity(noteIntent);
            }
        });
        cardFinancialPlanner = findViewById(R.id.cardViewFinancialPlanner);
        cardFinancialPlanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent financialPlannerIntent = new Intent(DashboardActivity.this, FinancialPlannerActivity.class);
                startActivity(financialPlannerIntent);
            }
        });

        cardSettings = findViewById(R.id.cardViewSettings);
        cardSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent(DashboardActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
    }

    private void initAds() {
        //Banner Ads Code Start here
        BannerView bannerView1 = new BannerView(this);
        BannerView bannerView2 = new BannerView(this);

        bannerView1.setAdId("q5je6iiarj");
        bannerView2.setAdId("m63dcgb6ty");

        bannerView1.setBannerAdSize(BannerAdSize.BANNER_SIZE_SMART);
        bannerView2.setBannerAdSize(BannerAdSize.BANNER_SIZE_320_50);

        ConstraintLayout constraintLayout1 = findViewById(R.id.ad_frame1);
        ConstraintLayout constraintLayout2 = findViewById(R.id.ad_frame2);

        constraintLayout1.addView(bannerView1);
        constraintLayout2.addView(bannerView2);

        // Create an ad request to load an ad.
        AdParam adParam1 = new AdParam.Builder().build();
        AdParam adParam2 = new AdParam.Builder().build();

        bannerView1.loadAd(adParam1);
        bannerView2.loadAd(adParam2);
        //Banner Ads Code End here
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.nav_home){
            //do nothing because currently at home page
        }
        if(itemId == R.id.nav_notes){
            Intent noteIntent = new Intent(DashboardActivity.this, NoteActivity.class);
            startActivity(noteIntent);
        }
        if(itemId == R.id.nav_financial){
            Intent financialPlannerIntent = new Intent(DashboardActivity.this, FinancialPlannerActivity.class);
            startActivity(financialPlannerIntent);
        }
        if (itemId == R.id.nav_settings){
            Intent settingsIntent = new Intent(DashboardActivity.this, SettingsActivity.class);
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
                Intent logoutIntent = new Intent(DashboardActivity.this, LoginHuaweiActivity.class);
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
        DatabasePrivacySecurity databasePrivacySecurity = new DatabasePrivacySecurity(this);
        databasePrivacySecurity.deletePrivacyPassword();
    }

}



