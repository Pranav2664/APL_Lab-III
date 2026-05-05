package com.example.demointent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.Arrays;
import java.util.List;

public class LayoutListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<String> layouts = Arrays.asList(
                "LinearLayout",
                "RelativeLayout",
                "ConstraintLayout",
                "FrameLayout",
                "TableLayout",
                "GridLayout",
                "CoordinatorLayout"
        );

        LayoutAdapter adapter = new LayoutAdapter(layouts);
        recyclerView.setAdapter(adapter);
    }

    private class LayoutAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final List<String> mData;

        LayoutAdapter(List<String> data) {
            this.mData = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String layoutName = mData.get(position);
            holder.tvLayoutName.setText(layoutName);
            holder.itemView.setOnClickListener(v -> 
                Toast.makeText(LayoutListActivity.this, "Selected: " + layoutName, Toast.LENGTH_SHORT).show()
            );
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLayoutName;

        ViewHolder(View itemView) {
            super(itemView);
            tvLayoutName = itemView.findViewById(R.id.tvLayoutName);
        }
    }
}
