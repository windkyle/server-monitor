package com.dyw.queue.entity;

public class StatusEntity {
    private String deviceIp;
    private String isLogin;
    private String passMode;
    private String cardNumber;

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    public String getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(String isLogin) {
        this.isLogin = isLogin;
    }

    public String getPassMode() {
        return passMode;
    }

    public void setPassMode(String passMode) {
        this.passMode = passMode;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
