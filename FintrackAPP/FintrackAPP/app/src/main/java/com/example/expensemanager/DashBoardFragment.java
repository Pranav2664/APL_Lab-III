package com.example.expensemanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensemanager.Model.Data;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DashBoardFragment extends Fragment {

    private TextView totalBalanceText, totalIncomeText, totalExpenseText;
    private RecyclerView recyclerViewRecent;
    private FloatingActionButton fabAdd;
    private PieChart pieChart;
    private MaterialButtonToggleGroup chartToggleGroup;
    
    private DatabaseHelper dbHelper;
    private String uid;
    private String currentChartType = "Expense";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dash_board, container, false);

        dbHelper = new DatabaseHelper(getActivity());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("email", "default_user");

        totalBalanceText = view.findViewById(R.id.total_balance_result);
        totalIncomeText = view.findViewById(R.id.income_set_result);
        totalExpenseText = view.findViewById(R.id.expense_set_result);
        recyclerViewRecent = view.findViewById(R.id.recycler_recent);
        fabAdd = view.findViewById(R.id.fb_main_plus_btn);
        pieChart = view.findViewById(R.id.pieChart);
        chartToggleGroup = view.findViewById(R.id.chart_toggle_group);

        recyclerViewRecent.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewRecent.setNestedScrollingEnabled(false);

        fabAdd.setOnClickListener(v -> showAddTransactionDialog());

        chartToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_show_income) {
                    currentChartType = "Income";
                } else if (checkedId == R.id.btn_show_expense) {
                    currentChartType = "Expense";
                }
                loadPieChartData(currentChartType);
            }
        });

        setupPieChart();
        updateDashboard();

        return view;
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.parseColor("#444444"));
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setCenterTextSize(18f);
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.getLegend().setTextColor(Color.WHITE);
    }

    public void updateDashboard() {
        int income = dbHelper.getTotalByType(uid, "Income");
        int expense = dbHelper.getTotalByType(uid, "Expense");
        int balance = income - expense;

        totalIncomeText.setText("₹ " + String.valueOf(income));
        totalExpenseText.setText("₹ " + String.valueOf(expense));
        totalBalanceText.setText("₹ " + String.valueOf(balance));
        
        if (balance < 0) totalBalanceText.setTextColor(Color.RED);
        else totalBalanceText.setTextColor(Color.WHITE);

        List<Data> recentTransactions = dbHelper.getAllTransactions(uid);
        recyclerViewRecent.setAdapter(new RecentTransactionAdapter(recentTransactions));

        loadPieChartData(currentChartType);
    }

    private void loadPieChartData(String type) {
        List<Data> transactions = dbHelper.getTransactionsByType(uid, type);
        Map<String, Integer> categoryMap = new HashMap<>();

        for (Data data : transactions) {
            String category = data.getType();
            categoryMap.put(category, categoryMap.getOrDefault(category, 0) + data.getAmount());
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : categoryMap.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        if (entries.isEmpty()) {
            pieChart.clear();
            pieChart.setNoDataText("No " + type + " data available");
            pieChart.setNoDataTextColor(Color.WHITE);
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, type + " Categories");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        
        ArrayList<Integer> colors = new ArrayList<>();
        if (type.equals("Income")) {
            colors.add(Color.parseColor("#2E7D32")); // Dark Green
            colors.add(Color.parseColor("#4CAF50")); // Green
            colors.add(Color.parseColor("#81C784")); // Light Green
            colors.add(Color.parseColor("#A5D6A7")); // Pale Green
        } else {
            colors.add(Color.parseColor("#C62828")); // Dark Red
            colors.add(Color.parseColor("#F44336")); // Red
            colors.add(Color.parseColor("#E57373")); // Light Red
            colors.add(Color.parseColor("#EF9A9A")); // Pale Red
        }
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.setCenterText(type);
        pieChart.invalidate();
        pieChart.animateY(1000);
    }

    private void showAddTransactionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_layout_for_insert_data, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        EditText edtAmount = view.findViewById(R.id.amount_edt);
        EditText edtNote = view.findViewById(R.id.note_edt);
        Spinner spinnerCategory = view.findViewById(R.id.category_spinner);
        MaterialButtonToggleGroup typeToggle = view.findViewById(R.id.type_toggle_group);
        
        String[] categories = {"Food", "Travel", "Salary", "Shopping", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(v -> {
            String amountStr = edtAmount.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();
            String note = edtNote.getText().toString().trim();

            String type = (typeToggle.getCheckedButtonId() == R.id.btn_income_type) ? "Income" : "Expense";

            if (TextUtils.isEmpty(amountStr)) {
                edtAmount.setError("Required");
                return;
            }

            Data data = new Data(Integer.parseInt(amountStr), category, note, UUID.randomUUID().toString(), DateFormat.getDateInstance().format(new Date()));
            dbHelper.insertTransaction(data, uid, type);
            
            Toast.makeText(getActivity(), "Transaction Saved", Toast.LENGTH_SHORT).show();
            updateDashboard();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private class RecentTransactionAdapter extends RecyclerView.Adapter<TransactionViewHolder> {
        private List<Data> list;

        public RecentTransactionAdapter(List<Data> list) { this.list = list; }

        @NonNull
        @Override
        public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_data, parent, false);
            return new TransactionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
            Data data = list.get(position);
            holder.amount.setText("₹ " + data.getAmount());
            holder.category.setText(data.getType()); 
            holder.date.setText(data.getDate());
            holder.note.setText(data.getNote());
            
            if("Income".equals(data.getTransactionType())) holder.amount.setTextColor(Color.GREEN);
            else holder.amount.setTextColor(Color.RED);
        }

        @Override
        public int getItemCount() { return list.size(); }
    }

    private static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView amount, category, date, note;
        public TransactionViewHolder(View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.ammount_txt_income);
            category = itemView.findViewById(R.id.type_txt_income);
            date = itemView.findViewById(R.id.date_txt_income);
            note = itemView.findViewById(R.id.note_txt_income);
        }
    }
}