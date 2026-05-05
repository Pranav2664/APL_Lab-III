package com.example.expensemanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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

public class ExpenseFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private String uid;

    private RecyclerView recyclerView;
    private TextView expenseSumResult;

    private EditText edtAmmount;
    private EditText edtType;
    private EditText edtNote;

    private Button btnUpdate;
    private Button btnDelete;

    private String type;
    private String note;
    private int ammount;
    private String post_key;

    private static final int BUDGET_LIMIT = 1000;
    private ExpenseAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_expense, container, false);

        dbHelper = new DatabaseHelper(getActivity());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("email", "default_user");

        expenseSumResult = myview.findViewById(R.id.expense_text_result);
        recyclerView = myview.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        updateExpenseData();
        createNotificationChannel();

        return myview;
    }

    private void updateExpenseData() {
        List<Data> expenseList = dbHelper.getTransactionsByType(uid, "Expense");
        int expenseSum = 0;
        for (Data data : expenseList) {
            expenseSum += data.getAmount();
        }
        expenseSumResult.setText(String.valueOf(expenseSum) + ".00");

        if (expenseSum > BUDGET_LIMIT) {
            showNotification("You have exceeded your budget limit!");
        }

        adapter = new ExpenseAdapter(expenseList);
        recyclerView.setAdapter(adapter);
    }

    public class ExpenseAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<Data> dataList;

        public ExpenseAdapter(List<Data> dataList) {
            this.dataList = dataList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_data, parent, false);
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
                    ammount = model.getAmount();
                    updateDataItem();
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView mType, mNote, mDate, mAmmount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mNote = mView.findViewById(R.id.note_txt_expense);
            mType = mView.findViewById(R.id.type_txt_expense);
            mDate = mView.findViewById(R.id.date_txt_expense);
            mAmmount = mView.findViewById(R.id.ammount_txt_expense);
        }

        private void setDate(String date) { mDate.setText(date); }
        private void setType(String type) { mType.setText(type); }
        private void setNote(String note) { mNote.setText(note); }
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
        edtAmmount.setText(String.valueOf(ammount));
        edtAmmount.setSelection(String.valueOf(ammount).length());

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

                dbHelper.updateTransaction(data, "Expense");
                updateExpenseData();
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.deleteTransaction(post_key);
                updateExpenseData();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "expense_channel")
                .setSmallIcon(R.drawable.noti)
                .setContentTitle("Budget Alert")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Expense Channel";
            String description = "Channel for expense alerts";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("expense_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}