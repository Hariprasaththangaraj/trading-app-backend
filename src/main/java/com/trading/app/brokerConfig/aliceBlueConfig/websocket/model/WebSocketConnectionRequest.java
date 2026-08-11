package com.trading.app.brokerConfig.aliceBlueConfig.websocket.model;

 public class WebSocketConnectionRequest {

    private String susertoken;
    private String t;
    private String actid;
    private String uid;
    private String source;

     public String getSusertoken() {
         return susertoken;
     }

     public void setSusertoken(String susertoken) {
         this.susertoken = susertoken;
     }

     public String getT() {
         return t;
     }

     public void setT(String t) {
         this.t = t;
     }

     public String getActid() {
         return actid;
     }

     public void setActid(String actid) {
         this.actid = actid;
     }

     public String getUid() {
         return uid;
     }

     public void setUid(String uid) {
         this.uid = uid;
     }

     public String getSource() {
         return source;
     }

     public void setSource(String source) {
         this.source = source;
     }
 }
