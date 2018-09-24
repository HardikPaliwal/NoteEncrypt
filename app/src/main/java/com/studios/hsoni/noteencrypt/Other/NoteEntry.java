package com.studios.hsoni.noteencrypt.Other;

import java.text.DateFormat;
import java.util.Date;

public class NoteEntry {
    private String entryData;
    private byte[] encryptedData;
    private String entryPassword;
    private String dateOfCreation;

    public String getEntryData() {
        return entryData;
    }

    public void setEntryData(String entryData) {
        this.entryData = entryData;
    }

    public NoteEntry(String entryData, byte[] encryptedData, String entryPassword) {
        this.entryData = entryData;
        this.encryptedData = encryptedData;
        this.entryPassword = entryPassword;
        this.dateOfCreation = DateFormat.getDateTimeInstance().format(new Date());
    }

    public String getEntryPassword() {

        return entryPassword;
    }

    public void setEntryPassword(String entryPassword) {
        this.entryPassword = entryPassword;
    }

    public byte[] getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(byte[] encryptedData) {
        this.encryptedData = encryptedData;
    }
}
