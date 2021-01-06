package com.amozeng.a2_notepad;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private final List<Note> noteList = new ArrayList<>();
    private Note noteHolder;
    private int tmpPos;

    private NotesAdaptor mAdaptor;
    private RecyclerView recyclerView;
    private static int ADD_REQUEST = 0;
    private static int EDIT_REQUEST = 1;
    private static int INFO_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readJSONData();
        String app_name = getResources().getString(R.string.app_name);

        setTitle( app_name + " (" + noteList.size() + ")");
        setContentView(R.layout.activity_main);


        // get recyclerView
        recyclerView = findViewById(R.id.recycler);

        // get RecyclerView Adaptor
        mAdaptor = new NotesAdaptor(noteList, this);
        recyclerView.setAdapter(mAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // try to figure out where the json file located
        File dir = getFilesDir();
        Log.d(TAG, "File dir: " + dir);

        // generate some random notes just for test
//        for(int i  = 0; i < 5; i++) {
//            noteList.add(new Note( "This is note " + (i+1), "content: for note No." + (i+1)));
//        }
        //writeJSONData();
        //noteList.clear();
        //readJSONData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        writeJSONData();
    }

    @Override
    public void onClick(View v) {     // call by ViewHolder
//        int pos = recyclerView.getChildLayoutPosition(v);
        tmpPos = recyclerView.getChildLayoutPosition(v);

        noteHolder = noteList.get(tmpPos);
        openEditViewEditNote(noteHolder);
//        Intent intent = new Intent(this, EditActivity.class);
//        intent.putExtra("EDIT_NOTE", m);
//        startActivityForResult(intent, EDIT_REQUEST);
        //Toast.makeText(v.getContext(), "SHORT: "+ noteHolder.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onLongClick(View v) {  // called by ViewHolder
        int pos = recyclerView.getChildLayoutPosition(v);
        Note m = noteList.get(pos);

        removeNoteDialog(pos);
        //noteList.remove(pos);
        //mAdaptor.notifyDataSetChanged();
        //Toast.makeText(v.getContext(), "LONG: "+ m.toString(), Toast.LENGTH_LONG).show();
        String app_name = getResources().getString(R.string.app_name);
        setTitle( app_name + " (" + noteList.size() + ")");
        mAdaptor.notifyDataSetChanged();

        return false;
    }

    @SuppressLint("ResourceAsColor")
    public void removeNoteDialog(final int pos) {
        // Simple Ok & Cancel dialog - no view used.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                noteList.remove(pos);
                mAdaptor.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.setMessage("Do you want to delete this note?");
        builder.setTitle("Delete Note");

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.secondaryColor);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.secondaryColor);
    }

    @Override
    public void onBackPressed(){
        //Toast.makeText(this, "Back Button was pressed - Bye!", Toast.LENGTH_LONG).show();
        super.onBackPressed();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_add:
                // jump to editActivity
                openEditViewAddNote();
                return true;
            case R.id.menu_info:
                // jump to infoActivity
                openInfoView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openEditViewAddNote() {
        Intent intent = new Intent(this, EditActivity.class);
        startActivityForResult(intent, ADD_REQUEST);
    }

    public void openEditViewEditNote(Note n) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("EDIT_NOTE", n);
        //intent.putExtra("Time", System.currentTimeMillis());
        startActivityForResult(intent, EDIT_REQUEST);
    }

    public void openInfoView() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivityForResult(intent, INFO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_REQUEST){
            if(resultCode == Activity.RESULT_OK) {
                if (data != null ){
                    Note newNote = (Note)data.getSerializableExtra("NEW_NOTE");
                    if(newNote != null){
                        noteList.add(newNote);
                        Collections.sort(noteList);
                        mAdaptor.notifyDataSetChanged();
                        String app_name = getResources().getString(R.string.app_name);
                        setTitle( app_name + " (" + noteList.size() + ")");
                    }
                }
            }
            Log.d(TAG, "onActivityResult: Return from Edit Activity: Add note");

        }else if (requestCode == EDIT_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                if(data != null) {
                    Note newNote = (Note)data.getSerializableExtra("EDIT_NOTE");
                    if(newNote != null){
                        noteList.get(tmpPos).setTitle(newNote.getTitle());
                        noteList.get(tmpPos).setContent(newNote.getContent());
                        noteList.get(tmpPos).setLastDate(System.currentTimeMillis());

                        // sort the list
                        Collections.sort(noteList);

                        mAdaptor.notifyDataSetChanged();
                    }
                }
            }
            Log.d(TAG, "onActivityResult: Return from Edit Activity: Edit existing note ");

        }else if (requestCode == INFO_REQUEST){
            Log.d(TAG, "onActivityResult: Return from Info Activity");
        }

        else{
            Toast.makeText(this, "Unexpected code received: " + requestCode, Toast.LENGTH_SHORT).show();
        }
    }
    

    private void writeJSONData() {
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.jsonFileName), Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            writer.setIndent("  ");
            writer.beginArray();
            for(Note n : noteList) {
                writer.beginObject();
                writer.name("title").value(n.getTitle());
                writer.name("content").value(n.getContent());
                writer.name("editDate").value(n.getLastDate().getTime());
                writer.endObject();
            }
            writer.endArray();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "writeJSONData: "+ e.getMessage());

        }
    }

    private void readJSONData() {
        try{
            FileInputStream fis = getApplicationContext().openFileInput(getString(R.string.jsonFileName));

            // Read string content from file
            byte[] data = new byte[(int) fis.available()];
            int loaded = fis.read(data); // size of the file
            Log.d(TAG, "readJSONData: Loaded " + loaded + " bytes");
            fis.close();
            String json = new String(data);

            // Create JSON Array from string file content
            JSONArray noteArr = new JSONArray(json);
            for(int i = 0; i < noteArr.length(); i++) {
                JSONObject nObj = noteArr.getJSONObject(i);

                String title = nObj.getString("title");
                String content = nObj.getString("content");
                long dateMS = nObj.getLong("editDate");

                Note n = new Note(title, content);
                n.setLastDate(dateMS);
                noteList.add(n);
            }
            Log.d(TAG, "readJSONData: " + noteList);


        }catch(Exception e){
            e.printStackTrace();
            Log.d(TAG, "readJSONData: " + e.getMessage());
        }
    }
}