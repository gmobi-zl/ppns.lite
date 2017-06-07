package com.poponews.lite;

import android.app.Application;
import android.content.Context;

import com.momock.data.Settings;
import com.momock.util.Logger;
import com.poponews.lite.app.ConfigNames;
import com.poponews.lite.services.MonyNewsService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by zl on 2016/10/21.
 */
public class MonyNewsApp extends Application {
    private static Settings appSetting = null;
    private static Context mContext;

    public static Settings getSetting() {
        return appSetting;
    }
    public static Context get() {
        return mContext;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        if (appSetting == null)
            appSetting = new Settings(this, ConfigNames.SETTINGS_FILE_NAME);
        mContext = this;

        Logger.setEnabled(true);
        String appName = getPackageName();
        Logger.open(this, appName, 5, Logger.LEVEL_DEBUG);

        MonyNewsService mnSvr = MonyNewsService.getInstance();
        mnSvr.start(this);

        //mnSvr.connect();
//        try {
//            InputStream is = getAssets().open("monynews.js");
//            String data = convert2String(is);
//            JSONArray obj = new JSONArray(data);
//            mnSvr.dataService.setCurrentListIndex(0);
//            mnSvr.dataService.setMutilNewsList(obj);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private String convert2String(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer("");
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
            buffer.append("\n");
        }
        return buffer.toString();
    }

}
