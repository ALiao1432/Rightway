package com.study.ian.rightway.util;

import android.content.Context;

import java.util.List;

public class SvgData {

    private static final String TAG = "SvgData";

    private final Context context;
    private DataPath morphPath;

    SvgData(Context context) {
        this.context = context;
    }

    public void setMorphRes(int fromId, int toId, MorphView view) {
        List<String> fromCmdList = this.getPathData(context, fromId);
        List<String> toCmdList = this.getPathData(context, toId);
        float[] viewports = getViewport(context, fromId);
        float[] scaleFactors = {
                view.getW_SIZE() / viewports[0],
                view.getH_SIZE() / viewports[1],
        };

        morphPath = new DataPath(scaleFactors);
        morphPath.setMorphPath(fromCmdList, toCmdList);
    }

    public DataPath getMorphPath(float mFactor) {
        morphPath.getMorphPath(mFactor);
        return morphPath;
    }

    // get path for single vector drawable
    public DataPath getPath(int vdId, MorphView view) {
        float[] viewports = getViewport(context, vdId);
        float[] scaleFactors = {
                view.getW_SIZE() / viewports[0],
                view.getH_SIZE() / viewports[1],
        };
        List<String> data = getPathData(context, vdId);
        DataPath dPath = new DataPath(data, scaleFactors);

        dPath.getPath();
        return dPath;
    }

    private List<String> getPathData(Context context, int vdId) {
        List<String> pathData;
        XmlLabelParser xmlLabelParser = new XmlLabelParser(context, vdId);
        pathData = xmlLabelParser.getLabelData("path", "pathData");

        return pathData;
    }

    private float[] getViewport(Context context, int vdId) {
        XmlLabelParser x1 = new XmlLabelParser(context, vdId);
        XmlLabelParser x2 = new XmlLabelParser(context, vdId);

        String[] viewportStrings = {
                x1.getLabelData("vector", "viewportWidth").get(0),
                x2.getLabelData("vector", "viewportHeight").get(0)
        };

        return new float[]{
                Float.valueOf(viewportStrings[0]),
                Float.valueOf(viewportStrings[1])
        };
    }
}
