package com.study.ian.rightway.customView;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.ScrollView;

import com.study.ian.rightway.R;
import com.study.ian.rightway.util.DataPath;
import com.study.ian.rightway.util.GatewayInfo;
import com.study.ian.rightway.util.MorphView;
import com.study.ian.rightway.util.SvgData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SpeedView extends MorphView {

    private final String TAG = "SpeedView";

    private ScrollView scrollView;
    private List<GatewayInfo> infoList;
    private Paint stringPaint;
    private Paint ovalPaint;
    private Paint linePaint;
    private Paint speedTextPaint;
    private Paint speedTextBoldPaint;
    private Paint speedCirclePaint;
    private Rect measureRect = new Rect();
    private RectF toUpRectF = new RectF();
    private RectF toDownRectF = new RectF();
    private DataPath toUpPath;
    private DataPath toDownPath;
    private SvgData svgData = new SvgData(this.getContext());
    private String[] speedColors = {
            "#512DA8", // speed < 20
            "#D32F2F", // 20 <= speed <= 39
            "#F57C00", // 40 <= speed <= 59
            "#ffff00", // 60 <= speed <= 79
            "#388E3C", // 40 <= speed
    };
    private String connectCode;
    private boolean isGatewayInfoReady = false;
    private int wSize;
    private int hSize;
    private int totalHSize = 0;
    private float paintWidth;
    private float stringSize;
    private float speedTextSize;
    private float singleGateSize;
    private float upDownRectSize;
    private float speedCircleRadius;

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = Math.round(event.getX());
                int y = Math.round(event.getY());

                if (toUpRectF.contains(x, y)) {
                    new Thread(() -> scrollView.smoothScrollTo(wSize / 2, 0)).start();
                } else if (toDownRectF.contains(x, y)) {
                    new Thread(() -> scrollView.smoothScrollTo(wSize / 2, totalHSize)).start();
                }
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
        paintWidth = wSize * .0125f;
        stringSize = wSize * .08f;
        speedTextSize = wSize * .068f;
        singleGateSize = hSize * .3247f;
        upDownRectSize = hSize * .12f;
        speedCircleRadius = wSize * .088f;

        initPaint();

        if (!isGatewayInfoReady) {
            setMeasuredDimension(wSize, hSize);
        } else {
            totalHSize = Math.round(infoList.size() * singleGateSize + upDownRectSize * 2);
            setMeasuredDimension(
                    wSize,
                    totalHSize
            );
        }
        initVdPath(totalHSize);
        initRectF();
    }

    public SpeedView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollView(ScrollView scrollView) {
        this.scrollView = scrollView;
    }

    private void initPaint() {
        stringPaint = getPaint("#fcfcfc", Paint.Style.FILL, stringSize, paintWidth);
        ovalPaint = getPaint("#202124", Paint.Style.FILL, 200, paintWidth);
        linePaint = getPaint("#00897b", Paint.Style.STROKE, 200, paintWidth);
        speedTextPaint = getPaint("#fcfcfc", Paint.Style.FILL, speedTextSize, paintWidth);
        speedTextBoldPaint = getPaint("#000000", Paint.Style.STROKE, speedTextSize, paintWidth);
        speedCirclePaint = getPaint(speedColors[0], Paint.Style.FILL, 0, paintWidth);

        speedTextBoldPaint.setStrokeWidth(1.5f);
    }

    private void initVdPath(int totalHSize) {
        Matrix matrix = new Matrix();
        RectF tempRectF = new RectF();

        matrix.setScale(2.5f, 2.2f);

        // initial path form vector drawable
        toDownPath = svgData.getPath(R.drawable.vd_to_down, this);
        toUpPath = svgData.getPath(R.drawable.vd_to_up, this);

        // enlarge the size of path
        toDownPath.transform(matrix);
        toUpPath.transform(matrix);

        // move to the center
        toUpPath.computeBounds(tempRectF, true);
        toUpPath.offset(
                (wSize - tempRectF.width() * 2f) * .5f,
                totalHSize - upDownRectSize - (upDownRectSize - tempRectF.height()) * .75f
        );
        toDownPath.computeBounds(tempRectF, true);
        toDownPath.offset(
                (wSize - tempRectF.width() * 2f) * .5f,
                (upDownRectSize - tempRectF.height()) * .75f
        );
    }

    private void initRectF() {
        toDownPath.computeBounds(toDownRectF, true);
        toUpPath.computeBounds(toUpRectF, true);
    }

    private Paint getPaint(String color, Paint.Style style, float textSize, float width) {
        Paint p = new Paint();

        p.setStyle(style);
        p.setColor(Color.parseColor(color));
        p.setTextSize(textSize);
        p.setAntiAlias(true);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(width);

        return p;
    }

    public void setConnectCode(String code) {
        infoList = getHighwayStatus(code);
    }

    public boolean updateGatewayStatus() {
        infoList = getHighwayStatus(connectCode);
        return infoList != null;
    }

    private List<GatewayInfo> getHighwayStatus(String connectCode) {
        this.connectCode = connectCode;
        List<GatewayInfo> tempList = new ArrayList<>();
        isGatewayInfoReady = false;

        new Thread(() -> {
            try {
                Document document = Jsoup.connect("http://1968.freeway.gov.tw/traffic/index/fid/" + connectCode).get();
                Element content = document.getElementById("secs_body");
                Elements secNames = content.getElementsByClass("sec_name");
                Elements speedLeft = content.getElementsByClass("speed speedLeft");
                Elements speedRight = content.getElementsByClass("speed speedRight");

                // show log
//                displayLongLog(document.toString());
//                Log.d(TAG, "secNames : " + secNames.size());

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
        canvas.drawRoundRect(
                wSize * .075f,
                upDownRectSize + singleGateSize * number + singleGateSize * .3f,
                wSize * .925f,
                upDownRectSize + singleGateSize * number + singleGateSize * .7f,
                singleGateSize * .2f,
                singleGateSize * .2f,
                linePaint
        );

        stringPaint.getTextBounds(info.getGatewayName(), 0, info.getGatewayName().length(), measureRect);
        canvas.drawText(
                info.getGatewayName(),
                wSize * .125f,
                upDownRectSize + singleGateSize * number + (singleGateSize + measureRect.height()) * .5f,
                stringPaint
        );

        stringPaint.getTextBounds(info.getGateLocation(), 0, info.getGateLocation().length(), measureRect);
        canvas.drawText(
                info.getGateLocation(),
                wSize * .875f - measureRect.width(),
                upDownRectSize + singleGateSize * number + (singleGateSize + measureRect.height()) * .5f,
                stringPaint
        );

        // draw up and down arrow
        canvas.drawPath(toUpPath, linePaint);
        canvas.drawPath(toDownPath, linePaint);
        canvas.drawCircle(
                toUpRectF.centerX(),
                toUpRectF.centerY(),
                toUpRectF.width() * .9f,
                linePaint
        );
        canvas.drawCircle(
                toDownRectF.centerX(),
                toDownRectF.centerY(),
                toDownRectF.width() * .9f,
                linePaint
        );

        if (info.getNorthSpeed() != null) {
            // south speed line
            canvas.drawLine(
                    wSize * .25f,
                    upDownRectSize + singleGateSize * number + singleGateSize * .7f,
                    wSize * .25f,
                    upDownRectSize + singleGateSize * (number + 1) + singleGateSize * .3f,
                    linePaint
            );
            canvas.drawLine(
                    wSize * .23f,
                    upDownRectSize + singleGateSize * (number + 1) + singleGateSize * .25f,
                    wSize * .25f,
                    upDownRectSize + singleGateSize * (number + 1) + singleGateSize * .3f,
                    linePaint
            );
            canvas.drawLine(
                    wSize * .27f,
                    upDownRectSize + singleGateSize * (number + 1) + singleGateSize * .25f,
                    wSize * .25f,
                    upDownRectSize + singleGateSize * (number + 1) + singleGateSize * .3f,
                    linePaint
            );

            // north speed line
            canvas.drawLine(
                    wSize * .75f,
                    upDownRectSize + singleGateSize * number + singleGateSize * .7f,
                    wSize * .75f,
                    upDownRectSize + singleGateSize * (number + 1) + singleGateSize * .3f,
                    linePaint
            );
            canvas.drawLine(
                    wSize * .73f,
                    upDownRectSize + singleGateSize * number + singleGateSize * .75f,
                    wSize * .75f,
                    upDownRectSize + singleGateSize * number + singleGateSize * .7f,
                    linePaint
            );
            canvas.drawLine(
                    wSize * .77f,
                    upDownRectSize + singleGateSize * number + singleGateSize * .75f,
                    wSize * .75f,
                    upDownRectSize + singleGateSize * number + singleGateSize * .7f,
                    linePaint
            );

            // south speed
            speedTextPaint.getTextBounds(info.getSouthSpeed(), 0, info.getSouthSpeed().length(), measureRect);
            speedCirclePaint.setColor(getSpeedCircleColor(info.getSouthSpeed()));
            canvas.drawCircle(
                    wSize * .25f,
                    upDownRectSize + singleGateSize * (number + 0.5f) + singleGateSize * .5f,
                    speedCircleRadius,
                    speedCirclePaint
            );
            canvas.drawCircle(
                    wSize * .25f,
                    upDownRectSize + singleGateSize * (number + 0.5f) + singleGateSize * .5f,
                    speedCircleRadius,
                    linePaint
            );
            canvas.drawText(
                    info.getSouthSpeed(),
                    wSize * .25f - measureRect.width() / 2,
                    upDownRectSize + singleGateSize * (number + 0.5f) + singleGateSize * .5f + measureRect.height() / 2,
                    speedTextPaint
            );
            canvas.drawText(
                    info.getSouthSpeed(),
                    wSize * .25f - measureRect.width() / 2,
                    upDownRectSize + singleGateSize * (number + 0.5f) + singleGateSize * .5f + measureRect.height() / 2,
                    speedTextBoldPaint
            );

            // north speed
            speedTextPaint.getTextBounds(info.getSouthSpeed(), 0, info.getSouthSpeed().length(), measureRect);
            speedCirclePaint.setColor(getSpeedCircleColor(info.getNorthSpeed()));
            canvas.drawCircle(
                    wSize * .75f,
                    upDownRectSize + singleGateSize * (number + 0.5f) + singleGateSize * .5f,
                    speedCircleRadius,
                    speedCirclePaint
            );
            canvas.drawCircle(
                    wSize * .75f,
                    upDownRectSize + singleGateSize * (number + 0.5f) + singleGateSize * .5f,
                    speedCircleRadius,
                    linePaint
            );
            canvas.drawText(
                    info.getNorthSpeed(),
                    wSize * .75f - measureRect.width() / 2,
                    upDownRectSize + singleGateSize * (number + 0.5f) + singleGateSize * .5f + measureRect.height() / 2,
                    speedTextPaint
            );
            canvas.drawText(
                    info.getNorthSpeed(),
                    wSize * .75f - measureRect.width() / 2,
                    upDownRectSize + singleGateSize * (number + 0.5f) + singleGateSize * .5f + measureRect.height() / 2,
                    speedTextBoldPaint
            );
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.parseColor("#212121"));

        if (isGatewayInfoReady) {
            int i = 0;
            for (GatewayInfo info : infoList) {
                drawGateway(canvas, info, i);
                i++;
            }
        }
    }

    private void displayLongLog(String log) {
        int MAX_LOG_SIZE = 2000;

        if (log.length() > MAX_LOG_SIZE) {
            Log.d(TAG, "Display Log : " + log.substring(0, MAX_LOG_SIZE));
            displayLongLog(log.substring(MAX_LOG_SIZE, log.length()));
        } else {
            Log.d(TAG, "Display Log : " + log);
        }
    }
}
