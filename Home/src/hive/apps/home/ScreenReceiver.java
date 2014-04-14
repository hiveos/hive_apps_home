package hive.apps.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	Class<? extends Activity> activityClass = MainActivity.class;
	    	if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
	            activityClass = MainActivity.class;
	        }
	    	Intent i = new Intent(context, activityClass);
	        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        context.startActivity(i);
	         
	    }
}
