package com.poponews.lite.util;

import android.app.Activity;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.momock.util.SystemHelper;

public class UiHelper {
	
	public final static int FROM_LAUCHER = 0;//main
	public final static int FROM_PUSH = 1;//notification
	public final static int FROM_WIDGET = 2;//widget




	private static boolean fetchedData = false;

	public static boolean isFetchedData() {
		return fetchedData;
	}

	public static void setFetchedData(boolean f) {
		UiHelper.fetchedData = f;
	}



	private static boolean favEditMode = false;

	public static boolean isFavEditMode() {
		return favEditMode;
	}

	public static void setFavEditMode(boolean favEditMode) {
		UiHelper.favEditMode = favEditMode;
	}
	
	
	
	/*正常的离线下载*/
	public static final int NOT_OFFLINE_DOWNLOAD  = 0;
	public static final int OFFLINE_DOWNLOADING  = 1;
	public static final int OFFLINE_ALARM_DOWNLOADING  = 2;


	private static int offlineDownloadMode = NOT_OFFLINE_DOWNLOAD;

	public static int isOfflineDownloadMode() {
		return offlineDownloadMode;
	}

	public static void setOfflineDownloadMode(int offlineDownloadMode) {
		UiHelper.offlineDownloadMode = offlineDownloadMode;
	}
	

	private static boolean mainIsAlive = false;
	public static boolean isMainAlive() {
		return mainIsAlive;
	}

	public static void setMainAlive(boolean alive) {
		UiHelper.mainIsAlive = alive;
	}

	/**
	 * 设置一体化状态栏
	 * @param activity
	 * @param statusBar
	 * @param color
	 */
	public static void setStatusBarColor(Activity activity, View statusBar, int color){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window w = activity.getWindow();
			w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//status bar height
			int actionBarHeight = getActionBarHeight(activity);
			int statusBarHeight = getStatusBarHeight(activity);
			//action bar height
			statusBar.getLayoutParams().height = statusBarHeight;
			statusBar.getLayoutParams().width = SystemHelper.getScreenWidth(activity);
			statusBar.setBackgroundColor(color);
		}
		else
		{
			statusBar.setVisibility(View.GONE);
		}
	}
	public static int getActionBarHeight(Activity activity) {
		int actionBarHeight = 0;
		TypedValue tv = new TypedValue();
		if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,activity.getResources().getDisplayMetrics());
		}
		return actionBarHeight;
	}

	public static int getStatusBarHeight(Activity activity) {
		int result = 0;
		int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = activity.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

}
