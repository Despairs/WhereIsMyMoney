package org.despairs.wmm.repository;

import org.despairs.wmm.repository.entity.Sms;

import java.util.List;

/**
 * Created by EKovtunenko on 15.11.2019.
 */
public interface SmsRepository {
    List<Sms> listBySender(String sender);
}
