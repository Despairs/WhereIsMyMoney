package org.despairs.wmm.ui.home.entity;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * Created by EKovtunenko on 27.11.2019.
 */
@Data
@Builder
public class SummaryPaycheckInfo {

    private final Double amount;
    private final LocalDateTime billingPeriod;
    private final String type;
    private List<Paycheck> items;
    private boolean expanded;
}
