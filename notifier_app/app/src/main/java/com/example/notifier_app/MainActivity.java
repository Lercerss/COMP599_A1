package com.example.notifier_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.notifier_app.MESSAGE";
    private static final String CHANNEL_ID = "testChannel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();

        Button buttonShowNotification = findViewById(R.id.button);
        Button detectionButton = findViewById(R.id.detection_button);
        EditText editText = (EditText) findViewById(R.id.editText);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "testChannel")
                .setSmallIcon(R.drawable.ic_baseline_child_care_24)
                .setContentTitle("New Notification")
                .setContentText("Message: " + editText.getText().toString())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        buttonShowNotification.setOnClickListener(v -> {

            String message = getMessage(v);
            builder.setContentText(message);
            notificationManager.notify(100, builder.build());
        });
        detectionButton.setOnClickListener( v -> {
            Intent intent = new Intent(this, ListenerDetectionActivity.class);
            startActivity(intent);
        });
    }

    public String getMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        return message;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}