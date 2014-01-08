package hive.apps.home;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppsPageFragment extends Fragment {

	ImageView mDrawerAvatarBlurred;
	ImageView mDrawerAvatar;
	TextView mDrawerUserName;
	TextView mDrawerUserClass;
	TextView mDrawerUserId;

	RelativeLayout mBooks;
	RelativeLayout mNotebooks;
	RelativeLayout mDrawings;

	ArrayList<String> mUserInformation = new ArrayList<String>();

	Blur mBlur;

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

		mBooks = (RelativeLayout) rootView.findViewById(R.id.app_drawer_books);
		mNotebooks = (RelativeLayout) rootView
				.findViewById(R.id.app_drawer_notebooks);
		mDrawings = (RelativeLayout) rootView
				.findViewById(R.id.app_drawer_drawings);

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
				mDrawingsIntent.setClassName("com.example.drawing",
						"com.example.drawing.Browser");
				mDrawingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(mDrawingsIntent);
			}
		});

		setUserValues();

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

}
