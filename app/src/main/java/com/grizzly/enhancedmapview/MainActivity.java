package com.grizzly.enhancedmapview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.grizzly.mapview.*;
import com.grizzly.mapview.BuildConfig;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EnhancedMapView mapView;
    private Button toggleDraw;
    private Button deleteLines;
    private boolean toggle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = (EnhancedMapView) findViewById(R.id.activity_main_mapview);
        toggleDraw = (Button) findViewById(R.id.activity_main_toolbox_draw);
        deleteLines = (Button) findViewById(R.id.activity_main_toolbox_deletelines);

        initializeComponents();
    }

    @Override
    protected void onResume(){
        super.onResume();
        try{
            //mapView.getMapView().onResume();
            mapView.onResume();
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        try{
            mapView.getMapView().onDestroy();
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    private void initializeComponents(){
        toggleDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!toggle){
                    toggle = true;
                    toggleDraw.setText(getString(R.string.draw_stop));
                }else{
                    toggle = false;
                    toggleDraw.setText(getString(R.string.draw));
                }

                mapView.setCaptureTouches(toggle);
            }
        });
        deleteLines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapView.clearPolylines();
            }
        });

        mapView.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                //Toast.makeText(getThis(), "camera was touched!", Toast.LENGTH_SHORT).show();
            }
        });

        mapView.setOnMapDrawingListener(new EnhancedMapView.OnMapDrawingListener() {
            @Override
            public void onMapDrawing(List<LatLng> poligon) {
                Toast.makeText(getThis(), "Coordinates!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private AppCompatActivity getThis(){
        return this;
    }
}
