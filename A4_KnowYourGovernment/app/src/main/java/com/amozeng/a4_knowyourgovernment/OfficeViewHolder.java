package com.amozeng.a4_knowyourgovernment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OfficeViewHolder extends RecyclerView.ViewHolder {

    TextView office;
    TextView name;

    public OfficeViewHolder(@NonNull View itemView) {
        super(itemView);
        office = itemView.findViewById(R.id.recycler_office);
        name = itemView.findViewById(R.id.recycler_NameParty);
    }
}
