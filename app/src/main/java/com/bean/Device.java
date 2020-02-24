package com.bean;

public class Device {

    private int id;
    private int room_id;
    private String iot_id;
    private String product_key;
    private String device_name;
    private int sort;
    private String created_at;
    private String updated_at;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setIot_id(String iot_id) {
        this.iot_id = iot_id;
    }

    public String getIot_id() {
        return iot_id;
    }

    public void setProduct_key(String product_key) {
        this.product_key = product_key;
    }

    public String getProduct_key() {
        return product_key;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getSort() {
        return sort;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

}

