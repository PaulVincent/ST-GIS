package edu.kit.gik.STGIS.view3d;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout.LayoutParams;

import edu.kit.gik.STGIS.dataresource.R;
import edu.kit.gik.STGIS.importer.GOCADConnector;
import edu.kit.gik.STGIS.importer.OGLLayer;
import edu.kit.gik.STGIS.opengles.InteractiveRenderer;
import edu.kit.gik.STGIS.opengles.InteractiveSurfaceView;



public class InteractiveActivity extends Activity /*implements SensorEventListener*/ {

	
	private static final String TAG = InteractiveActivity.class.getSimpleName();
	private static boolean AR = false;
	private static InteractiveSurfaceView mGLView;
	public static GOCADConnector connect3D = new GOCADConnector();
	public static OGLLayer tsobj;
	private FrameLayout frame;
	private static Button b1;
	private static Button b2;
	private static Switch s1;
	private String intentData = null;
	private String intentType = null;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		String intentData = null;
//		String intentType = null;

//		Bundle extras = getIntent().getExtras();
//		if (extras != null) {
//			intentData = extras.getString(getString(R.string.TSObject));
//			intentType = extras.getString("ResourceType");
//		}
//
//		getTSObject(intentData, intentType);
//		Log.d("daten", intentData);
//		ct = new CoordinateTrafo(epsg);

//		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//		initListeners();
		// Create a GLSurfaceView instance and set it
		// as the ContentView for this Activity
		mGLView = new InteractiveSurfaceView(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mGLView.mDensity = displayMetrics.density;

		createLayout();


		setContentView(frame);
	}

