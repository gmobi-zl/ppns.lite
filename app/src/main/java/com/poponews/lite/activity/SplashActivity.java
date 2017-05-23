package com.poponews.lite.activity;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.poponews.lite.R;
import com.momock.util.SystemHelper;
import com.poponews.lite.app.MessageTopics;
import com.poponews.lite.services.MonyNewsService;
import com.poponews.lite.util.AnimUtils;
import com.poponews.lite.util.UiHelper;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {
	RelativeLayout rlUpdate;
	RelativeLayout rlUpdateButton;
	public static Handler splashHandler;
	ProgressBar updatePb;
	TextView updateLength;
	TextView updateTitle;
	ImageView loading;

	private final static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;
	private final static int READ_PHONE_STATE_REQUEST_CODE = 2;
	private final static int ACCESS_FINE_LOCATION_REQUEST_CODE = 3;
	private final static int ACCESS_WRITE_SETTING_CODE = 4;

	void rsStart(){
		MonyNewsService.getInstance().connect();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		UiHelper.setStatusBarColor(this, findViewById(R.id.statusBarBackground),
				getResources().getColor(R.color.splash_top_bg));
		TextView tvVer = (TextView) findViewById(R.id.tv_splash_ver);
		updateLength =  (TextView) findViewById(R.id.update_message);
		updatePb =  (ProgressBar) findViewById(R.id.update_progress);
		updateTitle =  (TextView) findViewById(R.id.update_title);
		rlUpdate = (RelativeLayout) findViewById(R.id.rl_splash_update);
		rlUpdateButton =  (RelativeLayout) findViewById(R.id.rl_update_skip);
		loading = (ImageView)findViewById(R.id.iv_net_loading);
		String ver = SystemHelper.getAppVersion(this);
		tvVer.setText(getResources().getString(R.string.splash_ver, ver));


		startTopBgAnimation();
		AnimUtils.ShowLoading(loading, R.drawable.net_loading_anim, true, false);

		//MonyNewsService mnSvr = MonyNewsService.getInstance();
		//mnSvr.connect();
		
		splashHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch(msg.what){
                    case MessageTopics.SYSTEM_CONNECT_SERVER:
						AnimUtils.ShowLoading(loading, R.drawable.net_loading_anim, true, false);
                        rsStart();
                        break;
                    case MessageTopics.SYSTEM_ENTRY_MAIN:
						AnimUtils.ShowLoading(loading, R.drawable.net_loading_anim, false, false);
                    	entryApp();
                        break;
					case MessageTopics.SYSTEM_MSG_REQUEST_PERMISSION:
						boolean allPermissiobAllowed = true;
						if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
								!= PackageManager.PERMISSION_GRANTED) {

							ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
									WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
							allPermissiobAllowed = false;
						}

						if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_PHONE_STATE)
								!= PackageManager.PERMISSION_GRANTED) {

							ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE},
									READ_PHONE_STATE_REQUEST_CODE);
							allPermissiobAllowed = false;
						}

						if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
								!= PackageManager.PERMISSION_GRANTED) {

							ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
									ACCESS_FINE_LOCATION_REQUEST_CODE);
							allPermissiobAllowed = false;
						}
//						if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_SETTINGS)
//								!= PackageManager.PERMISSION_GRANTED) {
//
//							ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_SETTINGS},
//									ACCESS_WRITE_SETTING_CODE);
//							allPermissiobAllowed = false;
//						}

						if(allPermissiobAllowed)
						{
							new Timer().schedule(new TimerTask() {
								@Override
								public void run() {
									splashHandler.sendEmptyMessage(MessageTopics.SYSTEM_CONNECT_SERVER);
								}
							}, 100);
						} else {
							new Timer().schedule(new TimerTask() {
								@Override
								public void run() {
									splashHandler.sendEmptyMessage(MessageTopics.SYSTEM_MSG_REQUEST_PERMISSION);
								}
							}, 1000);
						}

						break;
				}

			}
		};
		
//		rlUpdateButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				App.get().getService(IUpdateService.class).updateCancel();
//				IConfigService configService = App.get().getService(IConfigService.class);
//				if(configService.isFirstEntry() && setEditionVisible())
//				{
//					entryEditonSetting();
//					configService.setFirstEntry(false);
//				}
//				else
//                	entryApp();
//
//			}
//		});
		
		/*
		new Handler().postDelayed(new Runnable(){

			@Override
			public void run() {
				Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
				mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(mainIntent);
				finish();
			}}, 2500);*/
		
		
		
	}

	private void startTopBgAnimation() {

		startAnimation(R.id.fl_splash_top_bg, R.anim.splash_top_bg, new AnimationListener() {

			@Override
			public void onAnimationStart(Animation paramAnimation) {

			}

			@Override
			public void onAnimationEnd(Animation paramAnimation) {
                startTileAnimation();
				startAnimation(R.id.iv_splash_logo, R.anim.splash_logo, new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
						final Timer readyTimer = new Timer();

						readyTimer.schedule(new TimerTask() {
							@Override
							public void run() {
								splashHandler.sendEmptyMessage(MessageTopics.SYSTEM_MSG_REQUEST_PERMISSION);
							}
						}, 1000);

					}

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
			}

			@Override
			public void onAnimationRepeat(Animation paramAnimation) {

			}

		});
	}

	private void startTileAnimation() {
		startAnimation(R.id.tv_splash_name, R.anim.splash_title, new AnimationListener(){
	
				@Override
				public void onAnimationEnd(Animation animation) {
					startAnimation(R.id.tv_splash_ver, R.anim.splash_title, null);
					startAnimation(R.id.tv_splash_foot, R.anim.splash_title, null);
				}
	
				@Override
				public void onAnimationRepeat(Animation animation) {
					
				}
	
				@Override
				public void onAnimationStart(Animation animation) {
	
					
				}
				
			});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
        	finish();
            System.exit(0);
	        return true;
		}

		return super.onKeyDown(keyCode, event);
		
	}

	/**
	 * basic animation function
	 * @param viewId       view which want to do animation
	 * @param resAnim      animation resource
	 * @param al           animation action listener
	 */
	private void startAnimation(int viewId, int resAnim, AnimationListener al){
		Animation anim = AnimationUtils.loadAnimation(this, resAnim);
		anim.reset();
		if(null != al){
			anim.setAnimationListener(al);
		}
		View l = findViewById(viewId);
		l.setVisibility(View.VISIBLE);
		l.clearAnimation();
		l.startAnimation(anim);
	}

    private void entryApp(){
		ImageView iv = (ImageView)findViewById(R.id.iv_splash_logo);
		if(iv != null) {
			AnimationDrawable loadAnim = (AnimationDrawable) iv.getBackground();
			if(loadAnim!=null &&loadAnim.isRunning())
				loadAnim.stop();
		}

        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }



   
    /*private void showMain()
	{
		Runnable showMain = new Runnable(){

			@Override
			public void run() {
				Intent intent;
				
				
				if (App.get().getCurrentActivity() == null)
				{
					intent = new Intent(App.get(),MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					App.get().startActivity(intent);
				}
				else
				{
					intent = new Intent(App.get().getCurrentActivity(),MainActivity.class);
					App.get().getCurrentActivity().startActivity(intent);
				}
					
			}
			
		};

		uiTaskService.runDelayed(showMain, 1000);
	}*/




	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

	}

}
