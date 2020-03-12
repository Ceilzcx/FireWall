package com.example.firewall.bean;

/**
 * 拦截号码：包括电话号码、拦截类型
 * 存放在SQLite数据库中
 */
public class InterceptPhone {
    //定义三种拦截：电话拦截、短信拦截、全部拦截
    public final static int TELE_INTERCEPT = 1;
    public final static int NOTE_INTERCEPT = 2;
    public final static int ALL_INTERCEPT = 3;

    private String number;
    private int type;

    public InterceptPhone(String number, int type){
        this.number = number;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setType(int type) {
        this.type = type;
    }

}
