package com.example.firewall.bean;

/**
 * 电话通信信息：
 *  包括通讯电话、时间、类型（呼入、呼出、已接）
 */
public class PhoneCallLogInfo {
    private String number;
    private String date;
    private int type;

    public static final int CALLIN = 1;
    public static final int CALLOUT = 2;
    public static final int UNCALLED = 3;

    public PhoneCallLogInfo(){

    }

    public PhoneCallLogInfo(String number, String date, int type){
        this.number = number;
        this.date = date;
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public String getDate() {
        return date;
    }

    public int getType() {
        return type;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
