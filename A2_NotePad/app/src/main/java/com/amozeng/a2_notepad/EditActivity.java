package com.amozeng.a2_notepad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    private EditText editTitle;
    private EditText editContent;
    private boolean isNewNote;
    private Note tmpNote;
    private long tmpTime;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);

        editContent.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        if (intent.hasExtra("EDIT_NOTE")) {
            tmpNote = (Note) intent.getSerializableExtra("EDIT_NOTE");
            if (tmpNote != null) {
                String title = tmpNote.getTitle();
                String content = tmpNote.getContent();
                tmpTime = intent.getLongExtra("Time", 0);
                editTitle.setText(title);
                editContent.setText(content);

            } else {
                editTitle.setText("CANNOT FIND THE NOTE");
            }
            isNewNote = false;
        } else {
            isNewNote = true;
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.edit_note_menu, menu);
        return true;
    }

    public void saveNote()
    {
        String title = editTitle.getText().toString();
        String content = editContent.getText().toString();

        if(isNewNote){ // creating a new note

            if(title.trim().isEmpty()){
                Toast.makeText(this, "Un-titled activity was not saved", Toast.LENGTH_LONG).show();
                EditActivity.super.onBackPressed();
                return;
            }
            Note newNote = new Note(title, content);
            Intent intent = new Intent();
            intent.putExtra("NEW_NOTE",  newNote);
            setResult(Activity.RESULT_OK, intent);
            finish();

        }else{ // editing a existing note
            if(title.trim().isEmpty()){
                Toast.makeText(this, "Un-titled activity was not saved", Toast.LENGTH_LONG).show();
                EditActivity.super.onBackPressed();
                return;
            }
            tmpNote.setTitle(title);
            tmpNote.setContent(content);
            tmpNote.setLastDate(tmpTime);
            Intent intent = new Intent();
            intent.putExtra("EDIT_NOTE",  tmpNote);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSave:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                saveNote();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditActivity.super.onBackPressed();
            }
        });

        builder.setMessage("Do you want to save this note?");
        builder.setTitle("Save Note");

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.secondaryColor);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.secondaryColor);
    }



}
