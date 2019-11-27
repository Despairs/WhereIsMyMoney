package org.despairs.wmm.ui.home.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by EKovtunenko on 27.11.2019.
 */
@Data
@AllArgsConstructor
public class Paycheck {

    private final LocalDateTime billingPeriod;
    private final LocalDateTime date;
    private final double amount;
    private final String type;

}