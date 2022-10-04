package com.example.travelbook.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Post implements Serializable {
       public String downloadUrl;
       public String country;
       public String comment;
       public String city;
       public String placeName;
       public Double lattitudeChoose;
       public Double longitudeChoose;

    public Post(String downloadUrl,String country,String city, String comment,
                String placeName,Double lattitudeChoose, Double longitudeChoose){
        this.downloadUrl= downloadUrl;
        this.city=city;
        this.country=country;
        this.comment= comment;
        this.placeName=placeName;
        this.lattitudeChoose= lattitudeChoose;
        this.longitudeChoose=longitudeChoose;
    }


}
