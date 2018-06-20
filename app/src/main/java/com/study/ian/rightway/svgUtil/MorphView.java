package com.study.ian.rightway.svgUtil;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class MorphView extends View {

    private static final String TAG = "XmlLabelParser";

    private int W_SIZE = 150;
    private int H_SIZE = 150;
    private final SvgData svgData;
    private final Paint paint = new Paint();
    private DataPath path;
    private ValueAnimator pointAnimator;
    private int currentId;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(W_SIZE, H_SIZE);
    }

    @SuppressWarnings("ClickableViewAccessibility")
    public MorphView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);

        svgData = new SvgData(context);

        initPaint();
        initAnimator();
    }

    private void initPaint() {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(W_SIZE / 10);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void initAnimator() {
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        final long animationDuration = 250;

        pointAnimator = ValueAnimator.ofFloat(0, 1, 1.025f, 1.0125f, 1);
        pointAnimator.setDuration(animationDuration);
        pointAnimator.setInterpolator(linearInterpolator);
        pointAnimator.addUpdateListener(valueAnimator -> {
            path.reset();
            path = svgData.getMorphPath((float) valueAnimator.getAnimatedValue());
            invalidate();
        });
    }

    private void initPath(int id) {
        path = svgData.getPath(id, this);
        currentId = id;
    }

    public void performAnimation(int toId) {
        svgData.setMorphRes(currentId, toId, this);
        currentId = toId;
        pointAnimator.start();
    }

    public void setSize(int w, int h) {
        this.W_SIZE = w;
        this.H_SIZE = h;
    }

    public int getW_SIZE() {
        return W_SIZE;
    }

    public int getH_SIZE() {
        return H_SIZE;
    }

    public void setCurrentId(int id) {
        initPath(id);
        invalidate();
    }

    public void setPaintColor(String color) {
        paint.setColor(Color.parseColor(color));
    }

    public void setPaintWidth(int w) {
        paint.setStrokeWidth(w);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(path, paint);
    }
}
