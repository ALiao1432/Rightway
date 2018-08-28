package com.study.ian.rightway;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.util.Log;
import android.widget.Button;

import com.study.ian.rightway.select.SelectActivity;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setExitTransition(new Explode());

        Button btnSelectSpeed = findViewById(R.id.btnSelectSpeed);
        Button btnSelectEvent = findViewById(R.id.btnSelectEvent);

        Button.OnClickListener onClickListener = view -> {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();

            switch (view.getId()) {
                case R.id.btnSelectSpeed:
                    Log.d(TAG, "btnSelectSpeed");
                    bundle.putString("CHOSE", "SPEED");
                    break;
                case R.id.btnSelectEvent:
                    Log.d(TAG, "btnSelectEvent");
                    bundle.putString("CHOSE", "EVENT");
                    break;
            }
            intent.putExtras(bundle);
            intent.setClass(this, SelectActivity.class);
            startActivity(intent);
        };

        btnSelectSpeed.setOnClickListener(onClickListener);
        btnSelectEvent.setOnClickListener(onClickListener);
    }
}
