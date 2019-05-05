package com.dyw.queue.entity;

public class FaceEquipmentEntity {
    private int id;
    private String faceCollectionName;
    private String faceCollectionIp;
    private String hostIp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFaceCollectionName() {
        return faceCollectionName;
    }

    public void setFaceCollectionName(String faceCollectionName) {
        this.faceCollectionName = faceCollectionName;
    }

    public String getFaceCollectionIp() {
        return faceCollectionIp;
    }

    public void setFaceCollectionIp(String faceCollectionIp) {
        this.faceCollectionIp = faceCollectionIp;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }
}
