package hive.apps.home;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WelcomeActivity extends Activity {

	Blur mBlur;
	Animations mAnimations;

	static ImageView mBackground;
	static ImageView mDrawerAvatar;
	static TextView mDrawerUserName;
	static TextView mDrawerUserClass;
	static TextView mDrawerUserId;
	static LinearLayout nameHolder, classHolder, idHolder, sLinearLayout;
	static Context kontekst;

	ArrayList<String> mUserInformation = new ArrayList<String>();
	
	public void zavrsi(){
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		//getWindow().setBackgroundDrawableResource(R.drawable.transparent);
		kontekst=this;

		mDrawerAvatar = (ImageView) findViewById(R.id.splash_avatar);
		mDrawerUserName = (TextView) findViewById(R.id.splash_user_name);
		mDrawerUserClass = (TextView) findViewById(R.id.splash_user_class);
		mDrawerUserId = (TextView) findViewById(R.id.splash_user_id);
		mBackground = (ImageView) findViewById(R.id.splash_bg);
		nameHolder=(LinearLayout)findViewById(R.id.splashNameHolder);
		classHolder=(LinearLayout)findViewById(R.id.splashClassHolder);
		idHolder=(LinearLayout)findViewById(R.id.splashIdHolder);
		sLinearLayout=(LinearLayout)findViewById(R.id.splashLinearLayout);
		
		setUserValues();
		Animations.translateAvatar();
		Animations.translateName();
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
			blurBackground(mUserAvatarPath);
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

	public void blurBackground(String file) {

		mBlur = new Blur();

		Bitmap fileToBlur = BitmapFactory.decodeFile(file);

		Bitmap mBlurredBmp = Blur.fastblur(this, fileToBlur, 25);

		mBackground.setImageBitmap(mBlurredBmp);
	}

}
