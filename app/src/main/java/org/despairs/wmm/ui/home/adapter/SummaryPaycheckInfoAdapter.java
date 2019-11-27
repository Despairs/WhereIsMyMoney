package org.despairs.wmm.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.despairs.wmm.BR;
import org.despairs.wmm.R;
import org.despairs.wmm.databinding.HomeSummaryInfoBinding;
import org.despairs.wmm.ui.home.entity.SummaryPaycheckInfo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by EKovtunenko on 18.11.2019.
 */
public class SummaryPaycheckInfoAdapter extends RecyclerView.Adapter<SummaryPaycheckInfoAdapter.ViewHolder> {

    private List<SummaryPaycheckInfo> items;

    public SummaryPaycheckInfoAdapter(List<SummaryPaycheckInfo> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HomeSummaryInfoBinding viewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.home_summary_info, parent, false);
        return new ViewHolder(viewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SummaryPaycheckInfo info = items.get(position);

        holder.binding.paycheckList.setHasFixedSize(true);
        holder.binding.paycheckList.setAdapter(new PaycheckAdapter(info.getItems()));
        holder.binding.paycheckList.setVisibility(info.isExpanded() ? View.VISIBLE : View.GONE);
        holder.binding.setVariable(BR.info, info);
        holder.binding.executePendingBindings();

        holder.binding.homeSummaryList.setOnClickListener(v -> {
            info.setExpanded(!info.isExpanded());
            notifyItemChanged(position);
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private HomeSummaryInfoBinding binding;

        ViewHolder(HomeSummaryInfoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}