	public static Boolean setTSObject(BufferedReader br) {
		try {
			tsobj = connect3D.readTSObject(br);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	private void getTSObject(String intentData, String intentType) {
		try {
			BufferedReader in = null;
			if (intentType.equalsIgnoreCase("WFS")) {
				in = new BufferedReader(new StringReader(intentData));
			} else if (intentType.equalsIgnoreCase("SDCARD")) {
				in = new BufferedReader(new FileReader(intentData));
			}

			tsobj = connect3D.readTSObject(in);
			Log.d("layername", tsobj.getName());
			in.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onPause() {
		Log.d("onPause","onPause");
		super.onPause();
		// HelloOpenGLES20Renderer.pan = false;
		// The following call pauses the rendering thread.
		// If your OpenGL application is memory intensive,
		// you should consider de-allocating objects that
		// consume significant memory here.
		mGLView.onPause();
//		if (mCamera != null) {
//			mPreview.setCamera(null);
//			mCamera.release();
//			mCamera = null;
//		}
//		mSensorManager.unregisterListener(this);
//		manager.removeUpdates(listener);
	}

	@Override
	protected void onResume() {
		Log.d("onResume","onResume");
		super.onResume();

		resetUI();
		
		
		// The following call resumes a paused rendering thread.
		// If you de-allocated graphic objects for onPause()
		// this is a good place to re-allocate them.

//		mCamera = Camera.open();
//		cameraCurrentlyLocked = defaultCameraId;
//		mPreview.setCamera(mCamera);
		// mSensorManager.registerListener(this,
		// mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
		// SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	protected void onStop() {
		Log.d("onStop","onStop");
		// TODO Auto-generated method stub
		super.onStop();
		
		mGLView.onPause();
//		if (mCamera != null) {
//			mPreview.setCamera(null);
//			mCamera.release();
//			mCamera = null;
//		}
//		mSensorManager.unregisterListener(this);
//		manager.removeUpdates(listener);
	}
	
	@Override
	protected void onRestart() {
		Log.d("onRestart","onRestart");
		// TODO Auto-generated method stub
		super.onRestart();
		
//		resetUI();
	}
	
	private void resetUI() {
		// TODO Auto-generated method stub
		
		AR = false;
//		mPreview.mSurfaceView.setVisibility(SurfaceView.INVISIBLE);
		b1.setEnabled(false);
		b2.setEnabled(true);
		InteractiveRenderer.mdX = 0.0f;
		InteractiveRenderer.mdY = 0.0f;
		InteractiveRenderer.mAngleX = 0.0f;
		InteractiveRenderer.mAngleY = 0.0f;
		InteractiveRenderer.mAngleY = 0.0f;
		InteractiveRenderer.scale = 1.0f;
		InteractiveRenderer.pan = true;
//		b1.setVisibility(Button.VISIBLE);
//		b2.setVisibility(Button.VISIBLE);
//		myZoomBar.setVisibility(VerticalSeekBar.INVISIBLE);
		InteractiveRenderer.AR = false;
		// mGLView.requestRender();
		s1.setChecked(false);
//		mSensorManager.unregisterListener(GREX3DActivity.this);
//		manager.removeUpdates(listener);
		mGLView.onResume();
//		mGLView.ScaleListener;
//		mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		mGLView.requestRender();

	}

	private void createLayout() {

		frame = new FrameLayout(this);
//		mPreview = new Preview(this);
		s1 = new Switch(this);
		s1.setText("");
		s1.setChecked(false);
		s1.setTextOff("Interactive");
		s1.setTextOn("Augmented Reality");
		LayoutParams paramsswitch1 = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		paramsswitch1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		paramsswitch1.addRule(RelativeLayout.CENTER_HORIZONTAL);
		s1.setLayoutParams(paramsswitch1);

		s1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {

					Intent intent = new Intent(InteractiveActivity.this, ARActivity.class);
					intent.putExtra(getString(R.string.TSObject), intentData);
					intent.putExtra("ResourceType", intentType);
					startActivity(intent);
					
//					setARprefs();
					// HelloOpenGLES20Renderer.AR = true;
					// b1.setVisibility(Button.INVISIBLE);
					// b2.setVisibility(Button.INVISIBLE);
					// mPreview.mSurfaceView.setVisibility(SurfaceView.VISIBLE);
					// myZoomBar.setVisibility(VerticalSeekBar.VISIBLE);
					// mSensorManager.registerListener(GREX3DActivity.this,
					// mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
					// SensorManager.SENSOR_DELAY_FASTEST);
					// manager.requestLocationUpdates(providerName, 3000, 0,
					// listener);
				} else {
					// mSensorManager.unregisterListener(GREX3DActivity.this);
					// manager.removeUpdates(listener);
					resetUI();

				}
			}
		});
//		mPreview.mSurfaceView.setVisibility(SurfaceView.INVISIBLE);
//		frame.addView(mPreview);
		frame.addView(mGLView);

		RelativeLayout rel = new RelativeLayout(this);
		LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rel.setLayoutParams(params);

		b1 = new Button(this);
		Drawable d1 = getResources().getDrawable(R.drawable.custom_button);
		b1.setBackgroundDrawable(d1);
		LayoutParams paramsbutton1 = new RelativeLayout.LayoutParams(128, 128);
		paramsbutton1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		paramsbutton1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		b1.setLayoutParams(paramsbutton1);

		b2 = new Button(this);
		Drawable d2 = getResources().getDrawable(R.drawable.custom_button_2);
		b2.setBackgroundDrawable(d2);
		b2.setEnabled(false);

		LayoutParams paramsbutton2 = new RelativeLayout.LayoutParams(128, 128);
		paramsbutton2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		paramsbutton2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		b2.setLayoutParams(paramsbutton2);

		b1.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				b1.setEnabled(false);
				b2.setEnabled(true);
				InteractiveRenderer.mdX = 0.0f;
				InteractiveRenderer.mdY = 0.0f;
				InteractiveRenderer.pan = true;
				mGLView.requestRender();
			}
		});

		b2.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				b1.setEnabled(true);
				b2.setEnabled(false);
				InteractiveRenderer.mAngleX = 0.0f;
				InteractiveRenderer.mAngleY = 0.0f;
				InteractiveRenderer.pan = false;
				mGLView.requestRender();
			}
		});
//		t = new TextView(this);
//		LayoutParams paramstext = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		paramstext.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//		t.setLayoutParams(paramstext);
//		rel.addView(t);
		rel.addView(b1);
		rel.addView(b2);

//		myZoomBar = new VerticalSeekBar(this);
//		myZoomBar.setVisibility(VerticalSeekBar.INVISIBLE);
//		myZoomBar.setMax((int) HelloOpenGLES20Renderer.xExtent);
//		myZoomBar.setProgress(myZoomBar.getMax());
//		myZoomBar.setOnSeekBarChangeListener(myZoomBarOnSeekBarChangeListener);
//		RelativeLayout.LayoutParams zoomBarParams = new RelativeLayout.LayoutParams(
//				LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
//		zoomBarParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//		zoomBarParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

		rel.addView(s1);
