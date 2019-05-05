package com.dyw.queue.entity;

public class FaceCollectionEntity {
    private int Id;
    private String Similation;
    private String Name;
    private String Nation;
    private String Sex;
    private String Birthday;
    private String ExpirationDate;
    private byte[] IdentificationPhoto;
    private byte[] StaffPhoto;
    private String Organization;
    private String CardId;

    public String getCardId() {
        return CardId;
    }

    public void setCardId(String cardId) {
        CardId = cardId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getSimilation() {
        return Similation;
    }

    public void setSimilation(String similation) {
        Similation = similation;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNation() {
        return Nation;
    }

    public void setNation(String nation) {
        Nation = nation;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public String getExpirationDate() {
        return ExpirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        ExpirationDate = expirationDate;
    }

    public byte[] getIdentificationPhoto() {
        return IdentificationPhoto;
    }

    public void setIdentificationPhoto(byte[] identificationPhoto) {
        IdentificationPhoto = identificationPhoto;
    }

    public byte[] getStaffPhoto() {
        return StaffPhoto;
    }

    public void setStaffPhoto(byte[] staffPhoto) {
        StaffPhoto = staffPhoto;
    }

    public String getOrganization() {
        return Organization;
    }

    public void setOrganization(String organization) {
        Organization = organization;
    }

    /*
     * 清空对象信息
     * */
    public void clear() {
        Id = 0;
        Similation = "0";
        Name = "";
        Nation = "";
        Sex = "";
        Birthday = "";
        ExpirationDate = "";
        IdentificationPhoto = null;
        StaffPhoto = null;
        Organization = "";
        CardId = "";
    }
}
