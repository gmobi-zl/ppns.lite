package com.poponews.lite.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.mocean.AdServiceManager;
import com.mocean.IAdService;
import com.mocean.IInterstitialAd;
import com.mocean.IServiceCallback;
import com.poponews.lite.R;
import com.poponews.lite.model.MNNews;
import com.poponews.lite.services.MonyNewsService;

import android.os.Bundle;

public class MNWebDetailActivity extends AppCompatActivity {
	
	WebView wvNewsReader;
	ProgressBar pbWebNewsLoad;
	AppCompatActivity selfActivity;
	boolean isShowAds = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mnweb_detail);
        selfActivity = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		String titleName = getResources().getString(R.string.app_title);
		toolbar.setTitle(titleName);
		toolbar.setNavigationIcon(R.drawable.navigaton_back);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closePage();
			}
		});
		
		pbWebNewsLoad = (ProgressBar)findViewById(R.id.pbWebNewsLoad);
        
        MonyNewsService app = MonyNewsService.getInstance();
        MNNews news = app.dataService.getCurrentDetailNews();
        
        wvNewsReader = (WebView)findViewById(R.id.wvNewsReader);
		WebSettings webSettings = wvNewsReader.getSettings();
		//webSettings.setJavaScriptEnabled(true);
		
		wvNewsReader.setWebChromeClient(new WebChromeClient() {
	          @Override
	          public void onProgressChanged(WebView view, int newProgress) {
	              if (newProgress == 100) {
	            	  pbWebNewsLoad.setVisibility(View.GONE);
	            	  //Appodeal.hide(selfActivity, Appodeal.MREC);
	              } else {
	            	  if (pbWebNewsLoad.getVisibility() != View.VISIBLE)
	            		  pbWebNewsLoad.setVisibility(View.VISIBLE);
	                  pbWebNewsLoad.setProgress(newProgress);
	              }
	              super.onProgressChanged(view, newProgress);
	          }
	          
			@Override
	        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
				return true;
				//return super.onConsoleMessage(consoleMessage);
	        }
	          
	      });
		
		wvNewsReader.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
                return true;
			}
		});
		
		if (news != null){
			//Log.d("PopoNews", "Detail page url : " + news.getLink());
			wvNewsReader.loadUrl(news.getLink());
		}
		//actionBar = getActionBar();
		//actionBar.setTitle("news.google.com");
		
//		String appKey = "48e14da4a39dd71d6f3807200b7dce326a1dba48b2c0ba27";
//		Appodeal.disableLocationPermissionCheck();
//		Appodeal.initialize(this, appKey, Appodeal.MREC);
//		Appodeal.setMrecCallbacks(new MrecCallbacks() {
//			  Toast mToast;
//			  @Override
//			  public void onMrecLoaded(boolean isPrecache) {
//			    showToast("onMrecLoaded");
//			    Appodeal.show(selfActivity, Appodeal.MREC);
//			  }
//
//			  @Override
//			  public void onMrecFailedToLoad() {
//			    showToast("onMrecFailedToLoad");
//			  }
//
//			  @Override
//			  public void onMrecShown() {
//			    showToast("onMrecShown");
//			  }
//
//			  @Override
//			  public void onMrecClicked() {
//			    showToast("onMrecClicked");
//			  }
//
//			  void showToast(final String text) {
//			    if (mToast == null) {
//			      mToast = Toast.makeText(selfActivity, text, Toast.LENGTH_SHORT);
//			    }
//			    mToast.setText(text);
//			    mToast.setDuration(Toast.LENGTH_SHORT);
//			    mToast.show();
//			  }
//			});

		if (isShowAds == false){
			AdServiceManager.get(this, new IServiceCallback<IAdService>(){
				@Override
				public void call(IAdService service) {
					IAdService adService = service;
					IInterstitialAd ad = adService.getInterstitialAd("i.litenews.detail");
					ad.popup();
				}
			});
			isShowAds = true;
		}
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	//Appodeal.onResume(this, Appodeal.MREC);
		//wvNewsReader.reload();
    }
    
    
    
    private void closePage(){
    	this.finish();
    }
    
    @Override
    public void onBackPressed() {
    	
    	if (wvNewsReader != null && wvNewsReader.canGoBack()){
    		wvNewsReader.goBack();
    		return;
    	}
    	
    	super.onBackPressed();
    }
}
