package com.example.budgetpal.categories;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetpal.DataModel;
import com.example.budgetpal.R;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private static final String TAG = "RecyclerAdapter";
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
        View view = layoutInflater.inflate(R.layout.row_item_cat, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel modal = recordsList.get(position);
        holder.cRowCountTextView.setText(String.valueOf(position));
        holder.cCategory.setText(modal.getCatName());
        holder.cDescription.setText(modal.getCatDescription());
        holder.cType.setText(modal.getCatType());
        holder.cBudget.setText(String.valueOf(modal.getCatBudget()));
        Log.i("getCatDescription", String.valueOf(modal.getCatDescription()));
    }

    @Override
    public int getItemCount() {
        return recordsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView cCategory, cDescription, cType, cBudget, cRowCountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewCat);
            cCategory = itemView.findViewById(R.id.cCategory);
            cDescription = itemView.findViewById(R.id.cDescription);
            cType = itemView.findViewById(R.id.cType);
            cBudget = itemView.findViewById(R.id.cBudget);
            cRowCountTextView = itemView.findViewById(R.id.cRowCountTextView);

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