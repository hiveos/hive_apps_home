package hive.apps.home;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
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

	private PagerAdapter mPagerAdapter;

	private static final int NUM_PAGES = 3;

	ArrayList<String> mUserInformation = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// if (isTablet(this) == true) {
		//
		// android.support.v4.app.FragmentManager mTimetableFragmentManager =
		// getSupportFragmentManager();
		// android.support.v4.app.FragmentTransaction
		// mTimetableFragmentTransaction = mTimetableFragmentManager
		// .beginTransaction();
		//
		// TimetableWidgetPageFragment mTimetableFragment = new
		// TimetableWidgetPageFragment();
		// mTimetableFragmentTransaction.replace(
		// R.id.layout_tablet_timetable_fragment_holder,
		// mTimetableFragment);
		// mTimetableFragmentTransaction.commit();
		//
		// android.support.v4.app.FragmentManager mLessonWidgetFragmentManager =
		// getSupportFragmentManager();
		// android.support.v4.app.FragmentTransaction
		// mLessonWidgetFragmentTransaction = mLessonWidgetFragmentManager
		// .beginTransaction();
		//
		// LessonWidgetPageFragment mLessonWidgetFragment = new
		// LessonWidgetPageFragment();
		// mLessonWidgetFragmentTransaction.replace(
		// R.id.layout_tablet_lesson_widget_fragment_holder,
		// mLessonWidgetFragment);
		// mLessonWidgetFragmentTransaction.commit();
		//
		// } else {

		Intent mSplashIntent = new Intent(this, WelcomeActivity.class);
		startActivity(mSplashIntent);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		// mPager.setPageTransformer(true, new PageTransformer()); // Zoom out
		// transition animation
		mPager.setAdapter(mPagerAdapter);
		mPager.setCurrentItem(0);
		// }

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

			Fragment fragment = new Fragment();

			switch (position) {
			case 0: {
				return fragment = new AppsPageFragment();
			}
			case 1: {
				return fragment = new LessonWidgetPageFragment();
			}
			case 2: {
				return fragment = new TimetableWidgetPageFragment();
			}
			default:
				break;
			}
			return fragment;

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
}
