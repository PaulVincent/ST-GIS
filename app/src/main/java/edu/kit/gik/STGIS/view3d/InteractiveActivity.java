package edu.kit.gik.STGIS.view3d;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Switch;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

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
        Log.d("onPause", "onPause");
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("onResume", "onResume");
        super.onResume();

        resetUI();
    }

    @Override
    protected void onStop() {
        Log.d("onStop", "onStop");
        // TODO Auto-generated method stub
        super.onStop();

        mGLView.onPause();
    }

    @Override
    protected void onRestart() {
        Log.d("onRestart", "onRestart");
        // TODO Auto-generated method stub
        super.onRestart();

    }

    private void resetUI() {
        // TODO Auto-generated method stub

        AR = false;
        b1.setEnabled(false);
        b2.setEnabled(true);

        InteractiveRenderer.mdX = 0.0f;
        InteractiveRenderer.mdY = 0.0f;
        InteractiveRenderer.mAngleX = 0.0f;
        InteractiveRenderer.mAngleY = 0.0f;
        InteractiveRenderer.mAngleY = 0.0f;
        InteractiveRenderer.scale = 1.0f;
        InteractiveRenderer.pan = true;
        InteractiveRenderer.AR = false;

        s1.setChecked(false);

        mGLView.onResume();
        mGLView.requestRender();

    }

    private void createLayout() {

        frame = new FrameLayout(this);
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


                } else {

                    resetUI();
                }
            }
        });

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

        rel.addView(b1);
        rel.addView(b2);
        rel.addView(s1);

        frame.addView(rel);

    }
}