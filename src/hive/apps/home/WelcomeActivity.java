package hive.apps.home;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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
	String isLogged;
	StringBuilder text = new StringBuilder();

	ArrayList<String> mUserInformation = new ArrayList<String>();

	public void zavrsi() {
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		// getWindow().setBackgroundDrawableResource(R.drawable.transparent);
		mDrawerAvatar = (ImageView) findViewById(R.id.splash_avatar);
		mDrawerUserName = (TextView) findViewById(R.id.splash_user_name);
		mDrawerUserClass = (TextView) findViewById(R.id.splash_user_class);
		mDrawerUserId = (TextView) findViewById(R.id.splash_user_id);
		mBackground = (ImageView) findViewById(R.id.splash_bg);
		nameHolder = (LinearLayout) findViewById(R.id.splashNameHolder);
		classHolder = (LinearLayout) findViewById(R.id.splashClassHolder);
		idHolder = (LinearLayout) findViewById(R.id.splashIdHolder);
		sLinearLayout = (LinearLayout) findViewById(R.id.splashLinearLayout);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		kontekst = this;
		File log = new File(Environment.getExternalStorageDirectory()
				+ "/HIVE/User/logged");
		if (log.exists()) {
			// Read text from file
			try {
				BufferedReader br = new BufferedReader(new FileReader(log));
				String line;
				while ((line = br.readLine()) != null) {
					// text.append(line);
					isLogged = line;
				}

				br.close();
			} catch (IOException e) {
				// You'll need to add proper error handling here
			}
			Log.d("TEXT in WA:", text.toString());
			if (isLogged.equals("false")) {
				Intent intent1 = new Intent(WelcomeActivity.this,
						NFCActivity.class);
				startActivity(intent1);
			}
		} else {
			Intent intent1 = new Intent(WelcomeActivity.this, NFCActivity.class);
			startActivity(intent1);
		}

		setUserValues();
		if (isTablet(kontekst)) {
			translateAvatar();
			translateName();
		} else {
			finish();
		}
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

	public void translateAvatar() {
		Animation animation = new TranslateAnimation(0, -250, 0, -465);
		animation.setDuration(2000);
		animation.setFillAfter(true);
		WelcomeActivity.mDrawerAvatar.startAnimation(animation);
	}

	public void translateName() {
		ObjectAnimator anim = ObjectAnimator.ofFloat(
				WelcomeActivity.mBackground, "", 0.0f);
		anim.addListener(new AnimatorListener() {
			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				((WelcomeActivity) WelcomeActivity.kontekst).finish();
				((Activity) WelcomeActivity.kontekst)
						.overridePendingTransition(R.anim.fade_in,
								R.anim.fade_out);
				Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
				startActivity(i);
			}
		});
		anim.setDuration(2000).start();

		Animation animation = new TranslateAnimation(0, 45, 0, -744);
		animation.setDuration(2000);
		animation.setFillAfter(true);
		WelcomeActivity.sLinearLayout.startAnimation(animation);

	}

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

}
