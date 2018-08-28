package com.study.ian.rightway.customView;

import android.annotation.SuppressLint;
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
import android.widget.ScrollView;

import com.study.ian.rightway.R;
import com.study.ian.rightway.util.DataPath;
import com.study.ian.rightway.util.IncidentInfo;
import com.study.ian.rightway.util.MorphView;
import com.study.ian.rightway.util.SvgData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventView extends MorphView {

    private final String TAG = "EventView";

    private ScrollView scrollView;
    private List<IncidentInfo> infoList;
    private Paint stringPaint;
    private Paint linePaint;
    private final Rect measureRect = new Rect();
    private final RectF toUpRectF = new RectF();
    private final RectF toDownRectF = new RectF();
    private DataPath toUpPath;
    private DataPath toDownPath;
    private DataPath smilePath;
    private final SvgData svgData = new SvgData(this.getContext());
    private String connectCode;
    private boolean isGatewayInfoReady = false;
    private int wSize;
    private int hSize;
    private int totalHSize = 0;
    private float paintWidth;
    private float stringSize;
    private float singleGateSize;
    private float upDownRectSize;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:
                int x = Math.round(event.getX());
                int y = Math.round(event.getY());

                if (toUpRectF.contains(x, y)) {
                    new Thread(() -> scrollView.smoothScrollTo(wSize / 2, 0)).start();
                } else if (toDownRectF.contains(x, y)) {
                    new Thread(() -> scrollView.smoothScrollTo(wSize / 2, totalHSize)).start();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        wSize = MeasureSpec.getSize(widthMeasureSpec);
        hSize = MeasureSpec.getSize(heightMeasureSpec);
        paintWidth = wSize * .0125f;
        stringSize = wSize * .08f;
        singleGateSize = hSize * .44f;
        upDownRectSize = hSize * .12f;

        initPaint();

        if (!isGatewayInfoReady) {
            setMeasuredDimension(wSize, hSize);
        } else {
            if (infoList.size() < 2) {
                setMeasuredDimension(wSize, hSize);
            } else {
                totalHSize = Math.round(infoList.size() * singleGateSize + upDownRectSize * 2);
                setMeasuredDimension(
                        wSize,
                        totalHSize
                );
            }
        }
        initVdPath(totalHSize);
        initRectF();
    }

    public EventView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollView(ScrollView scrollView) {
        this.scrollView = scrollView;
    }

    private void initPaint() {
        stringPaint = getPaint("#fcfcfc", Paint.Style.FILL, stringSize, paintWidth);
        linePaint = getPaint("#00897b", Paint.Style.STROKE, 200, paintWidth);
    }

    private void initVdPath(int totalHSize) {
        Matrix matrix = new Matrix();
        RectF tempRectF = new RectF();

        // initial path form vector drawable
        toDownPath = svgData.getPath(R.drawable.vd_to_down, this);
        toUpPath = svgData.getPath(R.drawable.vd_to_up, this);
        smilePath = svgData.getPath(R.drawable.vd_smile, this);

        // initial matrix
        float s = scaleFunction(wSize);
        matrix.setScale(s, s);

        // enlarge the size of path
        toDownPath.transform(matrix);
        toUpPath.transform(matrix);
        matrix.setScale(s * 2, s * 2); //smile path need to be bigger!!!
        smilePath.transform(matrix);

        // move to the center
        toUpPath.computeBounds(tempRectF, true);
        toUpPath.offset(
                (wSize - tempRectF.width() * 2f) * .5f,
                totalHSize - upDownRectSize - (upDownRectSize - tempRectF.height()) * .8f
        );
        toDownPath.computeBounds(tempRectF, true);
        toDownPath.offset(
                (wSize - tempRectF.width() * 2f) * .5f,
                (upDownRectSize - tempRectF.height()) * .8f
        );
        smilePath.computeBounds(tempRectF, true);
        smilePath.offset(
                (wSize - tempRectF.width()) * .5f,
                (hSize - tempRectF.height()) * .5f
        );
    }

    private float scaleFunction(float w) {
        return 0.0025f * (w - 1080) + 1.6f;
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

    private List<IncidentInfo> getHighwayStatus(String connectCode) {
        this.connectCode = connectCode;
        isGatewayInfoReady = false;
        List<TextNode> tempTextNodeList = new ArrayList<>();
        List<IncidentInfo> tempList = new ArrayList<>();
        List<Integer> fogList = new ArrayList<>();

        new Thread(() -> {
            try {
                Document document = Jsoup.connect("https://1968.freeway.gov.tw/incident/getincidents/fid/" + connectCode).get();
                Elements body = document.getElementsByTag("body");

                for (TextNode node : body.get(0).textNodes()) {
                    if (!node.text().equals(" ")) {
                        tempTextNodeList.add(node);
                    }
                }

                tempTextNodeList.stream()
                        .filter(s -> s.toString().equals(getResources().getString(R.string.fog)))
                        .forEach(s -> fogList.add(tempTextNodeList.indexOf(s)));
                fogList.forEach(i -> tempTextNodeList.add(i - 1, new TextNode(getResources().getString(R.string.both_way))));

                for (int i = 0; i < tempTextNodeList.size(); i += 4) {
                    tempList.add(new IncidentInfo(
                            tempTextNodeList.get(i).text(),
                            tempTextNodeList.get(i + 1).text(),
                            tempTextNodeList.get(i + 2).text(),
                            tempTextNodeList.get(i + 3).text()
                    ));
                }

                // show log
//                for (IncidentInfo incidentInfo : tempList) {
//                    Log.d(TAG, incidentInfo.toString());
//                }
//                displayLongLog(a.toString());
//                Log.d(TAG, "secNames : " + secNames.size());

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

    private void drawGateway(Canvas canvas, IncidentInfo info, int number) {
        if (infoList.size() > 2) {
            // draw up and down arrow
            canvas.drawPath(toUpPath, linePaint);
            canvas.drawPath(toDownPath, linePaint);
        }

        canvas.drawLine(
                wSize * .1f,
                upDownRectSize + singleGateSize * number + singleGateSize * .15f,
                wSize * .1f,
                upDownRectSize + singleGateSize * number + singleGateSize * .85f,
                linePaint
        );
        canvas.drawText(
                info.getLocation(),
                wSize * .15f,
                upDownRectSize + singleGateSize * number + singleGateSize * .25f,
                stringPaint
        );
        canvas.drawText(
                info.getDir(),
                wSize * .15f,
                upDownRectSize + singleGateSize * number + singleGateSize * .44f,
                stringPaint
        );
        canvas.drawText(
                info.getTime(),
                wSize * .15f,
                upDownRectSize + singleGateSize * number + singleGateSize * .63f,
                stringPaint
        );
        canvas.drawText(
                info.getDescription(),
                wSize * .15f,
                upDownRectSize + singleGateSize * number + singleGateSize * .82f,
                stringPaint
        );
    }

    private void drawNoIncident(Canvas canvas) {
        canvas.drawPath(smilePath, linePaint);

        String s = getResources().getText(R.string.no_incident).toString();
        stringPaint.getTextBounds(s, 0, s.length(), measureRect);
        canvas.drawText(
                s,
                (wSize - measureRect.width()) * .5f,
                hSize * .8f,
                stringPaint
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.parseColor("#212121"));

        int i = 0;
        if (infoList.size() != 0) {
            for (IncidentInfo incidentInfo : infoList) {
                drawGateway(canvas, incidentInfo, i);
                i++;
            }
        } else {
            drawNoIncident(canvas);
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
