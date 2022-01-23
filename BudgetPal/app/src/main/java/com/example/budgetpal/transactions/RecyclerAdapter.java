package com.example.budgetpal.transactions;


import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetpal.DataModel;
import com.example.budgetpal.DbConn;
import com.example.budgetpal.R;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private static final String TAG = "RecyclerAdapter";
    private DbConn dbConn;
    int count = 0;

    List<DataModel> recordsList;

    public RecyclerAdapter(List<DataModel> recordsList) {
        this.recordsList = recordsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.i(TAG, "onCreateViewHolder: " + count++);

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel modal = recordsList.get(position);
        holder.rowCountTextView.setText(String.valueOf(position));
        holder.tCategory.setText(modal.getTranCategory());
        holder.tDescription.setText(modal.getTranDescription());
        holder.tPrice.setText(String.valueOf(modal.getTransPrice()));
        holder.tDate.setText(modal.getTranDate());
        holder.tType = modal.getTransType();
        Log.i("getTranId", String.valueOf(modal.getTranId()));


        String tranType = String.valueOf(modal.getTransType());
        Log.i("TransType", modal.getTransType());
        if (tranType.equals("Debit")) {
            holder.imageView.setColorFilter(Color.parseColor("#ff001a"));
            holder.tPrice.setTextColor(Color.parseColor("#ff001a"));
        }

        boolean recStatus = Boolean.parseBoolean(modal.getTranRecurring());
        if (recStatus) {
            holder.tRecur.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return recordsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView, tRecur;
        TextView tCategory, tDescription, tPrice, tDate, rowCountTextView;
        String tType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            tRecur = itemView.findViewById(R.id.tRecur);
            tCategory = itemView.findViewById(R.id.tCategory);
            tDescription = itemView.findViewById(R.id.tDescription);
            tPrice = itemView.findViewById(R.id.tPrice);
            tDate = itemView.findViewById(R.id.tDate);
            rowCountTextView = itemView.findViewById(R.id.rowCountTextView);
            tType = "";

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    recordsList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    return true;
                }
            });
        }

        @Override
        public void onClick(View v) {
        }
    }
}