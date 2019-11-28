package org.despairs.wmm.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.despairs.wmm.databinding.FragmentHomeBinding;
import org.despairs.wmm.repository.JdbcSmsRepository;
import org.despairs.wmm.repository.entity.Sms;
import org.despairs.wmm.ui.home.adapter.SummaryPaycheckInfoAdapter;
import org.despairs.wmm.ui.home.entity.Paycheck;
import org.despairs.wmm.ui.home.entity.SummaryPaycheckInfo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.averagingInt;
import static org.despairs.wmm.utils.Converters.convertBillingPeriodToString;
import static org.despairs.wmm.utils.Converters.convertToStringDate;

public class HomeFragment extends Fragment {

    private final JdbcSmsRepository repo = new JdbcSmsRepository();
    private List<Paycheck> paycheckEntries;
    private List<SummaryPaycheckInfo> summaryInfos;
    private Map<LocalDateTime, List<Paycheck>> paycheckByPeriod;
    private Map<String, Double> averagePaycheckDay;

    private static final Pattern PAYCHECK_SMS_PATTERN = Pattern.compile(".*\\s(\\d+(\\.\\d+)?)р Баланс.*");

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentHomeBinding binding = FragmentHomeBinding.inflate(inflater);
        View root = binding.getRoot();
        binding.homeSummaryView.addItemDecoration(new DividerItemDecoration(root.getContext(), DividerItemDecoration.VERTICAL));
        binding.homeSummaryView.setAdapter(new SummaryPaycheckInfoAdapter(getSummaryInfo()));
        binding.homeSummaryView.setHasFixedSize(true);

        Paycheck lastPaycheck = paycheckEntries.stream().max((p1, p2) -> p1.getBillingPeriod().compareTo(p2.getBillingPeriod())).get();

        String lastPaycheckType = lastPaycheck.getType();
        String lastPaycheckValue = String.format("%s прислали %s за %s", convertToStringDate(lastPaycheck.getDate()), lastPaycheckType, convertBillingPeriodToString(lastPaycheck.getBillingPeriod()));
        binding.lastPaycheck.setText(lastPaycheckValue);

        String nextPaycheckType = lastPaycheck.getDate().getDayOfMonth() < 20 ? "Аванс" : "Остатки";
        int averageNextPaycheckDay = averagePaycheckDay.getOrDefault(nextPaycheckType, 0D).intValue();
        LocalDateTime averageNextPaycheckDate = LocalDateTime.now().withDayOfMonth(averageNextPaycheckDay).plusMonths(1);
        binding.nextPaycheckDaysValue.setText(String.format("%s потенциально пришлют %s", convertToStringDate(averageNextPaycheckDate), nextPaycheckType));
        return root;
    }

    private List<SummaryPaycheckInfo> getSummaryInfo() {
        if (summaryInfos == null) {
            paycheckEntries = getPaycheckEntries();
            averagePaycheckDay = paycheckEntries.stream()
                    .collect(Collectors.groupingBy(Paycheck::getType, averagingInt(p -> p.getDate().getDayOfMonth())));
            paycheckByPeriod = paycheckEntries.stream()
                    .collect(Collectors.groupingBy(Paycheck::getBillingPeriod));
            summaryInfos = paycheckByPeriod.keySet().stream()
                    .map(period -> {
                        List<Paycheck> entries = paycheckByPeriod.getOrDefault(period, emptyList());
                        return SummaryPaycheckInfo.builder()
                                .billingPeriod(period)
                                .amount(entries.stream().map(Paycheck::getAmount).mapToDouble(f -> f).sum())
                                .items(entries)
                                .build();
                    })
                    .sorted((o1, o2) -> o2.getBillingPeriod().compareTo(o1.getBillingPeriod()))
                    .collect(Collectors.toList());

        }
        return summaryInfos;
    }

    private List<Paycheck> getPaycheckEntries() {
        List<Sms> messages = repo.listBySenderAndMessageContainingWords("900", "зарплаты", "отпускных", "аванса", "премии");
        return messages.stream()
                .map(this::tryToParseAsPaycheck)
                .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
                .collect(Collectors.toList());
    }

    private Optional<Paycheck> tryToParseAsPaycheck(Sms sms) {
        Optional<Paycheck> ret = Optional.empty();
        Matcher matcher = PAYCHECK_SMS_PATTERN.matcher(sms.getMessage());
        if (matcher.matches()) {
            double amount = Double.parseDouble(matcher.group(1));
            LocalDateTime period = getBillingPeriod(sms);
            ret = Optional.of(new Paycheck(period, sms.getDate(), amount, getType(sms)));
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

}