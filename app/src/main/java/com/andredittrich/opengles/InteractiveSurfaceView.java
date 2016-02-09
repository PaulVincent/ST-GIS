package com.andredittrich.opengles;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;

public class InteractiveSurfaceView extends GLSurfaceView {

	private static final int INVALID_POINTER_ID = -1;
    // The active pointer is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;   
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
	private final float TOUCH_SCALE_FACTOR = 180.0f / 360;
    private InteractiveRenderer mRenderer;
    private float xstart;
    private float ystart;
    public float mDensity = 1f;
    public static float dy = 0;
    public static float dx = 0;
	
	public InteractiveSurfaceView(Context context) {
		
		super(context);

		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

		// Create an OpenGL ES 2.0 context.
		setEGLContextClientVersion(2);
		setZOrderMediaOverlay(true);
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		

		// set the mRenderer member
		mRenderer = new InteractiveRenderer();
		setRenderer(mRenderer);
//		getHolder().setFormat(PixelFormat.TRANSLUCENT);
//		getHolder().setFormat(PixelFormat.TRANSLUCENT);		
		// Render the view only when there is a change
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
				
		mScaleDetector.onTouchEvent(event);
	        
	        final int action = event.getAction();
	        switch (action & MotionEvent.ACTION_MASK) {
	        case MotionEvent.ACTION_DOWN: {

	        	Log.d("touch","touch");
	            final float x = event.getX();
	            final float y = event.getY();
                
	            xstart = x;
	            ystart = y;
	            mActivePointerId = event.getPointerId(0);
	            break;
	        }
	            
	        case MotionEvent.ACTION_MOVE: {
	        	Log.d("move","move");
	            final int pointerIndex = event.findPointerIndex(mActivePointerId);
	            final float x = event.getX(pointerIndex);
	            final float y = event.getY(pointerIndex);
	            // Only move if the ScaleGestureDetector isn't processing a gesture.
	            if (!mScaleDetector.isInProgress()) {
	                float dx = x - xstart;
	                float dy = y - ystart;
                InteractiveRenderer.mAngleY += dy * TOUCH_SCALE_FACTOR;
                InteractiveRenderer.mAngleX += dx * TOUCH_SCALE_FACTOR;
                
	                InteractiveRenderer.mdX += (dx/mDensity/2.5f) * 0.008* InteractiveRenderer.xExtent;
	                InteractiveRenderer.mdY += (dy/mDensity/2.5f) * 0.008* InteractiveRenderer.xExtent;
	               
	            }
	            requestRender();
	            xstart = x;
	            ystart = y;

	            break;
	        }
	            
	        case MotionEvent.ACTION_UP: {
	            mActivePointerId = INVALID_POINTER_ID;
	            break;
	        }
	            
	        case MotionEvent.ACTION_CANCEL: {
	            mActivePointerId = INVALID_POINTER_ID;
	            break;
	        }
	        
	        case MotionEvent.ACTION_POINTER_UP: {
	            final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) 
	                    >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
	            final int pointerId = event.getPointerId(pointerIndex);
	            if (pointerId == mActivePointerId) {
	                // This was our active pointer going up. Choose a new
	                // active pointer and adjust accordingly.
	                final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
	                xstart = event.getX(newPointerIndex);
	                ystart = event.getY(newPointerIndex);
	                mActivePointerId = event.getPointerId(newPointerIndex);
	            }
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

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
        	
            mScaleFactor *= detector.getScaleFactor();
            
            // Don't let the object get too small or too large.
//            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            InteractiveRenderer.scale = mScaleFactor;
            Log.d("scaleFactor", Float.toString(mScaleFactor));
           requestRender();
            return true;        	
        }
    }

	

}