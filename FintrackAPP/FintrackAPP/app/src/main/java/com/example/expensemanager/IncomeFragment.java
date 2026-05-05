package com.example.expensemanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensemanager.Model.Data;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class IncomeFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private String uid;

    private RecyclerView recyclerView;
    private TextView incomeTotalSum;

    private EditText edtAmmount;
    private EditText edtType;
    private EditText edtNote;

    private Button btnUpdate;
    private Button btnDelete;

    private String type;
    private String note;
    private int amount;
    private String post_key;

    private IncomeAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_income, container, false);

        dbHelper = new DatabaseHelper(getActivity());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("email", "default_user");

        incomeTotalSum = myview.findViewById(R.id.income_text_result);
        recyclerView = myview.findViewById(R.id.recycler_id_income);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        updateIncomeData();

        return myview;
    }

    private void updateIncomeData() {
        List<Data> incomeList = dbHelper.getAllIncome(uid);
        int totalvalue = 0;
        for (Data data : incomeList) {
            totalvalue += data.getAmount();
        }
        incomeTotalSum.setText(String.valueOf(totalvalue) + ".00");

        adapter = new IncomeAdapter(incomeList);
        recyclerView.setAdapter(adapter);
    }

    public class IncomeAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<Data> dataList;

        public IncomeAdapter(List<Data> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_data, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Data model = dataList.get(position);
            holder.setType(model.getType());
            holder.setNote(model.getNote());
            holder.setDate(model.getDate());
            holder.setAmount(model.getAmount());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    post_key = model.getId();
                    type = model.getType();
                    note = model.getNote();
                    amount = model.getAmount();
                    updateDataItem();
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mType, mNote, mDate, mAmmount;
        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mNote = mView.findViewById(R.id.note_txt_income);
            mType = mView.findViewById(R.id.type_txt_income);
            mDate = mView.findViewById(R.id.date_txt_income);
            mAmmount = mView.findViewById(R.id.ammount_txt_income);
        }

        private void setType(String type) { mType.setText(type); }
        private void setNote(String note) { mNote.setText(note); }
        private void setDate(String date) { mDate.setText(date); }
        private void setAmount(int ammount) { mAmmount.setText(String.valueOf(ammount)); }
    }

    private void updateDataItem() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item, null);
        mydialog.setView(myview);

        edtAmmount = myview.findViewById(R.id.amount_edt);
        edtType = myview.findViewById(R.id.type_edt);
        edtNote = myview.findViewById(R.id.note_edt);

        edtType.setText(type);
        edtType.setSelection(type.length());
        edtNote.setText(note);
        edtNote.setSelection(note.length());
        edtAmmount.setText(String.valueOf(amount));
        edtAmmount.setSelection(String.valueOf(amount).length());

        btnUpdate = myview.findViewById(R.id.btnUpdate);
        btnDelete = myview.findViewById(R.id.btnDelete);

        AlertDialog dialog = mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mType = edtType.getText().toString().trim();
                String mNote = edtNote.getText().toString().trim();
                String mAmountStr = edtAmmount.getText().toString().trim();
                int mAmount = Integer.parseInt(mAmountStr);

                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(mAmount, mType, mNote, post_key, mDate);

                dbHelper.updateIncome(data);
                updateIncomeData();
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.deleteIncome(post_key);
                updateIncomeData();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}