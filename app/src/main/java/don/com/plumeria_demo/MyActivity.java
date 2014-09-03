package don.com.plumeria_demo;

import android.app.TabActivity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MyActivity extends TabActivity {

    private Camera camInstance;
    private CameraPreview cameraPreview;

    TabHost tabHost;
    ImageButton pictureTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        camInstance = getCamera();
        cameraPreview = new CameraPreview(this, camInstance);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(cameraPreview);

        setTabs();
    }

    public static Camera getCamera() {
        Camera cam = Camera.open(0);
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
        pictureTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MyActivity.this, "Yo", Toast.LENGTH_SHORT).show();
            }
        });

    }

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
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}