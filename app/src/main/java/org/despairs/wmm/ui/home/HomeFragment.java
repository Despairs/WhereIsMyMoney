package org.despairs.wmm.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.despairs.wmm.R;
import org.despairs.wmm.repository.JdbcSmsRepository;
import org.despairs.wmm.repository.entity.Sms;
import org.despairs.wmm.ui.home.adapter.SalaryAdapter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {

    private static final Pattern SALARY_SMS_PATTERN = Pattern.compile(".*\\s(\\d+(\\.\\d+)?)р Баланс.*");

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        if (root instanceof RecyclerView) {
            Context context = root.getContext();
            RecyclerView recyclerView = (RecyclerView) root;
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(new SalaryAdapter(getSalary()));
            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        }

        return root;
    }

    private List<Salary> getSalary() {
        List<Sms> messages = new JdbcSmsRepository().listBySenderAndMessageContainingWords("900", "зарплаты", "отпускных", "аванса", "премии");
        //@TODO не использовать Salary как промежуточное хранилище?
        List<Salary> list = messages.stream()
                .map(this::tryToParseAsSalary)
                .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
                .collect(Collectors.toList());

        Map<LocalDateTime, Double> collect = list.stream()
                .collect(Collectors.groupingBy(Salary::getBillingPeriod, Collectors.summingDouble(Salary::getAmount)));

        return collect.entrySet().stream()
                .map(entry -> new Salary(entry.getKey(), entry.getValue(), getAllByPeriod(list, entry.getKey())))
                .sorted((o1, o2) -> o2.getBillingPeriod().compareTo(o1.getBillingPeriod()))
                .collect(Collectors.toList());
    }

    private List<Salary> getAllByPeriod(List<Salary> salaries, LocalDateTime billingPeriod) {
        return salaries.stream()
                .filter(s -> s.billingPeriod.equals(billingPeriod))
                .collect(Collectors.toList());
    }

    private Optional<Salary> tryToParseAsSalary(Sms sms) {
        Optional<Salary> ret = Optional.empty();
        Matcher matcher = SALARY_SMS_PATTERN.matcher(sms.getMessage());
        if (matcher.matches()) {
            double amount = Double.parseDouble(matcher.group(1));
            LocalDateTime period = getBillingPeriod(sms);
            ret = Optional.of(new Salary(period, amount, getType(sms)));
        }
        return ret;
    }

    private String getType(Sms sms) {
        LocalDateTime date = sms.getDate();
        String message = sms.getMessage();
        if (message.contains("зарплаты")) {
            return date.getDayOfMonth() < 20 ? "Остатки" : "Аванс";
        } else if (message.contains("отпускных")) {
            return "Отпускные";
        } else if (message.contains("аванса")) {
            return "Аванс";
        } else if (message.contains("премии")) {
            return "Премия";
        } else return "Неизвестно";
    }

    private LocalDateTime getBillingPeriod(Sms sms) {
        LocalDateTime date = sms.getDate();
        String message = sms.getMessage();
        if (date.getDayOfMonth() < 20 && !message.contains("отпускных")) {
            date = date.minusMonths(1);
        }
        return date.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
    }

    public static class Salary {

        public double amount;
        public LocalDateTime billingPeriod;
        public String type;
        public List<Salary> items = Collections.emptyList();
        public boolean expanded;

        public Salary(LocalDateTime billingPeriod, double amount) {
            this.amount = amount;
            this.billingPeriod = billingPeriod;
        }

        public Salary(LocalDateTime billingPeriod, double amount, List<Salary> items) {
            this.amount = amount;
            this.billingPeriod = billingPeriod;
            this.items = items;
        }

        public Salary(LocalDateTime billingPeriod, double amount, String type) {
            this.amount = amount;
            this.billingPeriod = billingPeriod;
            this.type = type;
        }

        public double getAmount() {
            return amount;
        }

        public LocalDateTime getBillingPeriod() {
            return billingPeriod;
        }
    }

}