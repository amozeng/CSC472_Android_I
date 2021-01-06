package com.amozeng.a2_notepad;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;


public class NotesAdaptor extends RecyclerView.Adapter<MyViewHolder> {
    private static final String TAG = "NotesAdaptor";
    private List<Note> noteList;
    private MainActivity mainActivity;

    NotesAdaptor(List<Note> nList, MainActivity mAct)
    {
        this.noteList = nList;
        this.mainActivity = mAct;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW MyViewHolder");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list, parent, false);
        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: FILLING ViewHolder Notes" + position);
        Note note = noteList.get(position);

        holder.title.setText(note.getTitle());
        // substring limit it to 80 characters
        String contentFull = note.getContent();
        if(contentFull.length() > 80){
            contentFull = contentFull.substring(0, 80);
            contentFull += "...";
        }
        holder.content.setText(contentFull);
//        holder.content.setText(note.getContent());
        holder.editDate.setText(note.getLastDate().toString());
    }


    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
