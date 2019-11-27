package org.despairs.wmm.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.despairs.wmm.BR;
import org.despairs.wmm.R;
import org.despairs.wmm.ui.home.entity.Paycheck;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by EKovtunenko on 27.11.2019.
 */
public class PaycheckAdapter extends RecyclerView.Adapter<PaycheckAdapter.ViewHolder> {

    private List<Paycheck> items;

    public PaycheckAdapter(List<Paycheck> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public PaycheckAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.home_summary_info_paycheck, parent, false);
        return new PaycheckAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PaycheckAdapter.ViewHolder holder, int position) {
        Paycheck paycheck = items.get(position);

        holder.binding.setVariable(BR.paycheck, paycheck);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding binding;

        ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}

