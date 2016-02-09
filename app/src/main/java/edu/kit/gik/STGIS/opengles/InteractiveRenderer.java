package edu.kit.gik.STGIS.opengles;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;

import edu.kit.gik.STGIS.importer.OGLLayer;
import edu.kit.gik.STGIS.view3d.InteractiveActivity;

public class InteractiveRenderer implements Renderer {

	public static boolean AR = false;
	public static boolean pan = false;
	public static float xExtent;
	public static float mAngleY = 1f;
	public static float mAngleX = 1f;
	public static float mdY;
	public static float mdX;
	public static float scale = 1.f;
	public static float XX = 0.0f;
	private int muMVPMatrixHandle;
	private int PuMVPMatrixHandle;

	private float[] mMVPMatrix = new float[16];
	private float[] mMVMatrix = new float[16];
	private float[] mMMatrix = new float[16];
	private float[] mVMatrix = new float[16];
	private float[] mProjMatrix = new float[16];
	
	private float[] RotmMMatrix = new float[16];
	/**
	 * Stores a copy of the model matrix specifically for the light position.
	 */
	private float[] mLightModelMatrix = new float[16];

	/** Store the accumulated rotation. */
	private final float[] mAccumulatedRotation = new float[16];

	/** Store the accumulated rotation. */
	private final float[] mAccumulatedTranslation = new float[16];

	/** Store the current rotation. */
	private final float[] mCurrentRotation = new float[16];

	/** Store the current rotation. */
	private final float[] mCurrentTranslation = new float[16];

	private final float[] mTemporaryMatrix = new float[16];
	
	float[] eyeVector = new float[4];
	float[] neweyeVector = new float[4];
	private OGLLayer layer;

	private int mNormalHandle;

	private int mProgram;
	private int PProgram;

	/** This will be used to pass in the light position. */
	private int mLightPosHandle;
	private int myPositionHandle;
	private int myPPositionHandle;
	/** This will be used to pass in the modelview matrix. */
	private int mMVMatrixHandle;

	/**
	 * Used to hold a light centered on the origin in model space. We need a 4th
	 * coordinate so we can get translations to work when we multiply this by
	 * our transformation matrices.
	 */
	private final float[] mLightPosInModelSpace = new float[] { -5000f, 5000f,
			20000f, 1.0f };
//	private final float[] mLightPosInModelSpace = new float[] { 0f, -10000f,
//			10000f, 1.0f };
	/**
	 * Used to hold the current position of the light in world space (after
	 * transformation via model matrix).
	 */
	private final float[] mLightPosInWorldSpace = new float[4];
	/**
	 * Used to hold the transformed position of the light in eye space (after
	 * transformation via modelview matrix)
	 */
	private final float[] mLightPosInEyeSpace = new float[4];

//	final String vertexShaderCode = "uniform mat4 uMVPMatrix;      \n" // A
//																		// constant
//																		// representing
//																		// the
//																		// combined
//																		// model/view/projection
//																		// matrix.
//			+ "uniform mat4 u_MVMatrix;       \n" // A constant representing the
//													// combined model/view
//													// matrix.
//
//			+ "attribute vec4 vPosition;     \n" // Per-vertex position
//													// information we will pass
//													// in.
//			+ "vec4 a_Color = vec4(0, 1, 1, 1.0);     \n"
//			+ "attribute vec3 a_Normal;       \n" // Per-vertex normal
//													// information we will pass
//													// in.
//
//			+ "varying vec3 v_Position;       \n" // This will be passed into
//													// the fragment shader.
//			+ "varying vec4 v_Color;          \n" // This will be passed into
//													// the fragment shader.
//			+ "varying vec3 v_Normal;         \n" // This will be passed into
//													// the fragment shader.
//
//			// The entry point for our vertex shader.
//			+ "void main()                                                \n"
//			+ "{                                                          \n"
//			// Transform the vertex into eye space.
//			+ "   v_Position = vec3(u_MVMatrix * vPosition);             \n"
//			// Pass through the color.
//			+ "   v_Color = a_Color + 0.75;                                   \n"
//			// Transform the normal's orientation into eye space.
//			+ "   v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));      \n"
//			// gl_Position is a special variable used to store the final
//			// position.
//			// Multiply the vertex by the matrix to get the final point in
//			// normalized screen coordinates.
//			+ "   gl_Position = uMVPMatrix * vPosition;                 \n"
//			+ "}                                                          \n";

