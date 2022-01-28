package com.xera.notetify.Controller;

import android.content.Context;

import com.xera.notetify.Database.DatabasePrivacySecurity;
import com.xera.notetify.Model.PrivacySecurityModel;

public class NotetifySecurityManager {
    private int privacyPassword;
    private int securityQuestion;
    private String securityAnswer;

    public NotetifySecurityManager() {
    }

    public boolean checkPasswordValidity(Context context, String inputPrivacyPassword){
        DatabasePrivacySecurity databasePrivacySecurity = new DatabasePrivacySecurity(context);
        return databasePrivacySecurity.checkPrivacyPassword(Integer.parseInt(inputPrivacyPassword));
    }

    public boolean checkSecurityAnswer(Context context, int inputSecurityQuestion, String inputSecurityAnswer){
        DatabasePrivacySecurity databasePrivacySecurity = new DatabasePrivacySecurity(context);
        return databasePrivacySecurity.checkSecurityAnswer(inputSecurityQuestion, inputSecurityAnswer);
    }

    public int getSecurityQuestion(Context context){
        DatabasePrivacySecurity databasePrivacySecurity = new DatabasePrivacySecurity(context);
        return databasePrivacySecurity.getSecurityQuestion();
    }

    public boolean checkPrivacySecurityDB(Context context){
        DatabasePrivacySecurity databasePrivacySecurity = new DatabasePrivacySecurity(context);
        return databasePrivacySecurity.checkPrivacySecurityDB();
    }

    public boolean setNewPassword(Context context, PrivacySecurityModel privacySecurityModel){
        DatabasePrivacySecurity databasePrivacySecurity = new DatabasePrivacySecurity(context);
        return databasePrivacySecurity.setPrivacyPassword(privacySecurityModel);
    }

    public boolean resetPassword(Context context, int newPassword){
        DatabasePrivacySecurity databasePrivacySecurity = new DatabasePrivacySecurity(context);
        return databasePrivacySecurity.updatePrivacyPassword(newPassword);
    }
}
