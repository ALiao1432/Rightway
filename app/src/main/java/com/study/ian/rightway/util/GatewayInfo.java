package com.study.ian.rightway.util;

import java.util.StringTokenizer;

public class GatewayInfo {

    private final String TAG = "GatewayInfo";

    private String gatewayName;
    private String gateLocation;
    private final String southSpeed;
    private final String northSpeed;

    public GatewayInfo(String gatewayName, String southSpeed, String northSpeed) {
        this.gatewayName = gatewayName;
        this.southSpeed = southSpeed;
        this.northSpeed = northSpeed;

        divideNameAndLocation(this.gatewayName);
    }

    public GatewayInfo(String gatewayName) {
        this.gatewayName = gatewayName;
        this.southSpeed = null;
        this.northSpeed = null;

        divideNameAndLocation(this.gatewayName);
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public String getGateLocation() {
        return gateLocation;
    }

    public String getSouthSpeed() {
        return southSpeed;
    }

    public String getNorthSpeed() {
        return northSpeed;
    }

    @Override
    public String toString() {
        return "GatewayName : " + gatewayName + ", gateLocation : " + gateLocation + ", SouthSpeed : " + southSpeed + ", NorthSpeed : " + northSpeed;
    }

    private void divideNameAndLocation(String gatewayName) {
        StringTokenizer tokenizer;

        gatewayName =  gatewayName.replace("(", " ");
        gatewayName =  gatewayName.replace(")", " ");

        tokenizer = new StringTokenizer(gatewayName);
        this.gatewayName = tokenizer.nextToken();
        this.gateLocation = tokenizer.nextToken() + "km";
    }
}
