package org.despairs.wmm.repository;

import android.content.ContentResolver;
import android.database.Cursor;

import org.despairs.wmm.App;
import org.despairs.wmm.repository.entity.Sms;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public List<Sms> listBySenderAndMessageContainingWords(String sender, String... containingWords) {
        List<Sms> ret = new ArrayList<>();

        ContentResolver contentResolver = App.context.getContentResolver();

        try (Cursor c = query(sender, containingWords, contentResolver)) {
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

    private Cursor query(String sender, String[] containingWords, ContentResolver contentResolver) {
        return contentResolver.query(
                CONTENT_URI,
                SELECTION_CONTENT,
                ADDRESS + " = ? AND " + likeClause(BODY, containingWords),
                argumentsArray(sender, containingWords),
                DATE + " DESC");
    }

    private String[] argumentsArray(String sender, String[] containingWords) {
        return Stream.concat(Stream.of(sender), Arrays.stream(containingWords).map(s -> "%" + s + "%"))
                .flatMap(Stream::of)
                .toArray(String[]::new);
    }

    private String likeClause(String column, String[] words) {
        return Arrays.stream(words)
                .map(s -> column + " LIKE ?")
                .collect(Collectors.joining(" OR "));
    }
}
