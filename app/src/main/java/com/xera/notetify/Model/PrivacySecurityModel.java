package com.xera.notetify.Model;

public class PrivacySecurityModel {
    private int privacyPassword;
    private int securityQuestion;
    private String securityAnswer;

    public PrivacySecurityModel() {
    }

    public PrivacySecurityModel(int privacyPassword, int securityQuestion, String securityAnswer) {
        this.privacyPassword = privacyPassword;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
    }

    public int getPrivacyPassword() {
        return privacyPassword;
    }

    public void setPrivacyPassword(int privacyPassword) {
        this.privacyPassword = privacyPassword;
    }

    public int getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(int securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }
}
