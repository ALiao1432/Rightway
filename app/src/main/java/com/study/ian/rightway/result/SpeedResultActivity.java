package com.study.ian.rightway.result;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ScrollView;

import com.study.ian.rightway.R;
import com.study.ian.rightway.customView.HighwayNameView;
import com.study.ian.rightway.customView.SpeedView;

public class SpeedResultActivity extends AppCompatActivity {

    private final String TAG = "SpeedResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_result);

        initView();
    }

    private void initView() {
        SpeedView speedView = findViewById(R.id.speedView);
        ScrollView scrollView = findViewById(R.id.speedScrollView);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.speedRefreshLayout);

        // set connect code
        Bundle bundle = getIntent().getExtras();
        speedView.setConnectCode(bundle.getString(HighwayNameView.KEY_CONNECT));

        speedView.setScrollView(scrollView);
        refreshLayout.setOnRefreshListener(() -> {
            if (speedView.updateGatewayStatus()) {
                refreshLayout.setRefreshing(false);
            }
        });
    }
}
