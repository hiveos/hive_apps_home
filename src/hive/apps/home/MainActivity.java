package hive.apps.home;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	Button mDrawerOpener;
//	Button mLauncherOpener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
		if (tabletSize) {
			setContentView(R.layout.activity_main);
		} else {
			setContentView(R.layout.activity_main);
		}

		mDrawerOpener = (Button) findViewById(R.id.OpenDrawer);
//		mLauncherOpener = (Button) findViewById(R.id.OpenLauncher);

		mDrawerOpener.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				Intent mAppDrawerIntent = new Intent(getApplicationContext(),
						AppDrawer.class);
				startActivity(mAppDrawerIntent);

			}
		});

//		mLauncherOpener.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View view) {
//
//				Intent intent = new Intent(Intent.ACTION_MAIN);
//
//				Intent mLauncherIntent = Intent.createChooser(intent,
//						"Choose Launcher");
//
//				if (mLauncherIntent.resolveActivity(getPackageManager()) != null) {
//					startActivity(mLauncherIntent);
//				}
//
//			}
//		});

		applyWallpaper();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void applyWallpaper() {
		final WallpaperManager wallpaperManager = WallpaperManager
				.getInstance(this);
		final Drawable wallpaperDrawable = wallpaperManager.getFastDrawable();
		getWindow().setBackgroundDrawable(wallpaperDrawable);
	}
}
