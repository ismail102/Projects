package com.bestapk.petukvai.model;

public class Area {
    private String name;
    private String id, cityId;

    public Area(String id, String name, String cityId) {
        this.name = name;
        this.id = id;
        this.cityId = cityId;
    }
    public Area(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getCityId() {
        return cityId;
    }

    @Override
    public String toString() {
        return name;
    }


}
