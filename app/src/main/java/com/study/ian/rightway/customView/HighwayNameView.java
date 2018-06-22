package com.study.ian.rightway.customView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.study.ian.rightway.R;
import com.study.ian.rightway.result.SpeedResultActivity;

public class HighwayNameView extends View {

    private final String TAG = "HighwayNameView";

    private Rect highwayNameRect = new Rect();
    private RectF[] rectF;
    private Paint stringPaint;
    private Paint ovalPaint;

    public final static String KEY_CONNECT = "KEY_CONNECT";
    private final String[] highwayNames = getResources().getStringArray(R.array.highway_names);
    private final String[] highwayConnectCode = getResources().getStringArray(R.array.highway_connect_code);
    private int wSize;
    private int hSize;
    private int singleSize;
    private float stringSize;
    private float paintWidth;
    private boolean isMoved = false;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        wSize = MeasureSpec.getSize(widthMeasureSpec);
        hSize = MeasureSpec.getSize(widthMeasureSpec);

        stringSize = wSize * .1f;
        paintWidth = wSize * .0125f;
        singleSize = Math.round(hSize * .325f);
        hSize = highwayNames.length * singleSize;

        initRect();
        initPaint();
        setMeasuredDimension(wSize, hSize);
    }

    HighwayNameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isMoved = false;
                return true;
            case MotionEvent.ACTION_UP:
                int x = Math.round(event.getX());
                int y = Math.round(event.getY());

                for (int i = 0; i < rectF.length; i++) {
                    if (!isMoved && rectF[i].contains(x, y)) {
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent();

                        bundle.putString(KEY_CONNECT, highwayConnectCode[i]);
                        intent.setClass(this.getContext(), SpeedResultActivity.class);
                        intent.putExtras(bundle);
                        this.getContext().startActivity(intent);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "MotionEvent.ACTION_MOVE");
                isMoved = true;
                return true;
        }

        performClick();
        return super.onTouchEvent(event);
    }

    private void initRect() {
        rectF = new RectF[highwayNames.length];

        for (int i = 0; i < highwayNames.length; i++) {
            rectF[i] = new RectF(
                    wSize * .1f,
                    singleSize * .2f + i * singleSize,
                    wSize * .9f,
                    singleSize * .8f + i * singleSize
            );
        }
    }

    private void initPaint() {
        stringPaint = getPaint("#fcfcfc", Paint.Style.FILL, stringSize, paintWidth);
        ovalPaint = getPaint("#00897b", Paint.Style.STROKE, 200, paintWidth);
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.parseColor("#212121"));

        for (int i = 0; i < highwayNames.length; i++) {
            stringPaint.getTextBounds(highwayNames[i], 0, highwayNames[i].length(), highwayNameRect);
            canvas.drawText(
                    highwayNames[i],
                    (wSize - highwayNameRect.width()) * .5f,
                    (i + 1) * singleSize - (singleSize - highwayNameRect.height()) * .5f,
                    stringPaint
            );
            canvas.drawRoundRect(rectF[i], 150, 150, ovalPaint);
        }
    }
}
