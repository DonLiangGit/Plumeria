package com.donliang.plumeria;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	// *IMPORTANT* must clear SurfaceView and SurfaceHolder concepts.
	
	private SurfaceHolder mHolder;
	private Camera mCamera;
	
	public CameraPreview(Context context, Camera camera) {
		// TODO Auto-generated constructor stub
		super(context);
		mCamera = camera;
		mHolder = getHolder();
		mHolder.addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
			mCamera.setPreviewDisplay(holder);
			// Preview Rotation
			mCamera.setDisplayOrientation(90);
			mCamera.startPreview(); //*IMPORTANT*
			
		} catch (IOException e) {
			Log.d("SurfaceCreated", "Error setting camera preview: " + e.getMessage()); // using Exception method getMessage()
		}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// TODO Auto-generated method stub
		// If the preview can change or rotate, we need to take care of these events in this block.
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		// We set it empty. 
		// *IMPORTANT* set release() the CameraPreview in the activity.
	}
	
	
}
