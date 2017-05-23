package com.poponews.lite.model;

import com.mocean.IAdItem;

import java.util.ArrayList;

/**
 * Created by zl on 2016/10/24.
 */
public class MNCategory {
    String name;
    String rssUrl;
    ArrayList<MNNews> news;
    ArrayList<IAdItem> ads;

    public MNCategory() {
        news = new ArrayList<MNNews>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRssUrl() {
        return rssUrl;
    }

    public void setRssUrl(String rssUrl) {
        this.rssUrl = rssUrl;
    }

    public ArrayList<MNNews> getNews() {
        return news;
    }

    public void setNews(ArrayList<MNNews> news) {
        this.news = news;
    }

    public ArrayList<IAdItem> getAds() {
        return ads;
    }

    public void setAds(ArrayList<IAdItem> ads) {
        this.ads = ads;
    }
}
