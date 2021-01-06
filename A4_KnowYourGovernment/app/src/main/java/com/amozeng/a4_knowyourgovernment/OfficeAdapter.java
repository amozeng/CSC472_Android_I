package com.amozeng.a4_knowyourgovernment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfficeAdapter extends RecyclerView.Adapter<OfficeViewHolder>{

    private static final String TAG = "'OfficeAdapter'";
    private List<Office> officeList;
    private MainActivity mainActivity;

    public OfficeAdapter(List<Office> _officeList, MainActivity _mainActivity){
        this.officeList = _officeList;
        this.mainActivity = _mainActivity;
    }

    @NonNull
    @Override
    public OfficeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Making new ViewHolder");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.office_entry, parent, false);
        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);
        return new OfficeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OfficeViewHolder holder, int position) {
        Office o = officeList.get(position);
        holder.office.setText(o.getOffice());
        holder.name.setText(o.getName() + " (" + o.getParty() + ")");
    }

    @Override
    public int getItemCount() {
        return officeList.size();
    }
}