	 private final String vertexShaderCode =
	 "uniform mat4 uMVPMatrix;      \n"
	 + "uniform mat4 u_MVMatrix;       \n"
	 + "uniform vec3 u_LightPos;       \n"
	 + "vec4 a_Color = vec4(0, 1, 1, 1.0);     \n"
	 + "attribute vec4 vPosition;     \n"
	 + "attribute vec3 a_Normal;       \n"
	 + "varying vec4 v_Color;   \n"
	 + "void main()                    \n"
	 + "{                              \n"
	 +
	 "   vec3 modelViewVertex = vec3(u_MVMatrix * vPosition);              \n"
	 +
	 "   vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));     \n"
	 +
	 "   float distance = length(u_LightPos - modelViewVertex);             \n"
	 +
	 "   vec3 lightVector = normalize(u_LightPos - modelViewVertex);        \n"
	 +
	 "   float diffuse = max(dot(modelViewNormal, lightVector), 0.1);       \n"
	 + "   diffuse = diffuse * (1.0 / (0.0001*distance));  \n"
	 +
	 "   v_Color = (a_Color + 0.75) * diffuse;                                      \n"
	 +
	 "   gl_Position = uMVPMatrix * vPosition;                            \n"
	 + "   gl_PointSize = 5.0;         \n"
	 +
	 "}                                                                     \n";

//	final String fragmentShaderCode = "precision mediump float;       \n" // Set
//																			// the
//																			// default
//																			// precision
//																			// to
//																			// medium.
//																			// We
//																			// don't
//																			// need
//																			// as
//																			// high
//																			// of
//																			// a
//	// precision in the fragment shader.
//			+ "uniform vec3 u_LightPos;       \n" // The position of the light
//													// in eye space.
//
//			+ "varying vec3 v_Position;		\n" // Interpolated position for this
//												// fragment.
//			+ "varying vec4 v_Color;          \n" // This is the color from the
//													// vertex shader
//													// interpolated across the
//			// triangle per fragment.
//			+ "varying vec3 v_Normal;         \n" // Interpolated normal for
//													// this fragment.
//
//			// The entry point for our fragment shader.
//			+ "void main()                    \n"
//			+ "{                              \n"
//			// Will be used for attenuation.
//			+ "   float distance = length(u_LightPos - v_Position);                  \n"
//			// Get a lighting direction vector from the light to the vertex.
//			+ "   vec3 lightVector = normalize(u_LightPos - v_Position);             \n"
//			// Calculate the dot product of the light vector and vertex normal.
//			// If the normal and light vector are
//			// pointing in the same direction then it will get max illumination.
//			+ "   float diffuse = max(dot(v_Normal, lightVector), 0.1);              \n"
//			// Add attenuation.
//			+ "   diffuse = diffuse * (1.0 / (0.0001*distance));  \n"
//			// Multiply the color by the diffuse illumination level to get final
//			// output color.
//			+ "   gl_FragColor = v_Color * diffuse;                                  \n"
//			+ "}                                                                     \n";

	 private final String fragmentShaderCode =
	 "precision mediump float;  \n" +
	 "varying vec4 v_Color;      \n" +
	 "void main(){              \n" +
	 " gl_FragColor =  v_Color; \n" +
	 "}                         \n";

