package com.example.notifier_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListenerDetectionActivity extends AppCompatActivity {
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listener_detection);

        Button button = findViewById(R.id.go_to_settings);
        button.setOnClickListener(v -> {
            startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
        });

        RecyclerView listenerRecycler = findViewById(R.id.listener_list);
        listenerRecycler.setLayoutManager(new LinearLayoutManager(this));
        List<String> detectedListeners = detectAllListeners();
        populateRecyclerView(listenerRecycler, detectedListeners);
    }

    private void populateRecyclerView(RecyclerView recycler, List<String> detectedListeners) {
        Adapter adapter = new Adapter(detectedListeners, this.getPackageManager());
        recycler.setAdapter(adapter);
    }

    private List<String> detectAllListeners() {
        List<String> detectedPackages = new ArrayList<>();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    detectedPackages.add(cn.getPackageName());
                }
            }
        }
        return detectedPackages;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView packageNameView;
        private final TextView packageLabelView;
//        private final ImageView packageIconView;
        private final PackageManager pm;

        public ViewHolder(@NonNull View itemView, PackageManager pm) {
            super(itemView);
            packageNameView = itemView.findViewById(R.id.package_name);
            packageLabelView = itemView.findViewById(R.id.package_label);
//            packageIconView = itemView.findViewById(R.id.package_icon);
            this.pm = pm;
        }

        public void populate(String packageName) {
            TextView textView = itemView.findViewById(R.id.package_name);
            textView.setText(packageName);

            try {
                ApplicationInfo app = pm.getApplicationInfo(packageName, 0);
//                Drawable icon = pm.getApplicationIcon(app);
                String name = pm.getApplicationLabel(app).toString();
//                packageIconView.setImageDrawable(icon);
                packageLabelView.setText(name);
                packageNameView.setText(packageName);
            } catch (PackageManager.NameNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    private static class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private final List<String> packages;
        private final PackageManager pm;

        private Adapter(List<String> packages, PackageManager pm) {
            this.packages = packages;
            this.pm = pm;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detected_listener_item, parent, false);
            return new ViewHolder(view, pm);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String pi = packages.get(position);
            holder.populate(pi);
        }

        @Override
        public int getItemCount() {
            return packages.size();
        }
    }

    ;
}