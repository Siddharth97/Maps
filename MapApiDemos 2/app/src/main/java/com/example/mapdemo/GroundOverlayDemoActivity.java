

package com.example.mapdemo;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * This shows how to add a ground overlay to a map.
 */
public class GroundOverlayDemoActivity extends AppCompatActivity
        implements OnSeekBarChangeListener, OnMapReadyCallback,
        GoogleMap.OnGroundOverlayClickListener {

    private static final int TRANSPARENCY_MAX = 100;

    private static final LatLng NEWARK = new LatLng(40.714086, -74.228697);

    private static final LatLng NEAR_NEWARK =
            new LatLng(NEWARK.latitude - 0.001, NEWARK.longitude - 0.025);

    private final List<BitmapDescriptor> mImages = new ArrayList<BitmapDescriptor>();

    private GroundOverlay mGroundOverlay;

    private GroundOverlay mGroundOverlayRotated;

    private SeekBar mTransparencyBar;

    private int mCurrentEntry = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ground_overlay_demo);

        mTransparencyBar = (SeekBar) findViewById(R.id.transparencySeekBar);
        mTransparencyBar.setMax(TRANSPARENCY_MAX);
        mTransparencyBar.setProgress(0);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Register a listener to respond to clicks on GroundOverlays.
        map.setOnGroundOverlayClickListener(this);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(NEWARK, 11));

        mImages.clear();
        mImages.add(BitmapDescriptorFactory.fromResource(R.drawable.newark_nj_1922));
        mImages.add(BitmapDescriptorFactory.fromResource(R.drawable.newark_prudential_sunny));

        // Add a small, rotated overlay that is clickable by default
        // (set by the initial state of the checkbox.)
        mGroundOverlayRotated = map.addGroundOverlay(new GroundOverlayOptions()
                .image(mImages.get(1)).anchor(0, 1)
                .position(NEAR_NEWARK, 4300f, 3025f)
                .bearing(30)
                .clickable(((CheckBox) findViewById(R.id.toggleClickability)).isChecked()));

        // Add a large overlay at Newark on top of the smaller overlay.
        mGroundOverlay = map.addGroundOverlay(new GroundOverlayOptions()
                .image(mImages.get(mCurrentEntry)).anchor(0, 1)
                .position(NEWARK, 8600f, 6500f));

        mTransparencyBar.setOnSeekBarChangeListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        map.setContentDescription("Google Map with ground overlay.");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mGroundOverlay != null) {
            mGroundOverlay.setTransparency((float) progress / (float) TRANSPARENCY_MAX);
        }
    }

    public void switchImage(View view) {
        mCurrentEntry = (mCurrentEntry + 1) % mImages.size();
        mGroundOverlay.setImage(mImages.get(mCurrentEntry));
    }

    /**
     * Toggles the visibility between 100% and 50% when a {@link GroundOverlay} is clicked.
     */
    @Override
    public void onGroundOverlayClick(GroundOverlay groundOverlay) {
        // Toggle transparency value between 0.0f and 0.5f. Initial default value is 0.0f.
        mGroundOverlayRotated.setTransparency(0.5f - mGroundOverlayRotated.getTransparency());
    }

    /**
     * Toggles the clickability of the smaller, rotated overlay based on the state of the View that
     * triggered this call.
     * This callback is defined on the CheckBox in the layout for this Activity.
     */
    public void toggleClickability(View view) {
        if (mGroundOverlayRotated != null) {
            mGroundOverlayRotated.setClickable(((CheckBox) view).isChecked());
        }
    }
}
