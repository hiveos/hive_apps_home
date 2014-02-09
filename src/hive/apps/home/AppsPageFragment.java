package hive.apps.home;

import hive.apps.home.TimetableWidgetListItems.TimetableItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class AppsPageFragment extends Fragment {

	ImageView mDrawerAvatarBlurred;
	ImageView mDrawerAvatar;
	TextView mDrawerUserName;
	TextView mDrawerUserClass;
	TextView mDrawerUserId;
	LinearLayout mSystemSettingsContainer;
	ImageView mSystemSettings;
	ImageView mWallpaperSelection;

	RelativeLayout mBooks;
	RelativeLayout mNotebooks;
	RelativeLayout mDrawings;

	ArrayList<String> mUserInformation = new ArrayList<String>();

	Blur mBlur;

	TextView mDayOfTheWeekView;
	TextView mLessonNameView;
	TextView mClassRemainingTimeView;
	TextView mClassRemainingTimeTitle;
	TextView mClassRemainingTimeUnits;

	Calendar mCalendar = Calendar.getInstance();
	RelativeLayout mLessonWidgetBody;
	LinearLayout mDivider;

	public int mMinute = 0, mHour = 0;
	public int mTotalTime = 0;

	public int mTimeUntilBell;
	public int mDay;
	public String mLesson;

	public boolean isThereClass;

	LayoutParams WidgetBodyParams;

	private TimetableWidgetItemAdapter mTimetableWidgetItemAdapter;

	private int mUpdateInterval = 5000;
	private Handler mWidgetUpdater;

	ArrayList<String> mLessonTimes = new ArrayList<String>();

	public static AppsPageFragment create(int pageNumber) {
		AppsPageFragment fragment = new AppsPageFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public AppsPageFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TimetableWidgetListItems mTimetableContent = new TimetableWidgetListItems();

		mTimetableWidgetItemAdapter = new TimetableWidgetItemAdapter(
				getActivity(), R.layout.row_layout, mTimetableContent.ITEMS);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_apps, container, false);

		mDrawerAvatar = (ImageView) rootView
				.findViewById(R.id.app_drawer_avatar);
		mDrawerUserName = (TextView) rootView
				.findViewById(R.id.app_drawer_user_name);
		mDrawerUserClass = (TextView) rootView
				.findViewById(R.id.app_drawer_user_class);
		mDrawerUserId = (TextView) rootView
				.findViewById(R.id.app_drawer_user_id);
		mDrawerAvatarBlurred = (ImageView) rootView
				.findViewById(R.id.app_drawer_avatar_blurred);
		mSystemSettings = (ImageView) rootView
				.findViewById(R.id.system_settings_button);
		mWallpaperSelection = (ImageView) rootView
				.findViewById(R.id.wallpaper_button);

		mBooks = (RelativeLayout) rootView.findViewById(R.id.app_drawer_books);
		mNotebooks = (RelativeLayout) rootView
				.findViewById(R.id.app_drawer_notebooks);
		mDrawings = (RelativeLayout) rootView
				.findViewById(R.id.app_drawer_drawings);
		mSystemSettingsContainer = (LinearLayout) rootView
				.findViewById(R.id.system_settings_container);

		mBooks.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mBooksIntent = new Intent(Intent.ACTION_ALL_APPS);
				mBooksIntent.setClassName("hive.apps.books",
						"hive.apps.books.Glavna");
				mBooksIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(mBooksIntent);
			}
		});

		mNotebooks.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mNotebooksIntent = new Intent(Intent.ACTION_ALL_APPS);
				mNotebooksIntent.setClassName("hive.apps.notebooks",
						"hive.apps.notebooks.Shelf");
				mNotebooksIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(mNotebooksIntent);
			}
		});

		mDrawings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mDrawingsIntent = new Intent(Intent.ACTION_ALL_APPS);
				mDrawingsIntent.setClassName("hive.apps.drawings",
						"hive.apps.drawings.Browser");
				mDrawingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(mDrawingsIntent);
			}
		});

		mSystemSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(
						android.provider.Settings.ACTION_SETTINGS), 0);
			}
		});

		mWallpaperSelection.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String title = getResources().getString(R.string.select_wallpaper);
				Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
				startActivity(Intent.createChooser(intent, title));
			}
		});

		if (getResources().getBoolean(R.bool.superUserMode)) {
			mSystemSettingsContainer.setVisibility(View.VISIBLE);
		}

		setUserValues();

		if (isTablet(getActivity())) {
			mDayOfTheWeekView = (TextView) rootView
					.findViewById(R.id.app_lesson_widget_day_of_the_week);
			mLessonNameView = (TextView) rootView
					.findViewById(R.id.app_lesson_widget_lesson_name);
			mClassRemainingTimeView = (TextView) rootView
					.findViewById(R.id.app_lesson_widget_remaining);
			mClassRemainingTimeUnits = (TextView) rootView
					.findViewById(R.id.app_lesson_widget_remaining_units);
			mClassRemainingTimeTitle = (TextView) rootView
					.findViewById(R.id.app_lesson_widget_remaining_title);
			mLessonWidgetBody = (RelativeLayout) rootView
					.findViewById(R.id.app_lesson_widget_parent);
			mDivider = (LinearLayout) rootView
					.findViewById(R.id.app_lesson_widget_divider1);

			mWidgetUpdater = new Handler();
			updateLessonWidgetValues();
			startUpdatingWidget();
		}
		return rootView;

	}

	public void setUserValues() {

		readUserInformation();

		String mUserAvatarPath = Environment.getExternalStorageDirectory()
				+ "/HIVE/User/avatar.png";

		File mUserAvatarFile = new File(mUserAvatarPath);
		File mUserInfoFile = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/User/information");

		BitmapFactory.Options mBitmapOptions = new BitmapFactory.Options();
		mBitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap mUserAvatar = BitmapFactory.decodeFile(mUserAvatarPath,
				mBitmapOptions);

		if (mUserAvatarFile.exists() && mUserInfoFile.exists()) {
			mDrawerAvatar.setImageBitmap(mUserAvatar);
			blurAvatar(mUserAvatarPath);
			mDrawerUserName.setText(mUserInformation.get(0).toUpperCase());
			mDrawerUserId.setText(mUserInformation.get(1).toUpperCase());
			mDrawerUserClass.setText(mUserInformation.get(2));

		} else {
			mDrawerAvatar.setImageResource(R.drawable.avatar_default_4);
			mDrawerUserName.setText("");
			mDrawerUserId.setText("");
			mDrawerUserClass.setText("");
		}

	}

	@SuppressWarnings("resource")
	public void readUserInformation() {
		File mUserInfoFile = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/User/information");
		try {
			BufferedReader mBufferReader = new BufferedReader(new FileReader(
					mUserInfoFile));
			String line;

			while ((line = mBufferReader.readLine()) != null) {
				mUserInformation.add(line);
			}
		} catch (IOException e) {
		}
	}

	public void blurAvatar(String file) {

		mBlur = new Blur();

		Bitmap fileToBlur = BitmapFactory.decodeFile(file);

		Bitmap mBlurredBmp = Blur.fastblur(getActivity(), fileToBlur, 20);

		mDrawerAvatarBlurred.setImageBitmap(mBlurredBmp);
	}

	@SuppressLint("SimpleDateFormat")
	public void setToday() {
		SimpleDateFormat mSimpleFormat = new SimpleDateFormat("EEEE");
		Date mDate = new Date();
		String mToday = mSimpleFormat.format(mDate);

		if (mDayOfTheWeekView != null) {
			mDayOfTheWeekView.setText(mToday);
		} else {
			Log.d("LISTITEM", "Null");
		}
	}

	public void getTime() {
		Calendar time = Calendar.getInstance();
		mMinute = time.get(Calendar.MINUTE);
		mHour = time.get(Calendar.HOUR_OF_DAY);
	}

	public void updateLessonWidgetValues() {
		setToday();
		setLesson();
	}

	public void setLesson() {

		getLessonWidgetValues();

		if (isThereClass == true) {

			RelativeLayout.LayoutParams WidgetBodyParams = new RelativeLayout.LayoutParams(
					600, 300);
			WidgetBodyParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
					RelativeLayout.TRUE);
			WidgetBodyParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
					RelativeLayout.TRUE);
			WidgetBodyParams.setMargins(0, 0, 0, 136);

			mLessonWidgetBody.setLayoutParams(WidgetBodyParams);

			mClassRemainingTimeView.setVisibility(View.VISIBLE);
			mClassRemainingTimeTitle.setVisibility(View.VISIBLE);
			mClassRemainingTimeUnits.setVisibility(View.VISIBLE);
			mDivider.setVisibility(View.VISIBLE);

			String mRemainingClassTime = mTimeUntilBell + "";

			if (mTimeUntilBell == 1) {
				mClassRemainingTimeUnits.setText(R.string.minute);
			} else {
				mClassRemainingTimeUnits.setText(R.string.minutes);
			}

			mClassRemainingTimeView.setText(mRemainingClassTime);
			mLessonNameView.setText(mLesson);

		} else if (isThereClass == false) {

			RelativeLayout.LayoutParams WidgetBodyParams = new RelativeLayout.LayoutParams(
					600, 180);
			WidgetBodyParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
					RelativeLayout.TRUE);
			WidgetBodyParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
					RelativeLayout.TRUE);
			WidgetBodyParams.setMargins(0, 0, 0, 136);

			mLessonWidgetBody.setLayoutParams(WidgetBodyParams);

			mClassRemainingTimeView.setVisibility(View.GONE);
			mClassRemainingTimeTitle.setVisibility(View.GONE);
			mClassRemainingTimeUnits.setVisibility(View.GONE);
			mDivider.setVisibility(View.GONE);

			mLessonNameView.setText(R.string.no_classes);

		}

	}

	public void getLessonWidgetValues() {

		getTime();

		mTotalTime = (mHour * 60) + mMinute;

		if (mHour == 8 && mMinute >= 20) {
			mTimeUntilBell = 540 - mTotalTime;
			mLesson = mTimetableWidgetItemAdapter.getLesson(0);
			isThereClass = true;
		}

		else if (mHour == 9 && mMinute < 15) {
			mTimeUntilBell = 555 - mTotalTime;
			mLesson = getResources().getString(R.string.breaktime);
			isThereClass = true;
		}

		else if (mHour == 9 && mMinute >= 15 && mMinute < 55) {
			mTimeUntilBell = 595 - mTotalTime;
			mLesson = mTimetableWidgetItemAdapter.getLesson(1);
			isThereClass = true;
		}

		else if ((mHour == 9 && mMinute >= 55) || (mHour == 10 && mMinute < 5)) {
			mTimeUntilBell = 605 - mTotalTime;
			mLesson = getResources().getString(R.string.breaktime);
			isThereClass = true;
		}

		else if (mHour == 10 && mMinute >= 5 && mMinute < 45) {
			mTimeUntilBell = 645 - mTotalTime;
			mLesson = mTimetableWidgetItemAdapter.getLesson(2);
			isThereClass = true;
		}

		else if (mHour == 10 && mMinute >= 45 && mMinute < 55) {
			mTimeUntilBell = 655 - mTotalTime;
			mLesson = getResources().getString(R.string.breaktime);
			isThereClass = true;
		}

		else if ((mHour == 10 && mMinute >= 55)
				|| (mHour == 11 && mMinute < 35)) {
			mTimeUntilBell = 695 - mTotalTime;
			mLesson = mTimetableWidgetItemAdapter.getLesson(3);
			isThereClass = true;
		}

		else if (mHour == 11 && mMinute >= 35 && mMinute < 45) {
			mTimeUntilBell = 705 - mTotalTime;
			mLesson = getResources().getString(R.string.breaktime);
			isThereClass = true;
		}

		else if ((mHour == 11 && mMinute >= 45)
				|| (mHour == 12 && mMinute < 25)) {
			mTimeUntilBell = 745 - mTotalTime;
			mLesson = mTimetableWidgetItemAdapter.getLesson(4);
			isThereClass = true;
		}

		else if ((mHour == 12 && mMinute >= 25)
				|| (mHour == 13 && mMinute < 10)) {
			mTimeUntilBell = 790 - mTotalTime;
			mLesson = getResources().getString(R.string.lunchbreak);
			isThereClass = true;
		}

		else if (mHour == 13 && mMinute >= 10 && mMinute < 50) {
			mTimeUntilBell = 830 - mTotalTime;
			mLesson = mTimetableWidgetItemAdapter.getLesson(5);
			isThereClass = true;
		}

		else if (mHour == 13 && mMinute >= 50) {
			mTimeUntilBell = 840 - mTotalTime;
			mLesson = getResources().getString(R.string.breaktime);
			isThereClass = true;
		}

		else if (mHour == 14 && mMinute < 40) {
			mTimeUntilBell = 880 - mTotalTime;
			mLesson = mTimetableWidgetItemAdapter.getLesson(6);
			isThereClass = true;
		}

		else if (mHour == 14 && mMinute >= 40 && mMinute < 50) {
			mTimeUntilBell = 890 - mTotalTime;
			mLesson = getResources().getString(R.string.breaktime);
			isThereClass = true;
		}

		else if ((mHour == 14 && mMinute > 50) || (mHour == 15 && mMinute < 30)) {
			mTimeUntilBell = 930 - mTotalTime;
			mLesson = mTimetableWidgetItemAdapter.getLesson(7);
			isThereClass = true;
		}

		else {
			mTimeUntilBell = 0;
			isThereClass = false;
		}
	}

	public class TimetableWidgetItemAdapter extends ArrayAdapter<TimetableItem> {

		private ArrayList<TimetableItem> items;

		public TimetableWidgetItemAdapter(Context context,
				int textViewResourceId, ArrayList<TimetableItem> objects) {
			super(context, textViewResourceId, objects);
			this.items = objects;
		}

		public String getLesson(int position) {
			TimetableItem item = items.get(position);
			String lesson = item.lesson;
			return lesson;
		}
	}

	Runnable mUpdate = new Runnable() {
		@Override
		public void run() {
			updateLessonWidgetValues();
			mWidgetUpdater.postDelayed(mUpdate, mUpdateInterval);
		}
	};

	public void startUpdatingWidget() {
		mUpdate.run();
	}

	public void stopUpdatingWidget() {
		mWidgetUpdater.removeCallbacks(mUpdate);
	}

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
}
