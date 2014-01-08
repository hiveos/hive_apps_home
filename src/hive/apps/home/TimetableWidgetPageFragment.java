package hive.apps.home;

import hive.apps.home.TimetableWidgetListItems.TimetableItem;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TimetableWidgetPageFragment extends Fragment {

	private ListView mTimetableList;
	private TimetableWidgetItemAdapter mTimetableWidgetItemAdapter;

	public static TimetableWidgetPageFragment create(int pageNumber) {
		TimetableWidgetPageFragment fragment = new TimetableWidgetPageFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public TimetableWidgetPageFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_widget_timetable, container, false);

		mTimetableList = (ListView) rootView
				.findViewById(R.id.widget_timetable_list);

		TimetableWidgetListItems mTimetableContent = new TimetableWidgetListItems();
		mTimetableWidgetItemAdapter = new TimetableWidgetItemAdapter(
				getActivity(), R.layout.row_layout, mTimetableContent.ITEMS);
		mTimetableList.setAdapter(mTimetableWidgetItemAdapter);

		// mTimetableContent.addItem(new TimetableItem("Lesson", "9",
		// "Teacher", "8:20 - 9:00")); 
		
		// We can add new items to the list by
		// using addItem method of
		// TimetableWidgetListItems class from
		// any other class...

		return rootView;
	}

	private class TimetableWidgetItemAdapter extends
			ArrayAdapter<TimetableItem> {

		private ArrayList<TimetableItem> items;

		public TimetableWidgetItemAdapter(Context context,
				int textViewResourceId, ArrayList<TimetableItem> objects) {
			super(context, textViewResourceId, objects);
			this.items = objects;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			if (v == null) {
				LayoutInflater vi = getActivity().getLayoutInflater();
				v = vi.inflate(R.layout.row_layout, null);
			}

			TimetableItem item = items.get(position);

			if (item != null) {
				TextView mLessonName = (TextView) v
						.findViewById(R.id.lesson_name);
				TextView mLessonNumber = (TextView) v
						.findViewById(R.id.lesson_number);
				TextView mTeacherName = (TextView) v
						.findViewById(R.id.teacher_name);
				TextView mLessonTime = (TextView) v
						.findViewById(R.id.lesson_times);

				if (mLessonName != null) {
					mLessonName.setText(item.lesson);
				} else {
					Log.d("LISTITEM", "Null");
				}
				if (mLessonName != null) {
					mLessonNumber.setText(item.lessonnumber);
				} else {
					Log.d("LISTITEM", "Null");
				}
				if (mTeacherName != null) {
					mTeacherName.setText(item.teacher);
				} else {
					Log.d("LISTITEM", "Null");
				}
				if (mLessonTime != null) {
					mLessonTime.setText(item.lessontimes);
				} else {
					Log.d("LISTITEM", "Null");
				}
			}

			return v;
		}
	}

}
