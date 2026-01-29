package com.example.appdevwardrobeinf246;

public class clothitem {
    public String name, description, area, type, imageUri;

    public clothitem(String name, String description,
                        String area, String type, String imageUri) {
        this.name = name;
        this.description = description;
        this.area = area;
        this.type = type;
        this.imageUri = imageUri;
    }
}