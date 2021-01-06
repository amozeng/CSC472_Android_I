package com.amozeng.a2_notepad;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView content;
    public TextView editDate;

    MyViewHolder(View view) {
        super(view);
        title = view.findViewById(R.id.noteTitle);

        // substring limit it to 80 characters
//        TextView contentHolder = view.findViewById(R.id.noteContent);
//        String contentFull = contentHolder.getText().toString();
//        if (contentFull.length() > 80) {
//            String contentTrimmed = contentFull.substring(0, 79);
//            content.setText(contentTrimmed);
//
//        }
//        else {
//            content.setText(contentFull);
//        }

        content= view.findViewById(R.id.noteContent);
        editDate = view.findViewById(R.id.editDate);
    }
}