	public void onSurfaceCreated(GL10 unused, EGLConfig config) {

		// Set the background frame color
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Enable culling, depth-testing and (alpha-)blending
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glEnable(GLES20.GL_BLEND_SRC_ALPHA);

		// Initialize geometry
		layer = InteractiveActivity.tsobj;
		
		XX = xExtent;
//		initShapes();

//		// Define a simple shader program for our point.
//		final String pointVertexShader = "uniform mat4 uMVPMatrix;      \n"
//				+ "attribute vec4 vPosition;     \n"
//				+ "void main()                    \n"
//				+ "{                              \n"
//				+ "   gl_Position = uMVPMatrix   \n"
//				+ "               * vPosition;   \n"
//				+ "   gl_PointSize = 5.0;         \n"
//				+ "}                              \n";
//
//		final String pointFragmentShader = "precision mediump float;       \n"
//				+ "void main()                    \n"
//				+ "{                              \n"
//				+ "   gl_FragColor = vec4(1.0,    \n"
//				+ "   1.0, 1.0, 1.0);             \n"
//				+ "}                              \n";
//
//		int PvertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
//				pointVertexShader);
//		int PfragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
//				pointFragmentShader);
//
//		PProgram = GLES20.glCreateProgram(); // create empty OpenGL Program
//		GLES20.glAttachShader(PProgram, PvertexShader); // add the vertex shader
//														// to program
//		GLES20.glAttachShader(PProgram, PfragmentShader); // add the fragment
//															// shader to program
//		GLES20.glLinkProgram(PProgram);
//
//		myPPositionHandle = GLES20.glGetAttribLocation(PProgram, "vPosition");
//		PuMVPMatrixHandle = GLES20.glGetUniformLocation(PProgram, "uMVPMatrix");
//
//		GLES20.glEnableVertexAttribArray(myPPositionHandle);

		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
				fragmentShaderCode);

		mProgram = GLES20.glCreateProgram(); // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader
														// to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment
															// shader to program
		GLES20.glLinkProgram(mProgram); // creates OpenGL program executables

		// get handle to the vertex shader's vPosition member
		myPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		Matrix.setIdentityM(mAccumulatedRotation, 0);
		Matrix.setIdentityM(mAccumulatedTranslation, 0);
		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		GLES20.glEnableVertexAttribArray(myPositionHandle);
		mNormalHandle = GLES20.glGetAttribLocation(mProgram, "a_Normal");
		GLES20.glEnableVertexAttribArray(mNormalHandle);

		mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVMatrix");
		mLightPosHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
		
		Matrix.setLookAtM(mVMatrix, 0, 0, 0, xExtent, 0f, 0f, 0f, 0f, 1.0f,
				0.0f);

	}

	public void onDrawFrame(GL10 unused) {
		Log.d("answer","answer");
		
		// Redraw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		// Add program to OpenGL environment
		GLES20.glUseProgram(mProgram);
		
		// Set the buffer to starting position
		layer.getVertexBuffer().position(0);
		layer.getNormalBuffer().position(0);

		// Set the vertex attribute pointers
		GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false,
				0, layer.getNormalBuffer());
		GLES20.glVertexAttribPointer(myPositionHandle, 3, GLES20.GL_FLOAT,
				false, 0, layer.getVertexBuffer());

		// Check for INTERACTIVE or AUGMENTED REALITY mode
		
//			Log.e("AR", "NO");
			// Set eye point to standard
			Matrix.setLookAtM(mVMatrix, 0, 0, 0, xExtent, 0f, 0f, 0f, 0f, 1.0f,
					0.0f);
			
			// Reset matrices to identity
			Matrix.setIdentityM(mMMatrix, 0);
			Matrix.setIdentityM(mCurrentRotation, 0);
			Matrix.setIdentityM(mCurrentTranslation, 0);

			// Check if user is panning or rotating
			if (pan) {				
				panScene();
			} else {
				rotateScene();				
			}
			// Scale the scene
			Matrix.scaleM(mMMatrix, 0, scale, scale, scale);


		// Combine ModelMatrix and ViewMatrix 
		Matrix.multiplyMM(mMVMatrix, 0, mVMatrix, 0, mMMatrix, 0);

		// Calculate light position
		calcLightPos();
//		Matrix.setIdentityM(mLightModelMatrix, 0);
//		Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0,
//				mLightPosInModelSpace, 0);
//		Matrix.multiplyMV(mLightPosInEyeSpace, 0, mMVPLMatrix, 0,
//				mLightPosInWorldSpace, 0);
//		GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0],
//				mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);
		
		// Transmit light matrix to shader
		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0);

		// Apply a ModelView Projection transformation
		Matrix.multiplyMM(mTemporaryMatrix, 0, mProjMatrix, 0, mMVMatrix, 0);
		System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);
		
		// Transmit ModelViewProjection Matrix to shader
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
//		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mProjMatrix, 0);
		// Draw geometry front (triangles) and backside (lines)
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, layer.getIndexBuffer()
				.capacity(), GLES20.GL_UNSIGNED_INT, layer.getIndexBuffer());
		GLES20.glDrawElements(GLES20.GL_LINES,
				layer.getLineBuffer().capacity(), GLES20.GL_UNSIGNED_INT,
				layer.getLineBuffer());

