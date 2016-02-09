package com.andredittrich.view3d;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.andredittrich.coordtrafo.CoordinateTrafo;
import com.andredittrich.coordtrafo.DatumParams;
import com.andredittrich.dataresource.FeatureTypeSelection;
import com.andredittrich.dataresource.R;
import com.andredittrich.importer.GOCADConnector;
import com.andredittrich.importer.OGLLayer;
import com.andredittrich.opengles.ARRenderer;
import com.andredittrich.opengles.ARSurfaceView;



public class ARActivity extends Activity implements SensorEventListener {

	
	private static final String TAG = ARActivity.class.getSimpleName();
	private static ARSurfaceView mGLView;
	public static OGLLayer tsobj;
	private static SensorManager mSensorManager;
	public static float[] rotvec = new float[3];
	public static float[] Q = new float[16];
	public static float[] RotMat = new float[16];
	private FrameLayout frame;
	public static VerticalSeekBar myZoomBar;
	private static Switch s1;
	public static TextView t1;
	public static TextView t2;
	public static TextView t3;

	// Camera variables
	public static CameraPreview mPreview;
	Camera mCamera;
	int numberOfCameras;
	int cameraCurrentlyLocked;
	int defaultCameraId;

	// Location variables
	private static LocationManager manager;
	private static LocationListener listener;
	private LocationProvider lp;
	private TextView textview;
	public static float minZ;
	private static String providerName;

	// variables to hold "Landeskoordinaten" and geographic coordinates
	private static double longitude = 0.0;
	private static double latitude = 0.0;
	private static double altitude = 0.0;
	public static float azimuth = 0f;
	public static float pitch = 0f;
	public static float roll = 0f;
	private static CoordinateTrafo ct;
	public static int epsg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tsobj = InteractiveActivity.tsobj;
	
		Log.d("correctx", Float.toString(InteractiveActivity.connect3D.getCorrectz()));
		Log.d("EPSG", Integer.toString(epsg));
		ct = new CoordinateTrafo(epsg);
//
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		initListeners();
		// Create a GLSurfaceView instance and set it
		// as the ContentView for this Activity
		mGLView = new ARSurfaceView(this);
//
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mGLView.mDensity = displayMetrics.density;

		createLayout();

		manager = (LocationManager) getSystemService(LOCATION_SERVICE);

		// Provider mit grober Aufl�sung
		// und niedrigen Energieverbrauch
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_HIGH);

		// Namen ausgeben
		providerName = manager.getBestProvider(criteria, false);

		Log.d("???" + TAG, providerName);
		// LocationListener-Objekt erzeugen
		manager.isProviderEnabled(providerName);

		setContentView(frame);
		
		listener = new LocationListener() {
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				Log.d(TAG, "onStatusChanged()");
				Log.d(TAG,
						Boolean.toString(manager.isProviderEnabled(provider)));

			}

			public void onProviderEnabled(String provider) {
				Log.d(TAG, "onProviderEnabled()");
				
				textview.setText("enabled");
				
			}

			public void onProviderDisabled(String provider) {
				Log.d(TAG, "onProviderDisabled()");
				textview.setText("disabled");
				
//				startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
//				Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//				startActivity(intent);
			}

			public void onLocationChanged(Location location) {

				if (location != null) {
					Log.d(TAG, "onLocationChanged()");
					latitude = location.getLatitude();
					longitude = location.getLongitude();
					altitude = location.getAltitude();
					String s = "Breite: " + latitude + "\nL�nge: " + longitude
							+ "\nH�he: " + altitude + "\nGenauigkeit: "
							+ location.getAccuracy();
					textview.setText(s);
				} else {
					Location lastKnownLocation = manager
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

					latitude = lastKnownLocation.getLatitude();
					longitude = lastKnownLocation.getLongitude();
					altitude = lastKnownLocation.getAltitude();

					String s = "Breite: " + latitude + "\nL�nge: " + longitude
							+ "\nH�he: " + altitude + "\nGenauigkeit: "
							+ lastKnownLocation.getAccuracy();
//					textview.setText(s);
				}
//				double[] gk = cc.latLon2GK(latitude, longitude);
				double[] transformedCoordinate = ct.transformCoordinate(latitude, longitude, altitude);

//				InterpolateCoordinates();
				Log.d("rechtswert ", Double.toString(transformedCoordinate[0]));
				Log.d("hochwert ", Double.toString(transformedCoordinate[1]));
				ARRenderer.eyeX = (float) (transformedCoordinate[0] - InteractiveActivity.connect3D.getCorrectx());
				ARRenderer.eyeY = (float) (transformedCoordinate[1] - InteractiveActivity.connect3D.getCorrecty());
				if (!myZoomBar.isEnabled()) {
					ARRenderer.eyeZ = (float) (altitude - InteractiveActivity.connect3D.getCorrectz());
				}
				
				String s = "Hochwert: " + ARRenderer.eyeY + "\nRechtswert: " + ARRenderer.eyeX
						+ "\nHöhe: " + ARRenderer.eyeZ;
				textview.setText(s);
			}
				
		};
		manager.requestLocationUpdates(providerName, 0, 0, listener);
	}

	
	@Override
	protected void onPause() {
		super.onPause();
		// HelloOpenGLES20Renderer.pan = false;
		// The following call pauses the rendering thread.
		// If your OpenGL application is memory intensive,
		// you should consider de-allocating objects that
		// consume significant memory here.
		mGLView.onPause();
		if (mCamera != null) {
			mPreview.setCamera(null);
			mCamera.release();
			mCamera = null;
		}
		mSensorManager.unregisterListener(this);
		manager.removeUpdates(listener);
	}

	@Override
	protected void onResume() {
		super.onResume();

		mGLView.onResume();
		mCamera = Camera.open();
		cameraCurrentlyLocked = defaultCameraId;
		mPreview.setCamera(mCamera);
		mSensorManager.registerListener(this,
		 mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
		 SensorManager.SENSOR_DELAY_FASTEST);

	}

	@Override
	protected void onStop() {
		super.onStop();
		
		mGLView.onPause();
		if (mCamera != null) {
			mPreview.setCamera(null);
			mCamera.release();
			mCamera = null;
		}
		mSensorManager.unregisterListener(this);
		manager.removeUpdates(listener);
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		setARprefs();
	}
	
	private void createLayout() {

		frame = new FrameLayout(this);
		mPreview = new CameraPreview(this);
		s1 = new Switch(this);
		s1.setChecked(true);
		s1.setText("");
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
				if (!isChecked) {
					finish();
				}
			}
		});
