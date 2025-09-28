package com.schema.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 알람 리시버가 백그라운드에서 액티비티를 직접 실행할 때 발생하는 문제를 해결하기 위한
 * 눈에 보이지 않는 '징검다리' 액티비티입니다.
 */
public class AlarmTrampolineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // AlarmReceiver로부터 받은 Intent를 그대로 PrepareActivity로 전달합니다.
        Intent prepareIntent = new Intent(this, PrepareActivity.class);
        if (getIntent().getExtras() != null) {
            prepareIntent.putExtras(getIntent().getExtras());
        }
        
        startActivity(prepareIntent);
        
        // 자신의 역할은 끝났으므로 즉시 종료합니다.
        finish();
    }
}
