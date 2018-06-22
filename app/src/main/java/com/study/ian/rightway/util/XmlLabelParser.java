package com.study.ian.rightway.util;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class XmlLabelParser {

    private static final String TAG = "XmlLabelParser";

    private final XmlResourceParser xmlResourceParser;

    XmlLabelParser(Context context, int id) {
        xmlResourceParser = context.getResources().getXml(id);
    }

    public List<String> getLabelData(@Nullable String labelName, @Nullable String attrName) {
        List<String> data = new ArrayList<>();

        try {
            int event = xmlResourceParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                event = xmlResourceParser.next();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
//                        Log.d(TAG, "Start parsing");
                        break;
                    case XmlPullParser.START_TAG:
//                        Log.d(TAG, "Current Label : " + xmlResourceParser.getName());
                        if (labelName == null) {
                            for (int i = 0; i < xmlResourceParser.getAttributeCount(); i++) {
//                                Log.d(TAG, xmlResourceParser.getAttributeName(i) + " : " + xmlResourceParser.getAttributeValue(i));
                            }
                        } else if (xmlResourceParser.getName().equals(labelName)) {
                            for (int i = 0; i < xmlResourceParser.getAttributeCount(); i++) {
                                if (attrName == null) {
//                                    Log.d(TAG, xmlResourceParser.getAttributeName(i) + " : " + xmlResourceParser.getAttributeValue(i));
                                } else if (xmlResourceParser.getAttributeName(i).equals(attrName)) {
                                    data.add(xmlResourceParser.getAttributeValue(i));
//                                    Log.d(TAG, xmlResourceParser.getAttributeName(i) + " : " + data);
                                }
                            }
                        }
                        break;
                    case XmlPullParser.TEXT:
//                        Log.d(TAG, "Text : " + xmlResourceParser.getText());
                        break;
                    case XmlPullParser.END_TAG:
//                        Log.d(TAG, "End parsing");
                        break;
                    default:
                        break;
                }
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
