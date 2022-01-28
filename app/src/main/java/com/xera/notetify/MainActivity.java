package com.xera.notetify;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.type.HAEventType;
import com.huawei.hms.analytics.type.HAParamType;
import com.huawei.hms.analytics.type.ReportPolicy;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.xera.notetify.Activities.DashboardActivity;
import com.xera.notetify.Activities.LoginHuaweiActivity;
import com.xera.notetify.Activities.NoteViewActivity;
import com.xera.notetify.Controller.NotificationService;
import com.xera.notetify.Controller.ReminderBroadcast;
import com.xera.notetify.Database.DatabaseUser;
import com.xera.notetify.Model.UserModel;

import java.util.HashSet;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private AccountAuthParams authParams;
    private AccountAuthService service;
    private Task<AuthAccount> task;
    private Animation topAnim, bottomAnim;
    private CircleImageView splashImgLogo;
    private TextView splashTxtLogo, splashTxtSlogan;
    private static int SPLASH_SCREEN = 1500;
    private DatabaseUser databaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();            //Initiate views
        initAnimations();       //Initiate splash screen animation
        initHiAnalytics();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }

    private void initViews() {
        splashImgLogo = findViewById(R.id.imgViewLogo);
        splashTxtLogo = findViewById(R.id.txtViewNotetify);
        splashTxtSlogan = findViewById(R.id.txtViewSlogan);
    }

    private void initAnimations() {
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //Splash Screen Animations
        splashImgLogo.setAnimation(topAnim);
        splashTxtLogo.setAnimation(bottomAnim);
        splashTxtSlogan.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                initValidateSignIn();    //Check if the user is login
            }
        }, SPLASH_SCREEN);
    }



    private void initValidateSignIn() {
        databaseUser = new DatabaseUser(MainActivity.this);
        UserModel userData = databaseUser.getAllData();

        //if no data of ID token in database it will initiate login activity, else directly goes into dashboard
        if(userData.getIdToken().equals("")){
            Intent loginHuaweiActivity = new Intent(MainActivity.this, LoginHuaweiActivity.class);
            startActivity(loginHuaweiActivity);
            finish();
        }
        else{
            Intent dashboardIntent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(dashboardIntent);
            finish();
        }
    }

    private void initHiAnalytics() {
        Context context = this.getApplicationContext();
        HiAnalyticsInstance instance = HiAnalytics.getInstance(context);
        instance.setUserProfile("userKey","value");

        // Create a policy that is used to report an event upon app switching to the background.
        ReportPolicy moveBackgroundPolicy = ReportPolicy.ON_MOVE_BACKGROUND_POLICY;
        // Create a policy that is used to report an event at the specified interval.
        ReportPolicy scheduledTimePolicy = ReportPolicy.ON_SCHEDULED_TIME_POLICY;
        // Set the event reporting interval to 600 seconds.
        scheduledTimePolicy.setThreshold(600);
        Set<ReportPolicy> reportPolicies = new HashSet<>();
        // Add the ON_SCHEDULED_TIME_POLICY and ON_MOVE_BACKGROUND_POLICY policies.
        reportPolicies.add(scheduledTimePolicy);
        reportPolicies.add(moveBackgroundPolicy);
        // Set the ON_MOVE_BACKGROUND_POLICY and ON_SCHEDULED_TIME_POLICY policies.
        instance.setReportPolicies(reportPolicies);

        // Enable tracking of the custom event in proper positions of the code.
        Bundle bundle = new Bundle();
        bundle.putString("exam_difficulty", "high");
        bundle.putString("exam_level", "1-1");
        bundle.putString("exam_time", "20190520-08");
        instance.onEvent("begin_examination", bundle);
        // Enable tracking of the predefined event in proper positions of the code.
        Bundle bundle_pre = new Bundle();
        bundle_pre.putString(HAParamType.PRODUCTID, "item_ID");
        bundle_pre.putString(HAParamType.PRODUCTNAME, "name");
        bundle_pre.putString(HAParamType.CATEGORY, "category");
        bundle_pre.putLong(HAParamType.QUANTITY, 100L);
        bundle_pre.putDouble(HAParamType.PRICE, 10.01);
        bundle_pre.putDouble(HAParamType.REVENUE, 10);
        bundle_pre.putString(HAParamType.CURRNAME, "currency");
        bundle_pre.putString(HAParamType.PLACEID, "location_ID");
        instance.onEvent(HAEventType.ADDPRODUCT2WISHLIST, bundle_pre);
    }
}