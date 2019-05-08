package com.dyw.queue.entity;

public class FaultSummationEntity {
    private String name;
    private String cardNumber;
    private int faultAccount;
    private String lastTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getFaultAccount() {
        return faultAccount;
    }

    public void setFaultAccount(int faultAccount) {
        this.faultAccount = faultAccount;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }
}
