package hive.apps.home;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setClassName("com.android.settings",
						"com.android.settings.wifi.WifiSettings");
				startActivity(intent);
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
