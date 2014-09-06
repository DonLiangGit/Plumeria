package don.com.plumeria_demo;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@SuppressWarnings("deprecation")
public class MyActivity extends TabActivity {

    private Camera camInstance;
    private CameraPreview cameraPreview;
    private SurfaceHolder previewHolder = null;
    private FrameLayout preview;
    private boolean inPreview = false;
    private int currentCameraID = 0;

    UIOrientationEventListener uiOrientationEventListener;

    TabHost tabHost;
    ImageButton pictureTab;

    LayoutInflater panelInflater = null;
    Button switch_camera = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        camInstance = getCamera(currentCameraID);
        cameraPreview = new CameraPreview(this, camInstance);
        previewHolder = cameraPreview.getHolder();
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);

        setTabs();

        // Directory Creation for Photos
        // Also do in background
        File photoDir = new File(Environment.getExternalStorageDirectory().getPath() + "/Plumeria/");
        if (photoDir.exists()) {
            // Do nothing
        } else {
            photoDir.mkdir();
        }

        // Orientation Listener
        uiOrientationEventListener = new UIOrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL);
        if (uiOrientationEventListener.canDetectOrientation()) {
            uiOrientationEventListener.enable();
        }

        // Layout overlay SurfaceOverview
        panelInflater = LayoutInflater.from(getBaseContext());
        View viewControl = panelInflater.inflate(R.layout.control_panel, null);
        LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addContentView(viewControl, layoutParamsControl);

        switch_camera = (Button)findViewById(R.id.button);
        switch_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(getBaseContext(), "Yo", Toast.LENGTH_SHORT).show();
                if(inPreview) {
                    camInstance.stopPreview();
                }
                camInstance.release();
                preview.removeView(cameraPreview);
                cameraPreview = null;

                // Swap the camera based on ID
                if(currentCameraID == 0) {
                    currentCameraID = 1;
                } else {
                    currentCameraID = 0;
                }
                camInstance = getCamera(currentCameraID);

                if(cameraPreview == null) {
                    cameraPreview = new CameraPreview(getApplicationContext(), camInstance);
                    preview.addView(cameraPreview);
                }
            }
        });


    }

    public static Camera getCamera(int currentCameraID) {
        Camera cam = Camera.open(currentCameraID);
        return cam;
    }

    private void setTabs() {
        tabHost = getTabHost();

        addNewTab("", R.drawable.gallery_tab, Tab.class);
        tabHost.getTabWidget().getChildTabViewAt(0).setBackgroundDrawable(null);

        addNewTab(" ", R.drawable.gallery_tab, Tab.class);
        // disable a specific tab
        tabHost.getTabWidget().getChildTabViewAt(1).setEnabled(false);
        tabHost.getTabWidget().getChildTabViewAt(1).setBackgroundDrawable(null);

        addNewTab("", R.drawable.thumb_tab, Tab.class);
        tabHost.getTabWidget().getChildTabViewAt(2).setBackgroundDrawable(null);

        pictureTab = (ImageButton)findViewById(R.id.capture);
        pictureTab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Toast.makeText(MyActivity.this, "Captured.", Toast.LENGTH_SHORT).show();
                // Callback for a photo capture
                camInstance.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean b, Camera camera) {
                        camInstance.takePicture(null, null, captureCallback);
                    }
                });
                return true;
            }
        });

    }

    private Camera.PictureCallback captureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = new File(Environment.getExternalStorageDirectory().getPath()
                    + "/Plumeria/" + "Plumeria" + System.currentTimeMillis() + ".jpeg");

            // Rotatation byte before FileOutputStream
            byte[] photoBytes;
            Bitmap rotateBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            rotateBitmap = Bitmap.createBitmap(rotateBitmap, 0, 0, rotateBitmap.getWidth(), rotateBitmap.getHeight(), matrix, true);

            // Compress and convert ByteArrayOutputStream
            ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
            rotateBitmap.compress(Bitmap.CompressFormat.JPEG, 100, BAOS);
            photoBytes = BAOS.toByteArray();

            try {
                FileOutputStream fOS = new FileOutputStream(pictureFile);
                fOS.write(photoBytes);
                fOS.close();
            } catch (IOException e){
                Log.d("Save failed", "Check bugs");
            }
            camInstance.startPreview();
        }
    };

    public void addNewTab(String labelID, int drawableID, Class<?> cls){
        Intent intent = new Intent().setClass(this, cls);

        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
        TextView title = (TextView)tabIndicator.findViewById(R.id.title);
        title.setText(labelID);

        ImageView icon = (ImageView)tabIndicator.findViewById(R.id.icon);
        icon.setImageResource(drawableID);

        TabHost.TabSpec spec = getTabHost().newTabSpec(labelID)
            .setIndicator(tabIndicator)
            .setContent(intent);
        getTabHost().addTab(spec);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
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

    @Override
    public void onPause() {
        if(inPreview) {
            camInstance.stopPreview();
        }
        camInstance.release();
        camInstance = null;
        inPreview = false;

        preview.removeView(cameraPreview);
        cameraPreview = null;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(camInstance == null) {
            camInstance = getCamera(currentCameraID);
        }
        if(cameraPreview == null) {
            cameraPreview = new CameraPreview(this, camInstance);
            preview.addView(cameraPreview);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // OrientationEventListener class for UI
    public class UIOrientationEventListener extends OrientationEventListener {

        public UIOrientationEventListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int arg0) {
            // when orientation changes
            if(arg0 == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return;
            } else if(arg0 > 315 || arg0 <= 45) {
                arg0 = 0;
            } else if(arg0 > 45 && arg0 <= 135) {
                // Counter clockwise 90
                arg0 = 90;
            } else if(arg0 > 135 && arg0 <= 225) {
                arg0 = 180;
            } else if(arg0 > 225 && arg0 <= 315) {
                arg0 = 270;
            }

            Log.d("oc", String.valueOf(arg0));
        }
    }
}
