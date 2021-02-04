package com.github.chagall.notificationlistenerexample;

import android.app.Notification;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * MIT License
 * <p>
 * Copyright (c) 2016 Fábio Alves Martins Pereira (Chagall)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class NotificationListenerExampleService extends NotificationListenerService {

    public static final String INTENT_KEY = "com.github.chagall.notificationlistenerexample";
    public static final String NOTIFICATION_CONTENT_KEY = "notification_content";

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        publishContentForNotification();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        publishContentForNotification();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        publishContentForNotification();
    }

    private void publishContentForNotification() {
        StatusBarNotification[] activeNotifications = getActiveNotifications();
        if (activeNotifications == null || activeNotifications.length == 0) {
            return;
        }
        String notifContent = parseContentFromNotification(activeNotifications[0].getNotification());

        Intent intent = new Intent(INTENT_KEY);
        intent.putExtra(NOTIFICATION_CONTENT_KEY, notifContent);
        sendBroadcast(intent);
    }


    /**
     * Extracts text content from a notification
     *
     * Attempts to do so by extracting `extras` inserted by the NotificationBuilder built-in.
     * In the future, we could also directly inspect the Views in the `Notification#contentView`
     * @param notif Notification to parse content from
     * @return Concatenated text content of the notification
     */
    private String parseContentFromNotification(Notification notif) {
        ExtrasStringBuilder result = new ExtrasStringBuilder();

        String title = notif.extras.getString(Notification.EXTRA_TITLE);
        String titleBig = notif.extras.getString(Notification.EXTRA_TITLE_BIG);
        String[] people = notif.extras.getStringArray(Notification.EXTRA_PEOPLE);
        CharSequence bigText = notif.extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
        CharSequence text = notif.extras.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence[] textLines = notif.extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
        CharSequence subText = notif.extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
        CharSequence infoText = notif.extras.getCharSequence(Notification.EXTRA_INFO_TEXT);
        CharSequence summaryText = notif.extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT);
        result.addLine(title);
        result.addLine(titleBig);
        result.addLines(people);
        result.addLine(bigText);
        result.addLine(text);
        result.addLines(textLines);
        result.addLine(subText);
        result.addLine(infoText);
        result.addLine(summaryText);

        return result.toString();
    }

    /**
     * Helper class for building the content string from `Notification#extras`
     */
    private static class ExtrasStringBuilder {
        private final StringBuilder sb = new StringBuilder();

        public void addLine(String line) {
            if (line == null || line.length() == 0)
                return;
            if (sb.length() > 0)
                sb.append("\n");
            sb.append(line);
        }

        public void addLine(CharSequence line) {
            if (line == null)
                return;
            addLine(line.toString());
        }

        public void addLines(String[] lines) {
            if (lines == null)
                return;
            for (String line : lines)
                addLine(line);
        }

        public void addLines(CharSequence[] lines) {
            if (lines == null)
                return;
            for (CharSequence line : lines)
                addLine(line);
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }
}
