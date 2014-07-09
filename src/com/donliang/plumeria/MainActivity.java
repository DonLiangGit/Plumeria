package com.donliang.plumeria;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera; 
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	private Camera camInstance;
	private CameraPreview camPreview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Actionbar transparent
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_background));
        setContentView(R.layout.activity_main);      
            
        //camInstance = getCameraInstance();
        camInstance = getFrontFacingCamera();
        
        camPreview = new CameraPreview(this, camInstance);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(camPreview);
                
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();
//        }
    }

//    Check the camera is available or not
    private boolean checkCameraHardware(Context context) {
    	if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
    		return true;
    	} else { 
    		return false;
    	}
    }

//    public static Camera getCameraInstance() {
//    	Camera c = null;
//    	try {
//    		// import android.hardware.Camera instead, 
//    		// importing android.graphics.Camera, because graphics one does not have open().
//    		c = Camera.open();
//    	}
//    	catch (Exception e){
//    		// Camera is not available
//    	}
//		// will return null if the camera is not available.
//    	return c;
//    	
//    }
    
    public static Camera getFrontFacingCamera() {
		int camCount = 0;
		Camera cam = null;
		Camera.CameraInfo camInfo = new Camera.CameraInfo();
		camCount = Camera.getNumberOfCameras();
		
		for ( int camIndex = 0; camIndex < camCount; camIndex ++ ) {
			Camera.getCameraInfo(camIndex, camInfo);
			if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				try {
					cam = Camera.open(camIndex);
				} catch (RuntimeException e) {
					Log.e ("Frontfacing","Front-facing camera failed!" + e.getLocalizedMessage());
				}
			}
		}
    	return cam;
    }
    
    private PictureCallback callback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			if(bitmap == null) {
				Toast.makeText(getApplicationContext(), "not taken", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "taken", Toast.LENGTH_SHORT).show();
			}
			camInstance.release();
		}    	
    };
    		
    public void TakePicture(View view) {
    	camInstance.takePicture(null, null, callback);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {      
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