//		textview = new TextView(this);
//		textview.setText("Start");
//		LayoutParams paramtext = new RelativeLayout.LayoutParams(
//				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		paramtext.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//		paramtext.addRule(RelativeLayout.CENTER_HORIZONTAL);
//		textview.setLayoutParams(paramstext);
//		rel.addView(textview);
//		rel.addView(myZoomBar, zoomBarParams);

		frame.addView(rel);

	}

//	protected static void setARprefs() {
//		AR = true;
//		
////		manager.requestLocationUpdates(providerName, 0, 0, listener);
//		HelloOpenGLES20Renderer.AR = true;
//		HelloOpenGLES20Renderer.XX = HelloOpenGLES20Renderer.xExtent;
//		b1.setVisibility(Button.INVISIBLE);
//		b2.setVisibility(Button.INVISIBLE);
//		s1.setChecked(true);
//		mPreview.mSurfaceView.setVisibility(SurfaceView.VISIBLE);
//		myZoomBar.setVisibility(VerticalSeekBar.VISIBLE);
//		myZoomBar.setMax((int) HelloOpenGLES20Renderer.xExtent);
//		myZoomBar.setProgress(myZoomBar.getMax());
//		myZoomBar.setEnabled(false);
//		try {
//			mSensorManager.registerListener(GREX3DActivity.class.newInstance(),
//					mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
//					SensorManager.SENSOR_DELAY_FASTEST);
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		}
//		InterpolateCoordinates();
////		Log.d("dx", Float.toString((float) (3492595.0 - connect3D.correctx)));
////		Log.d("dy", Float.toString((float) (5341589.0 - connect3D.correcty)));
////		Log.d("dz", Float.toString((float) (1000.0 - connect3D.correctz)));
////		HelloOpenGLES20Renderer.eyeX = (float) (3492595.0 - connect3D.correctx);
////		HelloOpenGLES20Renderer.eyeY = (float) (5341589.0 - connect3D.correcty);
////		HelloOpenGLES20Renderer.eyeZ = (float) (1000.0 - connect3D.correctz);
////		mGLView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
//		
//	}
	
	

//	public void initListeners() {
//		mSensorManager.registerListener(this,
//				mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
//				SensorManager.SENSOR_DELAY_FASTEST);
//	}

//	public void onAccuracyChanged(Sensor sensor, int accuracy) {
//		// TODO Auto-generated method stub
//
//	}

//	public void onSensorChanged(SensorEvent event) {
//		System.arraycopy(event.values, 0, rotvec, 0, 3);
//		SensorManager.getRotationMatrixFromVector(RotMat, event.values);
////		SensorManager.remapCoordinateSystem(RotMat, SensorManager.AXIS_Y,
////				SensorManager.AXIS_MINUS_X, RotMat);
////		mGLView.requestRender();
//
//	}
//
//	private OnSeekBarChangeListener myZoomBarOnSeekBarChangeListener = new OnSeekBarChangeListener() {
//		public void onProgressChanged(SeekBar seekBar, int progress,
//				boolean fromUser) {
//			// updateDataOnZoom();
//			// camScreen.invalidate();
//		}
//
//		public void onStartTrackingTouch(SeekBar seekBar) {
//			// Ignore
//		}
//
//		public void onStopTrackingTouch(SeekBar seekBar) {
//			// updateDataOnZoom();
//			// camScreen.invalidate();
//		}
//	};

//	public static void removeLocUpdates() {
//		// TODO Auto-generated method stub
//		manager.removeUpdates(listener);
//	}
//
//	public static void listenToLocUpdates() {
//		// TODO Auto-generated method stub
//		manager.requestLocationUpdates(providerName, 0, 0, listener);
////		Log.d("dx", Float.toString((float) (3492595.0 - connect3D.correctx)));
////		Log.d("dy", Float.toString((float) (5341589.0 - connect3D.correcty)));
////		Log.d("dz", Float.toString((float) (1000.0 - connect3D.correctz)));
////		HelloOpenGLES20Renderer.eyeX = (float) (3492595.0 - connect3D.correctx);
////		HelloOpenGLES20Renderer.eyeY = (float) (5341589.0 - connect3D.correcty);
////		HelloOpenGLES20Renderer.eyeZ = (float) (1000.0 - connect3D.correctz);
//	}

}