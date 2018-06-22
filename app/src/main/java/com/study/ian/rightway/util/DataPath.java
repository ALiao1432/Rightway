package com.study.ian.rightway.util;

import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class DataPath extends Path {

    private static final String TAG = "DataPath";

    private final String[] CMD_STRINGS = {
            "M",
            "C", "c",
            "L", "l",
            "H", "h",
            "V", "v",
            "Z",
    };
    private final float[] scaleFactors;
    private final List<String> fromCmdList = new ArrayList<>();
    private final List<String> toCmdList = new ArrayList<>();
    private PointF lastPointF = new PointF();
    private List<String> pathDataList;

    DataPath(List<String> data, float[] factors) {
        scaleFactors = factors;
        pathDataList = data;
    }

    DataPath(float[] factors) {
        scaleFactors = factors;
    }

    public void getPath() {
        for (String pathData : pathDataList) {
            for (String aCmd : getFullPathCmdList(pathData)) {
                switch (aCmd.charAt(0)) {
                    case 'M':
                        addMoveToCmd(aCmd);
                        break;
                    case 'C':
                        addCubicToCmd(aCmd);
                        break;
                    case 'c':
                        addRCubicToCmd(aCmd);
                        break;
                    case 'L':
                        addLineToCmd(aCmd);
                        break;
                    case 'l':
                        addRLineToCmd(aCmd);
                        break;
                    case 'H':
                        addHtoCmd(aCmd);
                        break;
                    case 'h':
                        addRHtoCmd(aCmd);
                        break;
                    case 'V':
                        addVtoCmd(aCmd);
                        break;
                    case 'v':
                        addRVtoCmd(aCmd);
                        break;
                    case 'Z':
                        addCloseCmd();
                        break;
                }
            }
        }
    }

    public void setMorphPath(List<String> fromList, List<String> toList) {
        for (int i = 0; i < fromList.size(); i++) {
            fromCmdList.addAll(getFullPathCmdList(fromList.get(i)));
            toCmdList.addAll(getFullPathCmdList(toList.get(i)));
        }
    }

    public void getMorphPath(float mFactor) {
        for (int i = 0; i < fromCmdList.size(); i++) {
            switch (fromCmdList.get(i).charAt(0)) {
                case 'M':
                    addMoveToCmd(fromCmdList.get(i), toCmdList.get(i), mFactor);
                    break;
                case 'C':
                    addCubicToCmd(fromCmdList.get(i), toCmdList.get(i), mFactor);
                    break;
                case 'c':
                    addRCubicToCmd(fromCmdList.get(i), toCmdList.get(i), mFactor);
                    break;
                case 'L':
                    addLineToCmd(fromCmdList.get(i), toCmdList.get(i), mFactor);
                    break;
                case 'l':
                    addRLineToCmd(fromCmdList.get(i), toCmdList.get(i), mFactor);
                    break;
                case 'H':
                    addHtoCmd(fromCmdList.get(i), toCmdList.get(i), mFactor);
                    break;
                case 'h':
                    addRHtoCmd(fromCmdList.get(i), toCmdList.get(i), mFactor);
                    break;
                case 'V':
                    addVtoCmd(fromCmdList.get(i), toCmdList.get(i), mFactor);
                    break;
                case 'v':
                    addRVtoCmd(fromCmdList.get(i), toCmdList.get(i), mFactor);
                    break;
                case 'Z':
                    addCloseCmd();
                    break;
            }
        }
    }

    private List<String> getFullPathCmdList(String pathData) {
        List<String> fullCmdList = new ArrayList<>();
        List<Integer> positionList = getCmdPositionList(pathData);

        for (int i = 0; i < positionList.size() - 1; i++) {
            fullCmdList.add(pathData.substring(positionList.get(i), positionList.get(i + 1)));
        }
        return fullCmdList;
    }

    private List<Integer> getCmdPositionList(String pathData) {
        List<Integer> cmdPositionList = new ArrayList<>();

        for (int i = 0; i < pathData.length(); i++) {
            for (String cmd : CMD_STRINGS) {
                if (pathData.substring(i, i + 1).contains(cmd)) {
                    cmdPositionList.add(i);
                }
            }
        }
        cmdPositionList.add(pathData.length());

        return cmdPositionList;
    }

    private void addMoveToCmd(String moveCmd) {
        PointF[] movePointFS = getPointFromCmd(moveCmd);
        int size = movePointFS.length;

        for (int i = 0; i < size; i++) {
            if (i == 0) {
                this.moveTo(movePointFS[i].x, movePointFS[i].y);
            } else {
                this.lineTo(movePointFS[i].x, movePointFS[i].y);
            }
        }

        lastPointF = movePointFS[size - 1];
    }

    private void addMoveToCmd(String fromCmd, String toCmd, float mFactor) {
        PointF[] fromPointFS = getPointFromCmd(fromCmd);
        PointF[] toPointFS = getPointFromCmd(toCmd);
        int size = fromPointFS.length;

        for (int i = 0; i < size; i++) {
            float tempX = fromPointFS[i].x + (toPointFS[i].x - fromPointFS[i].x) * mFactor;
            float tempY = fromPointFS[i].y + (toPointFS[i].y - fromPointFS[i].y) * mFactor;

            if (i == 0) {
                this.moveTo(tempX, tempY);
            } else {
                this.lineTo(tempX, tempY);
            }

            if (i == (size - 1)) {
                lastPointF.set(tempX, tempY);
            }
        }
    }

    private void addCubicToCmd(String cubicCmd) {
        PointF[] cubicPointFS = getPointFromCmd(cubicCmd);

        this.cubicTo(
                cubicPointFS[0].x, cubicPointFS[0].y,
                cubicPointFS[1].x, cubicPointFS[1].y,
                cubicPointFS[2].x, cubicPointFS[2].y
        );
        lastPointF = cubicPointFS[2];
    }

    private void addCubicToCmd(String fromCmd, String toCmd, float mFactor) {
        PointF[] fromPointFS = getPointFromCmd(fromCmd);
        PointF[] toPointFS = getPointFromCmd(toCmd);
        int size = fromPointFS.length;

        float[] tempXS = new float[3];
        float[] tempYS = new float[3];
        for (int i = 0; i < size; i++) {
            tempXS[i] = fromPointFS[i].x + (toPointFS[i].x - fromPointFS[i].x) * mFactor;
            tempYS[i] = fromPointFS[i].y + (toPointFS[i].y - fromPointFS[i].y) * mFactor;
        }
        this.cubicTo(
                tempXS[0], tempYS[0],
                tempXS[1], tempYS[1],
                tempXS[2], tempYS[2]
        );
        lastPointF.set(tempXS[2], tempYS[2]);
    }

    private void addRCubicToCmd(String rCubicCmd) {
        PointF[] rCubicPointFS = getPointFromCmd(rCubicCmd);

        this.rCubicTo(
                rCubicPointFS[0].x, rCubicPointFS[0].y,
                rCubicPointFS[1].x, rCubicPointFS[1].y,
                rCubicPointFS[2].x, rCubicPointFS[2].y
        );
        lastPointF = rCubicPointFS[2];
    }

    private void addRCubicToCmd(String fromCmd, String toCmd, float mFactor) {
        PointF[] fromPointFS = getPointFromCmd(fromCmd);
        PointF[] toPointFS = getPointFromCmd(toCmd);
        int size = fromPointFS.length;

        float[] tempXS = new float[3];
        float[] tempYS = new float[3];
        for (int i = 0; i < size; i++) {
            tempXS[i] = fromPointFS[i].x + (toPointFS[i].x - fromPointFS[i].x) * mFactor;
            tempYS[i] = fromPointFS[i].y + (toPointFS[i].y - fromPointFS[i].y) * mFactor;
        }
        this.rCubicTo(
                tempXS[0], tempYS[0],
                tempXS[1], tempYS[1],
                tempXS[2], tempYS[2]
        );
        lastPointF.set(tempXS[2], tempYS[2]);
    }

    private void addLineToCmd(String lineCmd) {
        PointF[] linePointFS = getPointFromCmd(lineCmd);
        int size = linePointFS.length;

        for (PointF pointF : linePointFS) {
            this.lineTo(pointF.x, pointF.y);
        }
        lastPointF = linePointFS[size - 1];
    }

    private void addLineToCmd(String fromCmd, String toCmd, float mFactor) {
        PointF[] fromPointFS = getPointFromCmd(fromCmd);
        PointF[] toPointFS = getPointFromCmd(toCmd);
        int size = fromPointFS.length;

        for (int i = 0; i < size; i++) {
            float tempX = fromPointFS[i].x + (toPointFS[i].x - fromPointFS[i].x) * mFactor;
            float tempY = fromPointFS[i].y + (toPointFS[i].y - fromPointFS[i].y) * mFactor;

            this.lineTo(tempX, tempY);

            if (i == size - 1) {
                lastPointF.set(tempX, tempY);
            }
        }
    }

    private void addRLineToCmd(String rLineCmd) {
        PointF[] rLinePointFS = getPointFromCmd(rLineCmd);
        int size = rLinePointFS.length;

        for (PointF pointF : rLinePointFS) {
            this.rLineTo(pointF.x, pointF.y);
        }
        lastPointF = rLinePointFS[size - 1];
    }

    private void addRLineToCmd(String fromCmd, String toCmd, float mFactor) {
        PointF[] fromPointFS = getPointFromCmd(fromCmd);
        PointF[] toPointFS = getPointFromCmd(toCmd);
        int size = fromPointFS.length;

        for (int i = 0; i < size; i++) {
            float tempX = fromPointFS[i].x + (toPointFS[i].x - fromPointFS[i].x) * mFactor;
            float tempY = fromPointFS[i].y + (toPointFS[i].y - fromPointFS[i].y) * mFactor;

            this.rLineTo(tempX, tempY);

            if (i == size - 1) {
                lastPointF.set(tempX, tempY);
            }
        }
    }

    private void addHtoCmd(String hCmd) {
        PointF[] hPointFS = getHPointFromCmd(hCmd);
        int size = hPointFS.length;

        for (PointF pointF : hPointFS) {
            this.lineTo(pointF.x, pointF.y);
        }
        lastPointF = hPointFS[size - 1];
    }

    private void addHtoCmd(String fromCmd, String toCmd, float mFactor) {
        PointF[] fromPointFS = getHPointFromCmd(fromCmd);
        PointF[] toPointFS = getHPointFromCmd(toCmd);
        int size = fromPointFS.length;

        for (int i = 0; i < size; i++) {
            float tempX = fromPointFS[i].x + (toPointFS[i].x - fromPointFS[i].x) * mFactor;
            float tempY = fromPointFS[i].y + (toPointFS[i].y - fromPointFS[i].y) * mFactor;

            this.lineTo(tempX, tempY);

            if (i == size - 1) {
                lastPointF.set(tempX, tempY);
            }
        }
    }

    private void addRHtoCmd(String rHCmd) {
        PointF[] rHPointFS = getHPointFromCmd(rHCmd);
        int size = rHPointFS.length;

        for (PointF pointF : rHPointFS) {
            this.rLineTo(pointF.x, 0);
        }
        lastPointF = rHPointFS[size - 1];
    }

    private void addRHtoCmd(String fromCmd, String toCmd, float mFactor) {
        PointF[] fromPointFS = getHPointFromCmd(fromCmd);
        PointF[] toPointFS = getHPointFromCmd(toCmd);
        int size = fromPointFS.length;

        for (int i = 0; i < size; i++) {
            float tempX = fromPointFS[i].x + (toPointFS[i].x - fromPointFS[i].x) * mFactor;
            float tempY = fromPointFS[i].y + (toPointFS[i].y - fromPointFS[i].y) * mFactor;

            this.rLineTo(tempX, 0);

            if (i == size - 1) {
                lastPointF.set(tempX, tempY);
            }
        }
    }

    private void addVtoCmd(String vCmd) {
        PointF[] vPointFS = getVPointFromCmd(vCmd);
        int size = vPointFS.length;

        for (PointF pointF : vPointFS) {
            this.lineTo(pointF.x, pointF.y);
        }
        lastPointF = vPointFS[size - 1];
    }

    private void addVtoCmd(String fromCmd, String toCmd, float mFactor) {
        PointF[] fromPointFS = getVPointFromCmd(fromCmd);
        PointF[] toPointFS = getVPointFromCmd(toCmd);
        int size = fromPointFS.length;

        for (int i = 0; i < size; i++) {
            float tempX = fromPointFS[i].x + (toPointFS[i].x - fromPointFS[i].x) * mFactor;
            float tempY = fromPointFS[i].y + (toPointFS[i].y - fromPointFS[i].y) * mFactor;

            this.lineTo(tempX, tempY);

            if (i == size - 1) {
                lastPointF.set(tempX, tempY);
            }
        }
    }

    private void addRVtoCmd(String rVCmd) {
        PointF[] rVPointFS = getVPointFromCmd(rVCmd);
        int size = rVPointFS.length;

        for (PointF pointF : rVPointFS) {
            this.rLineTo(0, pointF.y);
        }
        lastPointF = rVPointFS[size - 1];
    }

    private void addRVtoCmd(String fromCmd, String toCmd, float mFactor) {
        PointF[] fromPointFS = getVPointFromCmd(fromCmd);
        PointF[] toPointFS = getVPointFromCmd(toCmd);
        int size = fromPointFS.length;

        for (int i = 0; i < size; i++) {
            float tempX = fromPointFS[i].x + (toPointFS[i].x - fromPointFS[i].x) * mFactor;
            float tempY = fromPointFS[i].y + (toPointFS[i].y - fromPointFS[i].y) * mFactor;

            this.rLineTo(0, tempY);

            if (i == size - 1) {
                lastPointF.set(tempX, tempY);
            }
        }
    }

    private void addCloseCmd() {
        this.close();
    }

    private PointF[] getPointFromCmd(String cmd) {
        cmd = cmd.substring(1, cmd.length());

        String[] pointStrings = cmd.split(" ");
        PointF[] pointFS = new PointF[pointStrings.length];

        for (int i = 0; i < pointStrings.length; i++) {
            String[] xyString = pointStrings[i].split(",");
            pointFS[i] = new PointF(
                    Float.valueOf(xyString[0]) * scaleFactors[0],
                    Float.valueOf(xyString[1]) * scaleFactors[1]
            );
        }

        return pointFS;
    }

    private PointF[] getHPointFromCmd(String cmd) {
        cmd = cmd.substring(1, cmd.length());

        String[] pointStrings = cmd.split(" ");
        PointF[] pointFS = new PointF[pointStrings.length];

        for (int i = 0; i < pointStrings.length; i++) {
            pointFS[i] = new PointF(
                    Float.valueOf(pointStrings[i]) * scaleFactors[0],
                    lastPointF.y
            );
        }

        return pointFS;
    }

    private PointF[] getVPointFromCmd(String cmd) {
        cmd = cmd.substring(1, cmd.length());

        String[] pointStrings = cmd.split(" ");
        PointF[] pointFS = new PointF[pointStrings.length];

        for (int i = 0; i < pointStrings.length; i++) {
            pointFS[i] = new PointF(
                    lastPointF.x,
                    Float.valueOf(pointStrings[i]) * scaleFactors[1]
            );
        }

        return pointFS;
    }
}