//		mPreview.mSurfaceView.setVisibility(SurfaceView.INVISIBLE);
		frame.addView(mPreview);
		frame.addView(mGLView);
//
		RelativeLayout rel = new RelativeLayout(this);
		LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rel.setLayoutParams(params);

		myZoomBar = new VerticalSeekBar(this);
		minZ = InteractiveActivity.connect3D.minZ;
		myZoomBar.setMax((int) (ARRenderer.xExtent));//InteractiveActivity.connect3D.maxZ + 10);
		Log.d("xExtent", Float.toString(ARRenderer.xExtent));
		Log.d("setMax", Float.toString(myZoomBar.getMax()));
		myZoomBar.setProgress(myZoomBar.getMax());
		myZoomBar.setEnabled(false);
//		myZoomBar.setOnSeekBarChangeListener(myZoomBarOnSeekBarChangeListener);
		RelativeLayout.LayoutParams zoomBarParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		zoomBarParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		zoomBarParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
		textview = new TextView(this);
		textview.setText("Start");
		RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//
		rel.addView(s1);
		
		LinearLayout lin = new LinearLayout(this);
		t1 = new TextView(this);
		t1.setText(Float.toString(azimuth));
		
		t2 = new TextView(this);
		t2.setText(Float.toString(pitch));
		
		t3 = new TextView(this);
		t3.setText(Float.toString(roll));
		
		LayoutParams linparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		linparams.addRule(RelativeLayout.ALIGN_LEFT);
		linparams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		
		lin.addView(t1);
		lin.addView(t2);
		lin.addView(t3);
		lin.setOrientation(LinearLayout.VERTICAL);
		rel.addView(lin, linparams);
		rel.addView(myZoomBar, zoomBarParams);
		rel.addView(textview, textParams);
		
		frame.addView(rel);

	}

	public static void setARprefs() {
		if (!(altitude == 0)) {
		ARRenderer.eyeZ = (float) (altitude - InteractiveActivity.connect3D.getCorrectz());	
		} else {
		ARRenderer.eyeZ = ARRenderer.xExtent;
		}
		
		ARRenderer.XX = 0.0f;
		mPreview.mSurfaceView.setVisibility(SurfaceView.VISIBLE);
		
		myZoomBar.setMax((int) ARRenderer.eyeZ);
		myZoomBar.setProgress(myZoomBar.getMax());
		myZoomBar.setEnabled(false);

		
	}
		
	public void initListeners() {
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
				SensorManager.SENSOR_DELAY_FASTEST);
		
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		Log.d("accuracy", Integer.toString(accuracy));

	}

	public void onSensorChanged(SensorEvent event) {
		
		SensorManager.getRotationMatrixFromVector(RotMat, event.values);
		SensorManager.getOrientation(RotMat, rotvec);
		
		pitch = (float) (rotvec[1]*180.0f/Math.PI);
		roll = (float) (rotvec[2]*180.0f/Math.PI);
		azimuth = (float) (rotvec[0]*180.0f/Math.PI);
					
			t2.setText("pitch " + Float.toString(pitch));
			t3.setText("roll " + Float.toString(roll));
			t1.setText("azimuth " + Float.toString(azimuth));
		
		System.arraycopy(RotMat, 0, Q, 0, 16);
		/// if device ALWAYS PORTRAIT !!!!
//		SensorManager.remapCoordinateSystem(RotMat, SensorManager.AXIS_Y,
//				SensorManager.AXIS_MINUS_X, Q);
		
		mGLView.requestRender();

	}
}