package com.example.audiobook_app.Domain;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity
public class Chapter {

    @NonNull
    @ColumnInfo(name = "number")
    private int number;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @NonNull
    @ColumnInfo(name = "audioAddress")
    private String audioAddress;

    public Chapter(@NonNull int number, @NonNull String title, @NonNull String audioAddress) {
        this.number = number;
        this.title = title;
        this.audioAddress = audioAddress;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getAudioAddress() {
        return audioAddress;
    }

    @NonNull
    public int getNumber() {
        return number;
    }

    public void setNumber(@NonNull int number) {
        this.number = number;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public void setAudioAddress(@NonNull String audioAddress) {
        this.audioAddress = audioAddress;
    }
}
