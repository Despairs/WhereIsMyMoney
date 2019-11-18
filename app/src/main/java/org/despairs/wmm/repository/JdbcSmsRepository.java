package org.despairs.wmm.repository;

import android.content.ContentResolver;
import android.database.Cursor;

import org.despairs.wmm.App;
import org.despairs.wmm.repository.entity.Sms;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static android.provider.Telephony.Sms.Inbox.CONTENT_URI;
import static android.provider.Telephony.TextBasedSmsColumns.ADDRESS;
import static android.provider.Telephony.TextBasedSmsColumns.BODY;
import static android.provider.Telephony.TextBasedSmsColumns.DATE;

/**
 * Created by EKovtunenko on 15.11.2019.
 */
public class JdbcSmsRepository implements SmsRepository {

    private static final String[] SELECTION_CONTENT = {_ID, ADDRESS, BODY, DATE};

    @Override
    public List<Sms> listBySender(String sender) {
        List<Sms> ret = new ArrayList<>();

        ContentResolver contentResolver = App.applicationContext.getContentResolver();

        try (Cursor c = query(sender, contentResolver)) {
            if (c.moveToFirst()) {
                for (int i = 0; i < c.getCount(); i++) {
                    String text = c.getString(c.getColumnIndexOrThrow(BODY));
                    long dateInMillis = c.getLong(c.getColumnIndexOrThrow(DATE));

                    LocalDateTime date =
                            LocalDateTime.ofInstant(Instant.ofEpochMilli(dateInMillis), ZoneId.systemDefault());

                    ret.add(new Sms(text, date));
                    c.moveToNext();
                }
            }
        }
        return ret;
    }

    private Cursor query(String sender, ContentResolver contentResolver) {
        return contentResolver.query(
                CONTENT_URI,
                SELECTION_CONTENT,
                ADDRESS + " = ?",
                new String[]{sender},
                DATE + " DESC");
    }
}
