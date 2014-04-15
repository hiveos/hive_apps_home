package hive.apps.home;

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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
        k = this;

        if (isTablet(getApplicationContext())) {
            NUM_PAGES = 2;
        } else if (!isTablet(getApplicationContext())) {
            NUM_PAGES = 3;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (isNetworkAvailable()) {

        } else {
            Intent mNoNetworkIntent = new Intent();
            mNoNetworkIntent.setAction("hive.action.General");
            mNoNetworkIntent.putExtra("do", "ERROR_NO_CONNECTION");
            sendBroadcast(mNoNetworkIntent);
        }

        File log = new File(Environment.getExternalStorageDirectory()
                + "/HIVE/User/logged");
        if (log.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(log));
                String line;
                while ((line = br.readLine()) != null) {
                    isLogged2 = line;
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("TEXT in MA:", text2.toString());
            if (isLogged2.equals("false")) {
                Intent intent1 = new Intent(MainActivity.this,
                        WelcomeActivity.class);
                startActivity(intent1);
            } else {
                mPager = (ViewPager) findViewById(R.id.pager);
                mPagerAdapter = new ScreenSlidePagerAdapter(
                        getSupportFragmentManager());
                // mPager.setPageTransformer(true, new PageTransformer()); //
                // Zoom out
                // transition animation
                mPager.setAdapter(mPagerAdapter);
            }
        } else {
            Intent intent1 = new Intent(MainActivity.this,
                    WelcomeActivity.class);
            startActivity(intent1);
        }

        Intent mDeviceAdminIntent = new Intent();
        mDeviceAdminIntent.setAction("hive.action.General");
        mDeviceAdminIntent.putExtra("do", "REQUEST_DEVICE_ADMIN");
        sendBroadcast(mDeviceAdminIntent);

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

    public boolean isAdmin() {
        File mUserInfoFile = new File(Environment.getExternalStorageDirectory()
                + "/HIVE/User/information");

        try {
            BufferedReader mBufferReader = new BufferedReader(new FileReader(
                    mUserInfoFile));
            String line;

            mUserInformation.clear();

            while ((line = mBufferReader.readLine()) != null) {
                mUserInformation.add(line);
            }
        } catch (IOException e) {
        }

        String adminvalue = mUserInformation.get(5).substring(
                mUserInformation.get(5).indexOf("=") + 1);
        Log.d("TAG", adminvalue);

        if (adminvalue.equals("1")) {
            return true;
        } else {
            return false;
        }

    }
}
