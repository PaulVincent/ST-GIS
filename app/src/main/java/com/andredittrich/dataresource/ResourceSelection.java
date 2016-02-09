package com.andredittrich.dataresource;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ResourceSelection extends Activity {

	/**
	 * Button to choose the SD Card of the device to search for files
	 */
	private Button sdcard;
	/**
	 * Button to choose a web feature service to provide data
	 */
	private Button wfs;
	/**
	 * String to hold the path of the data folder on the SD Card
	 */
	public static final String ROOT_DIRECTORY = Environment
			.getExternalStorageDirectory().getAbsolutePath();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resourceselection);

		sdcard = (Button) findViewById(R.id.SDCard);
		sdcard.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String extState = Environment.getExternalStorageState();
				if(!extState.equals(Environment.MEDIA_MOUNTED)) {
					Toast.makeText(ResourceSelection.this, "No SD Card mounted!", Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(ResourceSelection.this,
							DataOnSDSelection.class);
					startActivity(intent);
				}				
			}
		});

		wfs = (Button) findViewById(R.id.WFS);
		wfs.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(ResourceSelection.this,
						WFSSelection.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
