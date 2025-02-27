package com.matriapp.mobile.model;

public class PartnerFields {


    public String title;
    public String type;
    public String value;

    public PartnerFields(String title, String type, String value) {
        this.title = title;
        this.type = type;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}