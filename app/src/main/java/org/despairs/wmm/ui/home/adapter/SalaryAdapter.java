package org.despairs.wmm.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.despairs.wmm.App;
import org.despairs.wmm.R;
import org.despairs.wmm.ui.home.HomeFragment;

import java.time.format.DateTimeFormatter;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by EKovtunenko on 18.11.2019.
 */
public class SalaryAdapter extends RecyclerView.Adapter<SalaryAdapter.ViewHolder> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("LLLL YYYY");
    private List<HomeFragment.Salary> items;

    public SalaryAdapter(List<HomeFragment.Salary> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeFragment.Salary salary = items.get(position);

        holder.factAmountView.setText(App.context.getString(R.string.salary_amount_fact, salary.getAmount()));
        holder.periodView.setText(FORMATTER.format(salary.getBillingPeriod()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView factAmountView;
        private TextView periodView;

        ViewHolder(View v) {
            super(v);
            factAmountView = v.findViewById(R.id.fragment_home_list_item_text_view_fact_amount);
            periodView = v.findViewById(R.id.fragment_home_list_item_text_view_period);
        }

    }
}