package com.matriapp.mobile.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class ExpressItem {
    String name,tag,matri_id,id,image,about,image_approval,date,receiver_response,ph_receiver_id,photo_view_status,
            photo_view_count,user_id,badge,badgeUrl,color,photoUrl,username;
    int icon;
    JSONObject action;

    String age,height,caste_name,religion_name,state_name,city_name,country_name,occupation_name,education_name,mtongue_name;

    public ExpressItem(String name, int icon ,String tag) {
        this.name = name;
        this.icon = icon;
        this.tag = tag;
    }

    public ExpressItem() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPhoto_view_status() {
        return photo_view_status;
    }

    public void setPhoto_view_status(String photo_view_status) {
        this.photo_view_status = photo_view_status;
    }

    public String getPhoto_view_count() {
        return photo_view_count;
    }

    public void setPhoto_view_count(String photo_view_count) {
        this.photo_view_count = photo_view_count;
    }

    public String getPh_receiver_id() {
        return ph_receiver_id;
    }

    public void setPh_receiver_id(String ph_receiver_id) {
        this.ph_receiver_id = ph_receiver_id;
    }

    public String getReceiver_response() {
        return receiver_response;
    }

    public void setReceiver_response(String receiver_response) {
        this.receiver_response = receiver_response;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMatri_id() {
        return matri_id;
    }

    public void setMatri_id(String matri_id) {
        this.matri_id = matri_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getImage_approval() {
        return image_approval;
    }

    public void setImage_approval(String image_approval) {
        this.image_approval = image_approval;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getBadgeUrl() {
        return badgeUrl;
    }

    public void setBadgeUrl(String badgeUrl) {
        this.badgeUrl = badgeUrl;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public JSONObject getAction() {
        return action;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getCaste_name() {
        return caste_name;
    }

    public void setCaste_name(String caste_name) {
        this.caste_name = caste_name;
    }

    public String getReligion_name() {
        return religion_name;
    }

    public void setReligion_name(String religion_name) {
        this.religion_name = religion_name;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getOccupation_name() {
        return occupation_name;
    }

    public void setOccupation_name(String occupation_name) {
        this.occupation_name = occupation_name;
    }

    public String getEducation_name() {
        return education_name;
    }

    public void setEducation_name(String education_name) {
        this.education_name = education_name;
    }

    public String getMtongue_name() {
        return mtongue_name;
    }

    public void setMtongue_name(String mtongue_name) {
        this.mtongue_name = mtongue_name;
    }

    public void setAction(JSONObject action) {
        this.action = action;
    }

    String plan_status;
    public String getPlan_status() {
        return plan_status;
    }

    public void setPlan_status(String plan_status) {
        this.plan_status = plan_status;
    }
}
