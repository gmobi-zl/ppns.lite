package com.poponews.lite.activity;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.momock.message.IMessageHandler;
import com.momock.message.Message;
import com.poponews.lite.MonyNewsEvents;
import com.poponews.lite.R;
import com.poponews.lite.model.MNCategory;
import com.poponews.lite.services.MonyNewsService;
import com.poponews.lite.ui.FragmentAdapter;
import com.poponews.lite.ui.ListFragment;

import java.util.ArrayList;
import java.util.List;

//public class MainActivity extends Activity implements MoPubInterstitial.InterstitialAdListener {
public class MainActivity extends AppCompatActivity {

	Context mContext;
	//WebView wvNews;
	ActionBar actionBar;

	private DrawerLayout mDrawerLayout;
	private ViewPager mViewPager;
	private TabLayout mTabLayout;
	FragmentAdapter mFragmentAdapteradapter;

	//MoPubInterstitial mInterstitial;

	//com.google.android.gms.ads.InterstitialAd mInterstitialAd;

	//private com.facebook.ads.InterstitialAd fbInterstitialAd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		String titleName = getResources().getString(R.string.app_title);
		toolbar.setTitle(titleName);
		
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		initViewPager();

		MonyNewsService mnSvr = MonyNewsService.getInstance();
		mnSvr.getMessageService().addHandler(MonyNewsEvents.MONY_NEWS_REFRESH, new IMessageHandler() {
			@Override
			public void process(Object sender, Message msg) {
				int currentTabPos = mTabLayout.getSelectedTabPosition();
				mFragmentAdapteradapter.refreshFragmentAtIndex(currentTabPos);
			}
		});

		//String appKey = "48e14da4a39dd71d6f3807200b7dce326a1dba48b2c0ba27";
		//Appodeal.initialize(this, appKey, Appodeal.INTERSTITIAL);

//		wvNews = (WebView)findViewById(R.id.wvNews);
//		WebSettings webSettings = wvNews.getSettings();
//		webSettings.setJavaScriptEnabled(true);
//		wvNews.loadUrl("https://news.google.com");
//		actionBar = getActionBar();
//		actionBar.setTitle("news.google.com");

//		Button btAdFB = (Button)findViewById(R.id.button1);
//		btAdFB.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(mContext, AdFBActivity.class);
//				mContext.startActivity(intent);
//			}
//		});
//
//		Button btAdMob = (Button)findViewById(R.id.button2);
//		btAdMob.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(mContext, AdMobActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				mContext.startActivity(intent);
//			}
//		});
//
//
//		Button btMopub = (Button)findViewById(R.id.button3);
//		btMopub.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(mContext, AdMopubActivity.class);
//				mContext.startActivity(intent);
//			}
//		});
//
//		mInterstitial = new MoPubInterstitial(this, "645312379c554704b7401db0b2a9b85b");
//		mInterstitial.setInterstitialAdListener(this);
//		Button btMopubIntAd = (Button) findViewById(R.id.button5);
//		btMopubIntAd.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mInterstitial.load();
//			}
//		});
//
//		mInterstitialAd = new InterstitialAd(this);
//		mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
//		mInterstitialAd.setAdListener(new AdListener() {
//			@Override
//			public void onAdClosed() {
//				//requestNewInterstitial();
//			}
//
//			@Override
//			public void onAdLoaded() {
//				mInterstitialAd.show();
//			}
//		});

//		//requestNewInterstitial();
//		Button btAdmobIntAd = (Button) findViewById(R.id.button6);
//		btAdmobIntAd.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				//requestNewInterstitial();
//				showAds.run();
//			}
//		});
//
//		Button btFBIntAd = (Button) findViewById(R.id.button7);
//		btFBIntAd.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				loadInterstitialAd();
//			}
//		});
	}

