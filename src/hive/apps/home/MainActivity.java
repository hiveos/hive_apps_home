package hive.apps.home;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

@SuppressLint("DefaultLocale")
public class MainActivity extends FragmentActivity {

	private ViewPager mPager;
	private FrameLayout mTabletTimetableContainer;
	private FrameLayout mTabletLessonWidgetContainer;
	static Context k;
	StringBuilder text2 = new StringBuilder();
	String isLogged2;

	private PagerAdapter mPagerAdapter;

	private static int NUM_PAGES;

	ArrayList<String> mUserInformation = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		k=this;

	}
	
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if (isNetworkAvailable()) {

		} else {
			Intent i = new Intent(this, NoNetworkActivity.class);
			startActivity(i);
		}
		
		File log = new File(Environment.getExternalStorageDirectory()+"/HIVE/User/logged");
		if(log.exists()){
			//Read text from file
			try {
			    BufferedReader br = new BufferedReader(new FileReader(log));
			    String line;
			    while ((line = br.readLine()) != null) {
			        isLogged2=line;
			    	//text2.append(line);
			    }
			    br.close();
			}
			catch (IOException e) {
			    //You'll need to add proper error handling here
			}
			Log.d("TEXT in MA:",text2.toString());
			if(isLogged2.equals("false")){
				Intent intent1 = new Intent(MainActivity.this, WelcomeActivity.class);
				startActivity(intent1);
			}
		}
//		else{
//			Intent intent1 = new Intent(MainActivity.this, WelcomeActivity.class);
//			startActivity(intent1);
//		}
		
		
		if (isTablet(getApplicationContext())) {
			NUM_PAGES = 2;
		} else if (!isTablet(getApplicationContext())) {
			NUM_PAGES = 3;
		}
		
		Intent mDeviceAdminIntent = new Intent();
		mDeviceAdminIntent.setAction("hive.action.General");
		mDeviceAdminIntent.putExtra("do", "REQUEST_DEVICE_ADMIN");
		sendBroadcast(mDeviceAdminIntent);
		
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		// mPager.setPageTransformer(true, new PageTransformer()); // Zoom out
		// transition animation
		mPager.setAdapter(mPagerAdapter);
		mPager.setCurrentItem(0);

		applyWallpaper();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
	}

	public void applyWallpaper() {
		final WallpaperManager wallpaperManager = WallpaperManager
				.getInstance(this);
		final Drawable wallpaperDrawable = wallpaperManager.getFastDrawable();
		getWindow().setBackgroundDrawable(wallpaperDrawable);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1) {
			applyWallpaper();
		}
	}

	@Override
	public void setTitle(CharSequence title) {
	}

	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			Fragment mFragment = new Fragment();

			if (isTablet(getApplicationContext()) == true) {

				switch (position) {
				case 0: {
					return mFragment = new AppsPageFragment();
				}
				case 1: {
					return mFragment = new TimetableWidgetPageFragment();
				}
				default:
					break;
				}
			} else {

				switch (position) {
				case 0: {
					return mFragment = new AppsPageFragment();
				}
				case 1: {
					return mFragment = new LessonWidgetPageFragment();
				}
				case 2: {
					return mFragment = new TimetableWidgetPageFragment();
				}
				default:
					break;
				}
			}

			return mFragment;
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
	}

	public static class PageTransformer implements ViewPager.PageTransformer {
		private static float MIN_SCALE = 0.85f;
		private static float MIN_ALPHA = 0.5f;

		public void transformPage(View view, float position) {
			int pageWidth = view.getWidth();
			int pageHeight = view.getHeight();

			if (position < -1) {
				view.setAlpha(0);

			} else if (position <= 1) {
				float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
				float vertMargin = pageHeight * (1 - scaleFactor) / 2;
				float horzMargin = pageWidth * (1 - scaleFactor) / 2;
				if (position < 0) {
					view.setTranslationX(horzMargin - vertMargin / 2);
				} else {
					view.setTranslationX(-horzMargin + vertMargin / 2);
				}

				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);

				view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
						/ (1 - MIN_SCALE) * (1 - MIN_ALPHA));

			} else {
				view.setAlpha(0);
			}
		}
	}

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
