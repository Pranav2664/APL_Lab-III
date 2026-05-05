package com.example.expensemanager;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensemanager.Model.Data;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Data> list;
    private OnItemLongClickListener longClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(Data data);
    }

    public TransactionAdapter(List<Data> list, OnItemLongClickListener longClickListener) {
        this.list = list;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Data data = list.get(position);
        holder.amount.setText("₹ " + data.getAmount());
        holder.category.setText(data.getType());
        holder.date.setText(data.getDate());
        holder.note.setText(data.getNote());

        // Color coding based on Category for now as 'type' isn't in Data model
        if (data.getType().equals("Salary")) {
            holder.amount.setTextColor(Color.parseColor("#4CAF50")); // Green
        } else {
            holder.amount.setTextColor(Color.parseColor("#F44336")); // Red
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(data);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView amount, category, date, note;
        public ViewHolder(View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.ammount_txt_income);
            category = itemView.findViewById(R.id.type_txt_income);
            date = itemView.findViewById(R.id.date_txt_income);
            note = itemView.findViewById(R.id.note_txt_income);
        }
    }
}