package com.study.ian.rightway.result;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ScrollView;

import com.study.ian.rightway.R;
import com.study.ian.rightway.customView.EventView;
import com.study.ian.rightway.customView.HighwayNameView;
import com.study.ian.rightway.customView.SpeedView;

public class EventResultActivity extends AppCompatActivity {

    private final String TAG = "EventResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_result);

        initView();
    }

    private void initView() {
        EventView eventView = findViewById(R.id.eventView);
        ScrollView scrollView = findViewById(R.id.eventScrollView);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.eventRefreshLayout);

        // set connect code
        Bundle bundle = getIntent().getExtras();
        eventView.setConnectCode(bundle.getString(HighwayNameView.KEY_CONNECT));

        eventView.setScrollView(scrollView);
        refreshLayout.setOnRefreshListener(() -> {
            if (eventView.updateGatewayStatus()) {
                refreshLayout.setRefreshing(false);
            }
        });
    }
}
