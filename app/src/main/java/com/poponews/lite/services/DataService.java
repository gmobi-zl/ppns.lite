package com.poponews.lite.services;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.mocean.AdServiceManager;
import com.mocean.IAd;
import com.mocean.IAdItem;
import com.mocean.IAdService;
import com.mocean.ICallback;
import com.mocean.INativeAd;
import com.mocean.IServiceCallback;
import com.momock.util.JsonHelper;
import com.momock.util.Logger;
import com.poponews.lite.MonyNewsEvents;
import com.poponews.lite.model.MNCategory;
import com.poponews.lite.model.MNChannel;
import com.poponews.lite.model.MNNews;
//import com.mopub.mraid.MraidNativeCommandHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by zl on 2016/10/21.
 */
public class DataService {

    //private static DataService dataServiceInstance;

    private JSONArray mutilNewsList;
    private int currentListIndex;
    private ArrayList<MNChannel> chList;

    private MNNews currentDetailNews;

    public JSONArray getMutilNewsList() {
        return mutilNewsList;
    }
    
    public MNNews getCurrentDetailNews() {
		return currentDetailNews;
	}

	public void setCurrentDetailNews(MNNews currentDetailNews) {
		this.currentDetailNews = currentDetailNews;
	}

	public void setMutilNewsList(JSONArray mutilNewsList) {
        this.mutilNewsList = mutilNewsList;
        setChList();
    }

    public int getCurrentListIndex() {
        return currentListIndex;
    }

    public void setCurrentListIndex(int currentListIndex) {
        this.currentListIndex = currentListIndex;
    }

    public ArrayList<MNCategory> getShowNewsTabList(){
        ArrayList<MNCategory> ret = null;
        try {
            MNChannel ch = chList.get(currentListIndex);
            ret = ch.getCategories();
        } catch (Exception e){

        }
        return ret;
    }

    public MNCategory findCurrentChannelCategoryByName(String name){
        MNCategory ret = null;
        try {
            MNChannel channel = chList.get(currentListIndex);
            ArrayList<MNCategory> categories = channel.getCategories();
            for (int n = 0; n < categories.size(); n++){
                MNCategory category = categories.get(n);
                if (category.getName().equalsIgnoreCase(name)){
                    ret = category;
                    break;
                }
            }
        } catch (Exception e){

        }
        return ret;
    }

    private void setChList(){
        try {
            for (int m = 0; m < mutilNewsList.length(); m++){
                JSONObject channelData = mutilNewsList.getJSONObject(m);
                String chName = channelData.getString("name");
                String chTitle = channelData.getString("title");

                MNChannel channel = new MNChannel();
                channel.setName(chName);
                channel.setTitle(chTitle);

                if (chList == null)
                    chList = new ArrayList<MNChannel>();

                chList.add(channel);

                JSONObject configData = channelData.getJSONObject("config");
                JSONArray cateArray = configData.getJSONArray("channels");

                for (int n = 0; n < cateArray.length(); n++){
                    JSONObject cate = cateArray.getJSONObject(n);
                    String cateName = cate.getString("name");
                    String cateRssUrl = cate.getString("rss_url");

                    MNCategory category = new MNCategory();
                    category.setName(cateName);
                    category.setRssUrl(cateRssUrl);

                    channel.getCategories().add(category);
                }
            }

            initNewsListData();
        } catch (Exception e){

        }
    }

    private void initNewsListData(){
        try {
            RemoteService rs = MonyNewsService.getInstance().remoteService;
            for (int m = 0; m < chList.size(); m++){
                final MNChannel channel = chList.get(m);
                ArrayList<MNCategory> categories = channel.getCategories();
                for (int n = 0; n < categories.size(); n++){

                    final MNCategory category = categories.get(n);
                    //RemoteService.getInstance().getChannelNewsData(channel.getName(), category.getName(), category.getRssUrl());
                    rs.getChannelNewsData(channel.getName(), category.getName(), category.getRssUrl(), new RemoteService.IRespStringCallback() {
                        @Override
                        public void onResult(String resp) {
                            if (resp != null) {
                                setNewsData(channel.getName(), category.getName(), resp);
                                MonyNewsService.getInstance().getMessageService().send(null, MonyNewsEvents.MONY_NEWS_REFRESH);
                            }
                        }
                    });
                }
            }
        } catch (Exception e){

        }
    }

