package org.despairs.wmm.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.despairs.wmm.BR;
import org.despairs.wmm.R;
import org.despairs.wmm.databinding.FragmentHomeListItemBinding;
import org.despairs.wmm.ui.home.HomeFragment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.databinding.BindingConversion;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by EKovtunenko on 18.11.2019.
 */
public class SalaryAdapter extends RecyclerView.Adapter<SalaryAdapter.ViewHolder> {

    private static final Locale RU = Locale.forLanguageTag("ru");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("LLLL YYYY", RU);
    private List<HomeFragment.Salary> items;

    public SalaryAdapter(List<HomeFragment.Salary> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentHomeListItemBinding viewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.fragment_home_list_item, parent, false);
        return new ViewHolder(viewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeFragment.Salary salary = items.get(position);

        holder.binding.detailList.setHasFixedSize(true);
        holder.binding.detailList.setAdapter(new DetailedSalaryAdapter(salary.items));
        holder.binding.detailList.setVisibility(salary.expanded ? View.VISIBLE : View.GONE);
        holder.binding.setVariable(BR.salary, salary);
        holder.binding.executePendingBindings();

        holder.binding.fullItem.setOnClickListener(v -> {
            salary.expanded = !salary.expanded;
            notifyItemChanged(position);
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private FragmentHomeListItemBinding binding;

        ViewHolder(FragmentHomeListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    @BindingConversion
    public static String convertBillingPeriodToString(LocalDateTime period) {
        return FORMATTER.format(period);
    }

    @BindingConversion
    public static String convertAmountToString(double period) {
        return String.format(RU, "%1$,.2f \u20BD", period);
    }

    public static class DetailedSalaryAdapter extends RecyclerView.Adapter<DetailViewHolder> {

        private List<HomeFragment.Salary> items;

        public DetailedSalaryAdapter(List<HomeFragment.Salary> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.fragment_home_list_item_detail_item, parent, false);
            return new DetailViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
            HomeFragment.Salary salary = items.get(position);

            holder.binding.setVariable(BR.salary, salary);
            holder.binding.executePendingBindings();
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public static class DetailViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding binding;

        DetailViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}