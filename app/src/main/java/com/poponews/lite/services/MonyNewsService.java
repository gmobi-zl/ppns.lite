package com.poponews.lite.services;

import android.content.Context;

import com.momock.service.AsyncTaskService;
import com.momock.service.CacheService;
import com.momock.service.HttpService;
import com.momock.service.IImageService;
import com.momock.service.IMessageService;
import com.momock.service.ImageService;
import com.momock.service.MessageService;
import com.momock.service.UITaskService;
import com.poponews.lite.MonyNewsEvents;
import com.poponews.lite.activity.SplashActivity;
import com.poponews.lite.app.MessageTopics;

/**
 * Created by zl on 2016/10/25.
 */
public class MonyNewsService {
    private static MonyNewsService mnServiceInstance;

    public DataService dataService;
    public RemoteService remoteService;
    public Context appContext;
    private IMessageService messageService;
    UITaskService uiTaskService;
    AsyncTaskService asyncTaskService;
    HttpService httpService;
    CacheService cacheService;
    IImageService imageService;
    IConfigService configService;

    public static MonyNewsService getInstance(){
        if (mnServiceInstance == null)
            mnServiceInstance = new MonyNewsService();
        return mnServiceInstance;
    }

    public IMessageService getMessageService() {
        return messageService;
    }

    public IImageService getImageService() {
        return imageService;
    }

    public void start(Context context){

        appContext = context;

        uiTaskService = new UITaskService();
        uiTaskService.start();

        asyncTaskService = new AsyncTaskService();
        asyncTaskService.start();

        httpService = new HttpService("PopoNews", uiTaskService, asyncTaskService);
        httpService.start();

        cacheService = new CacheService(context);
        cacheService.start();

        remoteService = new RemoteService(httpService, context);
        messageService = new MessageService(uiTaskService);
        dataService = new DataService();

        imageService = new ImageService(1024 * 1024 * 8, httpService, cacheService);
        configService = new ConfigService();
    }

    public void connect(){
        remoteService.connect(new RemoteService.IRespStringCallback() {
            @Override
            public void onResult(String resp) {
                dataService.setConnectRsp(resp);
                if (resp == null){
                    new Thread(new Runnable(){
                        public void run(){
                            try {
                                Thread.sleep(5000);
                            } catch (Exception e){

                            }
                            SplashActivity.splashHandler.sendEmptyMessage(MessageTopics.SYSTEM_CONNECT_SERVER);
                        }
                    }).start();
                } else {
                    SplashActivity.splashHandler.sendEmptyMessage(MessageTopics.SYSTEM_ENTRY_MAIN);
                    dataService.initGAds(appContext);
                }
            }
        });
    }
}
