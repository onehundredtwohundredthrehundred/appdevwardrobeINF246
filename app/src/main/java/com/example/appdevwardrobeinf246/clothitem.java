package com.example.appdevwardrobeinf246;

import java.util.Objects;

public class clothitem {
    public int id;
    public int user_id;
    public String name;
    public String type;
    public String area;
    public String description;
    public String imageUri;
    public Integer max_wear_count;
    public int current_wear_count;
    public String status;
    public clothitem(int user_id, String name, String type, String area,
                     String description, String imageUri, Integer max_wear_count) {
        this.user_id = user_id;
        this.name = name;
        this.type = type;
        this.area = area;
        this.description = description;
        this.imageUri = imageUri;
        this.max_wear_count = max_wear_count;
        this.current_wear_count = 0;
    }

    public boolean isDirty() {
        return max_wear_count != null && current_wear_count >= max_wear_count;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        clothitem that = (clothitem) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}