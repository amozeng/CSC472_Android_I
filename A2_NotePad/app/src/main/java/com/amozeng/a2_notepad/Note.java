package com.amozeng.a2_notepad;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

public class Note implements Comparable<Note>, Serializable {
    private String title;
    private Date editDate;
    private String content;

    private static int ctr = 1;

    public Note(String title, String content) {
        this.title = title;
        this.editDate = new Date();
        this.content = content;
        ctr++;
    }

    public String getTitle() { return this.title; }
    public String getContent() {
        return this.content;
    }
    public Date getLastDate() {
        return this.editDate;
    }

    public void setTitle(String newTitle) { this.title = newTitle; }
    public void setContent(String newContent) {
        this.content = newContent;
    }
    public void setLastDate(long lastTimeMS) { this.editDate = new Date(lastTimeMS); }
    //public void setLastDate() { this.editDate = new Date(); }


    @NonNull
    @Override
    public String toString() {
        return "\n" + title + " | " + content + " | " + " (" + editDate + ") ";
    }

    @Override
    public int compareTo(Note o) {
        if(editDate.before(o.editDate)) return 1;
        else if(editDate.after(o.editDate)) return -1;
        return 0;
    }
}
