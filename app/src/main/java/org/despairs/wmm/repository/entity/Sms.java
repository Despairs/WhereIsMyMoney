package org.despairs.wmm.repository.entity;

import java.time.LocalDateTime;

/**
 * Created by EKovtunenko on 15.11.2019.
 */
public class Sms {

    public Sms(String message, LocalDateTime date) {
        this.message = message;
        this.date = date;
    }

    private String message;
    private LocalDateTime date;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
