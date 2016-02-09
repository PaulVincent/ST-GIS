package edu.kit.gik.STGIS.opengles;

import edu.kit.gik.STGIS.view3d.ARActivity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class ARSurfaceView extends GLSurfaceView {

    private ARRenderer mRenderer;   
  
    public float mDensity = 1f;
    public static float dy = 0;
    public static float dx = 0;
	
	public ARSurfaceView(Context context) {
		
		super(context);

		// Create an OpenGL ES 2.0 context.
		setEGLContextClientVersion(2);
//		setZOrderMediaOverlay(true);
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		

		// set the mRenderer member
		mRenderer = new ARRenderer();
		setRenderer(mRenderer);
		getHolder().setFormat(PixelFormat.TRANSPARENT);
//		getHolder().setFormat(PixelFormat.TRANSLUCENT);		
		// Render the view only when there is a change
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
	        final int action = event.getAction();
	        switch (action & MotionEvent.ACTION_MASK) {
	        case MotionEvent.ACTION_DOWN: {
//	        	if (mRenderer.ZoomDown) {
	        		ARActivity.setARprefs();
//	        		ARActivity.myZoomBar.setEnabled(false);
//		        	ARActivity.mPreview.mSurfaceView.setVisibility(SurfaceView.VISIBLE);
		        	
//	        	}
	        	
	            
	        }
	            
	        case MotionEvent.ACTION_MOVE: {
	            break;
	        }
	            
	        case MotionEvent.ACTION_UP: {
	            
	            break;
	        }
	            
	        case MotionEvent.ACTION_CANCEL: {
	            
	            break;
	        }
	        
	        case MotionEvent.ACTION_POINTER_UP: {
	            
	            break;
	        }
	        }
	        
	        return true;
	    }
	
	@Override
	public void requestRender() {
		// TODO Auto-generated method stub
		super.requestRender();
	}
}