package hive.apps.home;

import hive.apps.home.TimetableWidgetListItems.TimetableItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class LessonWidgetPageFragment extends Fragment {

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

	public static LessonWidgetPageFragment create(int pageNumber) {
		LessonWidgetPageFragment fragment = new LessonWidgetPageFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public LessonWidgetPageFragment() {
	}

	@Override
	public void onResume() {
		super.onResume();
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
				R.layout.fragment_widget_lesson, container, false);

		mDayOfTheWeekView = (TextView) rootView
				.findViewById(R.id.lesson_widget_day_of_the_week);
		mLessonNameView = (TextView) rootView
				.findViewById(R.id.lesson_widget_lesson_name);
		mClassRemainingTimeView = (TextView) rootView
				.findViewById(R.id.lesson_widget_remaining);
		mClassRemainingTimeUnits = (TextView) rootView
				.findViewById(R.id.lesson_widget_remaining_units);
		mClassRemainingTimeTitle = (TextView) rootView
				.findViewById(R.id.lesson_widget_remaining_title);
		mLessonWidgetBody = (RelativeLayout) rootView
				.findViewById(R.id.lesson_widget_parent);
		mDivider = (LinearLayout) rootView
				.findViewById(R.id.lesson_widget_divider1);

		mWidgetUpdater = new Handler();
		updateLessonWidgetValues();
		startUpdatingWidget();

		return rootView;
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

			mLessonNameView.setText("No Classes!");

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
			mLesson = getResources().getString(R.string.breaktime);;
			isThereClass = true;
		}

		else if (mHour == 9 && mMinute >= 15 && mMinute < 55) {
			mTimeUntilBell = 595 - mTotalTime;
			mLesson = mTimetableWidgetItemAdapter.getLesson(1);
			isThereClass = true;
		}

		else if ((mHour == 9 && mMinute >= 55) || (mHour == 10 && mMinute < 5)) {
			mTimeUntilBell = 605 - mTotalTime;
			mLesson = getResources().getString(R.string.breaktime);;
			isThereClass = true;
		}

		else if (mHour == 10 && mMinute >= 5 && mMinute < 45) {
			mTimeUntilBell = 645 - mTotalTime;
			mLesson = mTimetableWidgetItemAdapter.getLesson(2);
			isThereClass = true;
		}

		else if (mHour == 10 && mMinute >= 45 && mMinute < 55) {
			mTimeUntilBell = 655 - mTotalTime;
			mLesson = getResources().getString(R.string.breaktime);;
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
			mLesson = getResources().getString(R.string.breaktime);;
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
			mLesson = getResources().getString(R.string.lunchbreak);;
			isThereClass = true;
		}

		else if (mHour == 13 && mMinute >= 10 && mMinute < 50) {
			mTimeUntilBell = 830 - mTotalTime;
			mLesson = mTimetableWidgetItemAdapter.getLesson(5);
			isThereClass = true;
		}

		else if (mHour == 13 && mMinute >= 50) {
			mTimeUntilBell = 840 - mTotalTime;
			mLesson = getResources().getString(R.string.breaktime);;
			isThereClass = true;
		}

		else if (mHour == 14 && mMinute < 40) {
			mTimeUntilBell = 880 - mTotalTime;
			mLesson = mTimetableWidgetItemAdapter.getLesson(6);
			isThereClass = true;
		}

		else if (mHour == 14 && mMinute >= 40 && mMinute < 50) {
			mTimeUntilBell = 890 - mTotalTime;
			mLesson = getResources().getString(R.string.breaktime);;
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
}
