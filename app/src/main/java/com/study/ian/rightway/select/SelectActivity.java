package com.study.ian.rightway.select;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.study.ian.rightway.R;
import com.study.ian.rightway.customView.HighwayNameView;

public class SelectActivity extends AppCompatActivity {

    private final String TAG = "SelectActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        initView();
    }

    private void initView() {
        HighwayNameView highwayNameView = findViewById(R.id.highwayNameView);

        highwayNameView.setEventOrSpeed(getIntent().getExtras().getString("CHOSE"));
        Log.d(TAG, getIntent().getExtras().getString("CHOSE"));
    }
}
