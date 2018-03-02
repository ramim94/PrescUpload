package com.mobodreamers.prescupload.model;

/**
 * Created by Ramim on 12/3/2016.
 */

public class Prescription {
    String name, image;

    public Prescription() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = "http://10.0.3.2//Hellopathy/prescriptions/"+image;
    }
}
