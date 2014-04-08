package com.innovators.ratemydress;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.parse.ParseUser;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	protected Button mCameraButton;
	protected Button mUploadButton;
	protected Button mProfileButton;
	protected Button mLogoutButton;
	
	public static final int TAKE_PHOTO_REQUEST = 0;
	public static final int MEDIA_TYPE_IMAGE = 4;
	public static final String TAG = MainActivity.class.getSimpleName();
	private static final int PICK_PHOTO_REQUEST = 1;
	
	protected Uri mMediaUri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
//		ParseAnalytics.trackAppOpened(getIntent());
		
		mCameraButton = (Button) findViewById(R.id.camera);
		mCameraButton.setOnClickListener(this);
		
		mUploadButton = (Button) findViewById(R.id.upload);
		mUploadButton.setOnClickListener(this);
		
		mProfileButton = (Button) findViewById(R.id.profile);
		mProfileButton.setOnClickListener(this);
		
		mLogoutButton = (Button) findViewById(R.id.logout);
		mLogoutButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			if(requestCode == PICK_PHOTO_REQUEST){
				if(data == null){
					Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
				}else{
					mMediaUri = data.getData();
				}
			}else{
				//Broadcast to add the images to the gallery
				Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				mediaScanIntent.setData(mMediaUri);
				sendBroadcast(mediaScanIntent);
			}
			
			Intent chooseFriendsIntent = new Intent(this, ChooseFriendsActivity.class);
			chooseFriendsIntent.setData(mMediaUri);
			startActivity(chooseFriendsIntent);
			
		}else if(resultCode == RESULT_CANCELED){ 	
			Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		
		case R.id.camera://Take Picture
			Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
			if(mMediaUri == null){
				//display an error
				Toast.makeText(MainActivity.this, R.string.error_external_storage, Toast.LENGTH_LONG).show();
			}else{
				takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
				startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
			}
			break;
		
		case R.id.upload://Choose and upload Picture
			//Intent to choose any type of content
			Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
			choosePhotoIntent.setType("image/*");
			startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
			break;
			
		case R.id.profile://View my profile
			Intent viewProfileIntent = new Intent(this, UserDetailsActivity.class);
			startActivity(viewProfileIntent);
			break;
		
		case R.id.logout://Logout from profile
			// Log the user out
			ParseUser.logOut();
			// Go to the login view
			startLoginActivity();
			break;
			
		default:
			break;
		}
	}

	private void startLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private Uri getOutputMediaFileUri(int mediaType) {
		//To be safe, should check that external storage is mounted
		//using Environment.getExternalStorageState()
		if(isExternalStorageAvailable()){
			//get the URI
			
			//1.Get the external storage directory
			String appName = MainActivity.this.getString(R.string.app_name);
			File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory
								(Environment.DIRECTORY_PICTURES), appName);
			//2.Create subdirectory specific to app
			if(! mediaStorageDir.exists()){
				if(!mediaStorageDir.mkdirs()){
					Log.e(TAG, "Failed to create directory");
				}
			}
			//3.Create a filename
			//4.Create the file
			File mediaFile = null;
			Date now = new Date();
			String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);
			String path = mediaStorageDir.getPath()+File.separator;
			if(mediaType == MEDIA_TYPE_IMAGE){
				mediaFile = new File(path+"IMG_"+timestamp+".jpg");
			}
			//5.Return the file's URI
			return Uri.fromFile(mediaFile);
		}else{
			return null;
		}
	}
	
	private boolean isExternalStorageAvailable(){
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
		
		
	}

}
