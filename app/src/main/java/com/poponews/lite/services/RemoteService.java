package com.poponews.lite.services;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.momock.http.HttpSession;
import com.momock.service.IHttpService;
import com.momock.util.Logger;
import com.momock.util.SystemHelper;
import com.poponews.lite.BuildConfig;
import com.poponews.lite.util.PreferenceHelper;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by zl on 2016/10/24.
 */
public class RemoteService {
//    private static RemoteService remoteServiceInstance;
//
//    public static RemoteService getInstance(){
//        if (remoteServiceInstance == null)
//            remoteServiceInstance = new RemoteService();
//        return remoteServiceInstance;
//    }
//
//    public void getChannelNewsData(final String local,final String chName, String url){
//        AsyncHttpClient client = new AsyncHttpClient();
//        Log.d("MonyNews", "news url: " + url);
//        client.get(url, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                if (statusCode == 200){
//                    String result = new String(responseBody);
//                    Log.d("MonyNews", result);
//
//                    DataService.getInstance().setNewsData(local, chName, responseBody);
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                Log.d("MonyNews", "get news data failed : " + statusCode + "  error : " + error);
//            }
//        });
//    }
    Context appContext;
    IHttpService httpService;

    //private static Response defaultErrResponse=new Response("{\"resultCode\" : \"404\", \"resultMsg\" : \"请检查网络\"}");
    public interface ICallback{
        void onResult(JSONObject json);
    }

    public interface IRespStringCallback{
        void onResult(String resp);
    }

    public interface IResponseCallback{
        void onResult(Response resp);
    }

    public static class Response {
        HttpSession session;
        int statusCode;
        String body;

        public Response(){

        }
        public Response(String body){
            this.body=body;
        }
        public int getStatusCode() {
            return statusCode;
        }
        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }
        public String getBody() {
            return body;
        }
        public void setBody(String body) {
            this.body = body;
        }
        public HttpSession getSession() {
            return session;
        }
        public void setSession(HttpSession session) {
            this.session = session;
        }
    }

    public RemoteService(IHttpService httpService, Context context){
        this.httpService = httpService;
        appContext = context;
    }

    public Response doGet(String url){
        Logger.debug("GET " + url);
        Response resp = new Response();
        HttpSession session = httpService.get(url);
        session.start(true);
        resp.body = session.getResultAsString();
        resp.statusCode = session.getStatusCode();
        resp.session = session;
        return resp;
    }
    public Response doPost(String url, String json){
        Response resp = doPostJsonWithHeaders(url, null, json);
        return resp;
    }

    public Response doPostJsonWithHeaders(String url, Header[] headers, String json){
        Logger.debug("POST " + url + " : " + json);
        Response resp = new Response();

        StringEntity entity = null;
        try{
            entity = new StringEntity(json, "UTF-8");
        }catch(Exception e){
            Logger.error(e);
        }

        int hLen = 0;
        if (headers != null){
            hLen = headers.length;
        }

        int hLength = hLen + 2;
        Header[] h = new Header[hLength];
        h[0] = new BasicHeader("Content-Type", "application/json");
        h[1] = new BasicHeader("appVer", SystemHelper.getAppVersion(appContext));
        if (headers != null){
            for (int i = 0; i < headers.length; i++) {
                h[i+2] = headers[i];
            }
        }

        HttpSession session = httpService.post(url, h, entity);
        session.start(true);
        resp.body = session.getResultAsString();
        resp.statusCode = session.getStatusCode();
        resp.session = session;

        return resp;
    }

    public void getChannelNewsData(final String local,final String chName, final String url, final IRespStringCallback callback){
        new Thread(){
            public void run() {
                Response resp = doGet(url);

                Logger.debug("getChannelNewsData Resp : " + resp.getBody() + "(" + resp.getStatusCode() + ")");

                if (resp.getStatusCode() == 200){
                    callback.onResult(resp.getBody());
                } else {
                    callback.onResult(null);
                }
            }
        }.start();
    }

    // news data

    private String getConnectUrl() {
        IConfigService configService = MonyNewsService.getInstance().configService;

        StringBuilder url = new StringBuilder(128);
        url.append(configService.getEntryBaseUrl());
        url.append("/api/lite/connect");
        //url.append(BuildConfig.GROUP);
        String ch = configService.getCurChannel();
        if (!ch.isEmpty()) {
            url.append("?channel=");
            url.append(ch);
        } else {
            String installerCh = PreferenceHelper.getInstallerChannel(appContext);
            if (!installerCh.equals("")) {
                url.append("?channel=");
                url.append(installerCh);
            }
            Logger.debug("use installerCh:" + installerCh);
        }
        return url.toString();
    }

    public JSONObject getDeviceInfo() {
        IConfigService configService = MonyNewsService.getInstance().configService;

        TelephonyManager mTelephonyMgr = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();
        String imei = mTelephonyMgr.getDeviceId();
        JSONObject json = new JSONObject();
        try {
            json.put("app", appContext.getPackageName());
            json.put("ch", configService.getCurChannel());
            json.put("group", BuildConfig.GROUP);
            json.put("app_v", SystemHelper.getAppVersion(appContext));
            json.put("imsi", imsi);
            json.put("imei", imei);
            json.put("sd", SystemHelper.hasSdcard(appContext));
            json.put("ua", SystemHelper.getUA(false));
            json.put("os", "android");
            json.put("os_v", SystemHelper.getOsVersion());
            json.put("lang", Locale.getDefault().getLanguage());
            json.put("country", SystemHelper.getCountry(appContext));
            json.put("wmac", SystemHelper.getWifiMac(appContext));
            json.put("bmac", "");
            json.put("sn", SystemHelper.getAndroidId(appContext));
            json.put("sa", SystemHelper.isSystemApp(appContext));
            json.put("sw", SystemHelper.getScreenWidth(appContext));
            json.put("sh", SystemHelper.getScreenHeight(appContext));

            json.put("dch", configService.getDch());
            json.put("gref", new JSONObject(configService.getReferrer()));

            json.put("user", configService.getUserInfo());
            json.put("pid", configService.getPushId());
            json.put("ppid", configService.getPrePushId());
            Logger.debug("Send Device Info: " + json.toString(4));

        } catch (JSONException e) {
            Logger.error(e);
        }
        return json;
    }

    public void connect(final IRespStringCallback callback) {
        IConfigService configService = MonyNewsService.getInstance().configService;

        String connectUrl = getConnectUrl();
        Logger.debug(connectUrl);

        JSONObject deviceInfo = getDeviceInfo();
        JSONObject jo = new JSONObject();

        try {
            jo.put("device", deviceInfo);
            jo.put("update", configService.checkDeviceInfoData(deviceInfo.toString()));

            if (configService.getDid() != null)
                jo.put("did", configService.getDid());
            Logger.debug("POPONews connect data = " + jo.toString());

            doConnect(connectUrl, jo.toString(), callback);

        } catch (Exception e1) {

            e1.printStackTrace();
        }
    }

    private void doConnect(final String url, final String body, final IRespStringCallback callback){
        new Thread(){
            public void run() {
                Response resp = doPost(url, body);

                Logger.debug("connect Resp : " + resp.getBody() + "(" + resp.getStatusCode() + ")");

                if (resp.getStatusCode() == 200){
                    callback.onResult(resp.getBody());
                } else {
                    callback.onResult(null);
                }
            }
        }.start();
    }

}
