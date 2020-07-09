package com.zy.autoclick;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText fixedTime;
    private EditText randomTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        fixedTime = findViewById(R.id.fixedTime);
        randomTime = findViewById(R.id.randomTime);
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.open).setOnClickListener(this);
        initValue();
    }

    private void initValue() {
        String fTime = (String) SPUtils.get(this, SPUtils.fixed_time, "30");
        String rTime = (String) SPUtils.get(this, SPUtils.random_time, "20");
        fixedTime.setText(fTime);
        randomTime.setText(rTime);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                modifyTime();
                break;
            case R.id.open:
                openSetting();
                break;
            default:
                break;
        }

    }

    private void modifyTime() {
        String fTime = fixedTime.getText().toString();
        String rTime = randomTime.getText().toString();
        if (!TextUtils.isEmpty(fTime) && !TextUtils.isEmpty(rTime)) {
            SPUtils.put(this, SPUtils.fixed_time, fTime);
            SPUtils.put(this, SPUtils.random_time, rTime);
        } else {
            Toast.makeText(this, "时间不能为空", Toast.LENGTH_SHORT).show();
        }


    }

    private void openSetting() {
        try {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        } catch (Exception e) {
            Log.i(TAG, "start ACTION_ACCESSIBILITY_SETTINGS fail: " + e.getMessage());
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }finally {
            finish();
        }
    }
}
