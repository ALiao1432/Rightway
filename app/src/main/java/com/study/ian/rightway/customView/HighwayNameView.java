package com.study.ian.rightway.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.study.ian.rightway.R;

public class HighwayNameView extends View {

    private final String TAG = "HighwayNameView";

    private Rect highwayNameRect = new Rect();
    private RectF[] rectF;
    private Paint stringPaint;
    private Paint ovalPaint;

    private final String[] highwayNames = getResources().getStringArray(R.array.highway_names);
    private int wSize;
    private int hSize;
    private int singleSize = 360;
    private boolean isMoved = false;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        wSize = MeasureSpec.getSize(widthMeasureSpec);
        hSize = highwayNames.length * singleSize;

        initRect();
        setMeasuredDimension(wSize, hSize);
    }

    HighwayNameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        initPaint();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "MotionEvent.ACTION_DOWN");
                isMoved = false;
                return true;
            case MotionEvent.ACTION_UP:
                int x = Math.round(event.getX());
                int y = Math.round(event.getY());
                Log.d(TAG, "MotionEvent.ACTION_UP");
                for (int i = 0; i < rectF.length; i++) {
                    if (!isMoved && rectF[i].contains(x, y)) {
                        Log.d(TAG, "touch : " + highwayNames[i]);
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
        stringPaint = getPaint("#fcfcfc", Paint.Style.FILL, 110);
        ovalPaint = getPaint("#00897b", Paint.Style.STROKE, 200);
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
