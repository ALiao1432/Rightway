package com.study.ian.rightway;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.study.ian.rightway.select.EventSelectActivity;
import com.study.ian.rightway.select.SpeedSelectActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSelectSpeed = findViewById(R.id.btnSelectSpeed);
        Button btnSelectEvent = findViewById(R.id.btnSelectEvent);

        Button.OnClickListener onClickListener = view -> {
            Intent intent = new Intent();

            switch (view.getId()) {
                case R.id.btnSelectSpeed:
                    intent.setClass(this, SpeedSelectActivity.class);
                    break;
                case R.id.btnSelectEvent:
                    intent.setClass(this, EventSelectActivity.class);
                    break;
            }

            startActivity(intent);
        };

        btnSelectSpeed.setOnClickListener(onClickListener);
        btnSelectEvent.setOnClickListener(onClickListener);
    }
}
