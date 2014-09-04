package don.com.plumeria_demo;

import android.app.TabActivity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@SuppressWarnings("deprecation")
public class MyActivity extends TabActivity {

    private Camera camInstance;
    private CameraPreview cameraPreview;
    FrameLayout preview;

    TabHost tabHost;
    ImageButton pictureTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        camInstance = getCamera();
        cameraPreview = new CameraPreview(this, camInstance);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);

        setTabs();

        // Directory Creation for Photos
        File photoDir = new File(Environment.getExternalStorageDirectory().getPath() + "/Plumeria/");
        if (photoDir.exists()) {
            // Do nothing
        } else {
            photoDir.mkdir();
        }
    }

    public static Camera getCamera() {
        Camera cam = null;
        cam = Camera.open();
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
        public void onPictureTaken(byte[] bytes, Camera camera) {
            File pictureFile = new File(Environment.getExternalStorageDirectory().getPath()
                    + "/Plumeria/" + "Plumeria" + System.currentTimeMillis() + ".jpeg");
            try {
                FileOutputStream fOS = new FileOutputStream(pictureFile);
                fOS.write(bytes);
                fOS.close();
            } catch (IOException e){
                Log.d("Save failed", "damn");
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
        super.onPause();
        camInstance.stopPreview();
        camInstance.release();
        camInstance = null;

        preview.removeView(cameraPreview);
        cameraPreview = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(camInstance == null) {
            camInstance = getCamera();
        }
        if(cameraPreview == null) {
            cameraPreview = new CameraPreview(this, camInstance);
            preview.addView(cameraPreview);
        }
    }

}
