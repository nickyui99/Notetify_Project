package com.xera.notetify.Model;

public class NoteModel {

    private int noteId;
    private String uniqueID;
    private String dateCreated;
    private String noteTitle;
    private String noteContent;
    private Boolean privacyLockStatus;

    public NoteModel() {
        this.noteId = -1;
        this.uniqueID = "";
        this.dateCreated = "";
        this.noteTitle = "";
        this.noteContent = "";
        this.privacyLockStatus = false;
    }

    public NoteModel(int noteId, String uniqueID, String dateCreated, String noteTitle, String noteContent, Boolean privacyLockStatus) {
        this.noteId = noteId;
        this.uniqueID = uniqueID;
        this.dateCreated = dateCreated;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.privacyLockStatus = privacyLockStatus;

    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public Boolean getPrivacyLockStatus() {
        return privacyLockStatus;
    }

    public void setPrivacyLockStatus(Boolean privacyLockStatus) {
        this.privacyLockStatus = privacyLockStatus;
    }
}
