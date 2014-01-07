package hive.apps.home;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class AppDrawer extends Activity {

	LinearLayout mBooksButton;
	LinearLayout mNotebooksButton;
	LinearLayout mDrawingsButton;
	LinearLayout mSettingsButton;
	LinearLayout mSystemSettingsButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appdrawer);

		applyWallpaper();

		mBooksButton = (LinearLayout) findViewById(R.id.Books);
		mNotebooksButton = (LinearLayout) findViewById(R.id.Notebooks);
		mDrawingsButton = (LinearLayout) findViewById(R.id.Drawings);
		mSettingsButton = (LinearLayout) findViewById(R.id.Settings);
		mSystemSettingsButton = (LinearLayout) findViewById(R.id.SystemSettings);

		mBooksButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				Intent mBooksIntent = new Intent(Intent.ACTION_ALL_APPS);
				mBooksIntent.setClassName("hive.apps.books",
						"hive.apps.books.Glavna");
				mBooksIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(mBooksIntent);

			}
		});

		mNotebooksButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				Intent mNotebooksIntent = new Intent(Intent.ACTION_ALL_APPS);
				mNotebooksIntent.setClassName("hive.apps.notebooks",
						"hive.apps.notebooks.Shelf");
				mNotebooksIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(mNotebooksIntent);

			}
		});

		mDrawingsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				Intent mDrawingsIntent = new Intent(Intent.ACTION_ALL_APPS);
				mDrawingsIntent.setClassName("com.example.drawing",
						"com.example.drawing.Browser");
				mDrawingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(mDrawingsIntent);

			}
		});

		mSettingsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				Intent mHIVESettingsIntent = new Intent(Intent.ACTION_ALL_APPS);
				mHIVESettingsIntent.setClassName("hive.framework",
						"hive.framework.settings.SettingsActivity");
				mHIVESettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(mHIVESettingsIntent);

			}
		});

		mSystemSettingsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				startActivityForResult(new Intent(
						android.provider.Settings.ACTION_SETTINGS), 0);

			}
		});

	}

	public void applyWallpaper() {
		final WallpaperManager wallpaperManager = WallpaperManager
				.getInstance(this);
		final Drawable wallpaperDrawable = wallpaperManager.getFastDrawable();
		getWindow().setBackgroundDrawable(wallpaperDrawable);
	}

}
