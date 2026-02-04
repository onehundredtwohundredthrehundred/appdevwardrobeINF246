package com.example.appdevwardrobeinf246;

public class clothitem {
    public String name;
    public String type;
    public String area;
    public String description;
    public String imageUri;
    public int timesWornSinceWash;
    public long lastWashedTimestamp;

    public clothitem() {
        this.timesWornSinceWash = 0;
        this.lastWashedTimestamp = 0;
    }

    public clothitem(String name, String type, String area, String description, String imageUri) {
        this.name = name;
        this.type = type;
        this.area = area;
        this.description = description;
        this.imageUri = imageUri;
        this.timesWornSinceWash = 0;
        this.lastWashedTimestamp = 0;
    }

    public void wear() {
        this.timesWornSinceWash++;
    }

    public void wash() {
        this.timesWornSinceWash = 0;
        this.lastWashedTimestamp = System.currentTimeMillis();
    }
}