//		GLES20.glUseProgram(PProgram);
//		drawPoints();
	}

	private void calcLightPos() {
		Matrix.setIdentityM(mLightModelMatrix, 0);
		Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0,
				mLightPosInModelSpace, 0);
		Matrix.multiplyMV(mLightPosInEyeSpace, 0, mMVMatrix, 0,
				mLightPosInWorldSpace, 0);
		GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0],
				mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);
		
	}

	private float[] calcViewCenter() {
		
		float[] oriVec = new float[4];
		float[] startVec = new float[4];
		startVec[0] = 0f;
		startVec[1] = 0f;
		startVec[2] = -1f;
		startVec[3] = 1f;
		Matrix.multiplyMV(oriVec, 0, mMMatrix, 0, startVec, 0);
//		float lambda = xExtent / oriVec[2];
//		float my = lambda * oriVec[0];
//		float ny = lambda * oriVec[1];
		
		 float lambda = neweyeVector[2]/ oriVec[2];
		 float my = neweyeVector[0] + lambda*oriVec[0];
		 float ny = neweyeVector[1] + lambda*oriVec[1];
		
		return new float[] {my, ny};
	}

	private void rotateScene() {
		Matrix.rotateM(mCurrentRotation, 0, mAngleX, 0.0f, 1.0f, 0.0f);
		Matrix.rotateM(mCurrentRotation, 0, mAngleY, 1.0f, 0.0f, 0.0f);
		mAngleX = 0.0f;
		mAngleY = 0.0f;

		Matrix.multiplyMM(mTemporaryMatrix, 0, mAccumulatedTranslation,
				0, mMMatrix, 0);
		System.arraycopy(mTemporaryMatrix, 0, mMMatrix, 0, 16);

		Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentRotation, 0,
				mAccumulatedRotation, 0);
		System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotation, 0,
				16);

		// Rotate the cube taking the overall rotation into account.
		Matrix.multiplyMM(mTemporaryMatrix, 0, mMMatrix, 0,
				mAccumulatedRotation, 0);
		System.arraycopy(mTemporaryMatrix, 0, mMMatrix, 0, 16);
		
	}

	private void panScene() {
		Matrix.translateM(mCurrentTranslation, 0, mdX, -mdY, 0);
		mdX = 0;
		mdY = 0;
		Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentTranslation, 0,
				mAccumulatedTranslation, 0);
		System.arraycopy(mTemporaryMatrix, 0, mAccumulatedTranslation,
				0, 16);

		Matrix.multiplyMM(mTemporaryMatrix, 0, mMMatrix, 0,
				mAccumulatedTranslation, 0);
		System.arraycopy(mTemporaryMatrix, 0, mMMatrix, 0, 16);

		Matrix.multiplyMM(mTemporaryMatrix, 0, mMMatrix, 0,
				mAccumulatedRotation, 0);
		System.arraycopy(mTemporaryMatrix, 0, mMMatrix, 0, 16);
		
	}

	private void drawPoints() {
		// Pass in the position information
		layer.getVertexBuffer().position(0);
		// layer.getLineBuffer().position(0);
		GLES20.glVertexAttribPointer(myPPositionHandle, 3, GLES20.GL_FLOAT,
				false, 0, layer.getVertexBuffer());

		GLES20.glEnableVertexAttribArray(myPPositionHandle);
		GLES20.glUniformMatrix4fv(PuMVPMatrixHandle, 1, false, mMVPMatrix, 0);

		// GLES20.glDrawArrays(GLES20.GL_POINTS, 0,
		// layer.getVertexBuffer().capacity()/3);

		layer.getLineBuffer().position(0);
		// GLES20.glDrawElements(GLES20.GL_LINES,
		// layer.getLineBuffer().capacity(),
		// GLES20.GL_UNSIGNED_INT, layer.getLineBuffer());

	}

	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		float ratio = (float) width / height;
		// this projection matrix is applied to object coodinates
		// in the onDrawFrame() method
		Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 1f, 200000);
//		Matrix.perspectiveM(mProjMatrix, 0, 35, ratio, 1, 1000);


	}

	private void initShapes() {
		layer = InteractiveActivity.tsobj;
	}

	private int loadShader(int type, String shaderCode) {

		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}

}
