package hive.apps.home;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NFCActivity extends Activity {

	public static final String MIME_TEXT_PLAIN = "text/plain";
	public static final String TAG = "NfcDemo";

	private TextView mTitle;
	private TextView mDesc;
	private ImageView mCard;
	private TextView mNfcSettings;

	private NfcAdapter mNfcAdapter;
	ImageLoader imgLoader;
	int loader = R.drawable.avatar_default_4;
	ReadText thread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc);

		mTitle = (TextView) findViewById(R.id.nfc_title);
		mDesc = (TextView) findViewById(R.id.nfc_desc);
		mCard = (ImageView) findViewById(R.id.nfc_card);
		mNfcSettings = (TextView) findViewById(R.id.go_to_nfc_settings);

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (mNfcAdapter == null) {
			Toast.makeText(this, "This device doesn't support NFC.",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		handleIntent(getIntent());
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);

		if (!hasFocus) {
			windowCloseHandler.postDelayed(windowCloserRunnable, 250);
		}
	}

	private void toggleRecents() {
		Intent closeRecents = new Intent(
				"com.android.systemui.recent.action.TOGGLE_RECENTS");
		closeRecents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		ComponentName recents = new ComponentName("com.android.systemui",
				"com.android.systemui.recent.RecentsActivity");
		closeRecents.setComponent(recents);
		this.startActivity(closeRecents);
	}

	private Handler windowCloseHandler = new Handler();
	private Runnable windowCloserRunnable = new Runnable() {
		@Override
		public void run() {
			ActivityManager am = (ActivityManager) getApplicationContext()
					.getSystemService(Context.ACTIVITY_SERVICE);
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

			if (cn != null
					&& cn.getClassName().equals(
							"com.android.systemui.recent.RecentsActivity")) {
				toggleRecents();
			}
		}
	};

	@Override
	public void onBackPressed() {
		if (isNetworkAvailable()) {
			if (!mNfcAdapter.isEnabled()) {
				mTitle.setText(R.string.error_nfc_disabled);
				mDesc.setText(R.string.error_nfc_disabled_info);
				mCard.setImageResource(R.drawable.ic_card_error);

				if (getResources().getBoolean(R.bool.superUserMode)) {
					mNfcSettings.setVisibility(View.VISIBLE);
				}
			} else {
				mTitle.setText(R.string.show_card);
				mDesc.setText(R.string.show_card_info);
				mCard.setImageResource(R.drawable.ic_card);
				mNfcSettings.setVisibility(View.GONE);
			}
		} else {
			Intent i = new Intent(this, NoNetworkActivity.class);
			startActivity(i);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Ovo mora biti jer ako nema ovoga, kada se aplikacija ponovo resume-a,
		// bio bi crash
		setupForegroundDispatch(this, mNfcAdapter);
		applyWallpaper();

		if (isNetworkAvailable()) {
			if (!mNfcAdapter.isEnabled()) {
				mTitle.setText(R.string.error_nfc_disabled);
				mDesc.setText(R.string.error_nfc_disabled_info);
				mCard.setImageResource(R.drawable.ic_card_error);

				if (getResources().getBoolean(R.bool.superUserMode)) {
					mNfcSettings.setVisibility(View.VISIBLE);
				}
			} else {
				mTitle.setText(R.string.show_card);
				mDesc.setText(R.string.show_card_info);
				mCard.setImageResource(R.drawable.ic_card);
				mNfcSettings.setVisibility(View.GONE);
			}
		} else {
			Intent i = new Intent(this, NoNetworkActivity.class);
			startActivity(i);
		}

		mNfcSettings.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(
						android.provider.Settings.ACTION_NFC_SETTINGS), 0);
			}
		});
	}

	@Override
	protected void onPause() {
		// Isto tako kada aplikacija ode u pozadinu
		stopForegroundDispatch(this, mNfcAdapter);

		super.onPause();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// Da ne bi svaki put pokretali iznova aplikaciju kada se primakne
		// kartica,
		// pozivat cemo ovu funkciju
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

			String type = intent.getType();
			if (MIME_TEXT_PLAIN.equals(type)) {

				Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				new NdefReaderTask().execute(tag);

			} else {
				Log.d(TAG, "Wrong mime type: " + type);
			}
		} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

			// Ako budemo koristili Tech Discovered Intent
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			String[] techList = tag.getTechList();
			String searchedTech = Ndef.class.getName();

			for (String tech : techList) {
				if (searchedTech.equals(tech)) {
					new NdefReaderTask().execute(tag);
					break;
				}
			}
		}
	}

	// Da se aplikacija pokre�e iz pozadine
	public static void setupForegroundDispatch(final Activity activity,
			NfcAdapter adapter) {
		final Intent intent = new Intent(activity.getApplicationContext(),
				activity.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		final PendingIntent pendingIntent = PendingIntent.getActivity(
				activity.getApplicationContext(), 0, intent, 0);

		IntentFilter[] filters = new IntentFilter[1];
		String[][] techList = new String[][] {};

		filters[0] = new IntentFilter();
		filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
		filters[0].addCategory(Intent.CATEGORY_DEFAULT);
		try {
			filters[0].addDataType(MIME_TEXT_PLAIN);
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("Check your mime type.");
		}

		adapter.enableForegroundDispatch(activity, pendingIntent, filters,
				techList);
	}

	public static void stopForegroundDispatch(final Activity activity,
			NfcAdapter adapter) {
		adapter.disableForegroundDispatch(activity);
	}

	// Citanje pozadinski
	private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

		@Override
		protected String doInBackground(Tag... params) {
			Tag tag = params[0];

			Ndef ndef = Ndef.get(tag);
			if (ndef == null) {
				return null;
			}

			NdefMessage ndefMessage = ndef.getCachedNdefMessage();

			NdefRecord[] records = ndefMessage.getRecords();
			for (NdefRecord ndefRecord : records) {
				if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN
						&& Arrays.equals(ndefRecord.getType(),
								NdefRecord.RTD_TEXT)) {
					try {
						return readText(ndefRecord);
					} catch (UnsupportedEncodingException e) {
						Log.e(TAG, "Unsupported Encoding", e);
					}
				}
			}

			return null;
		}

		private String readText(NdefRecord record)
				throws UnsupportedEncodingException {

			byte[] payload = record.getPayload();

			String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8"
					: "UTF-16";
			int languageCodeLength = payload[0] & 0063;
			return new String(payload, languageCodeLength + 1, payload.length
					- languageCodeLength - 1, textEncoding);
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				String image_url = "http://hive.bluedream.info/student/"
						+ result + "/info/image.png";
				imgLoader = new ImageLoader(getApplicationContext());
				imgLoader.DisplayImage(image_url, loader, mCard);

				String name_url = "http://hive.bluedream.info/student/"
						+ result + "/info/name.txt";
				String surname_url = "http://hive.bluedream.info/student/"
						+ result + "/info/surname.txt";
				String class_url = "http://hive.bluedream.info/student/"
						+ result + "/info/class.txt";
				String id_url = "http://hive.bluedream.info/student/" + result
						+ "/info/id.txt";
				String regno_url = "http://hive.bluedream.info/student/"
						+ result + "/info/regno.txt";

				String sName = null, sSurname = null, sClass = null, sId = null, sRegno;

				thread = new ReadText();
				thread.execute(name_url);
				try {
					// WE ARE TRYING TO GET STRING CONTENT FROM AS A RESULT
					sName = thread.get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				thread = new ReadText();
				thread.execute(surname_url);
				try {
					// WE ARE TRYING TO GET STRING CONTENT FROM AS A RESULT
					sSurname = thread.get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				thread = new ReadText();
				thread.execute(class_url);
				try {
					// WE ARE TRYING TO GET STRING CONTENT FROM AS A RESULT
					sClass = thread.get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				thread = new ReadText();
				thread.execute(id_url);
				try {
					// WE ARE TRYING TO GET STRING CONTENT FROM AS A RESULT
					sId = thread.get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				thread = new ReadText();
				thread.execute(regno_url);
				try {
					// WE ARE TRYING TO GET STRING CONTENT FROM AS A RESULT
					sRegno = thread.get();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				File informationFile = new File(
						Environment.getExternalStorageDirectory()
								+ "/HIVE/User/information");
				if (!informationFile.exists())
					try {
						informationFile.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				try {

					FileWriter fw = new FileWriter(informationFile);
					fw.append(sName + " " + sSurname + "\n" + sId + "\n"
							+ sClass);
					fw.flush();
					fw.close();
					fw = null;

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				File log = new File(Environment.getExternalStorageDirectory()
						+ "/HIVE/User/logged");
				if (!log.exists()) {
					try {
						log.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				FileWriter Write;
				try {
					Write = new FileWriter(log);
					Write.write("true");
					Write.flush();
					Write.close();
					Write = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Intent intent = new Intent(NFCActivity.this,
				// WelcomeActivity.class);
				// startActivity(intent);
				finish();

			}

		}
	}

	public void applyWallpaper() {
		final WallpaperManager wallpaperManager = WallpaperManager
				.getInstance(this);
		final Drawable wallpaperDrawable = wallpaperManager.getFastDrawable();
		getWindow().setBackgroundDrawable(wallpaperDrawable);
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
