package com.trading.app.brokerConfig.aliceBlueConfig.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VendorLoginRequest {


    @JsonProperty("checkSum")
     private String checkSum;


    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public VendorLoginRequest(String checkSum) {
        this.checkSum = checkSum;
    }
}
