package com.xera.notetify.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.huawei.cloud.base.auth.DriveCredential;
import com.huawei.cloud.base.http.FileContent;
import com.huawei.cloud.base.media.MediaHttpDownloader;
import com.huawei.cloud.base.util.StringUtils;
import com.huawei.cloud.client.exception.DriveCode;
import com.huawei.cloud.services.drive.Drive;
import com.huawei.cloud.services.drive.DriveScopes;
import com.huawei.cloud.services.drive.model.File;
import com.huawei.cloud.services.drive.model.FileList;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.framework.common.Logger;
import com.huawei.hms.support.api.entity.auth.Scope;
import com.huawei.hms.support.hwid.HuaweiIdAuthAPIManager;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;
import com.xera.notetify.Database.DatabaseNote;
import com.xera.notetify.Database.DatabaseUser;
import com.xera.notetify.Model.NoteModel;
import com.xera.notetify.Model.UserModel;
import com.xera.notetify.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    private static final int REQUEST_SIGN_IN_LOGIN = 1002;
    private MaterialToolbar toolbarSettings;
    private ConstraintLayout cl_backupSettings, cl_restoreSettings, cl_privacyPolicySettings;
    private DriveCredential mDriveCredential;
    private String mAccessToken;
    private String mUnionId;
    private File directoryCreated;
    private File fileUploaded;
    private File fileSearched;
    private UserModel userModel;
    private ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    private final int ACTION_BACKUP = 0;
    private final int ACTION_RESTORE_DATA = 1;

    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private static final Map<String, String> MIME_TYPE_MAP = new HashMap<>();

    static {
        MIME_TYPE_MAP.put(".doc", "application/msword");
        MIME_TYPE_MAP.put(".jpg", "image/jpeg");
        MIME_TYPE_MAP.put(".mp3", "audio/x-mpeg");
        MIME_TYPE_MAP.put(".mp4", "video/mp4");
        MIME_TYPE_MAP.put(".pdf", "application/pdf");
        MIME_TYPE_MAP.put(".png", "image/png");
        MIME_TYPE_MAP.put(".txt", "text/plain");
        MIME_TYPE_MAP.put(".db", "application/x-sqlite3");
    }

    private DriveCredential.AccessMethod refreshAccessToken = new DriveCredential.AccessMethod() {
        @Override
        public String refreshToken() {
            return mAccessToken;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        signIn();
        initDatabase();
        initView();
    }

    private void initDatabase(){
        DatabaseUser databaseUser = new DatabaseUser(this);
        userModel = databaseUser.getAllData();
    }

    private void initView() {
        toolbarSettings = findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbarSettings);
        toolbarSettings.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close activity when user click back button
                finish();
            }
        });

        cl_backupSettings = findViewById(R.id.cl_backupSettings);
        cl_backupSettings.setOnClickListener(view -> {

            DatabaseNote databaseNote = new DatabaseNote(SettingsActivity.this, "notetify_note.db");
            if(databaseNote.isDatabaseEmpty()){
                Log.d(TAG, "Empty database cannot be backup");
                Toast.makeText(SettingsActivity.this, "Oops. You don't have any notes that can be backup.", Toast.LENGTH_SHORT).show();
            }else if(!isConnected()){
                Log.d(TAG, "Internet Connection Fail");
                Toast.makeText(SettingsActivity.this, "Oops. Please check your Internet connection.", Toast.LENGTH_SHORT).show();
            } else{
                initProgressBar(ACTION_BACKUP);
                backUpFiles();
            }
        });

        cl_restoreSettings = findViewById(R.id.cl_restoreSettings);
        cl_restoreSettings.setOnClickListener(view -> {

            if(!isConnected()){
                Log.d(TAG, "Internet Connection Fail");
                Toast.makeText(SettingsActivity.this, "Oops. Please check your Internet connection.", Toast.LENGTH_SHORT).show();
            }else{
                initProgressBar(ACTION_RESTORE_DATA);
                restoreFile();
            }
        });

        cl_privacyPolicySettings = findViewById(R.id.cl_privacyPolicySettings);
        cl_privacyPolicySettings.setOnClickListener(view -> {

        });
    }

    private void initProgressBar(int ACTION) {
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);//you can cancel it by pressing back button
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        if(ACTION == ACTION_BACKUP){
            progressBar.setTitle("Backing up your notes file ...");
        }else if(ACTION == ACTION_RESTORE_DATA){
            progressBar.setTitle("Restoring your notes file ...");
        }
        progressBar.setProgress(0);//initially progress is 0
        progressBar.setMax(100);//sets the maximum value 100
        progressBar.show();//displays the progress bar
        progressBarStatus = 0;
    }

    private void backUpFiles() {

        new Thread(() -> {
            try {
                Drive drive = buildDrive();

                //DOWNLOAD OLD RESOURCE FILE
                String searchFileName = "notetify_note.db";
                String containers = "";
                String queryFile = "fileName = '" + searchFileName + "' and mimeType != 'application/vnd.huawei-apps.folder'";

                Drive.Files.List requestDownload = drive.files().list();
                FileList files;
                List<File> backupFiles = new ArrayList<>();

                while (true) {
                    // Query a file.
                    files = requestDownload.setQueryParam(queryFile)
                            .setPageSize(10)
                            .setOrderBy("fileName")
                            .setFields("category,nextCursor,files/id,files/fileName,files/size")
                            .setContainers(containers).execute();
                    if (files == null || files.getFiles().size() > 0) {
                        break;
                    }
                    // Read all file information.
                    backupFiles.addAll(files.getFiles());
                    String nextCursor = files.getNextCursor();
                    if (!StringUtils.isNullOrEmpty(nextCursor)) {
                        requestDownload.setCursor(files.getNextCursor());
                    } else {
                        break;
                    }
                }

                // Progress bar 25%
                progressBarHandler.post(() -> progressBar.setProgress(25));

                String path;
                Log.d(TAG, "query success");
                if (files != null && files.getFiles().size() > 0) {
                    Log.d(TAG, "Backup records found");
                    //IF BACKUP RECORD DOWNLOAD PREVIOUS FILE TO UPDATE NOTES
                    fileSearched = files.getFiles().get(0);

                    // Download a file.
                    long size = fileSearched.getSize();
                    Drive.Files.Get get = drive.files().get(fileSearched.getId());
                    MediaHttpDownloader downloader = get.getMediaHttpDownloader();

                    downloader.setContentRange(0, size - 1).setDirectDownloadEnabled(true);

                    String downloadFileName = "notetify_note_backup.db";
                    DatabaseNote restoreDatabase = new DatabaseNote(this, downloadFileName);
                    java.io.File f = new java.io.File(SettingsActivity.this.getDatabasePath(downloadFileName).getPath());
                    get.executeContentAndDownloadTo(new FileOutputStream(f));
                    Log.d(TAG, "download success to path: " + f.getPath());

                    //UPDATE DOWNLOADED DATABASE
                    updateDriveDatabase();

                    //START BACKING UP NOTE FILE
                    path = SettingsActivity.this.getDatabasePath("notetify_note_backup.db").getAbsolutePath();


                } else {
                    Log.d(TAG, "Backup records not found. Start uploading note file");
                    path = SettingsActivity.this.getDatabasePath("notetify_note.db").getAbsolutePath();
                }

                // Progress bar 50%
                progressBarHandler.post(() -> progressBar.setProgress(50));

                Log.d(TAG, "run: " + path);
                java.io.File fileObj = new java.io.File(path);
                if (!fileObj.exists()) {
                    Log.d(TAG, "file does not exists");
                    return;
                }else{
                    Log.d(TAG, "file does exists");
                }

                Map<String, String> appProperties = new HashMap<>();
                appProperties.put("appProperties", "property");

                String dirName = "NotetifyBackUpFile";

                File file = new File();
                file.setFileName(dirName)
                        .setAppSettings(appProperties)
                        .setMimeType("application/vnd.huawei-apps.folder");

                File folder = queryFolder(file);


                if(folder != null){
                    Log.d(TAG, "directory found: " + folder.getFileName());
                    Log.d(TAG, "directory id: " + folder.getId());

                    deleteFile(folder);
                }

                // Progress bar 75%
                progressBarHandler.post(() -> progressBar.setProgress(75));

                //create new folder if folder not found in drive
                directoryCreated = drive.files().create(file).execute();
                Log.d(TAG, "directory: " + directoryCreated);

                // Upload the file
                File fileToUpload = new File()
                        .setFileName("notetify_note.db")
                        .setMimeType(mimeType(fileObj))
                        .setParentFolder(Collections.singletonList(directoryCreated.getId()));

                //create new file in drive
                Drive.Files.Create request = drive.files()
                        .create(fileToUpload, new FileContent(mimeType(fileObj), fileObj));
                fileUploaded = request.execute();

                Log.d(TAG, "backup success");

                // Progress bar 100%
                progressBarHandler.post(() -> {
                    progressBar.setProgress(100);

                    // close the progress bar dialog
                    progressBar.dismiss();

                    Toast.makeText(SettingsActivity.this, "Backup Success", Toast.LENGTH_SHORT).show();
                });



            } catch (Exception e) {
                Log.d(TAG, "backup error " + e.toString());
            }
        }).start();
    }

    private File queryFolder(File filename) {
        try{
            String containers = "";
            String queryFile = "fileName = '" + filename.getFileName()
                    + "' and mimeType = 'application/vnd.huawei-apps.folder'";


            Drive drive = buildDrive();
            Drive.Files.List request = drive.files().list();
            FileList files;

            while (true) {
                files = request
                        .setQueryParam(queryFile)
                        .setPageSize(10)
                        .setOrderBy("fileName")
                        .setFields("category,nextCursor,files(id,fileName,size)")
                        .setContainers(containers)
                        .execute();

                if (files == null || files.getFiles().size() > 0) {
                    break;
                }

                if (!StringUtils.isNullOrEmpty(files.getNextCursor())) {
                    request.setCursor(files.getNextCursor());
                } else {
                    break;
                }
            }

            Log.d(TAG, "query success");
            if (files != null && files.getFiles().size() > 0) {
                fileSearched = files.getFiles().get(0);
                return fileSearched;
            } else {
                return null;
            }

        } catch (Exception e) {
            Log.d(TAG, "query error " + e.toString());
            return null;
        }
    }

    private void deleteFile(File file) {
        try {
            Drive drive = buildDrive();
            Drive.Files.Delete deleteFile = drive.files().delete(file.getId());
            deleteFile.execute();
        } catch (IOException ex) {
            Log.e(TAG, "deleteFile error: " + ex.toString());
        }
    }

    private void restoreFile(){

        new Thread(() -> {
            Drive drive = buildDrive();
            String searchFileName = "notetify_note.db";
            String containers = "";
            String queryFile = "fileName = '" + searchFileName + "' and mimeType != 'application/vnd.huawei-apps.folder'";
            try {
                Drive.Files.List request = drive.files().list();
                FileList files;
                List<File> backupFiles = new ArrayList<>();

                // Progress bar 25%
                progressBarHandler.post(() -> progressBar.setProgress(25));

                while (true) {
                    // Query a file.
                    files = request.setQueryParam(queryFile)
                            .setPageSize(10)
                            .setOrderBy("fileName")
                            .setFields("category,nextCursor,files/id,files/fileName,files/size")
                            .setContainers(containers).execute();
                    if (files == null || files.getFiles().size() > 0) {
                        break;
                    }
                    // Read all file information.
                    backupFiles.addAll(files.getFiles());
                    String nextCursor = files.getNextCursor();
                    if (!StringUtils.isNullOrEmpty(nextCursor)) {
                        request.setCursor(files.getNextCursor());
                    } else {
                        break;
                    }
                }

                // Progress bar 50%
                progressBarHandler.post(() -> progressBar.setProgress(50));

                Log.d(TAG, "query success");
                if (files != null && files.getFiles().size() > 0) {
                    fileSearched = files.getFiles().get(0);
                } else {
                    fileSearched = null;
                }

                // Progress bar 75%
                progressBarHandler.post(() -> progressBar.setProgress(75));

                // Download a file.
                long size = fileSearched.getSize();
                Drive.Files.Get get = drive.files().get(fileSearched.getId());
                MediaHttpDownloader downloader = get.getMediaHttpDownloader();

                downloader.setContentRange(0, size - 1).setDirectDownloadEnabled(true);

                String restoreFileName = "notetify_note_backup.db";
                java.io.File f = new java.io.File(SettingsActivity.this.getDatabasePath(restoreFileName).getPath());
                get.executeContentAndDownloadTo(new FileOutputStream(f));
                Log.d(TAG, "download success to path: " + f.getPath());

                // Progress bar 100%
                progressBarHandler.post(() -> {
                    progressBar.setProgress(100);
                    Toast.makeText(SettingsActivity.this, "Your notes have been restore in your device.", Toast.LENGTH_SHORT).show();
                });

                // close the progress bar dialog
                progressBar.dismiss();

                updateCurrentDatabase();
            } catch (IOException e) {
                Logger.e(TAG, "RestoreData error: " + e.toString());
            }
        }).start();
    }

    private void updateCurrentDatabase() {
        Log.d(TAG, "Updating local database");

        DatabaseNote localDatabase = new DatabaseNote(this, "notetify_note.db");
        DatabaseNote restoreDatabase = new DatabaseNote(this, "notetify_note_backup.db");

        ArrayList<NoteModel> restoreNoteModelArrayList = restoreDatabase.getAllData();
        ArrayList<NoteModel> localNoteModelArrayList = localDatabase.getAllData();

        for(NoteModel restoreNoteModel : restoreNoteModelArrayList) {
            boolean isNoteFound = false;
            for(NoteModel localNoteModel: localNoteModelArrayList){
                if(localNoteModel.getUniqueID().equals(restoreNoteModel.getUniqueID())){
                    Log.d(TAG, "Updating content unique id: " +restoreNoteModel.getUniqueID());
                    localDatabase.updateContent(restoreNoteModel);
                    isNoteFound = true;
                }
            }

            if(!isNoteFound){
                Log.d(TAG, "Adding notes file: " + restoreNoteModel.getUniqueID());
                NoteModel addNewNoteModel = new NoteModel(-1, restoreNoteModel.getUniqueID(), restoreNoteModel.getDateCreated(), restoreNoteModel.getNoteTitle(), restoreNoteModel.getNoteContent(), false);
                localDatabase.addNote(addNewNoteModel);
            }
        }
    }

    private void updateDriveDatabase(){
        Log.d(TAG, "Updating local database");

        DatabaseNote localDatabase = new DatabaseNote(this, "notetify_note.db");
        DatabaseNote onlineDatabase = new DatabaseNote(this, "notetify_note_backup.db");

        ArrayList<NoteModel> onlineNoteModelArrayList = onlineDatabase.getAllData();
        ArrayList<NoteModel> localNoteModelArrayList = localDatabase.getAllData();

        for(NoteModel localNoteModel : localNoteModelArrayList) {
            boolean isNoteFound = false;
            for(NoteModel onlineNoteModel: onlineNoteModelArrayList){
                if(localNoteModel.getUniqueID().equals(onlineNoteModel.getUniqueID())){
                    Log.d(TAG, "Updating content unique id: " +onlineNoteModel.getUniqueID());
                    onlineDatabase.updateContent(onlineNoteModel);
                    isNoteFound = true;
                }
            }

            if(!isNoteFound){
                Log.d(TAG, "Adding notes file: " + localNoteModel.getUniqueID());
                NoteModel addNewNoteModel = new NoteModel(-1, localNoteModel.getUniqueID(), localNoteModel.getDateCreated(), localNoteModel.getNoteTitle(), localNoteModel.getNoteContent(), false);
                onlineDatabase.addNote(addNewNoteModel);
            }
        }
    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    private void signIn() {
        List<Scope> scopeList = new ArrayList<>();
        scopeList.add(new Scope(DriveScopes.SCOPE_DRIVE));
        scopeList.add(new Scope(DriveScopes.SCOPE_DRIVE_APPDATA));
        scopeList.add(new Scope(DriveScopes.SCOPE_DRIVE_FILE));
        scopeList.add(new Scope(DriveScopes.SCOPE_DRIVE_METADATA));
        scopeList.add(new Scope(DriveScopes.SCOPE_DRIVE_METADATA_READONLY));
        scopeList.add(new Scope(DriveScopes.SCOPE_DRIVE_READONLY));
        scopeList.add(HuaweiIdAuthAPIManager.HUAWEIID_BASE_SCOPE);

        HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setAccessToken()
                .setIdToken()
                .setScopeList(scopeList)
                .createParams();

        HuaweiIdAuthService authService = HuaweiIdAuthManager.getService(this, authParams);
        startActivityForResult(authService.getSignInIntent(), REQUEST_SIGN_IN_LOGIN);
    }

    private Drive buildDrive() {
        Drive service = new Drive.Builder(mDriveCredential, this).build();
        return service;
    }

    private String mimeType(java.io.File file) {
        if (file != null && file.exists() && file.getName().contains(".")) {
            String fileName = file.getName();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            if (MIME_TYPE_MAP.containsKey(suffix)) {
                return MIME_TYPE_MAP.get(suffix);
            }
        }
        return "*/*";
    }

    public int init(String unionId, String accessToken, DriveCredential.AccessMethod refreshAccessToken) {
        if (StringUtils.isNullOrEmpty(unionId) || StringUtils.isNullOrEmpty(accessToken)) {
            return DriveCode.ERROR;
        }
        DriveCredential.Builder builder = new DriveCredential.Builder(unionId, refreshAccessToken);
        mDriveCredential = builder.build().setAccessToken(accessToken);
        return DriveCode.SUCCESS;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SIGN_IN_LOGIN) {
            Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
            if (authHuaweiIdTask.isSuccessful()) {
                AuthHuaweiId authHuaweiId = authHuaweiIdTask.getResult();
                mAccessToken = authHuaweiId.getAccessToken();
                mUnionId = authHuaweiId.getUnionId();
                int returnCode = init(mUnionId, mAccessToken, refreshAccessToken);

                if (returnCode == DriveCode.SUCCESS) {
                    Log.d(TAG, "onActivityResult: driveSignIn success");

                } else {
                    Log.d(TAG, "onActivityResult: driveSignIn failed");
                }
            } else {
                Log.d(TAG, "onActivityResult, signIn failed: " + ((ApiException) authHuaweiIdTask.getException()).getStatusCode());
            }
        }
}}