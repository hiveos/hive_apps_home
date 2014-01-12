package hive.apps.home;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

public class Animations {

	public static void translateAvatar() {
		Animation animation = new TranslateAnimation(0, -250, 0, -465);
		animation.setDuration(2000);
		animation.setFillAfter(true);
		WelcomeActivity.mDrawerAvatar.startAnimation(animation);
	}

	public static void translateName() {
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
				// TODO Auto-generated method stub
				((WelcomeActivity) WelcomeActivity.kontekst).finish();
				((Activity) WelcomeActivity.kontekst)
						.overridePendingTransition(R.anim.fade_in,
								R.anim.fade_out);
				
				;

			}
		});
		anim.setDuration(2000).start();

		Animation animation = new TranslateAnimation(0, 45, 0, -744);
		animation.setDuration(2000);
		animation.setFillAfter(true);
		WelcomeActivity.sLinearLayout.startAnimation(animation);

	}
}
