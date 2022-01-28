package com.xera.notetify.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.cloud.services.drive.DriveScopes;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.api.entity.auth.Scope;
import com.huawei.hms.support.hwid.HuaweiIdAuthAPIManager;
import com.huawei.hms.support.hwid.ui.HuaweiIdAuthButton;
import com.xera.notetify.Database.DatabaseUser;
import com.xera.notetify.Model.UserModel;
import com.xera.notetify.R;

import java.util.ArrayList;
import java.util.List;

public class LoginHuaweiActivity extends AppCompatActivity {

    // AccountAuthService provides a set of APIs, including silentSignIn, getSignInIntent, and signOut.
    private AccountAuthService mAuthService;

    // Set HUAWEI ID sign-in authorization parameters.
    private AccountAuthParams mAuthParam;

    private HuaweiIdAuthButton huaweiIdAuthButton;

    // Define the request code for signInIntent.
    private static final int REQUEST_CODE_SIGN_IN = 1000;

    // Define the log flag.
    private static final String TAG = "LoginHuaweiActivity";

    private ProgressDialog progressDialog;

    private int progressBarStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_huawei);

        huaweiIdAuthButton = findViewById(R.id.HuaweiIdAuthButton);
        huaweiIdAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                silentSignInByHwId();
            }
        });
    }


    private void silentSignInByHwId() {
        // 1. Use AccountAuthParams to specify the user information to be obtained, including the user ID (OpenID and UnionID), email address, and profile (nickname and picture).
        // 2. By default, DEFAULT_AUTH_REQUEST_PARAM specifies two items to be obtained, that is, the user ID and profile.
        // 3. If your app needs to obtain the user's email address, call setEmail().
        //  4. To support ID token-based HUAWEI ID sign-in, use setIdToken(). User information can be parsed from the ID token.
        List<Scope> scopeList = new ArrayList<>();
        scopeList.add(new Scope(DriveScopes.SCOPE_DRIVE));
        scopeList.add(new Scope(DriveScopes.SCOPE_DRIVE_APPDATA));
        scopeList.add(new Scope(DriveScopes.SCOPE_DRIVE_FILE));
        scopeList.add(new Scope(DriveScopes.SCOPE_DRIVE_METADATA));
        scopeList.add(new Scope(DriveScopes.SCOPE_DRIVE_METADATA_READONLY));
        scopeList.add(new Scope(DriveScopes.SCOPE_DRIVE_READONLY));
        scopeList.add(HuaweiIdAuthAPIManager.HUAWEIID_BASE_SCOPE);

        mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setAccessToken()
                .setIdToken()
                .setScopeList(scopeList)
                .setEmail()
                .createParams();

        // Use AccountAuthParams to build AccountAuthService.
        mAuthService = AccountAuthManager.getService(this, mAuthParam);
        // Sign in with a HUAWEI ID silently.
        Task<AuthAccount> task = mAuthService.silentSignIn();
        task.addOnSuccessListener(new OnSuccessListener<AuthAccount>() {
            @Override
            public void onSuccess(AuthAccount authAccount) {
                // The silent sign-in is successful. Process the returned AuthAccount object to obtain the HUAWEI ID information.
                dealWithResultOfSignIn(authAccount);
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // The silent sign-in fails. Your app will call getSignInIntent() to show the authorization or sign-in screen.
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    Intent signInIntent = mAuthService.getSignInIntent();
                    // If your app appears in full-screen mode duing user sign-in, that is, with no satus bar at the top of the phone screen, add the following parameter in the intent:
                    // intent.putExtra(CommonConstant.RequestParams.IS_FULL_SCREEN, true);
                    // Check the details in this FAQ.
                    startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
                }
            }
        });
    }

    /**
     * Process the returned AuthAccount object to obtain the HUAWEI ID information.
     *
     * @param authAccount AuthAccount object, which contains the HUAWEI ID information.
     */
    private void dealWithResultOfSignIn(AuthAccount authAccount) {
        Log.i(TAG, "accessToken:" + authAccount.getAccessToken());
        Log.i(TAG, "Name:" + authAccount.getDisplayName());
        Log.i(TAG, "Email:" + authAccount.getEmail());

        //Insert data into sqlite user database
        UserModel userModel = new UserModel(authAccount.getAccessToken(), authAccount.getIdToken(), authAccount.getDisplayName(), authAccount.getEmail());
        DatabaseUser databaseUser = new DatabaseUser(LoginHuaweiActivity.this);
        boolean success = databaseUser.insertData(userModel);

        //Proceed to Dashboard
        Intent loginSuccessIntent = new Intent(LoginHuaweiActivity.this, DashboardActivity.class);
        startActivity(loginSuccessIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Log.i(TAG, "onActivitResult of sigInInIntent, request code: " + REQUEST_CODE_SIGN_IN);
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                // The sign-in is successful, and the authAccount object that contains the HUAWEI ID information is obtained.
                AuthAccount authAccount = authAccountTask.getResult();
                dealWithResultOfSignIn(authAccount);
                Log.i(TAG, "onActivitResult of sigInInIntent, request code: " + REQUEST_CODE_SIGN_IN);
            } else {
                // The sign-in fails. Find the failure cause from the status code. For more information, please refer to the "Error Codes" section in the API Reference.
                Log.e(TAG, "sign in failed : " +((ApiException)authAccountTask.getException()).getStatusCode());
            }
        }
    }
}