package hive.apps.home;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class NoNetworkActivity extends Activity {

	TextView mWirelessSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_no_network);
		applyWallpaper();

		mWirelessSettings = (TextView) findViewById(R.id.go_to_wireless_settings);
		if (getResources().getBoolean(R.bool.superUserMode)) {
			mWirelessSettings.setVisibility(View.VISIBLE);
		}

		mWirelessSettings.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(
						android.provider.Settings.ACTION_WIFI_SETTINGS), 0);
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isNetworkAvailable()) {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		if (isNetworkAvailable()) {
			finish();
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);

		if (!hasFocus) {
			windowCloseHandler.postDelayed(windowCloserRunnable, 250);
		}
	}

	private void toggleRecents() {
		Intent closeRecents = new Intent(
				"com.android.systemui.recent.action.TOGGLE_RECENTS");
		closeRecents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		ComponentName recents = new ComponentName("com.android.systemui",
				"com.android.systemui.recent.RecentsActivity");
		closeRecents.setComponent(recents);
		this.startActivity(closeRecents);
	}

	private Handler windowCloseHandler = new Handler();
	private Runnable windowCloserRunnable = new Runnable() {
		@Override
		public void run() {
			ActivityManager am = (ActivityManager) getApplicationContext()
					.getSystemService(Context.ACTIVITY_SERVICE);
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

			if (cn != null
					&& cn.getClassName().equals(
							"com.android.systemui.recent.RecentsActivity")) {
				toggleRecents();
			}
		}
	};

	public void applyWallpaper() {
		final WallpaperManager wallpaperManager = WallpaperManager
				.getInstance(this);
		final Drawable wallpaperDrawable = wallpaperManager.getFastDrawable();
		getWindow().setBackgroundDrawable(wallpaperDrawable);
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

}
