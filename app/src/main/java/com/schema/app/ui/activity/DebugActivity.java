package com.schema.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.schema.app.R;

import java.util.Set;

public class DebugActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        TextView debugOutput = findViewById(R.id.tv_debug_output);
        StringBuilder sb = new StringBuilder();
        Intent intent = getIntent();

        sb.append("--- Intent Extras ---\n");
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Set<String> keys = extras.keySet();
            if (keys.isEmpty()) {
                sb.append("No extras found.\n");
            } else {
                for (String key : keys) {
                    Object value = extras.get(key);
                    sb.append(key).append(": ").append(value).append("\n");
                }
            }
        } else {
            sb.append("Extras bundle is null.\n");
        }

        debugOutput.setText(sb.toString());
    }
}
