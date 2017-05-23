package com.poponews.lite.model;

import java.util.ArrayList;

/**
 * Created by zl on 2016/10/24.
 */
public class MNChannel {
    String name;
    String title;
    ArrayList<MNCategory> categories;

    public MNChannel() {
        categories = new ArrayList<MNCategory>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<MNCategory> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<MNCategory> categories) {
        this.categories = categories;
    }
}
