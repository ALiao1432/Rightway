package com.study.ian.rightway.customView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.study.ian.rightway.util.GatewayInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SpeedView extends View {

    private final String TAG = "SpeedView";

    private ScrollView scrollView;
    private List<GatewayInfo> infoList;
    private Paint stringPaint;
    private Paint ovalPaint;
    private Paint linePaint;
    private Paint speedTextPaint;
    private Paint speedTextBoldPaint;
    private Paint speedCirclePaint;
    private String[] speedColors = {
            "#512DA8", // speed < 20
            "#D32F2F", // 20 <= speed <= 39
            "#F57C00", // 40 <= speed <= 59
            "#ffff00", // 60 <= speed <= 79
            "#388E3C", // 40 <= speed
    };
    private boolean isGatewayInfoReady = false;
    private int wSize;
    private int hSize;
    private float singleGateSize = 500;

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "MotionEvent.ACTION_DOWN");
                requestLayout();
                break;
        }
        performClick();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        wSize = MeasureSpec.getSize(widthMeasureSpec);
        hSize = MeasureSpec.getSize(heightMeasureSpec);

        if (!isGatewayInfoReady) {
            setMeasuredDimension(wSize, hSize);
        } else {
            setMeasuredDimension(wSize, Math.round(infoList.size() * singleGateSize));
        }
    }

    public SpeedView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initPaint();
    }

    public void setScrollView(ScrollView scrollView) {
        this.scrollView = scrollView;
    }

    private void initPaint() {
        stringPaint = getPaint("#fcfcfc", Paint.Style.FILL, 80);
        ovalPaint = getPaint("#202124", Paint.Style.FILL, 200);
        linePaint = getPaint("#00897b", Paint.Style.STROKE, 200);
        speedTextPaint = getPaint("#fcfcfc", Paint.Style.FILL, 80);
        speedTextBoldPaint = getPaint("#000000", Paint.Style.STROKE, 80);
        speedCirclePaint = getPaint(speedColors[0], Paint.Style.FILL, 0);

        speedTextBoldPaint.setStrokeWidth(1.5f);
    }

    private Paint getPaint(String color, Paint.Style style, float textSize) {
        Paint p = new Paint();

        p.setStyle(style);
        p.setColor(Color.parseColor(color));
        p.setTextSize(textSize);
        p.setAntiAlias(true);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(12);

        return p;
    }

    public void setConnectCode(String code) {
        infoList = getHighwayStatus(code);
    }

    private List<GatewayInfo> getHighwayStatus(String connectCode) {
        List<GatewayInfo> tempList = new ArrayList<>();
        isGatewayInfoReady = false;

        new Thread(() -> {
            try {
                Log.d(TAG, "http://1968.freeway.gov.tw/traffic/index/fid/" + connectCode);
                Document document = Jsoup.connect("http://1968.freeway.gov.tw/traffic/index/fid/" + connectCode).get();
                Element content = document.getElementById("secs_body");
                Elements secNames = content.getElementsByClass("sec_name");
                Elements speedLeft = content.getElementsByClass("speed speedLeft");
                Elements speedRight = content.getElementsByClass("speed speedRight");

                Log.d(TAG, "secNames : " + secNames.size());
                for (int i = 0; i < secNames.size(); i++) {
                    StringTokenizer tokenizer = new StringTokenizer(secNames.get(i).text(), " -");

                    tempList.add(new GatewayInfo(
                            tokenizer.nextToken(),
                            speedLeft.get(i).text(),
                            speedRight.get(i).text()
                    ));

                    if (i == (secNames.size() - 1)) {
                        tempList.add(new GatewayInfo(
                                tokenizer.nextToken()
                        ));
                    }
                }
                isGatewayInfoReady = true;

                ((Activity) getContext()).runOnUiThread(() -> {
                    requestLayout();
                    invalidate();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();

        return tempList;
    }

    private int getSpeedCircleColor(String speedString) {
        int speed = Integer.valueOf(speedString);

        if (speed < 20) {
            return Color.parseColor(speedColors[0]);
        } else if (speed <= 39) {
            return Color.parseColor(speedColors[1]);
        } else if (speed <= 59) {
            return Color.parseColor(speedColors[2]);
        } else if (speed <= 79) {
            return Color.parseColor(speedColors[3]);
        } else {
            return Color.parseColor(speedColors[4]);
        }
    }

    public void drawGateway(Canvas canvas, GatewayInfo info, int number) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.parseColor("#212121"));

        if (isGatewayInfoReady) {
            int i = 0;
            for (GatewayInfo info : infoList) {
//                canvas.drawLine(0, (i + 1) * singleGateSize, wSize, (i + 1) * singleGateSize, new Paint());
//                drawGateway(canvas, info, i);
                i++;
            }
        }
    }
}
