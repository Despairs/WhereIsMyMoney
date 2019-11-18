package org.despairs.wmm.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.despairs.wmm.R;
import org.despairs.wmm.repository.entity.Sms;

import java.time.format.DateTimeFormatter;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by EKovtunenko on 18.11.2019.
 */
public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.ViewHolder> {

    private List<Sms> items;

    public SmsAdapter(List<Sms> items) {
        items.removeIf(sms -> {
            boolean isNotSalary = !sms.getMessage().contains("зарплаты");
            boolean isNotOtpusk = !sms.getMessage().contains("отпускных");
            boolean isNotAvans = !sms.getMessage().contains("аванса");
            return isNotSalary && isNotOtpusk && isNotAvans;
        });
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
        Sms sms = items.get(position);

        holder.messageView.setText(sms.getMessage());
        holder.dateView.setText(DateTimeFormatter.ISO_DATE_TIME.format(sms.getDate()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView messageView;
        private TextView dateView;

        ViewHolder(View v) {
            super(v);
            messageView = v.findViewById(R.id.fragment_home_list_item_text_view_message);
            dateView = v.findViewById(R.id.fragment_home_list_item_text_view_date);
        }

    }
}