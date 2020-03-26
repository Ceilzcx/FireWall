package com.example.firewall.bean;

public class Port {
    private String xy;
    private String ip1;
    private String p1;
    private String ip2;
    private String p2;
    public Port(String a1,String a2) {
        this.xy = a1;
        this.p1 = a2;

    }

    public String getXy() {
        return xy;
    }

    public void setXy(String xy) {
        this.xy = xy;
    }

    public String getIp1() {
        return ip1;
    }

    public void setIp1(String ip1) {
        this.ip1 = ip1;
    }

    public String getP1() {
        return p1;
    }

    public void setP1(String p1) {
        this.p1 = p1;
    }

    public String getIp2() {
        return ip2;
    }

    public void setIp2(String ip2) {
        this.ip2 = ip2;
    }

    public String getP2() {
        return p2;
    }

    public void setP2(String p2) {
        this.p2 = p2;
    }

    public Port(String a1, String a2, String a3, String a4, String a5) {
        this.xy = a1;
        this.ip1 = a2;
        this.p1=a3;
        this.ip2=a4;
        this.p2=a5;
    }


}