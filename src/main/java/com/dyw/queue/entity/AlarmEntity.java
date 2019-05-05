package com.dyw.queue.entity;

import java.sql.Timestamp;
import java.util.List;

public class AlarmEntity {
    private int Id;
    private byte[] CapturePhoto;//抓拍的图片
    private String CardNumber;//卡号
    private String StaffName;//姓名
    private String EquipmentName;//设备名称
    private Timestamp Date;//事件时间
    private int Similarity;//相似度值
    private Boolean IsPass;//是否通过
    private int EventTypeId;//事件类型
    private String IP;//设备ip

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public byte[] getCapturePhoto() {
        return CapturePhoto;
    }

    public void setCapturePhoto(byte[] capturePhoto) {
        CapturePhoto = capturePhoto;
    }

    public String getCardNumber() {
        return CardNumber;
    }

    public void setCardNumber(String cardNumber) {
        CardNumber = cardNumber;
    }

    public String getStaffName() {
        return StaffName;
    }

    public void setStaffName(String staffName) {
        StaffName = staffName;
    }

    public String getEquipmentName() {
        return EquipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        EquipmentName = equipmentName;
    }

    public Timestamp getDate() {
        return Date;
    }

    public void setDate(Timestamp date) {
        Date = date;
    }

    public int getSimilarity() {
        return Similarity;
    }

    public void setSimilarity(int similarity) {
        Similarity = similarity;
    }

    public Boolean getPass() {
        return IsPass;
    }

    public void setPass(Boolean pass) {
        IsPass = pass;
    }

    public int getEventTypeId() {
        return EventTypeId;
    }

    public void setEventTypeId(int eventTypeId) {
        EventTypeId = eventTypeId;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void clear() {
        Id = 0;
        CapturePhoto = null;
        CardNumber = "";
        StaffName = "";
        EquipmentName = "";
        Date = null;
        Similarity = 0;
        IsPass = null;
        EventTypeId = 0;
        IP = "";
    }
}
