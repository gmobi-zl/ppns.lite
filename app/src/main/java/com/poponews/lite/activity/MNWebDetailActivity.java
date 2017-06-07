package com.poponews.lite.activity;

import android.app.Instrumentation;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.mocean.AdServiceManager;
import com.mocean.IAd;
import com.mocean.IAdItem;
import com.mocean.IAdService;
import com.mocean.ICallback;
import com.mocean.IInterstitialAd;
import com.mocean.INativeAd;
import com.mocean.IServiceCallback;
import com.momock.util.Logger;
import com.poponews.lite.MonyNewsEvents;
import com.poponews.lite.R;
import com.poponews.lite.model.MNNews;
import com.poponews.lite.services.MonyNewsService;
import com.poponews.lite.ui.DetailAdView;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MNWebDetailActivity extends AppCompatActivity {
	
	WebView wvNewsReader;
	ProgressBar pbWebNewsLoad;
	AppCompatActivity selfActivity;
	boolean isShowAds = false;
	IAdService adService;
	INativeAd nAds;

	Context mContex;
	RelativeLayout adContainer;

	TextView tvBottomLeft;
	TextView tvBottomRight;

	Handler adHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1001){
				if (adService != null && nAds != null){
					IAdItem item = nAds.getAdItem(0);
					if (item != null){
						DetailAdView adView = new DetailAdView(mContex);
						adView.refresh(item);
						RelativeLayout adViewLayout = adView.getViews();
						adContainer.addView(adViewLayout);
						adContainer.setVisibility(View.VISIBLE);

						tvBottomLeft.setVisibility(View.VISIBLE);
						tvBottomRight.setText(mContex.getResources().getText(R.string.bottom_close));
					}
				}
			}
		}
	};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mnweb_detail);
        selfActivity = this;
		mContex = this;
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

		adContainer = (RelativeLayout)findViewById(R.id.rlAdContainer);
		tvBottomLeft = (TextView)findViewById(R.id.tvBottomLeft);
		tvBottomRight = (TextView)findViewById(R.id.tvBottomRight);

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
//			AdServiceManager.get(this, new IServiceCallback<IAdService>(){
//				@Override
//				public void call(IAdService service) {
//					IAdService adService = service;
//					IInterstitialAd ad = adService.getInterstitialAd("i.litenews.detail");
//					ad.popup();
//				}
//			});
			isShowAds = true;

			AdServiceManager.get(this, new IServiceCallback<IAdService>(){
				@Override
				public void call(IAdService service) {
					Logger.debug("Detail Ads service callback");
					adService = service;
					nAds = adService.getNativeAd("detail.ad", 50, 50, 1, null);
					nAds.setOnLoadLisenter(new ICallback() {
						@Override
						public void call(int resultCode) {
							Logger.debug("Detail Ads get native ad callback = " + resultCode);
							if (resultCode == IAd.OK){
								adHandler.sendEmptyMessage(1001);
							}
						}
					});
					nAds.load();
				}
			});
		}

		TextView tvBottomLeft = (TextView)findViewById(R.id.tvBottomLeft);
		tvBottomLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openAd();
			}
		});

		TextView tvBottomRight = (TextView)findViewById(R.id.tvBottomRight);
		tvBottomRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onTouchBack();
			}
		});
    }

	public void onTouchBack(){
		new Thread(){
			public void run() {
				try{
					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
				}
				catch (Exception e) {
					Log.e("Exception when onBack", e.toString());
				}
			}
		}.start();
	}
    
    @Override
    protected void onResume() {
    	super.onResume();
    	//Appodeal.onResume(this, Appodeal.MREC);
		//wvNewsReader.reload();
    }

    private void closeAdViews(){
		tvBottomLeft.setVisibility(View.GONE);
		adContainer.setVisibility(View.GONE);
		tvBottomRight.setText(mContex.getResources().getText(R.string.bottom_back));
	}

	private void openAd(){
		if (nAds != null){
			IAdItem item = nAds.getAdItem(0);
			if (item != null){
				item.execute("go", null);
				closeAdViews();
			}
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Logger.info("Activity onKeyUp keyCode = " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_MENU){
			openAd();
		}

		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		Logger.info("Activity onKeyDown keyCode = " + keyCode);
		return super.onKeyDown(keyCode, event);
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

    	if (adContainer != null && adContainer.getVisibility() == View.VISIBLE){
			closeAdViews();
			return;
		}
    	
    	super.onBackPressed();
    }
}