    private MNCategory findCategoryByName(String ch, String name){
        MNCategory ret = null;
        try {
            for (int m = 0; m < chList.size(); m++){
                MNChannel channel = chList.get(m);
                if (channel.getName().equalsIgnoreCase(ch)){
                    ArrayList<MNCategory> categories = channel.getCategories();
                    for (int n = 0; n < categories.size(); n++){
                        MNCategory category = categories.get(n);
                        if (category.getName().equalsIgnoreCase(name)){
                            ret = category;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e){

        }
        return ret;
    }

    public void setNewsData(final String channel, final String categoryName, String newsXmlData){
        try {
            XmlPullParser xpp = Xml.newPullParser();
            ByteArrayInputStream bais = new ByteArrayInputStream(newsXmlData.getBytes());
            xpp.setInput(bais, "UTF-8");

            int eventType = xpp.getEventType();
            ArrayList<MNNews> mList = null;
            MNNews mnNew = null;
            boolean isItemStart = false;
            while (eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        MNCategory cate = findCategoryByName(channel, categoryName);
                        mList = cate.getNews();
                        break;
                    case XmlPullParser.START_TAG:
                        //Log.d("MonyNews", "Tag = " + xpp.getName());
                        if (xpp.getName().equals("item")) {
                            mnNew = new MNNews();
                            isItemStart = true;
                        } else if (xpp.getName().equals("title") && isItemStart == true) {
                            eventType = xpp.next();
                            //mnNew.setTitle(xpp.getText());
                            
                            String gTitle = xpp.getText();
                            
                            int sepStrIndex = gTitle.lastIndexOf("-");
                            if (sepStrIndex > 0){
                            	String tempTitle = gTitle.substring(0, sepStrIndex);
                            	String tempSource = gTitle.substring(sepStrIndex+2);
                            	mnNew.setTitle(tempTitle);
                            	mnNew.setSource(tempSource);
                            } else {
                            	mnNew.setTitle(gTitle);
                            	mnNew.setSource("");
                            }
                        } else if (xpp.getName().equals("link") && isItemStart == true) {
                            eventType = xpp.next();
                            mnNew.setLink(xpp.getText());
                        } else if (xpp.getName().equals("category") && isItemStart == true) {
                            eventType = xpp.next();
                            mnNew.setCategory(xpp.getText());
                        } else if (xpp.getName().equals("pubDate") && isItemStart == true) {
                            eventType = xpp.next();
                            
                            String pubDate = xpp.getText();
                            mnNew.setPubDate(pubDate);
                            
                            //long time=System.currentTimeMillis();
                            //Date date=new Date(time);
                            //SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy H:mm:ss z");
                            //SimpleDateFormat dateFormat2 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
                            //dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                            //dateFormat2.setTimeZone(TimeZone.getTimeZone("GMT"));
                            //String resultDate1 = dateFormat.format(date);
                            //String resultDate2 = dateFormat2.format(date);
                            //Log.e(time+"", "resultDate1 = " + resultDate1);
                            //Log.e(time+"", "resultDate2 = " + resultDate2);
                            Date pDate;
                            try{
                            	SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy H:mm:ss z");
                            	dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                            	pDate = dateFormat.parse(pubDate);
                            	long pDateLong = pDate.getTime();
                            	
                            	long nowTime = System.currentTimeMillis();
                            	SimpleDateFormat nowDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            	nowDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                            	String resultNowDate = nowDateFormat.format(nowTime);
                            	
                            	SimpleDateFormat dayDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            	dayDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                            	String resultNewsDay = dayDateFormat.format(pDateLong);
                            	
                            	SimpleDateFormat hourDateFormat = new SimpleDateFormat("HH:mm");
                            	hourDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                            	String resultNewsHour = hourDateFormat.format(pDateLong);
                            	
                            	if (resultNowDate.equalsIgnoreCase(resultNewsDay)){
                            		mnNew.setDateDay("");
                            		mnNew.setDateHour(resultNewsHour);
                            	} else {
                            		mnNew.setDateDay(resultNewsDay);
                            		mnNew.setDateHour("");
                            	}
                            	
                            } catch(Exception e){
                            	mnNew.setDateDay("");
                        		mnNew.setDateHour("");
                            }
                        } else if (xpp.getName().equals("description") && isItemStart == true) {
                            eventType = xpp.next();
                            String desc = xpp.getText();

                            int iconStartIdx = desc.indexOf("img src=\"");
                            if (iconStartIdx != -1){
                                int iconEndIdx = desc.indexOf("\"", iconStartIdx+9);
                                String iconUrl = desc.substring(iconStartIdx+9, iconEndIdx);
                                if (iconUrl.startsWith("//")){
                                    iconUrl = "http:" + iconUrl;
                                }
                                mnNew.setIcon(iconUrl);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (xpp.getName().equals("item")) { 
                            mList.add(mnNew);
                            mnNew = null;
                            isItemStart = false;
                        }
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e){
            Logger.error(e);
        }
    }

    public void setConnectRsp(String rsp){
        if (rsp == null)
            return;

        try{
            if (chList == null){
                chList = new ArrayList<MNChannel>();
                MNChannel ch = new MNChannel();
                chList.add(ch);
                currentListIndex = 0;
            }

            MNChannel channel = chList.get(currentListIndex);
            JSONArray connectArray = new JSONArray(rsp);
            for (int n = 0; n < connectArray.length(); n++){
                JSONObject cate = connectArray.getJSONObject(n);
                String cateName = cate.getString("name");
                String cateRssUrl = cate.getString("url");

                MNCategory category = new MNCategory();
                category.setName(cateName);
                category.setRssUrl(cateRssUrl);

                channel.getCategories().add(category);

                JSONArray articlesArray = cate.getJSONArray("articles");
                for (int i = 0; i < articlesArray.length(); i++){
                    JSONObject newsObj = articlesArray.getJSONObject(i);
                    String title = JsonHelper.selectString(newsObj, "title", "");
                    String url = JsonHelper.selectString(newsObj, "url", "");
                    String image = JsonHelper.selectString(newsObj, "image", "");
                    String date = JsonHelper.selectString(newsObj, "date", "");

                    MNNews mnNew = new MNNews();

                    mnNew.setIcon(image);
                    mnNew.setLink(url);

                    int sepStrIndex = title.lastIndexOf("-");
                    if (sepStrIndex > 0){
                        String tempTitle = title.substring(0, sepStrIndex);
                        String tempSource = title.substring(sepStrIndex+2);
                        mnNew.setTitle(tempTitle);
                        mnNew.setSource(tempSource);
                    } else {
                        mnNew.setTitle(title);
                        mnNew.setSource("");
                    }

                    Date pDate;
                    try{
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                        pDate = dateFormat.parse(date);
                        long pDateLong = pDate.getTime();

                        long nowTime = System.currentTimeMillis();
                        SimpleDateFormat nowDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        nowDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                        String resultNowDate = nowDateFormat.format(nowTime);

                        SimpleDateFormat dayDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        dayDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                        String resultNewsDay = dayDateFormat.format(pDateLong);

                        SimpleDateFormat hourDateFormat = new SimpleDateFormat("HH:mm");
                        hourDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                        String resultNewsHour = hourDateFormat.format(pDateLong);

                        if (resultNowDate.equalsIgnoreCase(resultNewsDay)){
                            mnNew.setDateDay("");
                            mnNew.setDateHour(resultNewsHour);
                        } else {
                            mnNew.setDateDay(resultNewsDay);
                            mnNew.setDateHour("");
                        }
                    } catch(Exception e){
                        mnNew.setDateDay("");
                        mnNew.setDateHour("");
                    }

                    category.getNews().add(mnNew);
                }
            }
        } catch (Exception e){
            Logger.error(e);
        }
    }

    IAdService adService = null;
    INativeAd nAds = null;
    int nextAdIndex = 0;
    public void initGAds(Context context){
        Logger.debug("initGAds adService = " + adService);
        if (adService == null){
            nextAdIndex = 0;
            Logger.debug("Init Ads");
            AdServiceManager.get(context, new IServiceCallback<IAdService>(){

                @Override
                public void call(IAdService service) {
                    Logger.debug("Init Ads service callback");
                    adService = service;
                    nAds = adService.getNativeAd("native.ad", 50, 50, 10, null);
                    nAds.setOnLoadLisenter(new ICallback() {
                        @Override
                        public void call(int resultCode) {
                            Logger.debug("Init Ads get native ad callback = " + resultCode);
                            if (resultCode == IAd.OK){
                                nextAdIndex = 0;
                                MonyNewsService.getInstance().getMessageService().send(null, MonyNewsEvents.MONY_NEWS_REFRESH);
                            }
                        }
                    });
                    nAds.load();
                }
            });
        }
    }

    public IAdItem getAcitiveAd(){
        IAdItem ad = null;
        if (nAds != null && nAds.getCount() > 0){
            int adCount = nAds.getCount();
            if (nextAdIndex < adCount){
                ad = nAds.getAdItem(nextAdIndex);
                nextAdIndex++;
            }
        }
        return ad;
    }

    public boolean hasActiviteAd(){
        if (nAds != null && nAds.getCount() > 0){
            int adCount = nAds.getCount();
            if (nextAdIndex < adCount){
                return true;
            }
        }
        return false;
    }
}
