package com.example.firewall.bean;

/**
 * 手机联系人信息:
 *      联系人名字+电话号码
 */
public class PhoneContactInfo {
    private String username;
    private String number;

    public PhoneContactInfo(){}

    public PhoneContactInfo(String username, String number){
        this.username = username;
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public String getUsername() {
        return username;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