//	private void requestNewInterstitial() {
//		AdRequest adRequest = new AdRequest.Builder()
//				.addTestDevice("E8A6BE8CD2A79EBBCC2E69F6722F5FCD")
//				.build();
//
//		mInterstitialAd.loadAd(adRequest);
//	}

//	private void loadInterstitialAd() {
//		AdSettings.addTestDevice("4910262321e748540ea06485d14562ae");
//		fbInterstitialAd = new com.facebook.ads.InterstitialAd(this, "1706620212994801_1708959332760889");
//		fbInterstitialAd.setAdListener(new com.facebook.ads.InterstitialAdListener() {
//			@Override
//			public void onInterstitialDisplayed(Ad ad) {
//
//			}
//
//			@Override
//			public void onInterstitialDismissed(Ad ad) {
//
//			}
//
//			@Override
//			public void onError(Ad ad, AdError adError) {
//
//			}
//
//			@Override
//			public void onAdLoaded(Ad ad) {
//				fbInterstitialAd.show();
//			}
//
//			@Override
//			public void onAdClicked(Ad ad) {
//
//			}
//		});
//		fbInterstitialAd.loadAd();
//	}

	@Override
	protected void onDestroy() {
		//mInterstitial.destroy();
		super.onDestroy();
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
////		int id = item.getItemId();
////		if (id == R.id.action_settings) {
////			return true;
////		}
//		return super.onOptionsItemSelected(item);
//	}

//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if ((keyCode == KeyEvent.KEYCODE_BACK) && wvNews.canGoBack()) {
//			wvNews.goBack();
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

	// InterstitialAdListener methods
//	@Override
//	public void onInterstitialLoaded(MoPubInterstitial interstitial) {
//		if (interstitial.isReady()) {
//			Log.e("MonyNews", "Mobpu onInterstitialLoaded show ");
//			mInterstitial.show();
//		} else {
//			// Other code
//			Log.e("MonyNews", "Mobpu onInterstitialLoaded else");
//		}
//	}
//
//	@Override
//	public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
//		Log.e("MonyNews", "Mobpu Intersitital Load Failed error code = " + errorCode);
//	}
//
//	@Override
//	public void onInterstitialShown(MoPubInterstitial interstitial) {
//	}
//
//	@Override
//	public void onInterstitialClicked(MoPubInterstitial interstitial) {
//	}
//
//	@Override
//	public void onInterstitialDismissed(MoPubInterstitial interstitial) {
//	}
//
//	Runnable showAds = new Runnable() {
//		@Override
//		public void run() {
//			try {
//				Thread.sleep(10000);
//				//requestNewInterstitial();
//				//loadInterstitialAd();
//
//				Intent intent = new Intent(mContext, AdMobActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				mContext.startActivity(intent);
//			} catch (Exception e){
//
//			}
//		}
//	};

	private void initViewPager() {
		mTabLayout = (TabLayout) findViewById(R.id.tabs);
		List<String> titles = new ArrayList<>();

		MonyNewsService mnSvr = MonyNewsService.getInstance();
		ArrayList<MNCategory> tabList = mnSvr.dataService.getShowNewsTabList();
		if (tabList != null){
			int count = tabList.size();
			List<ListFragment> fragments = new ArrayList<>();

			for(int i=0;i<count;i++){
				MNCategory cate = tabList.get(i);
				String title = cate.getName();
				mTabLayout.addTab(mTabLayout.newTab().setText(title));
				titles.add(title);

				ListFragment lFragment = new ListFragment();
				lFragment.setCategoryName(title);
				fragments.add(lFragment);
			}

			mFragmentAdapteradapter =
					new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
			//给ViewPager设置适配器
			mViewPager.setAdapter(mFragmentAdapteradapter);
			//将TabLayout和ViewPager关联起来。
			mTabLayout.setupWithViewPager(mViewPager);
			//给TabLayout设置适配器
			mTabLayout.setTabsFromPagerAdapter(mFragmentAdapteradapter);
		}


	}
}
